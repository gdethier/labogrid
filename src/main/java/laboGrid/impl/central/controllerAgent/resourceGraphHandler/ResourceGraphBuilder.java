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
package laboGrid.impl.central.controllerAgent.resourceGraphHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import laboGrid.configuration.middleware.LoadBalancingConfiguration;
import laboGrid.graphs.resource.HomoNetworkGenerator;
import laboGrid.graphs.resource.ResourceGraph;
import laboGrid.impl.central.controllerAgent.LBControllerAgent;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.events.EndOfTask;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.events.NewResource;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.events.NewTask;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.events.RemoveDAs;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.events.RemovedDA;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.events.TimerTO;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.messages.BenchmarkResult;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.messages.ResourceGraphBuilderMessage;
import laboGrid.impl.central.task.messages.SimulationStateMessage;
import laboGrid.math.IntegerVector;
import laboGrid.powerModel.PowerModel;
import laboGrid.powerModel.ResourceDataBase;
import laboGrid.powerModel.ResourcePowerModel;

import dimawo.agents.AgentException;
import dimawo.agents.LoggingAgent;
import dimawo.agents.UncaughtThrowable;
import dimawo.agents.UnknownAgentMessage;
import dimawo.middleware.communication.Communicator;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.timer.AgentTimer;
import dimawo.timer.TimedAgent;




/**
 * The ResourceGraphHandler maintains the resources graph
 * i.e. the database of available DA and the power of the
 * resources running them.
 * 
 * Proposed services:
 * -	Construction of a resource graph given a task
 * (the power of the resources is gathered if needed).
 * -	Proposal of new resource graphs during a task
 * execution (if new resources register during execution)
 * to a ResourceGraphConsumer.
 * 
 * @author Gérard Dethier
 *
 */
public class ResourceGraphBuilder
extends LoggingAgent {
	
	private CommunicatorInterface com;
	private LoadBalancingConfiguration loadConf;

	private long stableTO;
	private boolean stabilizationInProgress;
	private boolean eventDuringStabilization;
	private Timer timer;


	/** Database of available resources */
	protected ResourceDataBase resourcesDataBase;
	/** Descriptor of the task being currently executed */
	protected TaskDescriptor currentTaskDesc;
	/** The Resource graph consumer that submitted current task */
	protected ResourceGraphConsumer rGraphCons;
	/** Number of running benchmarks */
	protected int runningBenchmarksCount;
	

	/**
	 * Builds a Resource graph handler.
	 * 
	 * @param ms A message sender.
	 */
	public ResourceGraphBuilder(LBControllerAgent ctrl,
			LoadBalancingConfiguration loadConf,
			CommunicatorInterface com, long stableTO) {
		
		super(ctrl, "ResourceGraphBuilder");
		
		setPrintStream(ctrl.getHostingDA().getFilePrefix());
		
		this.com = com;
		this.loadConf = loadConf;

		this.stableTO = stableTO;
		stabilizationInProgress = false;
		eventDuringStabilization = false;

		resourcesDataBase = new ResourceDataBase();

		currentTaskDesc = null;
		rGraphCons = null;

	}
	
	
	////////////////////
	// Public methods //
	////////////////////
	
	public void submitResourceGraphBuilderMessage(ResourceGraphBuilderMessage msg)
	throws InterruptedException {

		submitMessage(msg);

	}

	/**
	 * Submits a new Task. This eventually leads to the submission of a
	 * Resource graph to the given Resource graph consumer.
	 * 
	 * @param taskDesc A description of the new Task.
	 * @param rGraphCons The Resource graph consumer.
	 * 
	 * @throws AgentException
	 * @throws InterruptedException
	 */
	public void submitNewTask(int[] subSize, TaskDescriptor taskDesc, ResourceGraphConsumer rGraphCons) throws InterruptedException {

		submitMessage(new NewTask(subSize, taskDesc, rGraphCons));

	}
	
	/**
	 * Signals the end of a Task. This will stop the production of new
	 * Resource graphs.
	 * 
	 * @param seqNum The sequence number of the finished Task.
	 * @throws InterruptedException 
	 * @throws AgentException 
	 */
	public void signalEndOfTask(int seqNum) throws AgentException, InterruptedException {

		submitMessage(new EndOfTask(seqNum));

	}

	/**
	 * Signals the availability of a new Resource. This can lead to the generation
	 * of a new Resource graph.
	 * 
	 * @param daIdData The ID data of the DA associated to the newly available Resource.
	 * 
	 * @throws AgentException
	 * @throws InterruptedException
	 */
	public void signalNewResource(int currentSimulationNumber, DAId daIdData) throws AgentException, InterruptedException {
		
		submitMessage(new NewResource(currentSimulationNumber, daIdData));
		
	}
	
	/**
	 * Signals a removed DA. The associated Resource is then considered as not
	 * available anymore.
	 * 
	 * @param daId The ID number of the broken DA.
	 * 
	 * @throws AgentException
	 * @throws InterruptedException
	 */
	public void signalRemovedDa(DAId daId) throws AgentException, InterruptedException {
		
		submitMessage(new RemovedDA(daId));
		
	}
	
	/**
	 * Signals the removal of listed DAs from the available DAs list.
	 * 
	 * @param toShutdown
	 * @throws InterruptedException 
	 */
	public void removeDAs(LinkedList<DAId> toShutdown) throws InterruptedException {
		
		submitMessage(new RemoveDAs(toShutdown));

	}
	
	
	///////////////////////////////
	// TimedAgent implementation //
	///////////////////////////////

	private void submitTimeOut(long timeOut) throws InterruptedException {

		submitMessage(new TimerTO());

	}
	
	
	/////////////////////////////
	// AbstractAgent overrides //
	/////////////////////////////

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
	}

	@Override
	protected void handleMessage(Object m) throws Exception {
		
		if(m instanceof NewTask) {

			handleNewTask((NewTask) m);
			
		} else if(m instanceof EndOfTask) {

			handleEndOfTask((EndOfTask) m);
			
		} else if(m instanceof NewResource) {

			handleNewResource((NewResource) m);
			
		} else if(m instanceof BenchmarkResult) {

			handleBenchmarkResult((BenchmarkResult) m);

		} else if(m instanceof RemovedDA) {

			RemovedDA bda = (RemovedDA) m;
			handleRemovedDA(bda.getBrokenDaId());

		} else if(m instanceof TimerTO) {

			handleTimerTO();

		} else if(m instanceof RemoveDAs) {

			handleRemoveDAs((RemoveDAs) m);

		} else {

			throw new UnknownAgentMessage(m);

		}

	}


	/////////////
	// Helpers //
	/////////////
	
	/**
	 * Sets a list of DAs as unavailable.
	 */
	private void handleRemoveDAs(RemoveDAs m) {
		
		resourcesDataBase.removeDas(m.getDasToRemove());

	}

	/**
	 * Called when a timer has expired. This means a new Resource
	 * Graph must be submitted to the current consumer (if any).
	 * 
	 * @throws InterruptedException 
	 */
	private void handleTimerTO() throws InterruptedException {
		
		if( ! stabilizationInProgress)
			return;
		
		if(stabilizationInProgress && eventDuringStabilization) {
			agentPrintMessage("Unstable configuration detected, waiting...");
			stabilizationInProgress = false; // Necessary before a new stabilization process is triggered
			eventDuringStabilization = false;
			triggerStabilization();
			return;
		}
		
		agentPrintMessage("Stable configuration detected, proceeding.");
		
		stabilizationInProgress = false;
		if(rGraphCons != null) {
			submitNewResourceGraph();
		}
		
	}

	/**
	 * Submits a new Resource Graph to the consumer.
	 * @throws InterruptedException 
	 */
	private void submitNewResourceGraph() throws InterruptedException {
		
		ResourceGraphBuildResult result = buildNewResourceGraph();
		if( ! result.isEmpty())
			try {
	
				agentPrintMessage("Built new resource graph with "+result.rGraph.getDasCount()+" resources.");
				rGraphCons.submitNewResourceGraph(currentTaskDesc.getSequenceNumber(),
						result.rGraph);
	
			} catch (ResourceGraphConsumerException e) {
	
				e.printStackTrace();
	
			}
		
	}

	/**
	 * Inserts the new DA's resource in the resources database.
	 * If powers are available, they are sent to the DA.
	 * 
	 * @param idData The identification data of the new DA.
	 * @throws InterruptedException 
	 * @throws IOException 
	 * 
	 */
	private void handleNewResource(NewResource newRes) throws InterruptedException, IOException {
		
		DAId idData = newRes.getDAId();
		
		agentPrintMessage("New resource: "+idData);

		ResourcePowerModel rd = resourcesDataBase.addDa(idData);
		if(loadConf.buildPowerModel()) {
			
			// Benchmark only when using load-balancing.
		
			// If a task is running, DA will benchmark resource.
			if(currentTaskDesc != null &&
					! rd.hasPower(currentTaskDesc.getPowerDescriptor())) {
	
				agentPrintMessage(idData+" is currently benchmarked.");
				++runningBenchmarksCount;
	
			}
			
		} else if(currentTaskDesc != null) {
			
			agentPrintMessage("Triggering stabilization.");
			triggerStabilization();

		}
		
		if(rd == null || (rd != null && rd.size() == 0)) {
			agentPrintMessage("No power descriptor available for DA "+idData);
			rd = null;
		} else {
			agentPrintMessage("Sending power descriptors to DA "+idData);
		}

		// Send initial data to DA
		int currentSimulationNumber = newRes.getCurrentSimulationNumber();
		com.sendDatagramMessage(new SimulationStateMessage(idData,
				currentSimulationNumber, rd));

	}

	/**
	 * Removes the association of the given DA to a resource.
	 * If the association was really removed, a new computation
	 * graph needs to be recomputed.
	 * 
	 * @param daId The identification number of the DA. 
	 */
	private void handleRemovedDA(DAId daId) {

		if(resourcesDataBase.removeDa(daId)) {
			triggerStabilization();
		}

	}

	/**
	 * Causes a new Resource graph to be built given a new Task.
	 * 
	 * @param newTask The data submitted for the new Task.
	 * @throws InterruptedException 
	 */
	private void handleNewTask(NewTask newTask) throws InterruptedException {
		assert currentTaskDesc == null;
		
		agentPrintMessage("New task submitted");

		currentTaskDesc = newTask.getTaskDescriptor();
		rGraphCons = newTask.getResourceGraphConsumer();
		
		if(! loadConf.buildPowerModel()) {
			triggerStabilization();
		} else {
			String powDesc = currentTaskDesc.getPowerDescriptor();
			runningBenchmarksCount = resourcesDataBase.listDaIdsToBenchmark(powDesc).size();
			if(runningBenchmarksCount == 0) {
				triggerStabilization();
			} else {
				agentPrintMessage("Waiting for "+runningBenchmarksCount+" benchmark results.");
			}
		}
	}


	private void triggerStabilization() {
		agentPrintMessage("Trigger stabilization");
		if(stabilizationInProgress) {
			eventDuringStabilization = true;
		} else {
			stabilizationInProgress = true;
			timer = new Timer(true);
			timer.schedule(new TimerTask() {
				public void run() {
					try {
						submitTimeOut(stableTO);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}, stableTO);
		}
	}
	
	/**
	 * Stops the automatic generation of new Resource graphs and
	 * clears the current Task descriptor.
	 * 
	 * @param eot The parameters of the Task end.
	 */
	private void handleEndOfTask(EndOfTask eot) {

		int seqNum = eot.getSequenceNumber();

		assert currentTaskDesc.getSequenceNumber() == seqNum;

		currentTaskDesc = null;
		rGraphCons = null;
		runningBenchmarksCount = 0; // So currently running benchmarks will not
									// cause a new Resource Graph submission.

	}

	/**
	 * 
	 * @param br
	 * 
	 * @throws InterruptedException
	 * @throws AgentException
	 */
	private void handleBenchmarkResult(BenchmarkResult br)
	throws InterruptedException, AgentException {
		
		DAId daId = br.getSourceDaId();
		String powerDesc = br.getPowerDescriptor();
		PowerModel power = br.getPower();
		
		agentPrintMessage("Received benchmark result from DA "+daId);
		agentPrintMessage(power.toString());
		resourcesDataBase.updateResourcePower(daId, powerDesc, power);
		
		if(currentTaskDesc != null &&
				currentTaskDesc.getPowerDescriptor().equals(powerDesc)) {
			
			--runningBenchmarksCount;
			if(runningBenchmarksCount == 0) {
				
				agentPrintMessage("No more benchmarks running.");
				submitNewResourceGraph();
				
			} else {
				agentPrintMessage("Waiting result of "+runningBenchmarksCount+" benchmarks.");
			}

		} // else currentTaskDesc == null || ! currentTaskDesc.getPowerDescriptor().equals(powerDesc)
		//		-> SKIP (no running task or obsolete result)

	}
	
	private class ResourceGraphBuildResult {
		
		public ResourceGraph rGraph;

		public ResourceGraphBuildResult(ResourceGraph rGraph) {

			this.rGraph = rGraph;

		}

		public boolean isEmpty() {
			return rGraph.isEmpty();
		}

	}

	private ResourceGraphBuildResult buildNewResourceGraph() {
		
		String powerDesc = null;
		HomoNetworkGenerator rGraphGen;
		if(loadConf.buildPowerModel()) {
		
			powerDesc = currentTaskDesc.getPowerDescriptor();
			rGraphGen = new HomoNetworkGenerator(true,
					powerDesc, resourcesDataBase);
		
		} else {
			
			rGraphGen = new HomoNetworkGenerator(false,
					null, resourcesDataBase);
			
		}

		ResourceGraph rGraph = rGraphGen.generateResourceGraph();
		return new ResourceGraphBuildResult(rGraph);

	}
	
	
	public void printMessage(String msg) {

		agentPrintMessage(msg);

	}


	public void readCCPFile(String ccpFile) throws IOException {
		resourcesDataBase.readText(ccpFile);
	}

}
