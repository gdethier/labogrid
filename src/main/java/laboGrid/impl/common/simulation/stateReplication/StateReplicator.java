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
package laboGrid.impl.common.simulation.stateReplication;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;

import laboGrid.CallBackNotImplemented;
import laboGrid.impl.common.simulation.LBSimulation;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.impl.common.simulation.algorithm.LBState.ContentType;
import laboGrid.impl.common.simulation.stateReplication.events.ContinueReplication;
import laboGrid.impl.common.simulation.stateReplication.events.ReplicateState;
import laboGrid.impl.common.simulation.stateReplication.events.StateFileAdded;

import dimawo.agents.AbstractAgent;
import dimawo.agents.UnknownAgentMessage;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.distributedAgent.DistributedAgentInterface;
import dimawo.middleware.fileSystem.DFSException;
import dimawo.middleware.fileSystem.FileSystemCallBack;
import dimawo.middleware.fileSystem.FileSystemAgent;
import dimawo.middleware.fileSystem.ReplicateFileResult;
import dimawo.middleware.fileSystem.fileDB.FileDescriptor.FileMode;



public class StateReplicator
extends AbstractAgent
implements FileSystemCallBack {

	private LBSimulation lbComp;
	private DistributedAgentInterface da;
	private FileSystemAgent fs;
	private Set<DAId> backupNeighbors;
	private int algCount;
	private ContentType compressFiles;
	private String filePrefix;

	private LinkedList<ReplicateState> waitingReplication;

	private int replicatedStates;
	private int fromIteration;
	private int currentOffset;
	private int lastOffset;
	private int replicationRate;
	
	private LinkedList<LBSimThread> waitingFinalReplication;


	public StateReplicator(LBSimulation lbComp,
			int fromIteration,
			int replicationRate,
			int lastStateOffset,
			Set<DAId> backupNeighbors,
			int algCount, ContentType compressFiles) {
		
		super(lbComp, "StateReplicator");

		this.lbComp = lbComp;
		this.da = lbComp.getDistributedAgent();
		this.fs = da.getFileSystemPeer();

		this.backupNeighbors = backupNeighbors;
		this.algCount = algCount;
		this.compressFiles = compressFiles;
		this.filePrefix = lbComp.getFilePrefix();

		waitingReplication = new LinkedList<ReplicateState>();

		this.fromIteration = fromIteration;
		replicatedStates = 0;
		this.lastOffset = lastStateOffset;
		if(replicationRate > 0)
			this.replicationRate = replicationRate;
		else
			this.replicationRate = lastStateOffset;
		if(fromIteration > 0)
			this.currentOffset = 0;
		else
			this.currentOffset = replicationRate;
		
		waitingFinalReplication = new LinkedList<LBSimThread>();

	}


	////////////////////
	// Public methods //
	////////////////////
	
	public void submitAlgorithm(LBSimThread a) throws InterruptedException {

		submitMessage(new ReplicateState(a));

	}


	///////////////////////////////
	// FileSystemPeer call-backs //
	///////////////////////////////
	
	@Override
	public void addFileCB(DFSException error, String fileUID, File file,
			FileMode mode) {
		try {
			submitMessage(new StateFileAdded(fileUID));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteFilesCB(Pattern p) {
		
		submitError(new CallBackNotImplemented("deleteFiles"));
		
	}

	@Override
	public void replicateFileCB(ReplicateFileResult res) {
		
		try {
			submitMessage(res);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void getFileCB(DFSException error, String fileUID, File f) {
		
		submitError(new CallBackNotImplemented("getFile"));
		
	}


	/////////////////////
	// Private methods //
	/////////////////////

	/**
	 * Replicates state of an LBAlgorithm.
	 * 
	 * @param alg LBAlgorithm which state must be replicated.
	 * @throws Exception 
	 */
	private void replicateState(LBSimThread alg) throws Exception {

		agentPrintMessage("Submit offset "+currentOffset+" of alg "+
				alg.getNodeId()+" for replication.");
		
		File stateFile = alg.generateStateFile(filePrefix, compressFiles);
		String fileUID = LBSimThread.getStateFileUID(alg.getNodeId(),
				alg.getCurrentIteration());
		
		if(currentOffset < lastOffset) {
			alg.notifyBackup(); // Simulation can continue
		} else {
			agentPrintMessage("Last state to replicate.");
			waitingFinalReplication.add(alg);
		}

		fs.addFile(fileUID, stateFile, FileMode.rw, this);
		
	}

	@Override
	public void removeFileCB(DFSException error, String fileUID, File newFile) {
		lbComp.signalChildError(new CallBackNotImplemented("removeFileCB"), this.getClass().getName());
	}


	//////////////////////////////////
	// AbstractAgent implementation //
	//////////////////////////////////

	@Override
	protected void exit() {
		agentPrintMessage("exit");
	}


	@Override
	protected void handleMessage(Object o) throws Throwable {
		
		if(o instanceof ReplicateState) {
			handleReplicateState((ReplicateState) o);
		} else if(o instanceof StateFileAdded) {
			handleStateFileAdded((StateFileAdded) o);
		} else if(o instanceof ReplicateFileResult) {
			handleStateFileReplicated((ReplicateFileResult) o);
		} else if(o instanceof ContinueReplication) {
			handleContinueReplication((ContinueReplication) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
		
	}


	private void handleContinueReplication(ContinueReplication o) throws Exception {
		
		if(currentOffset == lastOffset) {
			agentPrintMessage("Replication finished.");
			
			for(Iterator<LBSimThread> it = waitingFinalReplication.iterator();
			it.hasNext();) {
				it.next().notifyBackup();
			}
		} else {
		
			agentPrintMessage("Replication can continue.");
			
			// Update current state to replicate
			currentOffset += replicationRate;
			if(currentOffset > lastOffset) {
				currentOffset = lastOffset;
			}
			
			agentPrintMessage("Next offset to replicate: "+currentOffset);
			
			replicatedStates = 0;
	
			int r = handleQueuedReplications(); // Replicate queued states
			agentPrintMessage("Requested replication for "+r+" queued algs.");
		
		}
	}


	private void handleStateFileAdded(StateFileAdded o) {
		if(backupNeighbors.size() > 0) {
			agentPrintMessage("Replicating file "+o.getFileUID());
			da.log("begin-rep", o.getFileUID());
			fs.replicateFile(o.getFileUID(), backupNeighbors, this);
		} else {
			agentPrintMessage("No replication (no neighbors)");
			tryEndOfReplication(o.getFileUID());
		}
	}


	private void handleStateFileReplicated(ReplicateFileResult o) throws Exception {
		String fileUID = o.getFileUID();
		DFSException error = o.getError();
		if(error != null) {
			throw new Exception("Could not replicate state file "+fileUID, error);
		}
		
		da.log("end-rep", o.getFileUID());
		tryEndOfReplication(fileUID);
	}


	private void tryEndOfReplication(String fileUID) {
		++replicatedStates;
		agentPrintMessage("State file "+replicatedStates+"/"+algCount+"("+fileUID+") replicated.");
		if(replicatedStates == algCount) {
			// Signal replication finished for current state
			lbComp.replicationFinished(fromIteration + currentOffset);
		}
	}


	private void handleReplicateState(ReplicateState o) throws Exception {
		
		LBSimThread alg = o.getAlgorithm();
		int algOffset = alg.getCurrentOffset();
		if(algOffset == currentOffset) {
			replicateState(alg);
		} else if(algOffset > currentOffset) {
			agentPrintMessage("Queuing alg "+alg.getNodeId()+" for offset "+
					algOffset);
			waitingReplication.add(o);
		} else if(algOffset < currentOffset) {
			throw new Exception("Request replication for offset "+algOffset+" but " +
					currentOffset+" or greater awaited.");
		}
		
	}


	private int handleQueuedReplications() throws Exception {
		int r = 0;
		Iterator<ReplicateState> it = waitingReplication.iterator();
		while(it.hasNext()) {
			ReplicateState rs = it.next();
			int algOff = rs.getAlgOffset();
			if(algOff == currentOffset) {
				replicateState(rs.getAlgorithm());
				it.remove();
				++r;
			} else if(algOff < currentOffset) {
				throw new Exception(algOff+"<"+currentOffset);
			}
		}
		
		return r;
	}


	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
		
		agentPrintMessage("From iteration:"+fromIteration);
		agentPrintMessage("First offset to replicate: "+currentOffset);
		agentPrintMessage("Last offset to replicate: "+lastOffset);
		agentPrintMessage("Replication rate: "+replicationRate);
		agentPrintMessage("Replication neighbors:");
		for(DAId id : backupNeighbors) {
			agentPrintMessage("-- "+id);
		}
	}


	public void resume() {
		try {
			submitMessage(new ContinueReplication());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void agentPrintMessage(String msg) {
		lbComp.printMessage("[StateReplicator] "+msg);
	}
	
	@Override
	public void agentPrintMessage(Throwable e) {
		lbComp.printMessage(e);
	}

}
