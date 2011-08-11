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
package laboGrid.impl.central.task;

import java.io.FileNotFoundException;
import java.io.Serializable;

import laboGrid.configuration.LaBoGridConfiguration;
import laboGrid.configuration.middleware.BenchmarkConfiguration;
import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.impl.central.controllerAgent.experimenter.messages.GlobalConfigurationDone;
import laboGrid.impl.central.controllerAgent.experimenter.messages.GlobalReplicationDone;
import laboGrid.impl.central.controllerAgent.experimenter.messages.GlobalSimulationFinishedMessage;
import laboGrid.impl.central.controllerAgent.experimenter.messages.ReplicationDoneMessage;
import laboGrid.impl.central.controllerAgent.experimenter.messages.SimulationFinishedMessage;
import laboGrid.impl.central.controllerAgent.experimenter.messages.SimulationStateQuery;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.messages.BenchmarkResult;
import laboGrid.impl.central.mtwa.MTWAWorker;
import laboGrid.impl.central.task.messages.AvailablePowers;
import laboGrid.impl.central.task.messages.ConfigurationFinished;
import laboGrid.impl.central.task.messages.LBConfigData;
import laboGrid.impl.central.task.messages.SimulationStateMessage;
import laboGrid.impl.common.benchmark.BenchmarkHost;
import laboGrid.impl.common.benchmark.BenchmarkParameters;
import laboGrid.impl.common.benchmark.LBBenchmark;
import laboGrid.impl.common.mtwa.messages.MTWAWorkerMessage;
import laboGrid.impl.common.simulation.LBSimulation;
import laboGrid.impl.common.simulation.SimulationParameters;
import laboGrid.impl.common.simulation.messages.LBSimulationMessage;
import laboGrid.impl.common.task.LBTaskInterface;
import laboGrid.impl.common.task.events.BenchmarkFinished;
import laboGrid.impl.common.task.events.ConfigurationDone;
import laboGrid.impl.common.task.events.ReplicationDone;
import laboGrid.impl.common.task.events.SimulationFinished;
import laboGrid.lb.LBException;
import laboGrid.powerModel.PowerModel;
import laboGrid.powerModel.ResourcePowerModel;

import dimawo.WorkerAgent;
import dimawo.WorkerMessage;
import dimawo.agents.AgentException;
import dimawo.agents.UnknownAgentMessage;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.fileSystem.FileSystemAgent;
import dimawo.middleware.overlay.LeaderElectionInterface;
import dimawo.middleware.overlay.mntree.MnPeerState;
import dimawo.middleware.overlay.mntree.MnTreeLocalUpdate;


public class LBTask
extends WorkerAgent implements BenchmarkHost, LBTaskInterface {

	/** Distributed agent hosting this LBTask. */
	private DistributedAgent da;
	/** Global LaBoGrid Configuration. */
	private LaBoGridConfiguration laboConf;
	
	/** Power measurements available for this DA. */
	private ResourcePowerModel powers;

	/** Configuration data for current simulation */
	private SimulationParameters simConf;
	/** Current simulation */
	private LBSimulation simulation;
	private LBBenchmark currentBench;
	
	private MTWAWorker mtwaWorker;


	/**
	 * Constructor.
	 * 
	 * @param da The hosting distributed agent.
	 * @param laboConf The global LaBoGrid configuration.
	 * 
	 * @throws FileNotFoundException If the agent log file could not be created. 
	 */
	public LBTask(DistributedAgent da, LaBoGridConfiguration laboConf)
	throws FileNotFoundException {

		super(da, "LBTask");

		this.da = da;
		this.laboConf = laboConf;
		
		powers = new ResourcePowerModel(da.getHostName());
		powers.setDaId(da.getDaId());
		
		mtwaWorker = new MTWAWorker(this);

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
	
	@Override
	public void signalSimulationFinished(SimulationFinished simEnd) {
		try {
			submitMessage(simEnd);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void handleConfigurationDone(ConfigurationDone confDone) {
		LeaderElectionInterface lead = da.getOverlayInterface().getLeaderElectionInterface();
		int version = confDone.getVersion();
		int nSubs = confDone.getNSubs();
		lead.sendMessageToLeader(
				new ConfigurationFinished(da.getDaId(), version, nSubs));
	}

	@Override
	public void signalEndOfConfiguration(ConfigurationDone confDone) {
		try {
			submitMessage(confDone);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void handleReplicationDone(ReplicationDone repDone) {
		int version = repDone.getVersion();
		int nSubs = repDone.getNSubs();
		int iteration = repDone.getIteration();

		LeaderElectionInterface lead = da.getOverlayInterface().getLeaderElectionInterface();
		lead.sendMessageToLeader(
				new ReplicationDoneMessage(da.getDaId(), nSubs,
						iteration, version, 0L));
	}
	
	@Override
	public void signalEndOfReplication(ReplicationDone repDone) {
		try {
			submitMessage(repDone);
		} catch(InterruptedException e) {
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

		/*
		 * Query current simulation phase.
		 */
		DAId daId = da.getDaId();
		if(daId == null)
			throw new Exception("DA has no descriptor");
		da.getOverlayInterface().getLeaderElectionInterface().sendMessageToLeader(
			new SimulationStateQuery(da.getDaId()));
		
		mtwaWorker.start();

		da.getOverlayInterface().registerForMnTreeUpdates(this);
	}



	@Override
	protected void handleWorkerEvent(Object event) throws Exception {
		
		if(event instanceof BenchmarkFinished) {
			
			handleBenchmarkFinished((BenchmarkFinished) event);
			
		} else if(event instanceof SimulationFinished) {
			
			handleSimulationFinished((SimulationFinished) event);
			
		} else if(event instanceof ConfigurationDone) {
			
			handleConfigurationDone((ConfigurationDone) event);
			
		} else if(event instanceof ReplicationDone) {
			
			handleReplicationDone((ReplicationDone) event);
			
//		} else if(event instanceof MnTreeLocalUpdate) {
//			
//			handleMnTreeLocalUpdate((MnTreeLocalUpdate) event);
//			
		} else {

			throw new UnknownAgentMessage("Unknown event: "+
					event.getClass());

		}
		
	}
	
	@Override
	protected void onLocalTopologyChange(MnPeerState newState) throws Exception {
		// Do nothing (topology change is signaled by base class implementation)
	}


	@Override
	protected void handleWorkerMessage(WorkerMessage message) throws Exception {
		
		if(message instanceof LBSimulationMessage) {
			
			if(simulation != null)
				simulation.submitSimulationMessage((LBSimulationMessage) message);
			else
				throw new Error("Could not deliver message to sim.: "+message.getClass().getName());
			
		} else if(message instanceof LBConfigData) {
			
			handleLBConfigData((LBConfigData) message);

		} else if(message instanceof SimulationStateMessage) {
			
			handleSimulationStateMessage((SimulationStateMessage) message);
			
		} else if(message instanceof AvailablePowers) {
			
			handleAvailablePowers((AvailablePowers) message);
			
		} else if(message instanceof MTWAWorkerMessage) {
			
			mtwaWorker.submitMTWAWorkerMessage((MTWAWorkerMessage) message);
			
		} else if(message instanceof GlobalReplicationDone) {

			handleGlobalReplicationDone((GlobalReplicationDone) message);

		} else if(message instanceof GlobalConfigurationDone) {

			handleGlobalConfigurationDone((GlobalConfigurationDone) message);

		} else if(message instanceof GlobalSimulationFinishedMessage) {

			handleGlobalSimulationFinishedMessage((GlobalSimulationFinishedMessage) message);

		} else {

			throw new UnknownAgentMessage(message);

		}
		
	}


	/////////////////////
	// Private methods //
	/////////////////////
	
	private void handleGlobalConfigurationDone(GlobalConfigurationDone o) {
		
		if(simConf.getVersion() != o.getVersion()) {
			agentPrintMessage("Ignored wrong configuration done message");
			return;
		}
		
		simulation.signalEndOfGlobalConfiguration();
	}
	
	private void handleGlobalReplicationDone(GlobalReplicationDone grd) throws AgentException, InterruptedException {
		
		if(simConf.getVersion() != grd.getVersion()) {
			agentPrintMessage("Ignored wrong replication done message");
			return;
		}

		simulation.signalEndOfGlobalReplication(grd.getReplicatedState());
	}

	/**
	 * Simulation finished. Next benchmark can be instantiate if necessary.
	 * 
	 * @throws LBException 
	 * @throws InterruptedException 
	 */
	private void handleSimulationFinished(SimulationFinished message) throws LBException, InterruptedException {
		
		SimulationParameters finishedParams = message.getSimParams();
		if(finishedParams.getVersion() != simConf.getVersion()) {
			agentPrintMessage("Obsolete simulation finished.");
			return;
		}
		
		LeaderElectionInterface lead = da.getOverlayInterface().getLeaderElectionInterface();
		lead.sendMessageToLeader(
				new SimulationFinishedMessage(da.getDaId(), simConf.getVersion(),
						message.getSubIds(), simConf.getLastIteration(),
						message.getSimDuration()));

	}
	
	private void handleGlobalSimulationFinishedMessage(GlobalSimulationFinishedMessage simEnd) throws Exception {
		if(simEnd.getVersion() != simConf.getVersion()) {
			agentPrintMessage("Ignored obsolete global sim-end");
			return;
		}
		
		simulation.stop();
		simulation.join();
		simulation = null;
		
		agentPrintMessage("Simulation "+simConf.getSimulationNumber()+"@version "+simConf.getVersion()+" finished.");

		System.gc(); // Data from the finished simulation should be GCed.

		// Launch benchmark if needed.
		int nextPhaseNum = simConf.getSimulationNumber() + 1;
		if(nextPhaseNum < laboConf.getExperienceDescription().getSimulationsCount()) {

			simConf = new SimulationParameters(nextPhaseNum, laboConf, da);
			tryBenchmark();

		}
	}

	/**
	 * Handles a benchmark result submitted by the current LBBenchmark.
	 * @throws InterruptedException 
	 */
	private void handleBenchmarkFinished(BenchmarkFinished bf) throws InterruptedException {
		
		PowerModel power = currentBench.getPower();
		
		if(power != null) {
			
			String powerDesc = currentBench.getPowerDescriptor();

			powers.updatePower(powerDesc, power);
			da.getOverlayInterface().getLeaderElectionInterface().sendMessageToLeader(
					new BenchmarkResult(da.getDaId(),
					powerDesc, power));

		} else {

			Throwable t = currentBench.getErrors().getFirst();
			da.signalChildError(new LBException("Could not benchmark resource " +
					"(one cause given).", t), getClass().toString());

		}

		currentBench = null;

	}

	/**
	 * Sets the available powers for this DA.
	 */
	private void handleAvailablePowers(AvailablePowers ap) {

		ResourcePowerModel availablePowers = ap.getPowers();
		powers.merge(availablePowers);

		// If a benchmark is running for nothing, it is interrupted.
		if(currentBench != null) {
			
			String powerDesc = currentBench.getPowerDescriptor();
			if(powers.hasPower(powerDesc)) {

				currentBench.kill();
				currentBench = null;

			}

		}

	}

	/**
	 * Sets the current simulation phase (if not already set).
	 * 
	 * @throws LBException 
	 */
	private void handleSimulationStateMessage(SimulationStateMessage ssm) throws LBException {

		ResourcePowerModel provided = ssm.getPowers();
		if(provided != null) {
			this.powers.merge(provided);
		}
		
		if(simConf == null) {
			simConf = new SimulationParameters(ssm.getPhaseNumber(), laboConf, da);
			tryBenchmark();
		}
	}

	private void tryBenchmark() throws LBException {
		if(benchmarkIsNeeded()) {
			agentPrintMessage("Benchmarking needed.");
			launchBenchmark();
		} else {
			agentPrintMessage("No benchmarking needed.");
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

	private void handleLBConfigData(LBConfigData conf) throws InterruptedException, LBException, InstantiationException, IllegalAccessException, ClassNotFoundException, AgentException {
		
		stopBenchmark();
		stopCurrentSimulation();
		configureSimulation(conf);

	}

	private void configureSimulation(LBConfigData configuration) throws InterruptedException, LBException, InstantiationException, IllegalAccessException, ClassNotFoundException, AgentException {

		if(simConf == null || simConf.getSimulationNumber() != configuration.getPhaseNum()) {

			// Current simulation is not yet configured or is obsolete.
			simConf = new SimulationParameters(configuration.getPhaseNum(), laboConf, da);

		}

		simConf.updateConfiguration(configuration);
		agentPrintMessage("Configuring simulation "+simConf.getSimulationNumber()+"@version "+simConf.getVersion());
		agentPrintMessage("First iteration: "+simConf.getStartingIteration());
		agentPrintMessage("Last iteration: "+simConf.getLastIteration());
		
		simulation = new LBSimulation(this, simConf);
		simulation.start();

	}

	/**
	 * Stops the current simulation if needed.
	 * 
	 * @throws InterruptedException
	 * @throws AgentException
	 */
	private void stopCurrentSimulation() throws InterruptedException, AgentException {
		if(simulation != null && ! simulation.isFinished()) {
			simulation.stop();
			simulation.join();
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
		
		try {
			mtwaWorker.stop();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (AgentException e1) {
			e1.printStackTrace();
		}
		
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

			if(currentBench != null)
				currentBench.join();
			if(simulation != null)
				simulation.join();

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


	@Override
	public void signalMnTreeLocalUpdate(MnTreeLocalUpdate update) {
		try {
			submitMessage(update);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
