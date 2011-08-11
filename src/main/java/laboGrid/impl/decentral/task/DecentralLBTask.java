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
package laboGrid.impl.decentral.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import laboGrid.configuration.LaBoGridConfiguration;
import laboGrid.configuration.middleware.BenchmarkConfiguration;
import laboGrid.configuration.middleware.FaultToleranceConfiguration;
import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.graphs.mapping.kl.Vertex;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.impl.central.task.messages.LBConfigData;
import laboGrid.impl.common.benchmark.BenchmarkHost;
import laboGrid.impl.common.benchmark.BenchmarkParameters;
import laboGrid.impl.common.benchmark.LBBenchmark;
import laboGrid.impl.common.mtwa.messages.MTWAWorkerMessage;
import laboGrid.impl.common.simulation.LBSimulation;
import laboGrid.impl.common.simulation.SimulationParameters;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.impl.common.simulation.messages.LBSimulationMessage;
import laboGrid.impl.common.task.LBTaskInterface;
import laboGrid.impl.common.task.events.BenchmarkFinished;
import laboGrid.impl.common.task.events.ConfigurationDone;
import laboGrid.impl.common.task.events.ReplicationDone;
import laboGrid.impl.common.task.events.SimulationFinished;
import laboGrid.impl.decentral.controller.ExperienceState;
import laboGrid.impl.decentral.controller.NewMappingMessage;
import laboGrid.impl.decentral.controller.messages.BrokenDasMessage;
import laboGrid.impl.decentral.controller.messages.InitialMappingMessage;
import laboGrid.impl.decentral.mtwa.DecentralMTWAWorker;
import laboGrid.impl.decentral.mtwa.Sub2Da;
import laboGrid.impl.decentral.task.events.NewSub2Da;
import laboGrid.impl.decentral.task.messages.ConfBarrierReached;
import laboGrid.impl.decentral.task.messages.ReplicationBarrierReached;
import laboGrid.impl.decentral.task.messages.SimEndBarrierReached;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.powerModel.PowerModel;
import laboGrid.powerModel.ResourcePowerModel;

import dimawo.ReflectionException;
import dimawo.WorkerAgent;
import dimawo.WorkerMessage;
import dimawo.agents.AgentException;
import dimawo.agents.UnknownAgentMessage;
import dimawo.master.messages.MasterMessage;
import dimawo.middleware.barriersync.BarrierWaitCallBack;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.fileSystem.DFSException;
import dimawo.middleware.fileSystem.FileSystemCallBack;
import dimawo.middleware.fileSystem.FileSystemAgent;
import dimawo.middleware.fileSystem.ReplicateFileResult;
import dimawo.middleware.fileSystem.fileDB.FileDescriptor.FileMode;
import dimawo.middleware.overlay.BarrierID;
import dimawo.middleware.overlay.BarrierSyncCallBackInterface;
import dimawo.middleware.overlay.BarrierSyncInterface;
import dimawo.middleware.overlay.ComputerTreePosition;
import dimawo.middleware.overlay.mntree.MnPeerState;


public class DecentralLBTask extends WorkerAgent
implements BenchmarkHost, LBTaskInterface,
BarrierSyncCallBackInterface, FileSystemCallBack {

	/** Distributed agent hosting this LBTask. */
	private DistributedAgent da;
	/** Global LaBoGrid Configuration. */
	private LaBoGridConfiguration laboConf;
	private int availableCpus;

	/** Power measurements available for this DA. */
	private ResourcePowerModel powers;

	private ExperienceState expState;
	private String perfHashForAwaitedBench;
	private SimulationParameters simConf;
	private BarrierID currentConfBarrierId, currentReplBarrierId, currentSimEndBarrierId;
	private Sub2Da currentSub2Da;
	private ModelGraph currentMGraph;
	private int nextSavedIteration;

	/** Current simulation */
	private LBSimulation simulation;
	private LBBenchmark currentBench;
	private Pattern lastSavedFilesPattern;
	
	private Set<DAId> replicationNeighbors;
	
	private Vertex[] currentPart;
	private DecentralMTWAWorker mtwaWorker;

	private LinkedList<DAId> broken;
	private boolean currentMnStateIsBroken, triggerWaitGoodMnState, simWaitGoodMnState;
	private MnPeerState currentMnState;
	private ComputerTreePosition currentCtPos;


	public DecentralLBTask(DistributedAgent da,
			LaBoGridConfiguration laboConf,
			ResourcePowerModel powMod,
			int availableCpus) throws FileNotFoundException {
		super(da, "DecentralLBTask");
		
		this.da = da;
		this.laboConf = laboConf;
		this.availableCpus = availableCpus;
		
		powers = powMod;
		powers.setDaId(da.getDaId());
		
		replicationNeighbors = new TreeSet<DAId>();
		
		mtwaWorker = new DecentralMTWAWorker(this);
		currentMnStateIsBroken = triggerWaitGoodMnState = simWaitGoodMnState = false;
	}

	////////////////////
	// Public methods //
	////////////////////
	
	public String getFilePrefix() {

		return da.getFilePrefix();

	}
	
	public CommunicatorInterface getCommunicator() {

		return da.getCommunicator();

	}
	
	public FileSystemAgent getFileSystemPeer() {

		return da.getFileSystemPeer();

	}
	
	public DAId getDaId() {

		return da.getDaId();

	}

	/**
	 * Signals the end of a running benchmark.
	 * 
	 * @throws InterruptedException
	 */
	public void signalBenchmarkFinished() throws InterruptedException {
		submitMessage(new BenchmarkFinished());
	}
	
	/**
	 * Signals the end of the current simulation.
	 * 
	 * @throws InterruptedException
	 */
	public void signalSimulationFinished(SimulationFinished simEnd) {
		try {
			submitMessage(simEnd);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/////////////////////////
	// Task implementation //
	/////////////////////////

	@Override
	protected Serializable preWorkerExit() {

		agentPrintMessage("exit.");
		stopChildAgents();
		
		return null;

	}


	//////////////////////////////////
	// AbstractAgent implementation //
	//////////////////////////////////

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
		mtwaWorker.start();
		
		da.getOverlayInterface().registerForMnTreeUpdates(this);
	}

	@Override
	protected void handleWorkerEvent(Object event) throws Exception {
		
		if(event instanceof BenchmarkFinished) {
			
			handleBenchmarkFinished((BenchmarkFinished) event);
			
		} else if(event instanceof SimulationFinished) {
			
			handleSimulationFinished((SimulationFinished) event);
			
		} else if(event instanceof BarrierWaitCallBack) {
			
			handleBarrierWaitCallBack((BarrierWaitCallBack) event);
			
		} else if(event instanceof ReplicationDone) {
			
			handleReplicationDone((ReplicationDone) event);
			
		} else if(event instanceof ConfigurationDone) {
			
			handleConfigurationDone((ConfigurationDone) event);
			
		} else {

			throw new UnknownAgentMessage("Unknown event: "+
					event.getClass());

		}
		
	}
	
	private void handleReplicationDone(ReplicationDone done) throws Exception {
		int version = done.getVersion();
		int iteration = done.getIteration();
		
		if(version != expState.getVersion()) {
			agentPrintMessage("Ignored replication-done (version="+version+")");
			return;
		}
		
		agentPrintMessage("Iteration "+iteration+" replicated for simulation "+expState.getSimNum()+"@"+expState.getVersion());

		nextSavedIteration = iteration;
		barrierSyncOnReplication(iteration);
	}
	
	private void handleConfigurationDone(ConfigurationDone done) throws Exception {
		int version = done.getVersion();
		
		if(version != expState.getVersion()) {
			agentPrintMessage("Ignored configuration-done (version="+version+")");
			return;
		}
		
		agentPrintMessage("Configuration done for simulation "+expState.getSimNum()+"@"+expState.getVersion());
		
		barrierSyncOnConf();
	}
	
	private boolean isLeader() {
		return currentMnState != null &&
			! currentMnState.hasParent() &&
			currentMnState.isMainPeer(da.getDaId());
	}
	
	private void handleBarrierWaitCallBack(BarrierWaitCallBack event) throws Exception {
		BarrierID barrierId = event.getBarrierId();
		Throwable err = event.getError();
		
		if(barrierId.equals(currentReplBarrierId)) {
			if(err != null) {
				throw new Exception("barrier wait error", err);
			}

			agentPrintMessage("All LB tasks replicated iteration "+nextSavedIteration+",");
			expState.setLastSavedIteration(nextSavedIteration);
			currentReplBarrierId = null;
			simulation.signalEndOfGlobalReplication(nextSavedIteration);
			
			if(lastSavedFilesPattern != null) {
				da.getFileSystemPeer().deleteDFSFiles(
						lastSavedFilesPattern, this);
			}
			lastSavedFilesPattern =
				LBSimThread.getStatePattern(nextSavedIteration);
			
			if(isLeader())
				this.submitToLocalController(new ReplicationBarrierReached(expState.getVersion(), nextSavedIteration));
		} else if(barrierId.equals(currentSimEndBarrierId)) {
			if(err != null) {
				throw new Exception("barrier wait error", err);
			}

			agentPrintMessage("All LB tasks finished current simulation.");
			expState.setLastSavedIteration(nextSavedIteration);
			currentSimEndBarrierId = null;
			// Wait initial mapping from controller
			
			if(isLeader())
				this.submitToLocalController(new SimEndBarrierReached(expState.getVersion()));
		} else if(barrierId.equals(currentConfBarrierId)) {
			if(err != null) {
				throw new Exception("barrier wait error", err);
			}

			agentPrintMessage("All LB tasks configured @"+expState.getVersion());
			currentConfBarrierId = null;
			simulation.signalEndOfGlobalConfiguration();
			
			if(isLeader())
				this.submitToLocalController(new ConfBarrierReached(expState.getVersion()));
		} else {
			agentPrintMessage("Unknown barrier ID: "+barrierId);
		}
	}
	
	@Override
	protected void onLocalTopologyChange(MnPeerState newState) throws Exception {
		// Update computer tree position
		currentMnState = newState;
		boolean oldIsBroken = currentMnStateIsBroken;
		currentMnStateIsBroken = checkMnState(broken);
		if(oldIsBroken && ! currentMnStateIsBroken &&
				(triggerWaitGoodMnState || simWaitGoodMnState)) {
			broken = null;
			validateCtPosition();
		}
		
		// Update replication neighborhood
		FaultToleranceConfiguration ftConf = laboConf.getMiddlewareConfiguration().getFaultToleranceConfiguration();
		if(ftConf.replicationIsEnabled()) {
			int maxNeighbors = Math.min(ftConf.getBackupDegree(),
					newState.getThisMnSize() - 1);

			DAId[] peersPool = new DAId[newState.getThisMnSize()];
			newState.getThisMnPeersIds(peersPool);

			setReplicationNeighborsSet(maxNeighbors, peersPool);
		}
	}

	private void validateCtPosition() throws InterruptedException, AgentException, LBException, ReflectionException {
		agentPrintMessage("New valid CT position.");
		currentCtPos = generateCtPosition(currentMnState);
		if(triggerWaitGoodMnState) {
			triggerNewSim();
		} else if(simWaitGoodMnState) {
			da.getOverlayInterface().getBarrierSyncInterface().setComputerTreePosition(currentCtPos);
			simulation.start();
		}
	}

	private boolean checkMnState(LinkedList<DAId> brokenList) {
		if(brokenList == null)
			return false;
		
		for(DAId id : brokenList) {
			if(currentMnState.contains(id)) {
				return true;
			}
		}
		
		return false;
	}

	private ComputerTreePosition generateCtPosition(MnPeerState mnState) {
		ComputerTreePosition newPos = null;
		if(mnState.isMainPeer(da.getDaId())) {
			DAId parentId = mnState.getParentMnMainPeer();
			TreeSet<DAId> childrenSet = new TreeSet<DAId>();
			mnState.getThisMnPeersIds(childrenSet);
			childrenSet.remove(mnState.getThisMnMainPeer());
			for(int i = 0; i < mnState.getMaxNumOfChildren(); ++i) {
				DAId childId = mnState.getChildMnMainPeer(i);
				if(childId != null)
					childrenSet.add(childId);
			}
			DAId[] childrenIds = new DAId[childrenSet.size()];
			childrenSet.toArray(childrenIds);
			
			newPos = new ComputerTreePosition(da.getDaId(), parentId, childrenIds);
		} else {
			DAId parentId = mnState.getThisMnMainPeer();
			newPos = new ComputerTreePosition(da.getDaId(), parentId, new DAId[]{});
		}
		return newPos;
	}

	private void setReplicationNeighborsSet(int maxNeighbors, DAId[] peersPool) {
		if(maxNeighbors >= peersPool.length)
			throw new Error("Cannot extract more neighbors than available in pool");
		
		agentPrintMessage("Peers pool:");
		for(DAId id : peersPool)
			agentPrintMessage(id.toString());

		replicationNeighbors.clear();
		int pos = Arrays.binarySearch(peersPool, da.getDaId());
		if(pos < 0)
			throw new Error("This da is not part of the pool");
		
		pos = (pos + 1) % peersPool.length;
		for(int count = 0; count < maxNeighbors; ++count) {
			replicationNeighbors.add(peersPool[pos]);
			pos = (pos + 1) % peersPool.length;
		}
		
		agentPrintMessage("Number of replication neighbors: "+replicationNeighbors.size());
	}

	private void barrierSyncOnConf() {
		BarrierSyncInterface sync = da.getOverlayInterface().getBarrierSyncInterface();
		currentConfBarrierId =
			ExperienceBarrierKeys.getSimConfBarrierId(
				expState.getSimNum(), expState.getVersion());
		sync.barrierWait(currentConfBarrierId, this);
	}
	
	private void barrierSyncOnReplication(int iteration) {
		BarrierSyncInterface sync = da.getOverlayInterface().getBarrierSyncInterface();
		currentReplBarrierId =
			ExperienceBarrierKeys.getSimReplBarrierId(
				expState.getSimNum(), expState.getVersion(), iteration);
		sync.barrierWait(currentReplBarrierId, this);
	}
	
	private void barrierSyncOnSimEnd() {
		BarrierSyncInterface sync = da.getOverlayInterface().getBarrierSyncInterface();
		currentSimEndBarrierId =
			ExperienceBarrierKeys.getSimEndBarrierId(
				expState.getSimNum(), expState.getVersion());
		sync.barrierWait(currentSimEndBarrierId, this);
	}
	
	@Override
	protected void handleWorkerMessage(WorkerMessage message) throws Exception {
		
		if(message instanceof LBSimulationMessage) {
			if(simulation != null)
				simulation.submitSimulationMessage((LBSimulationMessage) message);
			else
				throw new Error("Could not deliver message to sim.: "+message.getClass().getName());
		} else if(message instanceof NewMappingMessage) {
			handleNewMappingMessage((NewMappingMessage) message);
		} else if(message instanceof InitialMappingMessage) {
			handleInitialMappingMessage((InitialMappingMessage) message);
		} else if(message instanceof MTWAWorkerMessage) {
			mtwaWorker.submitMTWAWorkerMessage((MTWAWorkerMessage) message);
		} else if(message instanceof BrokenDasMessage) {
			handleBrokenDasMessage((BrokenDasMessage) message);
		} else {
			throw new UnknownAgentMessage(message);
		}
		
	}


	/////////////////////
	// Private methods //
	/////////////////////
	
	private void handleBrokenDasMessage(BrokenDasMessage message) {
		broken = message.getBrokenDas();
		currentMnStateIsBroken = checkMnState(broken);
		if(currentMnStateIsBroken)
			agentPrintMessage("Current MN state is broken");
	}

	private void handleInitialMappingMessage(InitialMappingMessage initMap) throws Exception {
		agentPrintMessage("Received initial mapping.");
		cancelObsoleteComputations();
		updateCurrentState(initMap.getState());
		
		agentPrintMessage("New experience state:");
		agentPrintMessage(expState.toString());

		setInitialPart(initMap.getMapping());
		if(! currentMnStateIsBroken) {
			currentCtPos = generateCtPosition(currentMnState);
			triggerNewSim();
		} else {
			agentPrintMessage("Wait for valid CT position (trigger).");
			triggerWaitGoodMnState = true;
		}
	}

	private void setInitialPart(Sub2Da sub2Da) {
		// Set initial part
		LinkedList<Vertex> vertList = new LinkedList<Vertex>();
		DAId thisDaId = da.getDaId();
		
		for(Entry<Integer, DAId> e : sub2Da.getMap().entrySet()) {
			DAId id = e.getValue();
			if(id.equals(thisDaId)) {
				int subId = e.getKey();
				SubLattice sub = currentMGraph.getSubLattice(subId);
				int[] adj = new int[sub.getNeighborsCount()];
				sub.getNeighbors(adj);
				int[] weights = new int[sub.getNeighborsCount()];
				sub.getWeights(weights);
				
				Vertex v = new Vertex(subId);
				v.setAdjacencyList(adj);
				v.setWeights(weights);
				vertList.add(v);
			}
		}
		
		currentPart = new Vertex[vertList.size()];
		vertList.toArray(currentPart);
	}

	private void updateCurrentState(ExperienceState state) throws Exception {
		expState = state;
		if(simConf != null) {
			simConf.stopClients();
		}
		simConf = new SimulationParameters(expState.getSimNum(), laboConf, da);
		currentMGraph = simConf.generateModelGraph();
	}

	private void cancelObsoleteComputations() throws InterruptedException, AgentException {
		currentConfBarrierId = null;
		currentReplBarrierId = null;
		currentSimEndBarrierId = null;
		
		stopBenchmark();
		stopCurrentSimulation();
	}

	private void handleNewMappingMessage(NewMappingMessage map) throws InterruptedException, LBException, InstantiationException, IllegalAccessException, ClassNotFoundException, AgentException {
		int version = map.getVersion();
		if(version != expState.getVersion()) {
			agentPrintMessage("Ignored obsolete sub2Da ("+version+")");
			return;
		}
		
		agentPrintMessage("New mapping received for simulation "+
				expState.getSimNum()+"@"+expState.getVersion());
		currentSub2Da = map.getSub2Da();
		configureSimulation(generateLbConfigData(expState.getVersion(),
				currentSub2Da));
	}

	private void triggerNewSim() throws InterruptedException, AgentException,
			LBException, ReflectionException {
		
		if(! tryBenchmark()) {
			perfHashForAwaitedBench = null;
			long ccp = getCurrentCcp();
			agentPrintMessage("Requesting sub2Da for sim "+
					expState.getSimNum()+"@"+expState.getVersion());
			mtwaWorker.requestSub2Da(expState.getVersion(), ccp, currentPart,
					currentCtPos);
		} else {
			agentPrintMessage("Waiting benchmark result");
			perfHashForAwaitedBench = simConf.getPowerDescriptor();
		}
	}

	private long getCurrentCcp() {
		if(laboConf.getMiddlewareConfiguration().getLoadBalancingConfiguration().buildPowerModel()) {
			String powDesc = simConf.getPowerDescriptor();
			long ccp = powers.getPower(powDesc, currentMGraph.getSubLattice(0).getSize());
			if(ccp == -1)
				throw new Error("No CCP available for descriptor "+powDesc);
			return ccp;
		} else {
			return availableCpus;
		}
	}

	/**
	 * Simulation finished. Next benchmark can be instantiate if necessary.
	 * 
	 * @throws LBException 
	 * @throws InterruptedException 
	 * @throws AgentException 
	 */
	private void handleSimulationFinished(SimulationFinished simEnd) throws LBException, InterruptedException, AgentException {
		
		int version = simEnd.getVersion();
		if(version != expState.getVersion()) {
			agentPrintMessage("Ignored sim-end (version="+version+")");
			return;
		}

		agentPrintMessage(1, "Simulation "+simConf.getSimulationNumber()+"@version "+simConf.getVersion()+" finished in "+simEnd.getSimDuration()+"ms.");
		barrierSyncOnSimEnd();
	}

	/**
	 * Handles a benchmark result submitted by the current LBBenchmark.
	 * @throws InterruptedException 
	 */
	private void handleBenchmarkFinished(BenchmarkFinished bf) throws InterruptedException {
		PowerModel power = currentBench.getPower();
		
		agentPrintMessage("Received benchmark result");
		if(power != null) {
			String powerDesc = currentBench.getPowerDescriptor();
			powers.updatePower(powerDesc, power);
			
			if(powerDesc.equals(perfHashForAwaitedBench)) {
				agentPrintMessage("Awaited benchmark finished, requesting sub2Da");
				mtwaWorker.requestSub2Da(expState.getVersion(), getCurrentCcp(),
						currentPart, currentCtPos);
				perfHashForAwaitedBench = null;
			} else {
				agentPrintMessage("Unexpected benchmark finished, ignoring");
			}
		} else {
			Throwable t = currentBench.getErrors().getFirst();
			da.signalChildError(new LBException("Could not benchmark resource " +
					"(first cause given).", t), getClass().toString());
		}

		currentBench = null;
	}

	private boolean tryBenchmark() throws LBException {
		if(benchmarkIsNeeded()) {
			agentPrintMessage("Benchmarking needed.");
			launchBenchmark();
			return true;
		} else {
			agentPrintMessage("No benchmarking needed.");
			return false;
		}
	}

	private boolean benchmarkIsNeeded() {
		if(laboConf.getMiddlewareConfiguration().getLoadBalancingConfiguration().buildPowerModel()) {
			String powDesc = simConf.getPowerDescriptor();
			
			return ! powers.hasPower(powDesc);
		}
		
		return false;
	}


	/**
	 * Instantiates and launches a benchmark given the current simulation phase.
	 * 
	 * @throws LBException
	 */
	private void launchBenchmark() throws LBException {

		ProcessingChainDescription procChainDesc = simConf.getProcessingChainDescription();

		BenchmarkConfiguration benchConf =
			laboConf.getMiddlewareConfiguration().getLoadBalancingConfiguration().
			getBenchmarkConfiguration();

		String latticeClass = simConf.getLatticeClass();
		String solidClass = simConf.getSolidClass();

		BenchmarkParameters params =
			new BenchmarkParameters(benchConf, latticeClass, solidClass, procChainDesc);
		
		currentBench = new LBBenchmark(this, params);
		currentBench.start();

	}

	private void configureSimulation(LBConfigData configuration) throws InterruptedException, LBException, InstantiationException, IllegalAccessException, ClassNotFoundException, AgentException {
		if(simConf == null) {
			// Current simulation is not yet configured or is obsolete.
			simConf = new SimulationParameters(configuration.getPhaseNum(),
					laboConf, da);
		}

		simConf.updateConfiguration(configuration);
		agentPrintMessage("Configuring simulation "+simConf.getSimulationNumber()+"@"+simConf.getVersion());
		agentPrintMessage("First iteration: "+simConf.getStartingIteration());
		agentPrintMessage("Last iteration: "+simConf.getLastIteration());

		stopCurrentSimulation();
		simulation = new LBSimulation(this, simConf);
		
		// Set CT position
		currentCtPos = generateCtPosition(currentMnState);
		if(! currentMnStateIsBroken) {
			da.getOverlayInterface().getBarrierSyncInterface().setComputerTreePosition(currentCtPos);
			simulation.start();
		} else {
			agentPrintMessage("Wait for valid CT position (sim).");
			simWaitGoodMnState = true;
		}
	}

	/**
	 * Stops the current simulation if needed.
	 * 
	 * @throws InterruptedException
	 * @throws AgentException
	 */
	private void stopCurrentSimulation() throws InterruptedException, AgentException {
		if(simulation != null) {
			try {
				simulation.stop();
				simulation.join();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Stops the current benchmark if it exists.
	 */
	private void stopBenchmark() throws InterruptedException {

		if(currentBench != null) {

			currentBench.kill();
			currentBench.join();

		}

	}
	
	/**
	 * Stops the child agents if they were instantiated.
	 * @throws InterruptedException 
	 */
	private void stopChildAgents() {
		
		if(currentBench != null) {
			currentBench.kill();
		}
		
		if(simulation != null) {
			try {
				simulation.stop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (AgentException e) {
			}

		}
		
		try {
			mtwaWorker.stop();
		} catch (InterruptedException e1) {
		} catch (AgentException e1) {
		}
		
		try {

			if(currentBench != null)
				currentBench.join();
			if(simulation != null)
				simulation.join();
			mtwaWorker.join();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	
	public synchronized void printMessage(String msg) {

		agentPrintMessage(msg);

	}
	
	public synchronized void printMessage(Throwable e) {

		agentPrintMessage(e);

	}
	
	public void signalEndOfConfiguration(ConfigurationDone confDone) {
		try {
			submitMessage(confDone);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void signalEndOfReplication(ReplicationDone repDone) {
		try {
			submitMessage(repDone);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void submitNewSub2Da(int simNum, int version, Sub2Da sub2Da) {
		try {
			submitMessage(new NewSub2Da(simNum, version, sub2Da));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private LBConfigData generateLbConfigData(int version, Sub2Da sub2Da) {
		DAId thisDaId = da.getDaId();
		
		HashMap<Integer, SubLattice> partialSubs = new HashMap<Integer, SubLattice>();
		HashMap<Integer, DAId> partialSub2Da = new HashMap<Integer, DAId>();
		fillPartialStructs(thisDaId, sub2Da, partialSubs, partialSub2Da);
		
		agentPrintMessage("Running simulation on "+partialSubs.size()+" subs.");

		return new LBConfigData(thisDaId,
				expState.getSimNum(),
				expState.getLastSavedIteration(),
				partialSubs,
				partialSub2Da,
				replicationNeighbors,
				version);
	}

	private void fillPartialStructs(DAId thisDaId,
			Sub2Da sub2Da,
			HashMap<Integer, SubLattice> partialSubs,
			HashMap<Integer, DAId> partialSub2Da) {
		Map<Integer, DAId> sub2DaMap = sub2Da.getMap();
		SubLattice[] currentSubs = currentMGraph.getSubLattices();
		for(Entry<Integer, DAId> e : sub2DaMap.entrySet()) {
			int subId = e.getKey();
			DAId daId = e.getValue();
			
			if(daId.equals(thisDaId)) {
				partialSubs.put(subId, currentSubs[subId]);
			}
		}
		
		// TODO : copy only necessary entries
		partialSub2Da.putAll(sub2DaMap);
	}

	@Override
	public void barrierWaitCB(BarrierWaitCallBack param) {
		try {
			submitMessage(param);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected void submitToLocalController(MasterMessage msg) {
		if(! isLeader())
			throw new Error("this task must be executed by same DA as controller");
		msg.setSourceDaId(da.getDaId());
		msg.setSender(da.getDaId());
		msg.setRecipient(da.getDaId());
		da.submitMasterMessage(msg);
	}

	@Override
	public void addFileCB(DFSException error, String fileUID, File file,
			FileMode mode) {
		submitError(new Error("unimplemented"));
	}

	@Override
	public void deleteFilesCB(Pattern p) {
		agentPrintMessage("Files deleted for pattern "+p.toString());
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
}
