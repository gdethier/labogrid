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
package laboGrid.procChain.loggers;

import laboGrid.configuration.processingChain.LoggerDescription;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.procChain.ProcessingChainElement;
import laboGrid.procChain.loggers.client.LogClient;
import laboGrid.procChain.loggers.client.LogClientException;

public abstract class LBLogger extends ProcessingChainElement {
	
	private LoggerDescription desc;
	
	private LogClient client;
	private int subId;
	private int simNum;

	public LBLogger() {}

	protected LBLogger(LBLogger other) {
		
		this.desc = other.desc;

	}


	////////////////////
	// Public methods //
	////////////////////
	
	public int getRefreshRate() {
		return desc.getRefreshRate();
	}
	
	public int getSubLatticeId() {
		
		return subId;
		
	}
	
	
	public int getSimulationNumber() {
	
		return simNum;
		
	}
	
	
	public void setLoggingParameters(LoggerDescription desc) throws LBException {
		
		this.desc = desc;

		setParameters(desc.getLoggerParameters());

	}

	public void setLBAlgorithm(LBSimThread alg) throws LBException {

		super.setLBAlgorithm(alg);

		try {

			client = desc.getClient(getClientId(alg), alg.getCommunicator());

		} catch (LogClientException e) {

			throw new LBException("Could not create log client.", e);

		}

		this.subId = alg.getSubLattice().getId();
		this.simNum = alg.getSimulationNumber();

	}

	@Override
	public void apply() throws InterruptedException {

		int iteration = alg.getCurrentIteration();

		if(iteration%desc.getRefreshRate() == 0) {

			Log l = getLog(alg.getCurrentIteration());
			if(l != null) {

				client.putLog(l);

			}

		}

	}
	
	public String getId() {
		return desc.getLogId();
	}
	
	
	/////////////////////
	// Private methods //
	/////////////////////

	private String getClientId(LBSimThread alg) {

		int subId = alg.getSubLattice().getId();
		int simNum = alg.getSimulationNumber();

		return "sim"+simNum+"_sub"+subId+"_"+desc.getLogId();

	}
	
	
	//////////////////////
	// Abstract methods //
	//////////////////////

	/**
	 * Called when a log must be done.
	 * @return The log.
	 */
	protected abstract Log getLog(int iteration);
	
	/**
	 * Called to parse logger parameters.
	 * 
	 * @throws LBException 
	 */
	protected abstract void setParameters(String[] params) throws LBException;

	/**
	 * Called to close logger client.
	 */
	public void close() {
		client.close();
	}

}
