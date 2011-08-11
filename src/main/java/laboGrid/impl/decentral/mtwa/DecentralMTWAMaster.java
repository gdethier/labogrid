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

import laboGrid.impl.decentral.controller.DecentralLBController;
import laboGrid.impl.decentral.mtwa.events.RequestTwa;
import laboGrid.impl.decentral.mtwa.messages.DecentralMtwaMasterMessage;
import laboGrid.impl.decentral.mtwa.messages.TopologyChangeMessage;
import laboGrid.impl.decentral.mtwa.messages.UpdateConfigurationMessage;
import dimawo.agents.LoggingAgent;
import dimawo.agents.UnknownAgentMessage;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;

public class DecentralMTWAMaster extends LoggingAgent {
	
	private DecentralLBController ctrl;
	private DistributedAgent da;
//	private CommunicatorInterface com;
	
	private int currentReqId = -1;
//	private DAId currentCtRoot;
	

	public DecentralMTWAMaster(DecentralLBController ctrl) {
		super(ctrl, "DecentralMTWAMaster");

		this.ctrl = ctrl;
		this.da = ctrl.getHostingDA();
//		currentCtRoot = da.getDaId();
		
		setPrintStream(da.getFilePrefix());
	}
	
	public void requestSub2Da(int version) {
		try {
			submitMessage(new RequestTwa(version, -1, null, null));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
//		com = da.getCommunicator();
	}

	@Override
	protected void handleMessage(Object o) throws Throwable {
//		if(o instanceof TopologyChangeMessage) {
//			handleTopologyChangeMessage((TopologyChangeMessage) o);
//		} else 
		if(o instanceof RequestTwa) {
			handleRequestTwa((RequestTwa) o);
		} else if(o instanceof StableBarrierReached) {
			handleStableBarrierReached((StableBarrierReached) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}
	
	private void handleRequestTwa(RequestTwa o) {
		currentReqId = o.getRequestId();
//		com.sendDatagramMessage(new UpdateConfigurationMessage(
//				currentCtRoot,
//				currentReqId,
//				null));
		
		agentPrintMessage("New request, updating configuration ("+currentReqId+")");
	}

//	private void handleTopologyChangeMessage(TopologyChangeMessage tcm) {
//		DAId sourceDa = tcm.getSourceDaId();
//
//		++currentConfiguration;
//		com.sendDatagramMessage(new UpdateConfigurationMessage(
//				currentCtRoot,
//				currentConfiguration,
//				sourceDa));
//		
//		agentPrintMessage("New topology change, updating configuration ("+currentConfiguration+")");
//	}

	private void handleStableBarrierReached(StableBarrierReached reach) {
		int conf = reach.getConf();
		if(conf < currentReqId) {
			agentPrintMessage("Ignored obsolete stable-reached");
			return;
		} else if(conf > currentReqId) {
			throw new Error("MTWA master out of sync");
		}
		
		agentPrintMessage("Stable mapping ready");
		Sub2Da sub2Da = reach.getSub2Da();
		ctrl.submitNewSub2Da(sub2Da);
	}

	public void submitDecentralMtwaMasterMessage(DecentralMtwaMasterMessage msg) {
		try {
			submitMessage(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
