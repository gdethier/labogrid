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
import java.util.Map;
import java.util.Set;

import laboGrid.configuration.LaBoGridConfiguration;
import laboGrid.configuration.experience.IODescription;
import laboGrid.configuration.experience.SimulationDescription;
import laboGrid.configuration.lbConfiguration.LBConfiguration;
import laboGrid.configuration.lbConfiguration.LatticeDescription;
import laboGrid.configuration.lbConfiguration.SubLatticesConfiguration;
import laboGrid.configuration.middleware.FaultToleranceConfiguration;
import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.model.ModelGraphGenerator;
import laboGrid.impl.central.task.messages.LBConfigData;
import laboGrid.impl.common.simulation.algorithm.LBState.ContentType;
import laboGrid.ioClients.InputClient;
import laboGrid.ioClients.InputClientCallBack;
import laboGrid.ioClients.InputClientException;
import laboGrid.ioClients.InputClientMessage;
import laboGrid.ioClients.OutputClient;
import laboGrid.ioClients.controller.OutputClientMessage;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.lattice.LatticeDescriptor;
import laboGrid.procChain.ProcessingChain;

import dimawo.Reflection;
import dimawo.ReflectionException;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;



public class SimulationParameters {

	private int simNum;
	private LaBoGridConfiguration laboConf;
	
	private String latticeClass;
	private String solidClass;
	private String powerDesc;
	private ProcessingChain procChain;

	private SimulationDescription simDesc;	

	private LBConfigData configuration;
	private OutputClient outputClient;
	private InputClient inputClient;


	public SimulationParameters(int simNum, LaBoGridConfiguration laboConf,
			DistributedAgent da)
	throws LBException {
		
		this.simNum = simNum;
		this.laboConf = laboConf;

		simDesc = laboConf.getExperienceDescription().getSimulationDescription(simNum);

		LBConfiguration lbConf = laboConf.getLBConfiguration(simDesc.getLBConfigurationId());
		latticeClass = lbConf.getLatticeDescription().getClassName();
		solidClass = lbConf.getSolidDescription().getClassName();

		String chainId = simDesc.getProcessingChainId();
		ProcessingChainDescription pcd = laboConf.getProcessingChain(chainId);
		powerDesc = SimulationDescription.getPowerDescriptor(latticeClass, pcd);
		procChain = ProcessingChain.getOperatorsChain(pcd, false);

		// Set output client.
		IODescription outDesc = simDesc.getOutput();
		if(outDesc != null) {

			try {
				outputClient = outDesc.getOutputClient();
				outputClient.setDistributedAgent(da);
			} catch (Exception e) {
				throw new LBException("Could not start output client", e);
			}

		}
		
		// Set input client.
		IODescription inDesc = simDesc.getInput();
		if(inDesc != null) {

			try {
				inputClient = inDesc.getInputClient();
				inputClient.setDistributedAgent(da);
			} catch (Exception e) {
				throw new LBException("Could not start input client", e);
			}

		}

	}

	public int getVersion() {
		
		if(configuration == null)
			throw new Error("Configuration data are not set");

		return configuration.getVersion();

	}

	public ProcessingChainDescription getProcessingChainDescription() {

		String chainId = simDesc.getProcessingChainId();
		return laboConf.getProcessingChain(chainId);

	}

	public String getLatticeClass() {

		return latticeClass;

	}
	
	public String getSolidClass() {
		
		return solidClass;
		
	}

	public int getSimulationNumber() {
		
		return simNum;
		
	}

	public String getPowerDescriptor() {

		return powerDesc;

	}

	public SimulationDescription getSimulationDescription() {

		return simDesc;

	}

	public Lattice getLatticeInstance() throws LBException {

		try {

			return (Lattice) Reflection.newInstance(latticeClass);

		} catch (Exception e) {

			throw new LBException("Could not instantiate lattice.", e);

		}

	}

	public void updateConfiguration(LBConfigData configuration) {

		this.configuration = configuration;
		
	}

	public int getSubLatticesCount() {

		return configuration.getSubLattices().size();

	}

	public Map<Integer, DAId> getSub2Da() {

		return configuration.getSubToDA();

	}

	public SubLattice getSubLattices(int subId) {

		return configuration.getSubLattices().get(subId);

	}

	public int getStartingIteration() {

		return configuration.getStartingIteration();

	}

	public ProcessingChain getProcessingChainCopy() {

		return procChain.clone();

	}

	public int getLastIteration() {
		
		return simDesc.getLastIteration();
		
	}

	public int getBackupRate() {

		return laboConf.getMiddlewareConfiguration().
			getFaultToleranceConfiguration().getBackupRate();

	}

	public ContentType getStateFilesCompressed() {

		return laboConf.getMiddlewareConfiguration().
			getFaultToleranceConfiguration().getCompressFiles();

	}

	public int getIterationCount() {

		return simDesc.getLastIteration() - configuration.getStartingIteration();

	}

	public boolean getKeepFinalState() {

		return simDesc.getKeepStateForNextSimulation();

	}

	public Map<Integer, SubLattice> getSubLattices() {

		return configuration.getSubLattices();

	}

	public OutputClient getOutputClient() {

		return outputClient;

	}

	public Set<DAId> getReplicationNeighbors() {
		
		return configuration.getReplicationNeighbors();
		
	}

	public FaultToleranceConfiguration getFaultToleranceConfiguration() {
		return laboConf.getMiddlewareConfiguration().getFaultToleranceConfiguration();
	}

	public Set<DAId> getComputationNeighbors() {
		return configuration.getComputationNeighbors();
	}
	
	public void startOutputClient() throws Exception {
		if(outputClient != null) {
			outputClient.start();
		}
	}

	public void stopOutputClient() throws Exception {
		if(outputClient != null) {
			outputClient.stop();
			outputClient.join();
		}
	}
	
	public void stopClients() {
		if(outputClient != null) {
			try {
				outputClient.stop();
			} catch (Exception e) {
			}
		}
		if(inputClient != null) {
			try {
				inputClient.stop();
			} catch (Exception e) {
			}
		}
	}

	public void submitMessageToOutputClient(OutputClientMessage o) {
		if(outputClient != null) {
			outputClient.submitOutputClientMessage(o);
		}
	}

	public boolean isFirstOfSequence() {
		return simDesc.isFirstOfSequence();
	}

	public void submitMessageToInputClient(InputClientMessage o) {
		if(inputClient != null) {
			inputClient.submitInputClientMessage(o);
		}
	}

	public void startInputClient() throws Exception {
		if(inputClient != null) {
			inputClient.start();
		}
	}

	public void stopInputClient() throws Exception {
		if(inputClient != null) {
			inputClient.stop();
			inputClient.join();
		}
	}

	public void getFileFromInput(String fileUID, File dest, InputClientCallBack cb) throws InputClientException, IOException {
		inputClient.get(fileUID, dest, cb);
	}

	public ModelGraph generateModelGraph() throws ReflectionException {
		LBConfiguration lbConf = laboConf.getLBConfiguration(simDesc.getLBConfigurationId());

		SubLatticesConfiguration subConf = lbConf.getSubLatticesConfiguration();
		ModelGraphGenerator mGraphGen = (ModelGraphGenerator) Reflection.newInstance(subConf.getGeneratorClassName());
		
		LatticeDescription lattDesc = lbConf.getLatticeDescription();
		int[] size = lattDesc.getSize();
		Lattice latt = (Lattice) Reflection.newInstance(lattDesc.getClassName());
		LatticeDescriptor desc = latt.getLatticeDescriptor();
		mGraphGen.setParameters(size, subConf.getMinSubLatticesCount(), desc);
		
		return mGraphGen.generateModelGraph();
	}

	public boolean readFilesFromInput() {
		
		// NB: when simDesc.getStartingIteration() == 0, subsolids are read from
		// DFS.
		return
			simDesc.isFirstOfSequence() &&
			configuration.getStartingIteration() == simDesc.getStartingIteration() &&
			configuration.getStartingIteration() > 0;
	}
}
