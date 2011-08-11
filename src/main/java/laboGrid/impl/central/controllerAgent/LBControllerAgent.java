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
package laboGrid.impl.central.controllerAgent;

import java.io.IOException;

import laboGrid.configuration.LaBoGridConfiguration;
import laboGrid.configuration.middleware.ConfigurationSchedulerConfiguration;
import laboGrid.impl.central.controllerAgent.experimenter.Experimenter;
import laboGrid.impl.central.controllerAgent.experimenter.messages.ExperimenterMessage;
import laboGrid.impl.central.controllerAgent.inputServer.CentralInputServer;
import laboGrid.impl.central.controllerAgent.inputServer.messages.CentralInputServerMessage;
import laboGrid.impl.central.controllerAgent.outputServer.CentralOutputServer;
import laboGrid.impl.central.controllerAgent.outputServer.CentralOutputServerMessage;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.ResourceGraphBuilder;
import laboGrid.impl.central.controllerAgent.resourceGraphHandler.messages.ResourceGraphBuilderMessage;
import laboGrid.impl.common.control.DefaultJobSubmitter;
import laboGrid.ioClients.controller.OutputClientMessage;
import laboGrid.lb.LBException;

import dimawo.MasterAgent;
import dimawo.agents.AgentException;
import dimawo.agents.UnknownAgentMessage;
import dimawo.master.messages.MasterMessage;
import dimawo.master.messages.WorkerExitMessage;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;




public class LBControllerAgent extends MasterAgent {

	private static final long serialVersionUID = 1L;
	
	// Tasks sequence handler
	private Experimenter exper;

	private ResourceGraphBuilder rgb;
	
	// LBG Job(s) submiter
	private DefaultJobSubmitter jSubmit;
	
	private CentralOutputServer outputServer;
	private CentralInputServer inputServer;


	public LBControllerAgent(DistributedAgent hostingDa,
			LaBoGridConfiguration lbConf, String ccpFile)
	throws LBException, IOException, AgentException {
		
		super(hostingDa, "LBControllerAgent");
		
		ConfigurationSchedulerConfiguration cConf =
			lbConf.getMiddlewareConfiguration().getConfigurationSchedulerConf();
		rgb = new ResourceGraphBuilder(this,
				lbConf.getMiddlewareConfiguration().getLoadBalancingConfiguration(),
				hostingDa.getCommunicator(),
				cConf.getBenchTO());
		if(ccpFile != null) {
			agentPrintMessage("Reading CCP file");
			rgb.readCCPFile(ccpFile);
		}

		jSubmit = new DefaultJobSubmitter(this, 10, hostingDa.getDiscoveryService());
		exper = new Experimenter(this, lbConf, rgb, jSubmit);
		outputServer = new CentralOutputServer(this);
		inputServer = new CentralInputServer(this);
		inputServer.setFileProvider(exper);

	}
	
	
	////////////////////
	// Public methods //
	////////////////////


	/**
	 * Called by WorkFlowHandler to signal the end of the simulation
	 * job.
	 */
	public void jobFinished() {

		agentPrintMessage("Experience finished, shutting down LaBoGrid.");
		shutdown();

	}


	////////////////////////////////////
	// ControllerAgent implementation //
	////////////////////////////////////

	@Override
	protected void handleUserDefinedAgentMessage(MasterMessage msg)
	throws Exception {

		if(msg instanceof ExperimenterMessage) {

			exper.submitWorkflowHandlerMessage((ExperimenterMessage) msg);

		} else if(msg instanceof ResourceGraphBuilderMessage) {

			rgb.submitResourceGraphBuilderMessage((ResourceGraphBuilderMessage) msg);

		} else if(msg instanceof CentralOutputServerMessage) {

			outputServer.submitCentralOutputServerMessage((CentralOutputServerMessage) msg);

		} else if(msg instanceof CentralInputServerMessage) {

			inputServer.submitCentralInputServerMessage((CentralInputServerMessage) msg);

		} else {

			throw new UnknownAgentMessage(msg);

		}

	}

	@Override
	protected void onExit() {
		exitActions();
	}
	
	private void exitActions() {
		
		try {
			rgb.stop();
		} catch (InterruptedException e) {
		} catch (AgentException e) {
		}
		try {
			exper.stop();
		} catch (InterruptedException e) {
		} catch (AgentException e) {
		}
		
		try {
			jSubmit.stop();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (AgentException e1) {
			e1.printStackTrace();
		}
		
		try {
			outputServer.stop();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (AgentException e1) {
			e1.printStackTrace();
		}
		
		try {
			inputServer.stop();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (AgentException e1) {
			e1.printStackTrace();
		}

		try {
			rgb.join();
		} catch (InterruptedException e) {
		}
		try {
			exper.join();
		} catch (InterruptedException e) {
		}
		
		try {
			jSubmit.join();
		} catch (InterruptedException e1) {
		}
		
		try {
			outputServer.join();
		} catch (InterruptedException e1) {
		}
		
	}


	///////////////////////////////
	// ControllerAgent overrides //
	///////////////////////////////
	
	@Override
	protected void onStartup() throws Throwable {

		rgb.start();
		exper.start();
		jSubmit.start();
		outputServer.start();
		inputServer.start();

	}

	
	/////////////
	// Helpers //
	/////////////

	@Override
	protected void handleWorkerExit(WorkerExitMessage msg)
			throws InterruptedException {
		agentPrintMessage("Task finished on DA "+msg.getSourceDaId());
	}
	
	public void printMessage(String msg) {

		agentPrintMessage(msg);

	}


	@Override
	protected void handleMasterEvent(Object o) throws Exception {
		throw new UnknownAgentMessage(o);
	}

	public void submitOutputClientMessage(OutputClientMessage msg) {
		exper.submitOutputClientMessage(msg);
	}

	@Override
	protected void onTopologyChange(DAId id, ChangeType type)
			throws Exception {
		if(type.equals(ChangeType.join)) {
			agentPrintMessage("Signal new DA: "+id);
			jSubmit.signalNewDA();
		} else if(type.equals(ChangeType.leave)) {
			agentPrintMessage("Signal removed DA: "+id);
			exper.signalRemovedDa(id);
			rgb.signalRemovedDa(id);
			jSubmit.signalBrokenDA();
		} else {
			throw new Error("Unhandled topology change type");
		}
	}
	
}
