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
package laboGrid.impl.decentral.controller;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.regex.Pattern;

import laboGrid.configuration.LaBoGridConfiguration;
import laboGrid.impl.common.control.DefaultJobSubmitter;
import laboGrid.impl.decentral.controller.event.FileAdded;
import laboGrid.impl.decentral.controller.event.FilesDeleted;
import laboGrid.impl.decentral.controller.event.NewFile;
import laboGrid.impl.decentral.controller.event.NewMapping;
import laboGrid.impl.decentral.controller.messages.BrokenDasMessage;
import laboGrid.impl.decentral.controller.messages.InitialMappingMessage;
import laboGrid.impl.decentral.mtwa.DecentralMTWAMaster;
import laboGrid.impl.decentral.mtwa.Sub2Da;
import laboGrid.impl.decentral.mtwa.messages.DecentralMtwaMasterMessage;
import laboGrid.impl.decentral.task.messages.ConfBarrierReached;
import laboGrid.impl.decentral.task.messages.ReplicationBarrierReached;
import laboGrid.impl.decentral.task.messages.SimEndBarrierReached;
import laboGrid.ioClients.InputClientCallBack;
import laboGrid.ioClients.InputClientMessage;
import laboGrid.ioClients.OutputClientCB;
import laboGrid.ioClients.OutputClientCallBack;
import laboGrid.ioClients.controller.OutputClientMessage;
import laboGrid.lb.LBException;

import dimawo.MasterAgent;
import dimawo.agents.AgentException;
import dimawo.agents.UnknownAgentMessage;
import dimawo.master.messages.MasterMessage;
import dimawo.master.messages.WorkerExitMessage;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.fileSystem.DFSException;
import dimawo.middleware.fileSystem.FileSystemCallBack;
import dimawo.middleware.fileSystem.ReplicateFileResult;
import dimawo.middleware.fileSystem.fileDB.FileDescriptor.FileMode;
import dimawo.middleware.stabilizer.Stabilizer;
import dimawo.middleware.stabilizer.StabilizerCallbackInterface;
import dimawo.middleware.stabilizer.StableTopology;




public class DecentralLBController extends MasterAgent implements InputClientCallBack, FileSystemCallBack, OutputClientCallBack, StabilizerCallbackInterface {
	// LB Configuration
	private LaBoGridConfiguration lbConf;
	private ExperienceState expState;
	
	private Stabilizer stable;

	// LBG Job(s) submiter
	private DefaultJobSubmitter jSubmit;
	
	// Reference sim
	private SimulationData simData;
	
	private DecentralMTWAMaster mtwaMaster;
	private Sub2Da currentSub2Da;
	private boolean nextSimIsNew;
	
	private TreeSet<DAId> lastBroken;


	public DecentralLBController(DistributedAgent hostingDa,
			LaBoGridConfiguration lbConf)
			throws IOException {
		super(hostingDa, "DecentralLBController");
		
		this.lbConf = lbConf;

		if(lbConf.getMiddlewareConfiguration().getStabilizerConfiguration() == null)
			throw new IOException("stabilizer is not configured");
		long stabTime = lbConf.getMiddlewareConfiguration().getStabilizerConfiguration().getTimeout();
		stable = new Stabilizer(stabTime, this);
		agentPrintMessage("Stabilizer TO: "+stabTime);
		
		jSubmit = new DefaultJobSubmitter(this, 10, hostingDa.getDiscoveryService());
		mtwaMaster = new DecentralMTWAMaster(this);
		
		lastBroken = new TreeSet<DAId>();
	}

	@Override
	protected void handleUserDefinedAgentMessage(MasterMessage msg)
			throws Exception {
		if(msg instanceof DecentralMtwaMasterMessage) {
			mtwaMaster.submitDecentralMtwaMasterMessage((DecentralMtwaMasterMessage) msg);
		} else if(msg instanceof ReplicationBarrierReached) {
			handleReplicationBarrierReached((ReplicationBarrierReached) msg);
		} else if(msg instanceof ConfBarrierReached) {
			handleConfBarrierReached((ConfBarrierReached) msg);
		} else if(msg instanceof SimEndBarrierReached) {
			handleSimEndBarrierReached((SimEndBarrierReached) msg);
//		} else if(msg instanceof TopologyUpdateMessage) {
//			handleTopologyUpdateMessage((TopologyUpdateMessage) msg);
		} else if(msg instanceof OutputClientMessage) {
			simData.submitOutputClientMessage((OutputClientMessage) msg);
		} else if(msg instanceof InputClientMessage) {
			simData.submitInputClientMessage((InputClientMessage) msg);
		} else {
			throw new UnknownAgentMessage(msg);
		}
	}
	
	@Override
	protected void onTopologyChange(DAId subject, ChangeType type)
			throws Exception {
		// Logging topology change
		if(type.equals(ChangeType.leave)) {
			this.getHostingDA().log("topology", "leave");
			lastBroken.add(subject);
		} else if(type.equals(ChangeType.join)) {
			this.getHostingDA().log("topology", "join");
		}
		
		// Update current mapping if needed
		if(currentSub2Da == null) {
			agentPrintMessage("No current mapping to correct");
			return;
		}
		
		if(type.equals(ChangeType.leave)) {
			agentPrintMessage("Remove DA "+subject+" from current mapping.");
			removeFromMapping(subject);
		}

		nextSimIsNew = false;
		stable.signalTopologyChange();
		agentPrintMessage("Stabilizer notified");
	}
	
	private void handleReplicationBarrierReached(ReplicationBarrierReached reach) {
		int version = reach.getVersion();
		if(version != expState.getVersion()) {
			agentPrintMessage("Ignored obsolete replicatio barrier reached");
			return;
		}
	
		agentPrintMessage("All LB tasks have replicated their state (it="+reach.getIteration()+")");
		expState.setLastSavedIteration(reach.getIteration());
		// TODO : save state
	}
	
	private void handleConfBarrierReached(ConfBarrierReached reach) {
		int version = reach.getVersion();
		if(version != expState.getVersion()) {
			agentPrintMessage("Ignored obsolete sim-conf barrier reached");
			return;
		}

		agentPrintMessage("All LB tasks configured");
	}
	
	private void handleSimEndBarrierReached(SimEndBarrierReached reach) throws Exception {
		int version = reach.getVersion();
		if(version != expState.getVersion()) {
			agentPrintMessage("Ignored obsolete sim-end barrier reached");
			return;
		}
		
		this.getHostingDA().log("sim-end", expState.getSimNum()+"@"+expState.getVersion());

		agentPrintMessage("All LB tasks finished current simulation");
		if(expState.gotoNextSim() < lbConf.getExperienceDescription().getSimulationsCount()) {
			if(simData.prepareSimEnd())
				startNewSim();
		} else {
			agentPrintMessage("Experience finished.");
			if(simData.prepareSimEnd())
				this.shutdown();
		}
	}

	@Override
	protected void handleWorkerExit(WorkerExitMessage msg) throws Exception {
		agentPrintMessage("Task exit");
	}

	@Override
	protected void onExit() {
		exitActions();
	}
	
	private void exitActions() {
		if(simData != null) {
			simData.closeIoClients();
		}
		
		try {
			mtwaMaster.stop();
		} catch (InterruptedException e) {
		} catch (AgentException e) {
		}
		try {
			jSubmit.stop();
		} catch (InterruptedException e1) {
		} catch (AgentException e1) {
		}
		
		try {
			mtwaMaster.join();
		} catch (InterruptedException e1) {
		}
		try {
			jSubmit.join();
		} catch (InterruptedException e1) {
		}
		
	}

	@Override
	protected void onStartup() throws Throwable {
		jSubmit.start();
		mtwaMaster.start();
		
		expState = new ExperienceState();
		
		nextSimIsNew = true;
		stable.startStabilizer();
//		startNewSim();
	}

	@Override
	protected void handleMasterEvent(Object o) throws Exception {
		if(o instanceof NewMapping) {
			handleNewMapping((NewMapping) o);
		} else if(o instanceof NewFile) {
			handleNewFile((NewFile) o);
		} else if(o instanceof FileAdded) {
			handleFileAdded((FileAdded) o);
		} else if(o instanceof FilesDeleted) {
			handleFilesDeleted((FilesDeleted) o);
		} else if(o instanceof OutputClientCB) {
			handleOutputClientCB((OutputClientCB) o);
		} else if(o instanceof StableTopology) {
			handleStableTopology((StableTopology) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	private void handleStableTopology(StableTopology o) throws Exception {
		LinkedList<DAId> broken = new LinkedList<DAId>();
		lastBroken.addAll(lastBroken);
		lastBroken.clear();

		broadcastWorkerMessage(new BrokenDasMessage(broken),
				new BrokenDasMessage(broken));

		if(nextSimIsNew) {
			nextSimIsNew = false;
			startNewSim();
		} else {
			triggerSim();
		}
	}

	private void handleFilesDeleted(FilesDeleted o) throws Exception {
		if(simData != null) {
			if(simData.signalFilesDeleted(o)) {
				if(expState.getSimNum() < lbConf.getExperienceDescription().getSimulationsCount()) {
					startNewSim();
				} else {
					shutdown();
				}
			}
		} else {
			throw new Error("No sim data for files deleted");
		}
	}

	private void handleFileAdded(FileAdded o) throws Exception {
		Throwable err = o.getError();
		if(err != null) {
			throw new Error("Could not add file to DFS", err);
		}
		
		agentPrintMessage(o.getFileUID()+" added to DFS");
		
		if(simData != null) {
			if(simData.signalFileAdded(o)) {
				if(currentSub2Da == null) {
					agentPrintMessage("-- setting default mapping.");
					currentSub2Da = simData.getDefaultMapping();
				}
				triggerSim();
			}
		} else {
			throw new Error("No sim data for added file");
		}
	}

	private void handleNewFile(NewFile o) throws Exception {
		Throwable err = o.getError();
		if(err != null) {
			throw new Error("Could not get file from input", err);
		}
		
		agentPrintMessage(o.getFileUID()+" received.");

		if(simData != null) {
			if(simData.submitNewFile(o)) {
				if(currentSub2Da == null) {
					agentPrintMessage("-- setting default mapping.");
					currentSub2Da = simData.getDefaultMapping();
				}
				triggerSim();
			}
		} else {
			throw new Error("No sim data for new file");
		}
	}

	private void removeFromMapping(DAId id) {
		LinkedList<Integer> subIds = currentSub2Da.removeSubsFromDa(id);
		agentPrintMessage(subIds.size()+" subs to redistribute.");
		currentSub2Da.addSubsToDa(this.getHostingDA().getDaId(), subIds);
	}
	
	private void triggerSim() throws LBException, InterruptedException {
		agentPrintMessage("Trigger sim #"+expState.getSimNum());
		
		expState.incrementVersion();
		
		this.getHostingDA().log("sim-begin", expState.getSimNum()+"@"+expState.getVersion());
		
		// Prepare new mapping
		this.getHostingDA().log("sub2Da-begin", expState.getSimNum()+"@"+expState.getVersion());
		mtwaMaster.requestSub2Da(expState.getVersion());

		// Broadcast initial mapping (possibly the result of a previous mapping)
		broadcastWorkerMessage(
				new InitialMappingMessage(expState.clone(), currentSub2Da.clone()),
				new InitialMappingMessage(expState.clone(), currentSub2Da.clone()));
	}

	public void submitNewSub2Da(Sub2Da sub2Da) {
		try {
			submitMessage(new NewMapping(sub2Da));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void handleNewMapping(NewMapping o) throws Exception {
		agentPrintMessage("Received new mapping.");
		o.getMapping().print(this);
		
		this.currentSub2Da = o.getMapping();

		this.getHostingDA().log("sub2Da-end", expState.getSimNum()+"@"+expState.getVersion());
		
		broadcastWorkerMessage(new NewMappingMessage(expState.getVersion(), o.getMapping()),
				new NewMappingMessage(expState.getVersion(), o.getMapping()));
	}
	
	private void startNewSim() throws Exception {
		agentPrintMessage("New simulation started");

		simData = new SimulationData(this);
		currentSub2Da = null;

		if(simData.prepareSimData()) {
			agentPrintMessage("-- setting default mapping.");
			currentSub2Da = simData.getDefaultMapping();
			triggerSim();
		}
	}

	public LaBoGridConfiguration getConfiguration() {
		return lbConf;
	}

	public ExperienceState getExperienceState() {
		return expState;
	}
	
	public void printMessage(String msg) {
		agentPrintMessage(msg);
	}

	@Override
	public void inputClientGetCB(String fileUID, File file, Throwable error) {
		try {
			submitMessage(new NewFile(fileUID, file, error));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addFileCB(DFSException error, String fileUID, File file,
			FileMode mode) {
		try {
			submitMessage(new FileAdded(fileUID, error));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteFilesCB(Pattern p) {
		try {
			submitMessage(new FilesDeleted(p));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getFileCB(DFSException error, String fileUID, File f) {
		submitError(new Error("unimplemented"));
	}

	@Override
	public void replicateFileCB(ReplicateFileResult res) {
		submitError(new Error("unimplemented"));
	}

	@Override
	public void removeFileCB(DFSException error, String fileUID, File newFile) {
		submitError(new Error("unimplemented"));
	}

	@Override
	public void outputClientPutCB(OutputClientCB params) {
		try {
			submitMessage(params);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void handleOutputClientCB(OutputClientCB m) throws Exception {
		agentPrintMessage("output: "+m.getDestinationFileUID());
	}

	@Override
	public void signalStableTopology(StableTopology stab) {
		try {
			submitMessage(stab);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
