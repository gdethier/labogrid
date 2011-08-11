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
package laboGrid.impl.central.controllerAgent.experimenter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import laboGrid.CallBackNotImplemented;
import laboGrid.configuration.experience.SimulationDescription;
import laboGrid.configuration.lbConfiguration.LBConfiguration;
import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.impl.central.controllerAgent.experimenter.messages.GlobalConfigurationDone;
import laboGrid.impl.central.controllerAgent.experimenter.messages.GlobalReplicationDone;
import laboGrid.impl.central.controllerAgent.experimenter.messages.GlobalSimulationFinishedMessage;
import laboGrid.impl.central.controllerAgent.experimenter.messages.ReplicationDoneMessage;
import laboGrid.impl.central.controllerAgent.experimenter.messages.SimulationFinishedMessage;
import laboGrid.impl.central.task.messages.ConfigurationFinished;
import laboGrid.impl.central.task.messages.LBConfigData;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;

import dimawo.middleware.communication.Communicator;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.fileSystem.DFSException;
import dimawo.middleware.fileSystem.FileSystemCallBack;
import dimawo.middleware.fileSystem.FileSystemAgent;
import dimawo.middleware.fileSystem.ReplicateFileResult;
import dimawo.middleware.fileSystem.fileDB.FileDescriptor.FileMode;
import dimawo.middleware.overlay.BarrierSyncCallBackInterface;
import dimawo.middleware.overlay.BarrierSyncInterface;




/**
 * State of the execution of a simulation task. This class contains
 * the functionalities needed to handle new Resource graphs. It also
 * holds central informations needed for fault-tolerance. Operations
 * like state roll-back and restart are triggered from here.
 * 
 * @author Gérard Dethier
 *
 */
public class ExecutionState
implements FileSystemCallBack {
	
	/* Work Flow Handler, to signal errors */
	private Experimenter wfh;
	/* File system peer, used to check state availability */
	private FileSystemAgent fs;
	private CommunicatorInterface com;

	private int phaseNum;
	private SimulationDescription taskDesc;
	private ModelGraph mGraph;

	private boolean brokenResourceGraph;
	private long currentConfigStartTime;
	private GraphConfiguration currentConfig;
	private GraphConfiguration candidateConfig;
	private DAId[] sub2Da;
	/** Computation Graph */
	private Map<DAId, Set<Integer>> da2Sub;
	/** Replication Graph */
	private Map<DAId, Set<DAId>> bGraph;
	/** IDs of the DAs currently running the simulation task */
	private TreeSet<DAId> currentDas;
	
	private int lastSavedIteration;
	private int currentSavedIteration;
	private int finishedSubLattices;
	private int savedSubsCount;
	private int configuredSubsCount;
	private long simDuration;
	
	private int currentVersion;
	private Pattern currentCheckedStatePattern;
	private int currentCheckedState;
	
	private long firstTopologyTime;
	
	/**
	 * Constructor. 
	 * 
	 * @param wfh A reference to the instantiating Work Flow Handler.
	 * @param fs A reference to the File System Peer of the hosting DA.
	 * @param taskDesc Description of the simulation Task this state is associated to. 
	 * @param mGraph Model Graph.
	 * @param lbConf Global Lattice-Boltzmann configuration.
	 * @param procChain Processing chain to use for the associated simulation Task.
	 * 
	 * @throws LBException 
	 */
	public ExecutionState(
			Experimenter wfh,
			CommunicatorInterface com,
			FileSystemAgent fs,
			int phaseNum,
			SimulationDescription taskDesc,
			ModelGraph mGraph,
			LBConfiguration lbConf,
			ProcessingChainDescription procChain,
			long reconfTO) {

		this.wfh = wfh;
		this.fs = fs;
		this.com = fs.getHostingDA().getCommunicator();

		this.phaseNum = phaseNum;
		this.taskDesc = taskDesc;
		this.mGraph = mGraph;

		brokenResourceGraph = false;
		sub2Da = null;
		da2Sub = null;
		
		currentDas = new TreeSet<DAId>();
		
		currentVersion = -1;
		/*
		 * candidateSavedIteration must be the starting iteration so
		 * the associated state is used when the initial ResourceGraph
		 * is used to generate the first ComputationGraph for this phase.
		 */
		lastSavedIteration = -1;
		currentSavedIteration = taskDesc.getStartingIteration();

	}

	
	////////////////////
	// Public methods //
	////////////////////
	
	/**
	 * Invalidates the current Resource Graph if the DA
	 * was currently used for the execution.
	 */
	public void invalidateResourceGraph(DAId brokenDaId) {

		if(currentDas.remove(brokenDaId)) {

			brokenResourceGraph = true;

		}

	}

	/**
	 * Checks if the proposed graph configuration can be used for the
	 * current simulation. If it is the case, the configuration will 
	 * maybe be used (it depends on the remaining simulation time,
	 * resource failures...).
	 * 
	 * @param newConfig A graph configuration.
	 * 
	 * @return True iff the given configuration will be used.
	 * 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public boolean submitNewConfig(GraphConfiguration newConfig) throws InterruptedException, IOException {
		
//		if(executionIsInterrupted()) {
//
//			updateGraphs(newConfig);
//			return true;
//
//		}

//		if(newConfig.isBetterThan(currentConfig)) {
			
			/* TODO : Take into account current configuration age
			 * and next replication.
			 */
			updateGraphs(newConfig);
			return true;

//		} else {
//
//			/**
//			 * Proposed config is not better than current.
//			 */
//			return false;
//
//		}

	}

	/**
	 * Checks if a previously saved candidate configuration can be used
	 * know if any.
	 * 
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void useCandidateConfiguration() throws InterruptedException, IOException {
		
		if(candidateConfig != null)
			submitNewConfig(candidateConfig);

	}

	/**
	 * Indicates if the simulation task is currently executing or not.
	 * 
	 * @return True if the simulation task is currently not executed, false otherwise.
	 */
	public boolean executionIsInterrupted() {

		return currentConfig == null || brokenResourceGraph;

	}

	/**
	 * Indicates if a given Computation Graph calculated from a new Resource Graph
	 * should be used or not to execute the rest of the associated simulation task. 
	 * 
	 * @param newCGraph Computation Graph.
	 * @param rGraph Resource Graph.
	 * 
	 * @return True if the Computation Graph can be used to execute the rest of the
	 * simulation task.
	 */
//	public boolean newComputationGraphIsBetter(ComputationGraph newCGraph,
//			ResourceGraph rGraph) {
//	
//		ExecutionTime execTime = execTimeModel.estimate(newCGraph, mGraph, rGraph);
//
//		return execTime.getExecutionTime() < cGraphExecTime.getExecutionTime();
//		
//	}

	/**
	 * Updates the Graphs currently used. A call to this method
	 * causes the associated simulation task to be (re)started (from a previous state
	 * if possible).
	 * 
	 * @param newConfig New graphs configuration to be used.
	 * 
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void updateGraphs(GraphConfiguration newConfig) throws InterruptedException, IOException {

		currentConfig = newConfig;
		currentConfigStartTime = System.currentTimeMillis();

		candidateConfig = null;

		this.sub2Da = new DAId[mGraph.getSubLatticesCount()];
		this.da2Sub = new TreeMap<DAId, Set<Integer>>();
		this.bGraph = new TreeMap<DAId, Set<DAId>>();

		newConfig.setGraphStructures(com.getHostingDaId(),
				sub2Da, da2Sub, bGraph);
		
		printComputationGraph();
		if( ! bGraph.isEmpty()) // A replication map has been generated
			printReplicationGraph();
		
		++currentVersion;

		if(currentSavedIteration > 0) {

//			queryStateFiles(currentSavedIteration);
			configureDAs(currentSavedIteration);

		} else {

			configureDAs(0);

		}

	}
	
	private void printReplicationGraph() {
		
		printMessage("Replication graph:");
		
		Iterator<Entry<DAId, Set<DAId>>> it = bGraph.entrySet().iterator();
		while(it.hasNext()) {
			Entry<DAId, Set<DAId>> e = it.next();
			
			printMessage("DA "+e.getKey()+" replicates its state on "+e.getValue().size()+" neighbors.");
		}
		
	}


	private void printComputationGraph() {
		
		printMessage("Computational graph:");
		
		Iterator<Entry<DAId, Set<Integer>>> it = da2Sub.entrySet().iterator();
		while(it.hasNext()) {
			Entry<DAId, Set<Integer>> e = it.next();
			
			printMessage("DA "+e.getKey()+" hosts "+e.getValue().size()+" sub-lattices.");
		}
		
	}


	/**
	 * Handles a BackupDone message. If all DAs have done a backup
	 * of their state, the simulation can continue.
	 * 
	 * @param b The BackupDone message.
	 * 
	 * @return True iff current state replication is finished.
	 * 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public boolean handleReplicationDone(ReplicationDoneMessage b) throws InterruptedException, IOException {

		if(b.getVersion() != currentVersion) {

			printMessage("Replication done message ignored ("+b.getVersion()+")");
			return false;

		}

		int subCount = b.getCount();
		savedSubsCount = savedSubsCount + subCount;
		printMessage(savedSubsCount+"/"+mGraph.getSubLatticesCount()+" sub-lattices replicated @iteration "+b.getIteration());

		if(savedSubsCount == mGraph.getSubLatticesCount()) {

			savedSubsCount = 0;
			
			lastSavedIteration = currentSavedIteration;
			currentSavedIteration = b.getIteration();
			
			return true;

		}
		
		return false;

	}
	
	public void handleConfigurationFinished(ConfigurationFinished b) throws IOException {
		
		if(b.getVersion() != currentVersion) {

			printMessage("Configuration done message ignored.");
			return;

		}

		int subCount = b.getCount();
		configuredSubsCount += subCount;
		printMessage(configuredSubsCount+"/"+mGraph.getSubLatticesCount()+" sub-lattices configured.");

		if(configuredSubsCount == mGraph.getSubLatticesCount()) {

			configuredSubsCount = 0;
			
			notifyConfigurationDone();
			
		}
		
	}


	/**
	 * Called when a DA has finished its simulation phase. When
	 * all DAs have finished, end of task operations can start.
	 * 
	 * @param lcf
	 * @return True iff distributed simulation is finished on all DAs.
	 * @throws InterruptedException 
	 */
	public boolean handleLBComputationFinished(SimulationFinishedMessage lcf) throws InterruptedException {

		if(lcf.getVersion() != currentVersion) {

			printMessage("ignored wrong version LBComputationFinished message.");
			return false;

		}

		printMessage("Received a LBComputationFinished message from "+lcf.getSourceDaId());

		long duration = lcf.getSimDuration();
		if(finishedSubLattices == 0)
			simDuration = duration;
		else {
			if(duration > simDuration)
				simDuration = duration;
		}

		finishedSubLattices += lcf.getFinishedSubLattices().length;
		if(finishedSubLattices == mGraph.getSubLatticesCount()) {

			printMessage(1, "Simulation executed in "+simDuration+" ms.");
			return true;

		}
		
		return false;

	}


	//////////////////////////////////////////////
	// FileSystemCallBackHandler implementation //
	//////////////////////////////////////////////
	
	@Override
	public void addFileCB(DFSException error, String fileUID, File file,
			FileMode mode) {
		
	}
	
	@Override
	public void replicateFileCB(ReplicateFileResult res) {
		
		assert false; // No replication is called from here.
		
	}

	@Override
	public void getFileCB(DFSException error, String fileUID, File f) {
		
		assert false; // No file requested from here.
		
	}

	@Override
	public void deleteFilesCB(Pattern p) {
		wfh.signalChildError(new Exception("deleteFiles call-back not implemented"), "ExecutionState");
	}
	
	
	/////////////////////
	// Private methods //
	/////////////////////

	/**
	 * Configures DAs to execute a simulation task based on the current Computation
	 * and Backup Graphs. The simulation starts from the given iteration.
	 * @throws IOException 
	 * @throws InterruptedException 
	 * 
	 */
	private void configureDAs(int startingIteration) throws IOException {
		
		printMessage("Configuring task@iteration "+startingIteration+" (version "+currentVersion+")");
		
		if(firstTopologyTime == 0) {

			firstTopologyTime = System.currentTimeMillis();

		}

		finishedSubLattices = 0;
		savedSubsCount = 0;
		configuredSubsCount = 0;

		currentSavedIteration = startingIteration;

		SubLattice[] subLattices = mGraph.getSubLattices();
		currentDas.clear();
		LinkedList<Integer> toStop = new LinkedList<Integer>();

		Iterator<Entry<DAId, Set<Integer>>> daIt = da2Sub.entrySet().iterator();
		while(daIt.hasNext()) {

			Entry<DAId, Set<Integer>> e = daIt.next();
			DAId daId = e.getKey();
			Set<Integer> subIds = e.getValue();

			if(!subIds.isEmpty()) {

				currentDas.add(daId);

				Map<Integer, SubLattice> subs = extractSubLattices(subIds, subLattices);
				Map<Integer, DAId> subLToDA = extractSubLattToDA(daId, da2Sub, sub2Da, subLattices);
				Set<DAId> replicationNeighbors = bGraph.get(daId);

				LBConfigData data = new LBConfigData(daId,
						phaseNum,
						startingIteration,
						subs,
						subLToDA,
						replicationNeighbors,
						currentVersion);

				printMessage("Sending configuration to DA "+daId);
				com.sendDatagramMessage(data);

			} else {

//				printMessage("Closing DA "+daId);
//				toStop.add(daId);

			}

		}

//		try {
//			wfh.stopLBTasks(toStop);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

	}
	
	/**
	 * Notifies end of replication to DAs. This starts/resumes the simulation on the DAs.
	 * 
	 * @param state The current state.
	 * @throws IOException 
	 */
	protected void notifyReplicationDone() throws IOException {

		Iterator<DAId> it = currentDas.iterator();
		while(it.hasNext()) {
			
			DAId daId = it.next();
			printMessage("Notifying end of replication for state "+currentSavedIteration+" to DA "+daId);
			com.sendDatagramMessage(new GlobalReplicationDone(daId, currentVersion, currentSavedIteration));

		}

	}
	
	protected void notifySimulationDone() throws IOException {

		Iterator<DAId> it = currentDas.iterator();
		while(it.hasNext()) {
			
			DAId daId = it.next();
			printMessage("Notifying end of simulation for version "+currentVersion+" to DA "+daId);
			com.sendDatagramMessage(new GlobalSimulationFinishedMessage(daId, currentVersion));

		}

	}
	
	private void notifyConfigurationDone() throws IOException {
		Iterator<DAId> it = currentDas.iterator();
		while(it.hasNext()) {
			
			DAId daId = it.next();

			printMessage("Notifying end of configuration to DA "+daId);
			com.sendDatagramMessage(new GlobalConfigurationDone(daId, currentVersion));
		}
	}

	/**
	 * Builds a sub-lattices map from a sub-lattices IDs set and the sub-lattices
	 * database.
	 * 
	 * @param subLattIDs Sub-lattice IDs set.
	 * @param subLattices Sub-lattices database.
	 * 
	 * @return A sub-lattices map.
	 */
	private Map<Integer, SubLattice> extractSubLattices(Set<Integer> subLattIDs,
														SubLattice[] subLattices) {

		Map<Integer, SubLattice> subLatt = new TreeMap<Integer, SubLattice>();
		Iterator<Integer> it = subLattIDs.iterator();

		while(it.hasNext()) {

			Integer id = it.next();
			subLatt.put(id, subLattices[id]);

		}
		
		return subLatt;

	}
	
	/**
	 * Builds the adjacency map of the sub-lattices of a given DA. This way,
	 * when a sub-lattice of the given DA A sends information to another sub-lattice
	 * located on another DA B, A knows it must forward the message to B. 
	 * 
	 * @param DAId A DA ID number.
	 * @param da2Sub A map indicating what sub-lattices is hosted by what DA.
	 * @param sub2Da An array indicating what DA a sub-lattice is hosted by.
	 * @param subLattices The sub-lattices database.
	 * 
	 * @return The sub-lattices adjacency map for the given DA.
	 */
	private Map<Integer, DAId> extractSubLattToDA(DAId DAId,
			Map<DAId, Set<Integer>> da2Sub,
			DAId[] sub2Da,
			SubLattice[] subLattices) {

		Map<Integer, DAId> toReturn = new TreeMap<Integer, DAId>();

		Iterator<Integer> subsIt = da2Sub.get(DAId).iterator();
		while(subsIt.hasNext()) {

			SubLattice sub = subLattices[subsIt.next()];
			int numOfNeigh = sub.getNeighborsCount();
			for(int i = 0; i < numOfNeigh; ++i) {
				int neighId = sub.getNeighborFromIndex(i);

				if(neighId >= 0) {

					DAId neighDaId = sub2Da[neighId];
					if(! neighDaId.equals(DAId)) {
	
						toReturn.put(neighId, neighDaId);
	
					}
				
				}

			}

		}

		return toReturn;

	}
	
	public void printMessage(String msg) {
	
		printMessage(0, msg);
		
	}
	
	/**
	 * Prints a recognizable message on standard output.
	 * 
	 * @param msg The message to print.
	 */
	public void printMessage(int verbLevel, String msg) {
	
		wfh.printMessage(verbLevel, "[ExecutionState] "+msg);
		
	}

	
	/**
	 * Requests a state file list to the File System.
	 * 
	 * @param state The state of the files to list.
	 * @throws InterruptedException 
	 */
//	private void queryStateFiles(int state) {
//		
//		printMessage("List state files for iteration "+state);
//
//		currentCheckedStatePattern = LBAlgorithm.getStatePattern(state);
//		currentCheckedState = state;
//		fs.listDFSFiles(currentCheckedStatePattern, this);
//
//	}
	
	/**
	 * @return age of current configuration or a negative value if
	 * there is no current configuration available.
	 */
	private long currentConfigAge() {

		return System.currentTimeMillis() - currentConfigStartTime;

	}
	
	@Override
	public void removeFileCB(DFSException error, String fileUID, File newFile) {
		wfh.signalChildError(new CallBackNotImplemented("removeFileCB"), this.getClass().getName());
	}


	public int getLastSavedIteration() {
		return lastSavedIteration;
	}


	public int getCurrentSavedIteration() {
		return currentSavedIteration;
	}


}
