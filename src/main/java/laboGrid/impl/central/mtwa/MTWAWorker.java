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

import laboGrid.graphs.mapping.kl.Vertex;
import laboGrid.impl.central.mtwa.messages.DAPositioned;
import laboGrid.impl.central.mtwa.messages.InitialPartitionMessage;
import laboGrid.impl.central.mtwa.messages.PositionMessage;
import laboGrid.impl.central.mtwa.messages.StablePartitionMessage;
import laboGrid.impl.central.mtwa.messages.TriggerMTWAMessage;
import laboGrid.impl.central.task.LBTask;
import laboGrid.impl.common.mtwa.AbstractMTWAWorker;

import dimawo.agents.UnknownAgentMessage;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.overlay.ComputerTreePosition;


public class MTWAWorker extends AbstractMTWAWorker {

	private DistributedAgent da;

	public MTWAWorker(LBTask task) {
		super(task, "MTWAWorker", task.getDistributedAgent());

		da = task.getDistributedAgent();
	}

	private void handlePositionMessage(PositionMessage o) throws InterruptedException {
		int currentConfiguration = o.getConfiguration();
		DAId parentId = o.getParentId();
		DAId[] childIds = o.getChildIds();
		long ccp = o.getCCP();
		
		this.setCTPosition(currentConfiguration, new ComputerTreePosition(da.getDaId(), parentId, childIds));
		this.setCcp(ccp);
		
		agentPrintMessage("DA positioned in CT ("+currentConfiguration+")");
		agentPrintMessage("parent="+parentId);
		agentPrintMessage("children=");
		for(int i = 0; i < childIds.length; ++i) {
			if(childIds[i] != null)
				agentPrintMessage(childIds[i].toString());
		}
		
		da.getOverlayInterface().getLeaderElectionInterface().sendMessageToLeader(new DAPositioned(da.getDaId(),
				currentConfiguration));
	}

	private void handleInitialPartitionMessage(InitialPartitionMessage o) {
		int currentConfiguration = o.getConfiguration();
		this.setCurrentConfiguration(currentConfiguration);
		
		Vertex[] part = o.getPartition();
		this.setInitialPartition(part);
		
		agentPrintMessage("Updated configuration: "+currentConfiguration);
		agentPrintMessage("Received initial partition (size="+part.length+")");
	}

	@Override
	protected void handleSpecializedMessage(Object o) throws Exception {
		if(o instanceof InitialPartitionMessage) {
			handleInitialPartitionMessage((InitialPartitionMessage) o);
		} else if(o instanceof PositionMessage) {
			handlePositionMessage((PositionMessage) o);
		} else if(o instanceof TriggerMTWAMessage) {
			handleTriggerMTWAMessage((TriggerMTWAMessage) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	@Override
	protected void submitStablePartition(int conf, Vertex[] part) {
		agentPrintMessage("Submitting stable partition.");
		StablePartitionMessage spm =
			new StablePartitionMessage(da.getDaId(), part, conf);
		da.getOverlayInterface().getLeaderElectionInterface().sendMessageToLeader(spm);
	}
	
	private void handleTriggerMTWAMessage(TriggerMTWAMessage tmm) throws IOException {
		this.setCurrentConfiguration(tmm.getConf());
		if(! triggerMtwa()) {
			throw new Error("MTWA could not be triggered");
		}
	}

}
