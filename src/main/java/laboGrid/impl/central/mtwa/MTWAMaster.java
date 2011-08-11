/*
 * #%L
 * LaBoGrid
 * %%
 * Copyright (C) 2011 LaBoGrid Team
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package laboGrid.impl.central.mtwa;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import laboGrid.graphs.GraphTranslator;
import laboGrid.graphs.mapping.kl.Vertex;
import laboGrid.graphs.mapping.mtwa.CTNode;
import laboGrid.graphs.mapping.mtwa.ComputerTree;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.resource.ResourceGraph;
import laboGrid.impl.central.controllerAgent.experimenter.Experimenter;
import laboGrid.impl.central.controllerAgent.experimenter.events.MTWAGraphMapping;
import laboGrid.impl.central.mtwa.messages.DAPositioned;
import laboGrid.impl.central.mtwa.messages.InitialPartitionMessage;
import laboGrid.impl.central.mtwa.messages.MTWAMasterMessage;
import laboGrid.impl.central.mtwa.messages.PositionMessage;
import laboGrid.impl.central.mtwa.messages.StablePartitionMessage;
import laboGrid.impl.central.mtwa.messages.TriggerMTWAMessage;
import laboGrid.impl.common.mtwa.ModelGraphConverter;
import laboGrid.lb.SubLattice;

import dimawo.agents.LoggingAgent;
import dimawo.agents.UnknownAgentMessage;
import dimawo.middleware.commonEvents.BrokenDA;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.communication.Message;
import dimawo.middleware.communication.outputStream.MOSCallBack;
import dimawo.middleware.distributedAgent.DAId;


public class MTWAMaster extends LoggingAgent implements MOSCallBack {

	private Experimenter exp;
	private CommunicatorInterface com;

	private int currentConfiguration = -1;

	private ModelGraph mGraph;
	private ResourceGraph rGraph;
	private GraphTranslator trans;
	private ComputerTree ct;
	
	private int waitingPositioned;
	private int awaitedPartitions;

	private TreeMap<DAId, Vertex[]> da2Sub;
	private DAId[] sub2Da;
	
	private TreeMap<DAId, Vertex[]> candidateDa2Sub;
	

	public MTWAMaster(Experimenter exp) {
		super(exp, "MTWAMaster");
		
		this.exp = exp;
		com = exp.getCommunicator();
		
		setPrintStream(exp.getFilePrefix());
	}

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
	}

	@Override
	protected void handleMessage(Object o) throws Throwable {
		if(o instanceof ModelGraph) {
			handleNewModelGraph((ModelGraph) o);
		} else if(o instanceof BrokenDA) {
			handleBrokenDA((BrokenDA) o);
		} else if(o instanceof ResourceGraph) {
			handleNewResourceGraph((ResourceGraph) o);
		} else if(o instanceof MTWAMasterMessage) {
			handleMTWAMasterMessage((MTWAMasterMessage) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	private void handleMTWAMasterMessage(MTWAMasterMessage o) throws Exception {
		if(o.getConfiguration() != currentConfiguration) {
			agentPrintMessage("Ignored osbolete message: "+o.getClass().getName());
			return;
		}
		
		if(o instanceof DAPositioned) {
			handleDAPositioned((DAPositioned) o);
		} else if(o instanceof StablePartitionMessage) {
			handleStablePartitionMessage((StablePartitionMessage) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	private void handleStablePartitionMessage(StablePartitionMessage o) {
		DAId senderId = o.getSender();
		Vertex[] part = o.getPartition();
		
		agentPrintMessage("Received stable partition from "+senderId);
		
		candidateDa2Sub.put(senderId, part);
		
		--awaitedPartitions;
		if(awaitedPartitions == 0) {
			setCurrentPartitioning();
		} else {
			agentPrintMessage("Waiting "+awaitedPartitions+" more stable partitions");
		}
	}

	private void setCurrentPartitioning() {
		agentPrintMessage("Submitting stable partitioning to experimenter ("+currentConfiguration+")");
		
		da2Sub.clear();
		da2Sub.putAll(candidateDa2Sub);
		
		// Build sub2Da
		for(Iterator<Entry<DAId, Vertex[]>> it = da2Sub.entrySet().iterator(); it.hasNext();) {
			Entry<DAId, Vertex[]> e = it.next();
			
			DAId daId = e.getKey();
			Vertex[] part = e.getValue();
			for(int i = 0; i < part.length; ++i) {
				int subId = part[i].getSubID();
				
				sub2Da[subId] = daId;
			}
		}
		
		exp.submitMTWAGraphMapping(rGraph, da2Sub, sub2Da);
	}

	private void handleDAPositioned(DAPositioned o) throws IOException {
		agentPrintMessage("DA "+o.getSender()+" positioned in CT");

		int conf = o.getConfiguration();
		if(conf != currentConfiguration) {
			agentPrintMessage("Ignored obsolete positioning.");
			return;
		}
		
		--waitingPositioned;
		if(waitingPositioned == 0) {
			agentPrintMessage("Triggering MTWA.");
			DAId rootDaId = ct.getRoot().getDaId();
			
			TriggerMTWAMessage trig = new TriggerMTWAMessage(rootDaId, currentConfiguration);
			trig.setCallBack(this);
			com.sendDatagramMessage(trig);
		} else {
			agentPrintMessage("Waiting for "+waitingPositioned+" more positionings");
		}
	}

	private void handleNewResourceGraph(ResourceGraph o) throws IOException {
		++currentConfiguration; // next config.
		agentPrintMessage("New configuration "+currentConfiguration);
		
		rGraph = o;
		ct = ComputerTree.newInstance(o);
		ct.labelNodes(mGraph.getSubLattice(0).getSize(), rGraph);
		trans = new GraphTranslator(o.getNewIds());
		trans.translateComputerTree(ct);
		
		if(da2Sub.isEmpty()) {
			// No stable partitioning available
			CTNode root = ct.getRoot();
			DAId rootDaId = root.getDaId();
			
			Vertex[] all = ModelGraphConverter.convertModelGraph(mGraph);
			
			for(Iterator<CTNode> it = ct.iterator(); it.hasNext();) {
				CTNode n = it.next();
				DAId daId = n.getDaId();
				
				agentPrintMessage("Sending initial partition to DA "+daId);
				InitialPartitionMessage ipm;
				if(! daId.equals(rootDaId)) {
					ipm = new InitialPartitionMessage(daId);
					ipm.setConfiguration(currentConfiguration);
					ipm.setPartition(new Vertex[]{});
					ipm.setCallBack(this);
				} else {
					ipm = new InitialPartitionMessage(rootDaId);
					ipm.setConfiguration(currentConfiguration);
					ipm.setPartition(all);
					ipm.setCallBack(this);
				}

				com.sendDatagramMessage(ipm);
			}
		}
		
		triggerMapping();
	}

	private void triggerMapping() throws IOException {
		awaitedPartitions = waitingPositioned = ct.size();
		candidateDa2Sub = new TreeMap<DAId, Vertex[]>();
		
		for(Iterator<CTNode> it = ct.iterator(); it.hasNext();) {
			CTNode n = it.next();
			CTNode parent = n.getParent();
			DAId parentId = null;
			if(parent != null) {
				parentId = parent.getDaId();
			}
			
			DAId[] childId = new DAId[n.getNumOfChildren()];
			for(int i = 0; i < childId.length; ++i) {
				CTNode child = n.getChild(i);
				if(child != null) {
					childId[i] = child.getDaId();
				}
			}
			
			agentPrintMessage("Positioning DA "+n.getDaId());
			
			PositionMessage pm = new PositionMessage(n.getDaId());
			pm.setParentId(parentId);
			pm.setChildIds(childId);
			pm.setCCP(n.getCCP());
			pm.setConfiguration(currentConfiguration);
			
			com.sendDatagramMessage(pm);
		}
		
		// Wait all DAs are positioned
	}

	private void handleNewModelGraph(ModelGraph m) throws IOException {
		mGraph = m;
		
		// Initially, 
		da2Sub = new TreeMap<DAId, Vertex[]>();
		sub2Da = new DAId[mGraph.getSubLatticesCount()];
	}
	
	private void handleBrokenDA(BrokenDA da) {
		DAId daId = da.getDAId();
		agentPrintMessage("Broken DA "+daId);
		Vertex[] subs = da2Sub.remove(daId);
		if(subs != null && subs.length > 0) {
			throw new Error("unimplemented");
		}
	}

	public void requestMapping(ResourceGraph rGraph) {
		try {
			submitMessage(rGraph);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void submitNewModelGraph(ModelGraph currentModelGraph) {
		try {
			submitMessage(currentModelGraph);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void signalBroken(BrokenDA bda) {
		try {
			submitMessage(bda);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void signalSent(Message m, boolean success) {
		if(! success) {
			try {
				submitMessage(new BrokenDA(m.getRecipient()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void submitMTWAMasterMessage(MTWAMasterMessage m) {
		try {
			submitMessage(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
