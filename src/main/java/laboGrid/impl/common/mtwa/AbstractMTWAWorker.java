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
package laboGrid.impl.common.mtwa;

import java.io.IOException;

import laboGrid.graphs.mapping.kl.KLPart;
import laboGrid.graphs.mapping.kl.KLRefinement;
import laboGrid.graphs.mapping.kl.Vertex;
import laboGrid.graphs.mapping.mtwa.MigrationScheduler;
import laboGrid.impl.common.mtwa.messages.BottomUpPropMessage;
import laboGrid.impl.common.mtwa.messages.DestinationPartitionMessage;
import laboGrid.impl.common.mtwa.messages.MTWAInterWorkerMessage;
import laboGrid.impl.common.mtwa.messages.MTWAWorkerMessage;
import laboGrid.impl.common.mtwa.messages.MigrationMessage;
import laboGrid.impl.common.mtwa.messages.RequestPartitionMessage;
import laboGrid.impl.common.mtwa.messages.TopDownPropMessage;
import laboGrid.impl.common.mtwa.messages.TriggerBottomUpPropMessage;
import laboGrid.impl.decentral.mtwa.SyncDa2Subs;

import dimawo.agents.ErrorHandler;
import dimawo.agents.LoggingAgent;
import dimawo.agents.UnknownAgentMessage;
import dimawo.middleware.commonEvents.BrokenDA;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.overlay.ComputerTreePosition;


public abstract class AbstractMTWAWorker extends LoggingAgent {
	
	private CommunicatorInterface com;

	// Computer tree data
	private int currentConfiguration;
	private ComputerTreePosition ctPos;
	
	// Associated partition
	private Vertex[] part;
	
	// TWA variables
	private long ccp, aCcp;
	private int aNSubs;
	private int[] childANSubs;
	private long[] childACcps;
	private int awaitedBottomUp;
	
	private double quota, aQuota;
	private double[] childAQuotas;
	
	private MigrationScheduler sched;


	public AbstractMTWAWorker(ErrorHandler parent, String name,
			DistributedAgent da) {
		super(parent, name);
		
		com = da.getCommunicator();
		setPrintStream(da.getFilePrefix());
	}
	
	protected void setCTPosition(int conf, ComputerTreePosition ctPos) {
		this.currentConfiguration = conf;
		this.ctPos = ctPos;
		
		int maxNumOfChildren = ctPos.getMaxNumOfChildren();
		childACcps = new long[maxNumOfChildren];
		childANSubs = new int[maxNumOfChildren];
		childAQuotas = new double[maxNumOfChildren];
		sched = new MigrationScheduler(maxNumOfChildren);
	}
	
	protected boolean triggerMtwa() throws IOException {
		if(ctPos.hasParent())
			return false;

		agentPrintMessage("MTWA triggered ("+currentConfiguration+")");
		
		if(ctPos.hasNoChild()) {
			submitStablePartition(currentConfiguration, part);
		} else {
			triggerBottomUpPropagation();
		}
		
		return true;
	}
	
	protected void setCcp(long ccp) {
		this.ccp = ccp;
	}
	
	protected void setInitialPartition(Vertex[] part) {
		this.part = part;
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
	}

	@Override
	protected void handleMessage(Object o) throws Throwable {
		if(o instanceof BrokenDA) {
			handleBrokenDA((BrokenDA) o);
		} else if(o instanceof MTWAInterWorkerMessage) {
			handleMTWAInterWorkerMessage((MTWAInterWorkerMessage) o);
		} else {
			handleSpecializedMessage(o);
		}
	}
	
	private void handleBrokenDA(BrokenDA o) throws InterruptedException {
		DAId daId = o.getDAId();
		agentPrintMessage(daId+" broken.");
	}

	private void handleMTWAInterWorkerMessage(MTWAInterWorkerMessage tmm) throws Throwable {
		int msgConf = tmm.getConfiguration();
		if(msgConf < currentConfiguration) {
			agentPrintMessage("Ignored obsolete inter-worker message: "+tmm.getClass().getName());
			return;
		} else if(msgConf > currentConfiguration) {
			throw new Error("Must queue messages");
		}

		if(tmm instanceof TopDownPropMessage) {
			handleTopDownPropMessage((TopDownPropMessage) tmm);
		} else if(tmm instanceof TriggerBottomUpPropMessage) {
			handleTriggerBottomUpPropMessage((TriggerBottomUpPropMessage) tmm);
		} else if(tmm instanceof BottomUpPropMessage) {
			handleBottomUpPropMessage((BottomUpPropMessage) tmm);
		} else if(tmm instanceof RequestPartitionMessage) {
			handleRequestPartitionMessage((RequestPartitionMessage) tmm);
		} else if(tmm instanceof DestinationPartitionMessage) {
			handleDestinationPartitionMessage((DestinationPartitionMessage) tmm);
		} else if(tmm instanceof MigrationMessage) {
			handleMigrationMessage((MigrationMessage) tmm);
		} else {
			throw new UnknownAgentMessage(tmm);
		}
	}

	private void handleMigrationMessage(MigrationMessage tmm) throws Exception {
		if(sched.isStable()) {
			throw new Error("Partition is already stable");
		}
		
		agentPrintMessage("Inserting nodes in partition");
		
		Vertex[] mig = tmm.getVertices();
		KLPart klPart = new KLPart(part);
		klPart.add(mig);
		klPart.checkValidity();
		part = klPart.getVertices();

		DAId senderId = tmm.getSender();
		if(ctPos.isParent(senderId)) {
			sched.setRecvFromParent(0);
		} else {
			int childIndex = ctPos.getChildIndex(senderId);
			if(childIndex < 0)
				throw new Error("Not a child: "+senderId);
			
			sched.setRecvFromChild(childIndex, 0);
		}
		
		if(sched.isStable()) {
			submitStablePartition(currentConfiguration, part);
		} else if(sched.isSourceOnly()) {
			triggerMigration();
		}
	}

	private void handleRequestPartitionMessage(RequestPartitionMessage tmm) throws IOException {
		if(sched.isStable()) {
			throw new Error("Partition is already stable");
		}

		DAId senderId = tmm.getSender();
		Vertex[] partCopy = part.clone();
		
		DestinationPartitionMessage dpm = new DestinationPartitionMessage(senderId, currentConfiguration, partCopy);
		com.sendDatagramMessage(dpm);
	}
	
	private void handleDestinationPartitionMessage(DestinationPartitionMessage tmm) throws Exception {
		DAId senderId = tmm.getSender();
		Vertex[] destPart = tmm.getPartition();
		
		if(ctPos.isParent(senderId)) {
			int toSend = sched.getSendToParent();

			migrateVertices(senderId, destPart, toSend);
			
			sched.setSendToParent(0);

		} else {
			int childIndex = ctPos.getChildIndex(senderId);
			if(childIndex == -1)
				throw new Error("Unknown DA ID: "+senderId);
			
			int toSend = sched.getSendToChild(childIndex);

			migrateVertices(senderId, destPart, toSend);
			
			sched.setSendToChild(childIndex, 0);
		}

		if(sched.isStable()) {
			submitStablePartition(currentConfiguration, part);
		}
	}

	private void migrateVertices(DAId destId, Vertex[] destPart, int toMove)
			throws Exception {
		KLPart a = new KLPart(part);
		KLPart b = new KLPart(destPart);
		KLRefinement ref = new KLRefinement();
		ref.setPartitions(a, b);
		int[] toMigrate = ref.getVertexToMigrateFromAToB(toMove);
		Vertex[] X = a.remove(toMigrate);
		part = a.getVertices();
		
		MigrationMessage mm = new MigrationMessage(destId, currentConfiguration, X);
		com.sendDatagramMessage(mm);
	}

	private void handleBottomUpPropMessage(BottomUpPropMessage tmm) throws IOException {
		DAId childId = tmm.getSender();
		int childIndex = ctPos.getChildIndex(childId);
		
		if(childIndex < 0)
			throw new Error("DA "+childId+" is not a child");

		childACcps[childIndex] = tmm.getACcp();
		childANSubs[childIndex] = tmm.getANSubs();
		
		aCcp += tmm.getACcp();
		aNSubs += tmm.getANSubs();

		--awaitedBottomUp;
		if(awaitedBottomUp == 0) {
			agentPrintMessage("aCcp="+aCcp);
			agentPrintMessage("aNSubs="+aNSubs);
		
			DAId parentId = ctPos.getParentId();
			if(parentId != null)
				com.sendDatagramMessage(new BottomUpPropMessage(parentId,
						currentConfiguration, aCcp, aNSubs));
			else
				propagateAverageLoad();
		}
	}

	private void handleTriggerBottomUpPropMessage(TriggerBottomUpPropMessage tmm) throws IOException {
		if(ctPos.hasNoChild()) {
			aCcp = ccp;
			aNSubs = part.length;
			com.sendDatagramMessage(new BottomUpPropMessage(ctPos.getParentId(), currentConfiguration, aCcp, aNSubs));
		} else {
			triggerBottomUpPropagation();
		}
	}

	private void handleTopDownPropMessage(TopDownPropMessage tmm) throws IOException {
		double avgLoad = tmm.getAverageLoad();
		triggerTopDownProp(avgLoad);
	}

	private void triggerBottomUpPropagation() throws IOException {
		agentPrintMessage("Trigger bottom-up propagation.");
		awaitedBottomUp = 0;
		aCcp = ccp;
		aNSubs = part.length;
		for(int i = 0; i < ctPos.getMaxNumOfChildren(); ++i) {
			DAId childId = ctPos.getChildId(i);
			if(childId != null) {
				++awaitedBottomUp;
				com.sendDatagramMessage(new TriggerBottomUpPropMessage(childId,
						currentConfiguration));
			}
		}
	}

	private void propagateAverageLoad() throws IOException {
		double avgLoad = aNSubs / (double) aCcp;
		
		triggerTopDownProp(avgLoad);
	}

	private void triggerTopDownProp(double avgLoad) throws IOException {
		quota = ccp * avgLoad;
		aQuota = aCcp * avgLoad;
		
		agentPrintMessage("quota="+quota);
		agentPrintMessage("aQuota="+aQuota);
		
		if(ctPos.hasParent()) {
			sched.setAggregatedVariables(aNSubs, aQuota);
		}
		
		for(int i = 0; i < childAQuotas.length; ++i) {
			if(ctPos.hasChild(i)) {
				childAQuotas[i] = childACcps[i] * avgLoad;
				agentPrintMessage("childAQuotas["+i+"]="+childAQuotas[i]);
				
				sched.setChildAggregatedVariables(i, childANSubs[i], childAQuotas[i]);
			}
		}
		
		propagateDown(avgLoad);
		
		sched.schedule();
		if(sched.isSourceOnly()) {
			triggerMigration();
		} else if(sched.isStable()) {
			submitStablePartition(currentConfiguration, part);
		}
	}

	private void triggerMigration() throws IOException {
		agentPrintMessage("Trigger migration.");
		sched.correctSchedule(part.length, quota);
		
		int sendToParent = sched.getSendToParent();
		if(sendToParent > 0) {
			RequestPartitionMessage req = new RequestPartitionMessage(ctPos.getParentId(),
					currentConfiguration);
			com.sendDatagramMessage(req);
		}
		
		for(int i = 0; i < ctPos.getMaxNumOfChildren(); ++i) {
			DAId childId = ctPos.getChildId(i);
			int send = sched.getSendToChild(i);
			if(childId != null && send > 0) {
				RequestPartitionMessage req = new RequestPartitionMessage(childId,
						currentConfiguration);
				com.sendDatagramMessage(req);
			}
		}
	}

	private void propagateDown(double avgLoad) throws IOException {
		agentPrintMessage("Top-down propagation");
		for(int i = 0; i < ctPos.getMaxNumOfChildren(); ++i) {
			DAId childId = ctPos.getChildId(i);
			if(childId != null) {
				TopDownPropMessage palm = new TopDownPropMessage(childId,
						currentConfiguration);
				palm.setAverageLoad(avgLoad);
				com.sendDatagramMessage(palm);
			}
		}
	}

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
	}

	public void submitMTWAWorkerMessage(MTWAWorkerMessage m) {
		try {
			submitMessage(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected boolean isCtRoot() {
		return ctPos != null && ! ctPos.hasParent();
	}
	
	protected void setCurrentConfiguration(int conf) {
		if(conf != -1 && conf < currentConfiguration)
			throw new Error("setting obsolete configuration");
		this.currentConfiguration = conf;
	}
	
	protected int getCurrentConfiguration() {
		return currentConfiguration;
	}
	
	protected ComputerTreePosition getComputerTreePosition() {
		return ctPos;
	}
	
	protected abstract void handleSpecializedMessage(Object o) throws Exception;
	protected abstract void submitStablePartition(int conf, Vertex[] part);
}
