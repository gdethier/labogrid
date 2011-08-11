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
import java.util.regex.Pattern;

import laboGrid.configuration.LaBoGridConfiguration;
import laboGrid.configuration.experience.IODescription;
import laboGrid.configuration.experience.SimulationDescription;
import laboGrid.configuration.lbConfiguration.LBConfiguration;
import laboGrid.configuration.lbConfiguration.SolidDescription;
import laboGrid.configuration.lbConfiguration.SolidDescription.SolidType;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.impl.central.controllerAgent.experimenter.GetModelGraph;
import laboGrid.impl.central.controllerAgent.experimenter.GetSubsolid;
import laboGrid.impl.central.controllerAgent.experimenter.PendingExitActions;
import laboGrid.impl.central.controllerAgent.experimenter.PendingInitActions;
import laboGrid.impl.common.simulation.SimulationParameters;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.impl.decentral.controller.event.FileAdded;
import laboGrid.impl.decentral.controller.event.FilesDeleted;
import laboGrid.impl.decentral.controller.event.NewFile;
import laboGrid.impl.decentral.mtwa.Sub2Da;
import laboGrid.ioClients.InputClient;
import laboGrid.ioClients.InputClientException;
import laboGrid.ioClients.InputClientMessage;
import laboGrid.ioClients.OutputClient;
import laboGrid.ioClients.controller.ControllerInputClient;
import laboGrid.ioClients.controller.ControllerOutputClient;
import laboGrid.ioClients.controller.OutputClientMessage;
import laboGrid.ioClients.standalone.StandAloneInputClient;
import laboGrid.ioClients.standalone.StandAloneOutputClient;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.lb.solid.Solid;
import laboGrid.math.IntegerVector;

import dimawo.Reflection;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.fileSystem.FileSystemAgent;
import dimawo.middleware.fileSystem.fileDB.FileDescriptor.FileMode;


public class SimulationData {
	private DecentralLBController ctrl;
	private LaBoGridConfiguration laboConf;
	private ExperienceState expState;
	private SimulationParameters simParams;
	private File mGraphFile;
	private ModelGraph mGraph;
	private SimulationDescription currentSimulation;
	
	private InputClient currentInputClient;
	private OutputClient currentOutputClient;
	
	private PendingInitActions pendingActions;
	private PendingExitActions exitActions;
	
	private Pattern currentAllPattern;


	public SimulationData(DecentralLBController ctrl) throws Exception {
		this.ctrl = ctrl;
		laboConf = ctrl.getConfiguration();
		expState = ctrl.getExperienceState();
		simParams = new SimulationParameters(
				expState.getSimNum(), ctrl.getConfiguration(), ctrl.getHostingDA());
	}
	
	public Sub2Da getDefaultMapping() {
		Sub2Da sub2Da = new Sub2Da();
		DAId id = ctrl.getHostingDA().getDaId();
		for(int subId = 0; subId < mGraph.getSubLatticesCount(); ++subId) {
			sub2Da.add(subId, id);
		}
		return sub2Da;
	}

	public boolean prepareSimData() throws Exception {
		pendingActions = new PendingInitActions();
		exitActions = new PendingExitActions();

		printMessage("Preparing simulation "+expState.getSimNum());
		
		currentSimulation =
			laboConf.getExperienceDescription().getSimulationDescription(expState.getSimNum());
		LBConfiguration lbConf =
			laboConf.getLBConfiguration(currentSimulation.getLBConfigurationId());
		
		printMessage("Updating last saved iteration: "+currentSimulation.getStartingIteration());
		expState.setLastSavedIteration(currentSimulation.getStartingIteration());

		IODescription inputDesc = currentSimulation.getInput();
		if(inputDesc == null) {

			currentInputClient = null;
			printMessage("No given input client.");

		} else {

			currentInputClient = inputDesc.getInputClient();
			currentInputClient.setDistributedAgent(ctrl.getHostingDA());
			if(currentInputClient instanceof ControllerInputClient) {
				ControllerInputClient client = (ControllerInputClient) currentInputClient;
				client.setTurnToLocal(true);
			} else if(currentInputClient instanceof StandAloneInputClient) {
				StandAloneInputClient client = (StandAloneInputClient) currentInputClient;
				client.setToCA(true);
			}
			currentInputClient.start();
			
			printMessage("Using input client "+inputDesc.getClientClass());

		}

		IODescription outputDesc = currentSimulation.getOutput();
		if(outputDesc == null) {

			currentOutputClient = null;
			printMessage("No given output client.");

		} else {

			currentOutputClient = outputDesc.getOutputClient();
			currentOutputClient.setDistributedAgent(ctrl.getHostingDA());
			if(currentOutputClient instanceof ControllerOutputClient) {
				ControllerOutputClient client = (ControllerOutputClient) currentOutputClient;
				client.setTurnToLocal(true);
			} else if(currentOutputClient instanceof StandAloneOutputClient) {
				StandAloneOutputClient client = (StandAloneOutputClient) currentOutputClient;
				client.setToCA(true);
			}
			currentOutputClient.start();
			printMessage("Using output client "+outputDesc.getClientClass());

		}

		if(currentSimulation.getStartingIteration() == 0) {

			////////////////////////////////////			
			// Initial data must be generated //
			////////////////////////////////////

			printMessage("Initial simulation.");
			
			mGraph = simParams.generateModelGraph();
			
			mGraphFile = new File(ctrl.getWorkingDir()+ModelGraph.defaultModelGraphFileUID);
			mGraphFile.deleteOnExit();
			mGraph.write(mGraphFile.getAbsolutePath());
			
			try {
				getSolidFromInput(lbConf.getSolidDescription());
			} catch (IOException e) {
				throw new LBException("Could not insert sub-solids into DFS.", e);
			}

		} else if(currentSimulation.isFirstOfSequence()) {

			printMessage("Continued simulation and data read from input.");

			///////////////////////////////////////////////////////
			// Initial data should be read from simulation input //
			///////////////////////////////////////////////////////

			getGraphFromInputClient();

		} else {

			printMessage("Continued simulation and data read from DFS.");

			/////////////////////////////////////////////
			// Initial data are still available in DFS //
			/////////////////////////////////////////////
			
			mGraph = simParams.generateModelGraph();
			
			mGraphFile = new File(ctrl.getWorkingDir()+ModelGraph.defaultModelGraphFileUID);
			mGraphFile.deleteOnExit();
			mGraph.write(mGraphFile.getAbsolutePath());

		}
		
		if(pendingActions.isEmpty()) {
			putInitialDataToCurrentOutput();
			return true;
		}

		return false;
	}

	private void printMessage(String msg) {
		ctrl.printMessage("[SimulationData] "+msg);
	}
	
	public void closeIoClients() {
		if(currentInputClient != null) {
			try {
				currentInputClient.stop();
			} catch (Exception e) {
			}
			try {
				currentInputClient.join();
			} catch (InterruptedException e) {
			}
			currentInputClient = null;
		}
		
		if(currentOutputClient != null) {
			try {
				currentOutputClient.stop();
			} catch (Exception e) {
			}
			try {
				currentOutputClient.join();
			} catch (InterruptedException e) {
			}
			currentOutputClient = null;
		}
	}
	
	private void getSolidFromInput(SolidDescription solidDesc) throws Exception {

		// Get solid file.
		File solidFile = new File(ctrl.getWorkingDir()+"solid.dat");
		if(solidFile.exists() && ! solidFile.delete()) {

			throw new IOException("Could not delete old solid file.");

		}
		solidFile.deleteOnExit();

		try {

			String fileID = solidDesc.getFileName();
			currentInputClient.get(fileID, solidFile, ctrl);
			pendingActions.addInputGet(fileID, solidDesc);

		} catch (InputClientException e) {

			throw new LBException("Could not get solid file with input client.", e);
			
		}
		
	}
	
	private void getGraphFromInputClient() throws IOException, LBException {

		// Get model graph file.
		mGraphFile = new File(ctrl.getWorkingDir()+ModelGraph.defaultModelGraphFileUID);
		if(mGraphFile.exists() && ! mGraphFile.delete()) {

			throw new IOException("Could not delete old model graph file.");

		}
		mGraphFile.deleteOnExit();

		try {

			currentInputClient.get(ModelGraph.defaultModelGraphFileUID, mGraphFile, ctrl);
			pendingActions.addInputGet(ModelGraph.defaultModelGraphFileUID, new GetModelGraph());

		} catch (Exception e) {

			throw new LBException("Could not get model graph file with input client.", e);

		}
		
	}

	public boolean submitNewFile(NewFile m) throws Exception {
		String fileUID = m.getFileUID();
		File file = m.getFile();
		Throwable error = m.getError();
		
		if(error != null)
			throw new LBException("Could not read file "+fileUID, error);
		
		Object attach = pendingActions.removePendingInput(fileUID);
		if(attach instanceof SolidDescription) {
			SolidDescription solidDesc = (SolidDescription) attach;
			
			printMessage("Solid read from input");
			
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
			FileSystemAgent fs = ctrl.getHostingDA().getFileSystemPeer();
			SubLattice[] subs = mGraph.getSubLattices();
			for(int i = 0; i < subs.length; ++i) {

				SubLattice sub = subs[i];
				String subSolidfileUID = i+".solid";
				String fileName = ctrl.getWorkingDir()+subSolidfileUID;

				int[] from = sub.getPosition();
				int[] to = IntegerVector.add(from, sub.getSize());
				Solid subSolid = solid.getPartition(from, to);
				subSolid.writeBinSolid(fileName);

				File subSolidFile = new File(fileName);
				subSolidFile.deleteOnExit();
				fs.addFile(subSolidfileUID, subSolidFile, FileMode.rw, ctrl);
				
				pendingActions.addFSAdd(subSolidfileUID);

			}
			
		} else if(attach instanceof GetSubsolid) {
			
			FileSystemAgent fs = ctrl.getHostingDA().getFileSystemPeer();
			pendingActions.addFSAdd(fileUID);
			fs.addFile(fileUID, file, FileMode.rw, ctrl);
			
		} else if(attach instanceof GetModelGraph) {
			
			// Read model graph from file.
			try {
				mGraph = ModelGraph.read(file.getAbsolutePath());
			} catch (ClassNotFoundException e) {
				throw new LBException("Could not read model graph from file.", e);
			}

		} else {
			throw new Exception("Unknown attachment");
		}
		
		if(pendingActions.isEmpty()) {
			putInitialDataToCurrentOutput();
			return true;
		}
		
		return false;
	}

	public boolean signalFileAdded(FileAdded m) throws Exception {
		String fileUID = m.getFileUID();
		printMessage("File "+fileUID+" added to DFS.");
		
		pendingActions.removePendingAddFile(fileUID);
		if(pendingActions.isEmpty()) {
			putInitialDataToCurrentOutput();
			return true;
		}
		return false;
	}

	public boolean prepareSimEnd() {
		closeIoClients();
		
		if( ! currentSimulation.getKeepStateForNextSimulation()) {
			printMessage("Simulation files can be deleted.");
			
			// Delete states
			currentAllPattern = LBSimThread.getAllPattern();
			ctrl.getHostingDA().getFileSystemPeer().deleteDFSFiles(
					currentAllPattern, ctrl);

			return false;
		} else {
			printMessage("Simulation files are kept for next simulation.");
			return true;
		}
	}

	public boolean signalFilesDeleted(FilesDeleted m) {
		printMessage("Callback for deleted files: "+m.getPattern().toString());
		if(m.getPattern() == currentAllPattern) {
			currentAllPattern = null;
			return true;
		}
		
		return false;
	}
	
	private void putInitialDataToCurrentOutput() throws Exception {
		
		if(currentOutputClient != null) {
			printMessage("Putting initial data to output");

			// Put model graph file.
			printMessage("Model graph file "+ModelGraph.defaultModelGraphFileUID+" output.");
			currentOutputClient.put(mGraphFile, ModelGraph.defaultModelGraphFileUID, ctrl);
			exitActions.addPutFile(ModelGraph.defaultModelGraphFileUID);
		
		}

	}

	public void submitOutputClientMessage(OutputClientMessage msg) {
		currentOutputClient.submitOutputClientMessage(msg);
	}
	
	public void submitInputClientMessage(InputClientMessage msg) {
		currentInputClient.submitInputClientMessage(msg);
	}
}
