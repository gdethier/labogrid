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
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import laboGrid.CallBackNotImplemented;
import laboGrid.ConfigurationException;
import laboGrid.configuration.LaBoGridConfiguration;
import laboGrid.configuration.experience.ExperienceDescription;
import laboGrid.configuration.experience.IODescription;
import laboGrid.configuration.experience.SimulationDescription;
import laboGrid.configuration.lbConfiguration.LBConfiguration;
import laboGrid.configuration.lbConfiguration.LatticeDescription;
import laboGrid.configuration.lbConfiguration.SolidDescription;
import laboGrid.configuration.lbConfiguration.SubLatticesConfiguration;
import laboGrid.configuration.lbConfiguration.SolidDescription.SolidType;
import laboGrid.configuration.middleware.BenchmarkConfiguration;
import laboGrid.configuration.middleware.LoadBalancingConfiguration;
import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.graphs.GenerationException;
import laboGrid.graphs.mapping.GraphMapper;
import laboGrid.graphs.mapping.GraphMapping;
import laboGrid.graphs.mapping.kl.Vertex;
import laboGrid.graphs.mapping.mtwa.ComputerTree;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.model.ModelGraphGenerator;
import laboGrid.graphs.resource.ResourceGraph;
import laboGrid.impl.central.controllerAgent.LBControllerAgent;
import laboGrid.impl.central.controllerAgent.experimenter.events.CurrentTaskFinished;
import laboGrid.impl.central.controllerAgent.experimenter.events.MTWAGraphMapping;
import laboGrid.impl.central.controllerAgent.experimenter.events.NewResourceGraph;
import laboGrid.impl.central.controllerAgent.experimenter.events.RemovedDA;
import laboGrid.impl.central.controllerAgent.experimenter.events.TimeOut;
import laboGrid.impl.central.controllerAgent.experimenter.messages.ExperimenterMessage;
import laboGrid.impl.central.controllerAgent.experimenter.messages.ReplicationDoneMessage;
import laboGrid.impl.central.controllerAgent.experimenter.messages.SimulationFinishedMessage;
import laboGrid.impl.central.controllerAgent.experimenter.messages.SimulationStateQuery;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.ResourceGraphBuilder;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.ResourceGraphConsumer;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.ResourceGraphConsumerException;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.TaskDescriptor;
import laboGrid.impl.central.mtwa.MTWAMaster;
import laboGrid.impl.central.mtwa.messages.MTWAMasterMessage;
import laboGrid.impl.central.task.messages.ConfigurationFinished;
import laboGrid.impl.common.control.DefaultJobSubmitter;
import laboGrid.impl.common.simulation.DeleteFilesCB;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.ioClients.InputClient;
import laboGrid.ioClients.InputClientCallBack;
import laboGrid.ioClients.InputClientException;
import laboGrid.ioClients.OutputClient;
import laboGrid.ioClients.OutputClientCB;
import laboGrid.ioClients.OutputClientCallBack;
import laboGrid.ioClients.controller.ControllerInputClient;
import laboGrid.ioClients.controller.ControllerOutputClient;
import laboGrid.ioClients.controller.OutputClientMessage;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.lattice.LatticeDescriptor;
import laboGrid.lb.solid.Solid;
import laboGrid.math.IntegerVector;

import dimawo.Reflection;
import dimawo.ReflectionException;
import dimawo.agents.AgentException;
import dimawo.agents.LoggingAgent;
import dimawo.agents.UnknownAgentMessage;
import dimawo.fileTransfer.server.FileProvider;
import dimawo.middleware.barriersync.BarrierCreateCallBack;
import dimawo.middleware.barriersync.BarrierWaitCallBack;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.fileSystem.DFSException;
import dimawo.middleware.fileSystem.FileSystemCallBack;
import dimawo.middleware.fileSystem.FileSystemAgent;
import dimawo.middleware.fileSystem.ReplicateFileResult;
import dimawo.middleware.fileSystem.fileDB.FileDescriptor.FileMode;
import dimawo.middleware.overlay.BarrierSyncCallBackInterface;
import dimawo.timer.AgentTimer;
import dimawo.timer.TimedAgent;




/**
 * The Experimenter directs the simulations work flow
 * described by an Experience. It also holds the state
 * of the current simulation execution.
 * 
 * @author Gérard Dethier
 */
public class Experimenter
extends LoggingAgent
implements ResourceGraphConsumer, TimedAgent,
InputClientCallBack, OutputClientCallBack, FileSystemCallBack, FileProvider {

	private LBControllerAgent controller;
	private LaBoGridConfiguration laboConf;

	private ResourceGraphBuilder rgb;
	private DefaultJobSubmitter jSubmit;
	private AgentTimer timer;
	private MTWAMaster mtwaMaster;

	private ExperienceDescription experience;
	private BenchmarkConfiguration benchConf;

	private File mGraphFile;
	private ModelGraph currentModelGraph;
	private SimulationDescription currentSimulation;
	private int currentSimulationNumber;
	private ExecutionState executionState;

	private String currentInputPath;
	private InputClient currentInputClient;
	private OutputClient currentOutputClient;
	private LatticeDescriptor currentLatticeDesc;
	
	private PendingInitActions pendingActions;
	private PendingExitActions exitActions;
	
	private Pattern currentObsoletePattern;
	private Pattern currentAllPattern;


	public Experimenter(LBControllerAgent controller,
			LaBoGridConfiguration lbConf, ResourceGraphBuilder rgb,
			DefaultJobSubmitter jSubmit) {
		
		super(controller, "Experimenter");
		
		this.setPrintStream(controller.getHostingDA().getFilePrefix());
		
		this.controller = controller;
		this.laboConf = lbConf;

		this.rgb = rgb;
		this.jSubmit = jSubmit;
		timer = new AgentTimer(this);
		mtwaMaster = new MTWAMaster(this);

		experience = lbConf.getExperienceDescription();
		benchConf =
			lbConf.getMiddlewareConfiguration().getLoadBalancingConfiguration().getBenchmarkConfiguration();
		
		currentSimulationNumber = -1; // So first simulation has ID 0.

	}
	
	
	////////////////////
	// Public methods //
	////////////////////
	
	public void submitWorkflowHandlerMessage(ExperimenterMessage msg) throws InterruptedException {

		submitMessage(msg);

	}

	
	/**
	 * Called by ExecutionState to signal the end of the current
	 * simulation phase.
	 * @throws InterruptedException 
	 */
	public void taskFinished() throws InterruptedException {

		submitMessage(new CurrentTaskFinished());

	}
	
	public void signalRemovedDa(DAId id) throws InterruptedException {

		submitMessage(new RemovedDA(id));

	}


	//////////////////////////////////////////
	// ResourceGraphConsumer implementation //
	//////////////////////////////////////////

	@Override
	public void submitNewResourceGraph(int seqNum, ResourceGraph rGraph)
	throws ResourceGraphConsumerException, InterruptedException {
		
		submitMessage(new NewResourceGraph(seqNum, rGraph));
		
	}
	
	
	///////////////////////////////
	// TimedAgent implementation //
	///////////////////////////////

	@Override
	public void submitTimeOut(long timeOut) throws InterruptedException {

		submitMessage(new TimeOut(timeOut));

	}
	

	//////////////////////////////////
	// AbstractAgent implementation //
	//////////////////////////////////

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
		exitActions();
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
		mtwaMaster.start();
		prepareNextTask();
	}

	@Override
	protected void handleMessage(Object m) throws Exception {
		
		if(m instanceof NewResourceGraph) {
			
			handleNewResourceGraph((NewResourceGraph) m);
			
		} else if(m instanceof CurrentTaskFinished) {
			
			handleCurrentTaskFinished((CurrentTaskFinished) m);

		} else if(m instanceof RemovedDA) {
			
			handleRemovedDA((RemovedDA) m);

		} else if(m instanceof ReplicationDoneMessage) {
			
			handleReplicationDone((ReplicationDoneMessage) m);

		} else if(m instanceof ConfigurationFinished) {
			
			handleConfigurationFinished((ConfigurationFinished) m);

		} else if(m instanceof SimulationFinishedMessage) {
			
			handleSimulationFinished((SimulationFinishedMessage) m);

		} else if(m instanceof SimulationStateQuery) {
			
			handleSimulationStateQuery((SimulationStateQuery) m);

		} else if(m instanceof TimeOut) {
			
			handleTimeOut((TimeOut) m);

		} else if(m instanceof InputFile) {
			
			handleInputFile((InputFile) m);

		} else if(m instanceof FileAdded) {
			
			handleFileAdded((FileAdded) m);

		} else if(m instanceof OutputClientMessage) {
			
			currentOutputClient.submitOutputClientMessage((OutputClientMessage) m);

		} else if(m instanceof OutputClientCB) {
			
			handleOutputClientCB((OutputClientCB) m);

		} else if(m instanceof DeleteFilesCB) {
			
			handleDeleteFilesCB((DeleteFilesCB) m);

		} else if(m instanceof MTWAMasterMessage) {
			
			mtwaMaster.submitMTWAMasterMessage((MTWAMasterMessage) m);

		} else if(m instanceof MTWAGraphMapping) {
			
			handleMTWAGraphMapping((MTWAGraphMapping) m);

		} else {

			throw new UnknownAgentMessage(m);

		}

	}


	private void handleMTWAGraphMapping(MTWAGraphMapping m) throws InterruptedException, IOException, GenerationException {
		
		ResourceGraph rGraph = m.getResourceGraph();
		TreeMap<DAId, Vertex[]> mtwaDa2Sub = m.getDa2Sub();
		DAId[] mtwaSub2Da = m.getSub2Da();

		DAId[] newIds = rGraph.getNewIds();		
		TreeMap<DAId, Integer> oldIds = new TreeMap<DAId, Integer>();
		for(int i = 0; i < newIds.length; ++i) {
			oldIds.put(newIds[i], i);
		}
		
		TreeSet<Integer>[] da2Sub = new TreeSet[newIds.length];
		int[] sub2Da = new int[mtwaSub2Da.length];
		for(Iterator<Entry<DAId, Vertex[]>> it = mtwaDa2Sub.entrySet().iterator(); it.hasNext();) {
			Entry<DAId, Vertex[]> e = it.next();
			
			int newId = oldIds.get(e.getKey());
			TreeSet<Integer> subs = new TreeSet<Integer>();
			Vertex[] v = e.getValue();
			for(int i = 0; i < v.length; ++i) {
				int subID = v[i].getSubID();
				subs.add(subID);
				sub2Da[subID] = newId;
			}
			
			da2Sub[newId] = subs;
		}
		
		GraphMapping cGraph = new GraphMapping(da2Sub, sub2Da);
		
		GraphConfiguration config = new GraphConfiguration(
				laboConf.getMiddlewareConfiguration().getFaultToleranceConfiguration(),
				currentModelGraph,
				rGraph, cGraph, currentLatticeDesc);

		executionState.submitNewConfig(config);
		
	}


	private void handleSimulationFinished(SimulationFinishedMessage m) throws Exception {
		if(executionState.handleLBComputationFinished(m)) {
			
			executionState.notifySimulationDone();
			if( ! currentSimulation.getKeepStateForNextSimulation()) {
				
				agentPrintMessage("Simulation files can be deleted.");
				
				// Delete states
				currentAllPattern = LBSimThread.getAllPattern();
				controller.getHostingDA().getFileSystemPeer().deleteDFSFiles(
						currentAllPattern, this);
		
			} else {
				agentPrintMessage("Simulation files are kept for next simulation.");
				handleCurrentTaskFinished(new CurrentTaskFinished());
			}
		}
	}


	private void handleOutputClientCB(OutputClientCB m) throws Exception {
		
		String fileUID = m.getDestinationFileUID();
		exitActions.removePutFile(fileUID);
		
		if(exitActions.isEmpty())
			tryPrepareNextTask();
		
	}


	private void handleFileAdded(FileAdded m) throws Exception {
		
		String fileUID = m.getFileUID();
		Exception error = m.getError();
		
		agentPrintMessage("File "+fileUID+" added to DFS.");
		
		if(error != null)
			throw error;
		
		pendingActions.removePendingAddFile(fileUID);
		if(pendingActions.isEmpty())
			launchCurrentTask();
		
	}
	
	
	/////////////
	// Helpers //
	/////////////
	
	private void handleReplicationDone(ReplicationDoneMessage rd)
	throws InterruptedException, AgentException, IOException {

		if(executionState.handleReplicationDone(rd)) {
			int currentObsoleteState = executionState.getLastSavedIteration();
			int currentSavedState = executionState.getCurrentSavedIteration();
			
			if(currentObsoleteState != currentSavedState) {

				// Delete old files
				currentObsoletePattern =
					LBSimThread.getStatePattern(currentObsoleteState);
				controller.getHostingDA().getFileSystemPeer().deleteDFSFiles(
						currentObsoletePattern, this);
			
			} else {
				executionState.notifyReplicationDone();
			}
		}

	}
	
	private void handleDeleteFilesCB(DeleteFilesCB m) throws Exception {
		agentPrintMessage("Callback for deleted files: "+m.getPattern().toString());
		if(m.getPattern() == currentObsoletePattern) {
			currentObsoletePattern = null;
			executionState.notifyReplicationDone();
		}
		
		if(m.getPattern() == currentAllPattern) {
			currentAllPattern = null;
			handleCurrentTaskFinished(new CurrentTaskFinished());
		}
	}
	
	private void handleConfigurationFinished(ConfigurationFinished m) throws IOException {
		executionState.handleConfigurationFinished(m);
	}

	/**
	 * Updates graphs currently used.
	 * 
	 * @param to Time-out event.
	 * 
	 * @throws AgentException
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	private void handleTimeOut(TimeOut to) throws AgentException, InterruptedException, IOException {

		timer.cancelTimer();
		executionState.useCandidateConfiguration();

	}

	private void handleSimulationStateQuery(SimulationStateQuery ssq) throws AgentException, InterruptedException {

		rgb.signalNewResource(currentSimulationNumber, ssq.getDAId());

	}

	/**
	 * Current resource graph is invalidated.
	 */
	private void handleRemovedDA(RemovedDA bda) {

		if(executionState != null)
			executionState.invalidateResourceGraph(bda.getBrokenDaId());

	}

	/**
	 * Prepares and launches the next task. If there are no more
	 * tasks to execute, the simulation job is finished. LaBoGrid
	 * shutdown can be initiated.
	 * @throws Exception 
	 */
	private void handleCurrentTaskFinished(CurrentTaskFinished m) throws Exception {

		rgb.signalEndOfTask(currentSimulationNumber);

		exitActions.removeTask();
		if( ! exitActions.isEmpty())
			return;

		tryPrepareNextTask();

	}
	
	private void tryPrepareNextTask() throws Exception {
		
		if( ! prepareNextTask()) {

			agentPrintMessage("Experience finished.");
			controller.jobFinished();

		}
		
	}

	/**
	 * Initializes all required fields linked to the current task
	 * with the data of the next task to be executed.
	 * 
	 * @return True if new current task has been set. False if there are
	 * no more tasks to execute.
	 * @throws Exception 
	 * 
	 * @throws IOException 
	 */
	private boolean prepareNextTask() throws Exception {
		
		if(currentInputClient != null) {
			currentInputClient.stop();
			currentInputClient.join();
			currentInputClient = null;
		}
		
		if(currentOutputClient != null) {
			currentOutputClient.stop();
			currentOutputClient.join();
			currentOutputClient = null;
		}
		
		++currentSimulationNumber;
		if(currentSimulationNumber >= experience.getSimulationsCount())
			return false;
		
		pendingActions = new PendingInitActions();
		exitActions = new PendingExitActions();

		agentPrintMessage("Preparing simulation "+currentSimulationNumber);

		currentSimulation =
			experience.getSimulationDescription(currentSimulationNumber);
		LBConfiguration lbConf =
			laboConf.getLBConfiguration(currentSimulation.getLBConfigurationId());

		LatticeDescription lDesc = lbConf.getLatticeDescription();
		Lattice f;
		try {
			f = (Lattice) Reflection.newInstance(lDesc.getClassName());
		} catch (Exception e) {
			throw new LBException("Lattice type for next simulation not instantiable.", e);
		}
		currentLatticeDesc = f.getLatticeDescriptor();


		IODescription inputDesc = currentSimulation.getInput();
		if(inputDesc == null) {

			currentInputClient = null;
			agentPrintMessage("No given input client.");

		} else {

			currentInputClient = inputDesc.getInputClient();
			currentInputClient.setDistributedAgent((DistributedAgent) this.controller.getHostingDA());
			if(currentInputClient instanceof ControllerInputClient) {
				ControllerInputClient client = (ControllerInputClient) currentInputClient;
				client.setTurnToLocal(true);
			}
			currentInputClient.start();
			
			currentInputPath = currentInputClient.getPath();
			
			agentPrintMessage("Using input client "+inputDesc.getClientClass());

		}

		IODescription outputDesc = currentSimulation.getOutput();
		if(outputDesc == null) {

			currentOutputClient = null;
			agentPrintMessage("No given output client.");

		} else {

			currentOutputClient = outputDesc.getOutputClient();
			currentOutputClient.setDistributedAgent((DistributedAgent) this.controller.getHostingDA());
			if(currentOutputClient instanceof ControllerOutputClient) {
				ControllerOutputClient client = (ControllerOutputClient) currentOutputClient;
				client.setTurnToLocal(true);
			}
			currentOutputClient.start();
			agentPrintMessage("Using output client "+outputDesc.getClientClass());

		}

		if(currentSimulation.getStartingIteration() == 0) {

			////////////////////////////////////			
			// Initial data must be generated //
			////////////////////////////////////

			agentPrintMessage("Initial simulation.");

			SubLatticesConfiguration subConf = lbConf.getSubLatticesConfiguration();

			// Generate model graph
			try {


				ModelGraphGenerator mGraphGen = (ModelGraphGenerator)
				Reflection.newInstance(subConf.getGeneratorClassName());
				mGraphGen.setParameters(lDesc.getSize(), subConf.getMinSubLatticesCount(),
						f.getLatticeDescriptor());

				currentModelGraph = mGraphGen.generateModelGraph();
				
				mGraphFile = new File(controller.getWorkingDir()+ModelGraph.defaultModelGraphFileUID);
				mGraphFile.deleteOnExit();
				currentModelGraph.write(mGraphFile.getAbsolutePath());

				mtwaMaster.submitNewModelGraph(currentModelGraph);
				jSubmit.signalNewModelGraph(currentModelGraph);

			} catch (Exception e) {
				throw new LBException("Could not set model graph for next simulation.", e);
			}

			try {
				getSolidFromInput(lbConf.getSolidDescription());
			} catch (IOException e) {
				throw new LBException("Could not insert sub-solids into DFS.", e);
			}

		} else if(currentSimulation.isFirstOfSequence()) {

			agentPrintMessage("Continued simulation and data read from input.");

			///////////////////////////////////////////////////////
			// Initial data should be read from simulation input //
			///////////////////////////////////////////////////////

			getGraphFromInputClient();

		} else {

			agentPrintMessage("Continued simulation and data read from DFS.");

			/////////////////////////////////////////////
			// Initial data are still available in DFS //
			/////////////////////////////////////////////

		}
		
		if(pendingActions.isEmpty())
			launchCurrentTask();

		return true;

	}


	/**
	 * Starts the current Task. A Task descriptor is submitted to the Resource
	 * Graph builder. On reception of the Resource Graph, a Computation Graph
	 * will be built and the simulation will be able to start.
	 * 
	 * @throws AgentException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws LBException 
	 */
	private void launchCurrentTask() throws InterruptedException, LBException {
		
		agentPrintMessage("launching current task");
		
		try {

			putInitialDataToCurrentOutput();

		} catch (Exception e) {

			throw new LBException("Could not save simulation data.", e);

		}
		
		ProcessingChainDescription pc =
			laboConf.getProcessingChain(currentSimulation.getProcessingChainId());
		LBConfiguration lbConf =
			laboConf.getLBConfiguration(currentSimulation.getLBConfigurationId());

		executionState = new ExecutionState(
				this,
				controller.getHostingDA().getCommunicator(),
				controller.getHostingDA().getFileSystemPeer(),
				currentSimulationNumber,
				currentSimulation, currentModelGraph,
				lbConf, pc,
				laboConf.getMiddlewareConfiguration().getConfigurationSchedulerConf().
					getReconfTO());

		TaskDescriptor taskDesc = new TaskDescriptor(
				currentSimulationNumber,
				lbConf,
				pc,
				benchConf);

		int[] refSize = currentModelGraph.getSubLattice(0).getSize();
		rgb.submitNewTask(refSize, taskDesc, this);

	}
	
	/**
	 * If the submitted resource graph was generated for the current simulation,
	 * it is checked against current configuration and is possibly used.
	 * 
	 * @param nRGraph A Resource Graph.
	 * 
	 * @throws InterruptedException
	 * @throws IOException 
	 * @throws GenerationException 
	 * @throws ConfigurationException 
	 * @throws ReflectionException 
	 */
	private void handleNewResourceGraph(NewResourceGraph nRGraph) throws InterruptedException, IOException, GenerationException, ConfigurationException, ReflectionException {

		if(currentSimulationNumber != nRGraph.getSequenceNumber()) {

			System.out.print("Received an obsolete Resources graph.");
			return;

		}
		
		LoadBalancingConfiguration loadConf =
			laboConf.getMiddlewareConfiguration().getLoadBalancingConfiguration();
		
		ResourceGraph rGraph = nRGraph.getResourceGraph();
		if(loadConf.mtwaIsEnabled()) {
			
			mtwaMaster.requestMapping(rGraph);
			
		} else {
		
			GraphMapper cGraphGen = (GraphMapper)
			Reflection.newInstance(loadConf.getGeneratorClassName());
			cGraphGen.setParameters(loadConf.getGeneratorParameters());
			
			// Generating mapping
			GraphMapping cGraph = cGraphGen.map(rGraph, currentModelGraph);
	
			GraphConfiguration config = new GraphConfiguration(
					laboConf.getMiddlewareConfiguration().getFaultToleranceConfiguration(),
					currentModelGraph,
					rGraph, cGraph, currentLatticeDesc);
	
			executionState.submitNewConfig(config);
		
		}

	}

	/**
	 * A Model Graph is used to cut the solid into pieces. The solid pieces are
	 * written in files that are submitted to the file sharing service of the hosting
	 * DA. 
	 * 
	 * @param solid The solid.
	 * @param graph A model graph.
	 * @throws Exception 
	 */
	private void getSolidFromInput(SolidDescription solidDesc) throws Exception {

		// Get solid file.
		File solidFile = new File(controller.getWorkingDir()+"solid.dat");
		if(solidFile.exists() && ! solidFile.delete()) {

			throw new IOException("Could not delete old solid file.");

		}
		solidFile.deleteOnExit();

		try {

			String fileID = solidDesc.getFileName();
			currentInputClient.get(fileID, solidFile, this);
			pendingActions.addInputGet(fileID, solidDesc);

		} catch (InputClientException e) {

			throw new LBException("Could not get solid file with input client.", e);
			
		}
		
	}
	
	private void handleInputFile(InputFile m) throws Exception {

		String fileUID = m.getFileUID();
		File file = m.getFile();
		Throwable error = m.getError();
		
		if(error != null)
			throw new LBException("Could not read file "+fileUID, error);
		
		Object attach = pendingActions.removePendingInput(fileUID);
		if(attach instanceof SolidDescription) {
			SolidDescription solidDesc = (SolidDescription) attach;
			
			agentPrintMessage("Solid read from input");
			
			// Read solid from file.
			SolidType solidType = solidDesc.getType();
			Solid solid = null;
			if(solidType.equals(SolidType.bin)) {
				solid = Solid.readBinSolid(file.getAbsolutePath());
			} else if(solidType.equals(SolidType.ascii)) {
				// ascii file.
				try {
					solid = (Solid) Reflection.newInstance(solidDesc.getClassName());
					solid.readAsciiSolid(file.getAbsolutePath());
				} catch (Exception e) {
					throw new LBException("Could not read ascii solid file.", e);
				}
			} else if(solidType.equals(SolidType.compressed_ascii)) {
				// ascii file.
				try {
					solid = (Solid) Reflection.newInstance(solidDesc.getClassName());
					solid.readCompressedAsciiSolid(file.getAbsolutePath());
				} catch (Exception e) {
					throw new LBException("Could not read ascii solid file.", e);
				}
			} else {
				throw new Error("Unsupported solid type");
			}
			file.delete(); // file not needed anymore
			
			// Create sub-solids and insert them in DFS.
			FileSystemAgent fs = controller.getHostingDA().getFileSystemPeer();
			SubLattice[] subs = currentModelGraph.getSubLattices();
			for(int i = 0; i < subs.length; ++i) {

				SubLattice sub = subs[i];
				String subSolidfileUID = i+".solid";
				String fileName = controller.getWorkingDir()+subSolidfileUID;

				int[] from = sub.getPosition();
				int[] to = IntegerVector.add(from, sub.getSize());
				Solid subSolid = solid.getPartition(from, to);
				subSolid.writeBinSolid(fileName);

				File subSolidFile = new File(fileName);
				subSolidFile.deleteOnExit();
				fs.addFile(subSolidfileUID, subSolidFile, FileMode.rw, this);
				
				pendingActions.addFSAdd(subSolidfileUID);

			}
			
		} else if(attach instanceof GetSubsolid) {
			
			FileSystemAgent fs = controller.getHostingDA().getFileSystemPeer();
			pendingActions.addFSAdd(fileUID);
			fs.addFile(fileUID, file, FileMode.rw, this);
			
		} else if(attach instanceof GetModelGraph) {
			
			// Read model graph from file.
			try {
				currentModelGraph = ModelGraph.read(mGraphFile.getAbsolutePath());
			} catch (ClassNotFoundException e) {
				throw new LBException("Could not read model graph from file.", e);
			}

			mtwaMaster.submitNewModelGraph(currentModelGraph);
			jSubmit.signalNewModelGraph(currentModelGraph);
			
		} else {
			throw new Exception("Unknown attachment");
		}
		
		if(pendingActions.isEmpty())
			launchCurrentTask();
		
	}
	
	
	/**
	 * Inserts sub-solids in DFS after they are read from current input
	 * client.
	 * @throws Exception 
	 */
	private void getSubsolidsFromClient() throws Exception  {

		int subs = currentModelGraph.getSubLatticesCount();
		for(int i = 0; i < subs; ++i) {

			String fileUID = i+".solid";
			String fileName = controller.getWorkingDir()+fileUID;
			File subSolidFile = new File(fileName);
			if(subSolidFile.exists() && ! subSolidFile.delete()) {

				throw new IOException("Could not delete old sub-solid file.");

			}
			subSolidFile.deleteOnExit();

			currentInputClient.get(fileUID, subSolidFile, this);
			pendingActions.addInputGet(fileUID, new GetSubsolid());

		}

	}

	
	/**
	 * Instantiates model graph from a file got with current input client.
	 * 
	 * @return A model graph.
	 * 
	 * @throws IOException If an already present model graph file could not be deleted.
	 * @throws LBException An error occured during the reception or the instantiation of
	 * the model graph.
	 */
	private void getGraphFromInputClient() throws IOException, LBException {

		// Get model graph file.
		mGraphFile = new File(controller.getWorkingDir()+ModelGraph.defaultModelGraphFileUID);
		if(mGraphFile.exists() && ! mGraphFile.delete()) {

			throw new IOException("Could not delete old model graph file.");

		}
		mGraphFile.deleteOnExit();

		try {

			currentInputClient.get(ModelGraph.defaultModelGraphFileUID, mGraphFile, this);
			pendingActions.addInputGet(ModelGraph.defaultModelGraphFileUID, new GetModelGraph());

		} catch (Exception e) {

			throw new LBException("Could not get model graph file with input client.", e);

		}
		
	}
	
	
	/**
	 * Puts current model graph and sub-solid to current output
	 * client. 
	 * @throws Exception 
	 */
	private void putInitialDataToCurrentOutput() throws Exception {
		
		if(currentOutputClient != null) {
			
			agentPrintMessage("Putting initial data to output");

			// Put model graph file.
			agentPrintMessage("Model graph file "+ModelGraph.defaultModelGraphFileUID+" output.");
			currentOutputClient.put(mGraphFile, ModelGraph.defaultModelGraphFileUID, this);
			exitActions.addPutFile(ModelGraph.defaultModelGraphFileUID);
		
		}

	}
	
	
	public void printMessage(String msg) {

		agentPrintMessage(msg);

	}
	
	public void printMessage(int verbLevel, String msg) {

		agentPrintMessage(verbLevel, msg);

	}


	@Override
	public void inputClientGetCB(String fileUID, File file, Throwable error) {
		try {
			submitMessage(new InputFile(fileUID, file, error));
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
			submitMessage(new DeleteFilesCB(p));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void getFileCB(DFSException error, String fileUID, File f) {
		submitError(new CallBackNotImplemented("getFile"));
	}


	@Override
	public void removeFileCB(DFSException error, String fileUID, File newFile) {
		submitError(new CallBackNotImplemented("removeFile"));
	}


	@Override
	public void replicateFileCB(ReplicateFileResult res) {
		submitError(new CallBackNotImplemented("replicateFile"));
	}


	@Override
	public void outputClientPutCB(OutputClientCB params) {
		try {
			submitMessage(params);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void exitActions() {
		if(currentInputClient != null)
			try {
				currentInputClient.stop();
				currentInputClient.join();
			} catch (Exception e) {
				e.printStackTrace();
			};
			
		if(currentOutputClient != null)
			try {
				currentOutputClient.stop();
				currentOutputClient.join();
			} catch (Exception e) {
				e.printStackTrace();
			};
			
		try {
			mtwaMaster.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (AgentException e) {
			e.printStackTrace();
		}
	}


	public void submitOutputClientMessage(OutputClientMessage msg) {
		try {
			submitMessage(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	@Override
	public File getFile(String fileUID) {
		return new File(ControllerInputClient.getInputFileName(currentInputPath, fileUID));
	}


	public CommunicatorInterface getCommunicator() {
		return controller.getHostingDA().getCommunicator();
	}


	public void submitMTWAGraphMapping(ResourceGraph rGraph,
			TreeMap<DAId, Vertex[]> da2Sub,
			DAId[] sub2Da) {
		try {
			submitMessage(new MTWAGraphMapping(rGraph, da2Sub, sub2Da));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public String getFilePrefix() {
		return controller.getHostingDA().getFilePrefix();
	}
	
	public DistributedAgent getDistributedAgent() {
		return controller.getHostingDA();
	}

}
