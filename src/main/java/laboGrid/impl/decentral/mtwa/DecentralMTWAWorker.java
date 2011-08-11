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
package laboGrid.impl.decentral.mtwa;

import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeSet;

import laboGrid.graphs.mapping.kl.Vertex;
import laboGrid.impl.common.mtwa.AbstractMTWAWorker;
import laboGrid.impl.decentral.mtwa.events.RequestTwa;
import laboGrid.impl.decentral.mtwa.messages.TriggerMtwaMessage;
import laboGrid.impl.decentral.task.DecentralLBTask;

import dimawo.agents.UnknownAgentMessage;
import dimawo.master.messages.MasterMessage;
import dimawo.middleware.barriersync.BarrierWaitCallBack;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.overlay.BarrierID;
import dimawo.middleware.overlay.BarrierSyncCallBackInterface;
import dimawo.middleware.overlay.BarrierSyncInterface;
import dimawo.middleware.overlay.ComputerTreePosition;
import dimawo.middleware.overlay.mntree.MnPeerState;


public class DecentralMTWAWorker extends AbstractMTWAWorker
implements BarrierSyncCallBackInterface {

	private DistributedAgent da;
	private CommunicatorInterface com;
	
//	private boolean waitingMapping = false;
	private BarrierID waitConfBarrierId;
	
	private SyncDa2Subs syncDa2Subs;

	public DecentralMTWAWorker(DecentralLBTask task) {
		super(task, "DecentralMTWAWorker", task.getDistributedAgent());
		
		da = task.getDistributedAgent();
	}
	
	@Override
	protected void init() throws Throwable {
		super.init();

		com = da.getCommunicator();
		
//		OverlayInterface overInt = da.getOverlayInterface();
	}

	@Override
	protected void handleSpecializedMessage(Object o) throws Exception {
		if(o instanceof TriggerMtwaMessage) {
			handleTriggerMtwaMessage((TriggerMtwaMessage) o);
//		} else if(o instanceof UpdateConfigurationMessage) {
//			handleUpdateConfigurationMessage((UpdateConfigurationMessage) o);
		} else if(o instanceof RequestTwa) {
			handleRequestTwa((RequestTwa) o);
		} else if(o instanceof BarrierWaitCallBack) {
			handleBarrierWaitCallBack((BarrierWaitCallBack) o);
		} else if(o instanceof SubtreeDa2SubsMessage) {
			handleSubtreeDa2SubsMessage((SubtreeDa2SubsMessage) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	private void handleSubtreeDa2SubsMessage(SubtreeDa2SubsMessage o) {
		if(this.getCurrentConfiguration() != o.getConf()) {
			agentPrintMessage("Ignored obsolete subtree da2Subs message");
			return;
		}
		
		syncDa2Subs.addChildDa2Subs(o.getDa2Subs());
		if(syncDa2Subs.isComplete()) {
			forwardStableDa2Subs();
		}
	}

	private void handleBarrierWaitCallBack(BarrierWaitCallBack o) throws IOException {
		BarrierID id = o.getBarrierId();
		Throwable err = o.getError();
		
		if(id.equals(waitConfBarrierId)) {
			if(err != null) {
				agentPrintMessage("Ignored following error:");
				agentPrintMessage(err);
				return;
			}

			agentPrintMessage("All nodes are synchronized, twa can start.");
			triggerMtwa();
		} else {
			agentPrintMessage("Unknown barrier ID: "+id);
		}
	}
	
	protected void submitToLocalController(MasterMessage msg) {
		if(! this.isCtRoot())
			throw new Error("this task must be executed by same DA as controller");
		msg.setSourceDaId(da.getDaId());
		msg.setSender(da.getDaId());
		msg.setRecipient(da.getDaId());
		da.submitMasterMessage(msg);
	}

	@Override
	protected void submitStablePartition(int conf, Vertex[] part) {
		int[] subIds = new int[part.length];
		for(int i = 0; i < subIds.length; ++i)
			subIds[i] = part[i].getSubID();

		Da2Subs da2Sub = new Da2Subs(da.getDaId(), subIds);
		syncDa2Subs.addRootDa2Subs(da2Sub);
		
		if(syncDa2Subs.isComplete()) {
			forwardStableDa2Subs();
		}
	}

	private void forwardStableDa2Subs() {
		agentPrintMessage("Subtree sub2Das is stable.");
//		waitingMapping = false;
		if(this.isCtRoot()) {
			Sub2Da sub2Da = generateSub2Da(syncDa2Subs.getDa2Subs());
			submitToLocalController(new StableBarrierReached(this.getCurrentConfiguration(), sub2Da));
		} else {
			com.sendDatagramMessage(new SubtreeDa2SubsMessage(this.getComputerTreePosition().getParentId(), this.getCurrentConfiguration(), syncDa2Subs.getDa2Subs()));
		}
	}
	
	private Sub2Da generateSub2Da(LinkedList<Da2Subs> da2Subs) {
		Sub2Da sub2Da = new Sub2Da();
		for(Da2Subs x : da2Subs) {
			DAId daId = x.getDaId();
			int[] subIds = x.getSubs();
			
			for(int i = 0; i < subIds.length; ++i) {
				sub2Da.add(subIds[i], daId);
			}
		}
		return sub2Da;
	}

	@Override
	public void barrierWaitCB(BarrierWaitCallBack param) {
		try {
			submitMessage(param);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void handleTriggerMtwaMessage(TriggerMtwaMessage o) throws IOException {
		int conf = o.getConf();
		if(conf != this.getCurrentConfiguration()) {
			agentPrintMessage("Ignored obsolete trigger message");
			return;
		}
		
		this.triggerMtwa();
	}

//	private void handleUpdateConfigurationMessage(UpdateConfigurationMessage o) {
//		int currentConf = this.getCurrentConfiguration();
//		int newConf = o.getNewConf();
//		if(currentConf >= newConf) {
//			agentPrintMessage("Ignoring obsolete conf update message");
//			return;
//		}
//		
//		agentPrintMessage("Updating configuration ("+newConf+")");
//		setCurrentConfiguration(newConf);
//		if(waitingMapping) {
//			agentPrintMessage("Syncing on conf");
//			if(this.getComputerTreePosition() != null)
//				barrierSyncConf();
//			else
//				agentPrintMessage("Not yet part of the computer tree");
//		}
//		
//		// Forward to children
//		ComputerTreePosition ctPos = this.getComputerTreePosition();
//		if(ctPos != null) {
//			for(int childIndex = 0; childIndex < ctPos.getMaxNumOfChildren();
//			++childIndex) {
//				DAId childId = ctPos.getChildId(childIndex);
//				if(childId != null) {
//					com.sendDatagramMessage(new UpdateConfigurationMessage(
//							childId, newConf, o.getChangeSource()));
//				}
//			}
//		}
//	}
	
	private void barrierSyncConf() {
		BarrierSyncInterface sync = da.getOverlayInterface().getBarrierSyncInterface();
		if(waitConfBarrierId != null)
			sync.barrierDestroy(waitConfBarrierId);
		waitConfBarrierId = DecentralMtwaKeys.getConfBarrierId(this.getCurrentConfiguration());
		agentPrintMessage("Barrier sync on "+waitConfBarrierId);
		
		syncDa2Subs = new SyncDa2Subs(this.getComputerTreePosition()); // Must
		// be instantiated before mtwa is triggered

		sync.setComputerTreePosition(getComputerTreePosition());
		sync.barrierWait(waitConfBarrierId, this);
	}
	
	public void requestSub2Da(int reqId, long ccp, Vertex[] initPart,
			ComputerTreePosition pos) {
		try {
			submitMessage(new RequestTwa(reqId, ccp, initPart, pos));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void handleRequestTwa(RequestTwa o) {
		int reqId = o.getRequestId();
		long ccp = o.getCcp();
		setCcp(ccp);
		setCTPosition(reqId, o.getCtPosition());

		agentPrintMessage("Received sub2Da request (ccp="+ccp+", reqId="+reqId+")");
		
		Vertex[] part = o.getInitialPartition();
		this.setInitialPartition(part);
		
//		if(! waitingMapping) {
//			waitingMapping = true;
			
			if(this.getComputerTreePosition() != null) {
				barrierSyncConf();
			} 
//			else
//				agentPrintMessage("Not yet part of the computer tree");
//		}
	}
}
