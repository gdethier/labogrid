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
package laboGrid.impl.common.simulation;

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
import laboGrid.configuration.middleware.FaultToleranceConfiguration;
import laboGrid.impl.common.simulation.algorithm.LBData;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.impl.common.simulation.algorithm.LBState.ContentType;
import laboGrid.impl.common.simulation.events.AlgToBackup;
import laboGrid.impl.common.simulation.events.AlgorithmFinished;
import laboGrid.impl.common.simulation.events.ConnectionEstablished;
import laboGrid.impl.common.simulation.events.EndOfGlobalConfiguration;
import laboGrid.impl.common.simulation.events.EndOfGlobalReplication;
import laboGrid.impl.common.simulation.events.NewFile;
import laboGrid.impl.common.simulation.events.ReplicationFinished;
import laboGrid.impl.common.simulation.messages.LBBufferedData;
import laboGrid.impl.common.simulation.messages.LBSimulationMessage;
import laboGrid.impl.common.simulation.stateReplication.StateReplicator;
import laboGrid.impl.common.task.LBTaskInterface;
import laboGrid.impl.common.task.events.ConfigurationDone;
import laboGrid.impl.common.task.events.ReplicationDone;
import laboGrid.impl.common.task.events.SimulationFinished;
import laboGrid.ioClients.InputClientCallBack;
import laboGrid.ioClients.InputClientException;
import laboGrid.ioClients.InputClientMessage;
import laboGrid.ioClients.controller.OutputClientMessage;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;

import dimawo.agents.AbstractAgent;
import dimawo.agents.AgentException;
import dimawo.agents.UnknownAgentMessage;
import dimawo.middleware.commonEvents.BrokenDA;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.communication.ConnectCallBack;
import dimawo.middleware.communication.ConnectionRequestCallBack;
import dimawo.middleware.communication.Message;
import dimawo.middleware.communication.MessageHandler;
import dimawo.middleware.communication.outputStream.MOSAccessorInterface;
import dimawo.middleware.communication.outputStream.MOSCallBack;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgentInterface;
import dimawo.middleware.fileSystem.DFSException;
import dimawo.middleware.fileSystem.FileSystemCallBack;
import dimawo.middleware.fileSystem.FileSystemAgent;
import dimawo.middleware.fileSystem.ReplicateFileResult;
import dimawo.middleware.fileSystem.fileDB.FileDescriptor.FileMode;



public class LBSimulation
extends AbstractAgent 
implements FileSystemCallBack, ConnectionRequestCallBack, MOSCallBack, InputClientCallBack, MessageHandler {
	
	private SimulationParameters simParam;
	
	private LBTaskInterface lbTask;
	private CommunicatorInterface com;
	private FileSystemAgent fs;

	/** Files to receive. Map gives sub-lattice ID from fileUID. */
	private TreeMap<String, Integer> filesToReceive;
	private TreeSet<DAId> connectionsToEstablish;

	private int algCount;
	private LBSimThread nullThread;
	private Map<Integer, LBSimThread> algs;
	private Map<Integer, DAId> sub2Da;
	private TreeMap<DAId, MOSAccessorInterface> access;

	private boolean faultTolerance;
	private Set<DAId> replicationNeighbors;
	private boolean initialReplication;
	private StateReplicator stateReplicator;

	private int finishedAlgs;
	private long simDuration;
	private int[] finishedSubs;


	public LBSimulation(LBTaskInterface lbTask, SimulationParameters simParam) throws LBException {

		super(lbTask, "LBSimulation");

		this.simParam = simParam;

		this.lbTask = lbTask;
		
		this.com = lbTask.getDistributedAgent().getCommunicator();
		com.registerMessageHandler(getHandlerId(simParam.getVersion()), this);

		this.fs = lbTask.getDistributedAgent().getFileSystemPeer();

		filesToReceive = new TreeMap<String, Integer>();
		connectionsToEstablish = new TreeSet<DAId>();
		connectionsToEstablish.addAll(simParam.getComputationNeighbors());

		finishedAlgs = 0;

		algCount = simParam.getSubLatticesCount();
		algs = new TreeMap<Integer, LBSimThread>();
		
		FaultToleranceConfiguration ftConf = simParam.getFaultToleranceConfiguration();
		faultTolerance = ftConf.replicationIsEnabled();
		
		if(faultTolerance) {
			initialReplication = simParam.getStartingIteration() > 0;
			replicationNeighbors = simParam.getReplicationNeighbors();
		} else {
			initialReplication = false;
			replicationNeighbors = null;
		}

		sub2Da = simParam.getSub2Da();
		access = new TreeMap<DAId, MOSAccessorInterface>();
		
	}
	
	public LBSimulation() {

		super(null, "LBComputation");

		this.lbTask = null;
		finishedAlgs = 0;
		algs = new TreeMap<Integer, LBSimThread>();

	}	
	
	
	////////////////////
	// Public methods //
	////////////////////
	
	/**
	 * Indicates if the simulation is finished.
	 */
	public boolean isFinished() {
		return finishedAlgs == algCount;
	}

	public void replicationFinished(int savedIteration) {
		
		try {
			submitMessage(new ReplicationFinished(savedIteration));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void submitSimulationMessage(LBSimulationMessage message) {

		try {
			submitMessage(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

//	/**
//	 * Kills the current simulation.
//	 *  
//	 * @throws InterruptedException 
//	 */
//	public void killAlgs() {
//
//		Iterator<LBSimThread> it = algs.values().iterator();
//		while(it.hasNext()) {
//
//			LBSimThread a = it.next();
//			a.kill();
//
//		}
//
//	}

	public void replicateState(LBSimThread algorithm) throws InterruptedException {
	
		submitMessage(new AlgToBackup(algorithm));
		
	}
	
	public String getFilePrefix() {
		return lbTask.getDistributedAgent().getFilePrefix();
	}
	
	public CommunicatorInterface getCommunicator() {
		return lbTask.getDistributedAgent().getCommunicator();
	}
	
	
	/**
	 * Called by the LB algorithms to signal they finished.
	 * 
	 * @param algorithm
	 * @throws InterruptedException 
	 */
	public void algFinished(LBSimThread algorithm) throws InterruptedException {

		submitMessage(new AlgorithmFinished(algorithm));

	}

	
	///////////////////////////
	// FileSystem call-backs //
	///////////////////////////
	
	@Override
	public void addFileCB(DFSException error, String fileUID, File file,
			FileMode mode) {
		submitError(new Exception("addFileCB not implemented."));
	}

	@Override
	public void deleteFilesCB(Pattern p) {
		submitError(new Exception("deleteFilesCB not implemented."));
	}

	@Override
	public void getFileCB(DFSException error, String fileUID, File f) {

		try {
			submitMessage(new NewFile(fileUID, f, false, error));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void replicateFileCB(ReplicateFileResult res) {

		assert false;

	}


	//////////////////////////////////
	// AbstractAgent implementation //
	//////////////////////////////////

	@Override
	protected void handleMessage(Object o) throws Exception {

		if(o instanceof LBData) { // Data aggregation

			handleLBData((LBData) o);

		} else if(o instanceof LBBufferedData) { // Data aggregation

			handleLBBufferedData((LBBufferedData) o);

		} else if(o instanceof AlgToBackup) {

			handleAlgToBackup((AlgToBackup) o);

		} else if(o instanceof ReplicationFinished) {
			
			handleReplicationFinished((ReplicationFinished) o);

		} else if(o instanceof AlgorithmFinished) {

			handleAlgorithmFinished((AlgorithmFinished) o);

		} else if(o instanceof NewFile) {

			handleNewFile((NewFile) o);

		} else if(o instanceof ConnectionEstablished) {

			handleConnectionEstablished((ConnectionEstablished) o);

		} else if(o instanceof OutputClientMessage) {

			handleOutputClientMessage((OutputClientMessage) o);

		} else if(o instanceof InputClientMessage) {

			handleInputClientMessage((InputClientMessage) o);

		} else if(o instanceof EndOfGlobalConfiguration) {

			handleEndOfGlobalConfiguration((EndOfGlobalConfiguration) o);

		} else if(o instanceof EndOfGlobalReplication) {

			handleEndOfGlobalReplication((EndOfGlobalReplication) o);

		} else {

			throw new UnknownAgentMessage(o);

		}
	}


	private void handleLBBufferedData(LBBufferedData o) throws LBException, InterruptedException, AgentException {
		LinkedList<LBData> buffer = o.getBuffer();
		for(LBData d : buffer) {
			handleLBData(d);
		}
	}

	private void handleOutputClientMessage(OutputClientMessage o) {
		
		if(simParam != null) {
			simParam.submitMessageToOutputClient(o);
		}
		
	}
	
	private void handleInputClientMessage(InputClientMessage o) {
		
		if(simParam != null) {
			simParam.submitMessageToInputClient(o);
		}
		
	}

	@Override
	protected void exit() {
		agentPrintMessage("LBComputation exits.");
		exitActions();
	}


	@Override
	protected void init() throws Exception {
		agentPrintMessage("init");
		if(algCount > 0) {
			establishConnections();
			requestFiles();
			
			// Start clients
			simParam.startOutputClient();
			simParam.startInputClient();
	
			if(faultTolerance)
				startStateReplicator();
		} else {
			nullThread = new LBSimThread(this, simParam);
			lbTask.signalEndOfConfiguration(
					new ConfigurationDone(simParam.getVersion(), 0));
		}
	}
	
	
	////////////////////
	// DFS Call-backs //
	////////////////////

	@Override
	public void removeFileCB(DFSException error, String fileUID, File newFile) {
		submitError(new CallBackNotImplemented("removeFileCB"));
	}

	public DistributedAgentInterface getDistributedAgent() {
		return lbTask.getDistributedAgent();
	}

	@Override
	public void agentPrintMessage(String msg) {
		lbTask.printMessage("[LBSimulation] "+msg);
	}
	
	@Override
	public void agentPrintMessage(Throwable e) {
		lbTask.printMessage(e);
	}

	public ContentType compressFiles() {
		return simParam.getStateFilesCompressed();
	}
	
	
	///////////////////////////
	// Connection call-backs //
	///////////////////////////

	@Override
	public void connectCallBack(ConnectCallBack cb) {
		try {
			submitMessage(new ConnectionEstablished(cb.getDaId(), cb.isSuccessful(), cb.getAccess()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	////////////////////
	// MOS call-backs //
	////////////////////

	@Override
	public void signalBroken(BrokenDA bda) {
		// SKIP
	}

	/////////////////////
	// Private methods //
	/////////////////////
	
	/**
	 * New file received. If all files are received, the algorithms can start.
	 * 
	 * @throws LBException 
	 * @throws InterruptedException 
	 */
	private void handleNewFile(NewFile nf) throws LBException, InterruptedException {
		
		String fileUID = nf.getFileUID();
		Integer subId = filesToReceive.remove(fileUID);
		
		agentPrintMessage("Received file "+fileUID);

		if(subId != null) {

			int from = simParam.getStartingIteration();

			try {

				File file = nf.getFile();
				if(file == null)
					throw new Error("Could not get file "+fileUID, nf.getError());
				
				ContentType ct;
				if(nf.isFromInput()) {
					ct = ContentType.compress;
				} else {
					ct = simParam.getStateFilesCompressed();
				}

				LBSimThread alg = new LBSimThread(
						this,
						subId,
						simParam,
						file,
						ct);
				
				if(nf.isFromInput()) {
					file.delete();
				}

				algs.put(subId, alg);

				if(faultTolerance && from > 0) {

					// Initial backup (to initialize backup storage)
					stateReplicator.submitAlgorithm(alg);

				}

			} catch(Exception e) {

				throw new LBException("Could not instantiate new LBAlgorithm.", e);

			}

			trySimulationStart();

		} else {
			
			throw new LBException("Unexpected file: "+fileUID);
			
		}
		
	}

	private void trySimulationStart() throws LBException,
			InterruptedException {
		
		if(filesToReceive.isEmpty() && connectionsToEstablish.isEmpty()) {
			
			agentPrintMessage("All needed informations available, linking...");

			linkAlgs();

			if(simParam.getStartingIteration() == 0 || ! faultTolerance) {

				lbTask.signalEndOfConfiguration(
						new ConfigurationDone(simParam.getVersion(), algs.size()));

			}

		}
	}

//	private void deleteStateFiles(int state) throws InterruptedException {
//
//		Pattern p = LBAlgorithm.getStatePattern(state);
//		fs.deleteDFSFiles(p, this);
//
//	}
	
	
//	private void deleteAllStateFiles() throws InterruptedException {
//		
//		agentPrintMessage("Deleting all state files.");
//		
//		assert ! waitingAllStateFilesDeletion;
//		waitingAllStateFilesDeletion = true;
//
//		// Delete states
//		statePattern = LBAlgorithm.getStatePattern();
//		fs.deleteDFSFiles(statePattern, this);
//
//		// Delete solids
//		solidPattern = Pattern.compile(".*\\.solid");
//		fs.deleteDFSFiles(solidPattern, this);
//
//	}
	

	private void startAlgs() {
		if(algCount == 0) {
			agentPrintMessage("Starting null thread.");
			nullThread.start();
		} else {
			Iterator<LBSimThread> it = algs.values().iterator();
			while(it.hasNext()) {
				LBSimThread a = it.next();
				agentPrintMessage("Starting simthread "+a.getSubLattice().getId());
				a.start();
			}
		}
	}


	private void handleAlgorithmFinished(AlgorithmFinished alg) throws Exception {
		if(algCount == 0) {
			agentPrintMessage("Null-thread finished.");
			simulationPhaseFinished();
			return;
		}
		
		// Simulation duration (maximum of all algs durations)
		long duration = alg.getSimDuration();
		if(finishedAlgs == 0) {
			simDuration = duration;
		} else {
			if(duration > simDuration)
				simDuration = duration;
		}

		
		++finishedAlgs;
		agentPrintMessage(finishedAlgs+"/"+algs.size()+" finished algs.");

		if(finishedAlgs == algs.size()) {

			simulationPhaseFinished();

		}

	}
	
	private void handleReplicationFinished(ReplicationFinished rf) throws InterruptedException {
		
		agentPrintMessage("Signaling end of replication to controller.");
		lbTask.signalEndOfReplication(new ReplicationDone(rf.getSavedIteration(), simParam.getVersion(), algs.size()));

	}

	private void handleAlgToBackup(AlgToBackup atb) throws InterruptedException {

		LBSimThread alg = atb.getAlgorithm();
		stateReplicator.submitAlgorithm(alg);

	}

	private void handleLBData(LBData m) throws LBException, InterruptedException, AgentException {
		if(m.getVersion() == simParam.getVersion()) {
			LBSimThread alg = algs.get(m.getSubLTo());
			if(alg == null) {

				throw new LBException("DA "+lbTask.getDistributedAgent().getDaId()+
						" could not find sub-lattice "+m.getSubLTo());

			}

			alg.writeBlockingMessage(m);
		} else {
			agentPrintMessage("Ignored obsolete LBData.");
		}
	}

	private void simulationPhaseFinished() throws Exception {
		finishedSubs = getSubIds();
		lbTask.signalSimulationFinished(new SimulationFinished(simParam, finishedSubs, simDuration));
	}

	private int[] getSubIds() {
		if(algCount == 0)
			return new int[0];

		int[] finishedAlgs = new int[algs.size()];
		Iterator<LBSimThread> it = algs.values().iterator();
		LBSimThread alg = it.next();
		finishedAlgs[0] = alg.getNodeId();
		for(int i = 1; i < finishedAlgs.length; ++i) {
			alg = it.next();
			finishedAlgs[i] = alg.getNodeId();
		}
		
		return finishedAlgs;
		
	}

	
	private void startStateReplicator() throws InterruptedException {
		
		if(stateReplicator != null) {
			stateReplicator.join();
		}
		
		int from = simParam.getStartingIteration();
		int to = simParam.getLastIteration();
		int backupRate = simParam.getBackupRate();
		
		int maxOff = (to - from);
		int lastStateOffset = maxOff;
		if(! simParam.getKeepFinalState() && backupRate > 0) {
			int tmp = maxOff % backupRate;
			if(tmp == 0)
				lastStateOffset -= backupRate;
			else
				lastStateOffset -= tmp;
		}
		
		agentPrintMessage("Last state offset: "+lastStateOffset);

		stateReplicator = new StateReplicator(this,
				from,
				backupRate, lastStateOffset,
				replicationNeighbors,
				algCount, simParam.getStateFilesCompressed());

		try {
			stateReplicator.start();
		} catch (AgentException e) {
			e.printStackTrace();
		}

	}

	
	private void linkAlgs() throws LBException {

		assert algCount == algs.size();

		Iterator<Entry<Integer, LBSimThread>> algIt = algs.entrySet().iterator();
		while(algIt.hasNext()) {
			
			Entry<Integer, LBSimThread> entry = algIt.next();
			LBSimThread currentAlg = entry.getValue();
			SubLattice currentSubL = currentAlg.getSubLattice();
			
			// Data agregation
			int numOfNeigh = currentSubL.getNeighborsCount();
			for(int i = 0; i < numOfNeigh; ++i) {

				int neighId = currentSubL.getNeighborFromIndex(i);
				
				LBSimThread neighAlg = algs.get(neighId);
				if(neighAlg == null) {
					
					// Data must be sent to another DA

					DAId id = sub2Da.get(neighId);
					if(id == null) {
						throw new LBException("Did not find DA for sub-lattice "+neighId);
					}

					currentAlg.addConsumer(neighId, access.get(id));

				} else {

					currentAlg.addConsumer(neighId, neighAlg);

				}
				
			}

		}

	}
	
	
	private void exitActions() {
		com.unregisterMessageHandler(getHandlerId(simParam.getVersion()), this);

		// Stopping algorithms.
		Iterator<LBSimThread> it = algs.values().iterator();
		while(it.hasNext()) {

			LBSimThread a = it.next();
			agentPrintMessage("Stopping sim-thread "+a.getNodeId());
			a.kill();

		}
		
		// Stopping backup submitter.
		if(stateReplicator != null) {

			try {
				stateReplicator.stop();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (AgentException e1) {
				e1.printStackTrace();
			}

		}
		
		it = algs.values().iterator();
		while(it.hasNext()) {

			LBSimThread a = it.next();
			try {
				agentPrintMessage("Waiting for sim-thread "+a.getNodeId());
				a.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		
		try {
			if(stateReplicator != null) {
				agentPrintMessage("Waiting for replicator.");
				stateReplicator.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		algs.clear();
		
		// Stop clients
		if(simParam != null) {
			try {
				simParam.stopOutputClient();
			} catch (Exception e) {
			}
			try {
				simParam.stopInputClient();
			} catch (Exception e) {
			}
		}

		agentPrintMessage("exit.");
	}
	
	
	/**
	 * Requests files needed to start this simulation.
	 * 
	 * @throws InterruptedException
	 * @throws IOException 
	 * @throws InputClientException 
	 */
	private void requestFiles() throws InterruptedException, InputClientException, IOException {

		Map<Integer, SubLattice> subs = simParam.getSubLattices();
		int from = simParam.getStartingIteration();
		
		Iterator<Entry<Integer, SubLattice>> it = subs.entrySet().iterator();
		while(it.hasNext()) {

			Entry<Integer, SubLattice> ent = it.next();
			int subLId = ent.getKey();

			String fileUID;
			if(from == 0) {
				// Only solid needs to be requested
				fileUID = subLId+".solid";
			} else {
				// Complete state is needed
				fileUID = LBSimThread.getStateFileUID(subLId, from);
			}
			
			filesToReceive.put(fileUID, subLId);
			if(simParam.readFilesFromInput()) {
				agentPrintMessage("Requesting state file "+fileUID+" from input.");
				int simNum = simParam.getSimulationNumber();
				int version = simParam.getVersion();
				String prefix = lbTask.getDistributedAgent().getFilePrefix();
				File dest = new File(prefix+simNum+"_"+version+"_"+fileUID+".input");
				simParam.getFileFromInput(fileUID, dest, this);
			} else {
				agentPrintMessage("Requesting file "+fileUID+" from DFS.");
				fs.getFile(fileUID, this);
			}

		}

	}
	
	private void establishConnections() {
		Iterator<DAId> it = connectionsToEstablish.iterator();
		while(it.hasNext()) {
			DAId daId = it.next();
			
			try {
				com.asyncConnect(daId, this, this, null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void printMessage(String msg) {

		agentPrintMessage(msg);

	}
	
	public void printMessage(Throwable e) {

		agentPrintMessage(e);

	}
	
	private void handleConnectionEstablished(ConnectionEstablished o) throws Exception {
		
		DAId daId = o.getDAId();

		if(o.isSuccessfull()) {
			
			if( ! connectionsToEstablish.remove(daId))
				throw new Exception("Unexpected connection to DA "+daId);
			
			access.put(daId, o.getAccessor());
			
			trySimulationStart();
			
		} else {
			
			agentPrintMessage("Could not connect to DA "+daId);
			
		}

	}

	@Override
	public void signalSent(Message m, boolean success) {
		// SKIP
	}

	@Override
	public void inputClientGetCB(String fileUID, File f, Throwable error) {
		try {
			submitMessage(new NewFile(fileUID, f, true, error));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void signalEndOfGlobalConfiguration() {
		try {
			submitMessage(new EndOfGlobalConfiguration());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void signalEndOfGlobalReplication(int iteration) {
		try {
			submitMessage(new EndOfGlobalReplication(iteration));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void handleEndOfGlobalConfiguration(EndOfGlobalConfiguration o) {
		
		agentPrintMessage("Computation can start.");
		if( ! faultTolerance || simParam.getStartingIteration() == 0)
			startAlgs();
		
	}
	
	private void handleEndOfGlobalReplication(EndOfGlobalReplication grd) throws AgentException, InterruptedException {
		if(initialReplication) {
			initialReplication = false;
			agentPrintMessage("Computation can start.");
			startAlgs();
		}

		agentPrintMessage("Next backup can start.");
		stateReplicator.resume();
	}
	
	public static String getHandlerId(int version) {
		return "LBSim@"+version;
	}

	@Override
	public void submitIncomingMessage(Message msg) {
		try {
			submitMessage(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
