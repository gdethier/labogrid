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
package laboGrid.clusterBench;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import laboGrid.configuration.LaBoGridConfiguration;
import laboGrid.configuration.experience.ExperienceDescription;
import laboGrid.configuration.experience.SimulationDescription;
import laboGrid.configuration.lbConfiguration.LBConfiguration;
import laboGrid.configuration.middleware.BenchmarkConfiguration;
import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.impl.common.benchmark.BenchmarkHost;
import laboGrid.impl.common.benchmark.BenchmarkParameters;
import laboGrid.impl.common.benchmark.LBBenchmark;
import laboGrid.impl.common.task.events.BenchmarkFinished;
import laboGrid.lb.LBException;
import laboGrid.powerModel.PowerModel;
import laboGrid.powerModel.ResourcePowerModel;

import dimawo.WorkerAgent;
import dimawo.WorkerMessage;
import dimawo.agents.AgentException;
import dimawo.agents.UncaughtThrowable;
import dimawo.agents.UnknownAgentMessage;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.overlay.mntree.MnPeerState;



public class ClusterBenchmarkTask extends WorkerAgent implements BenchmarkHost {
	
	private LaBoGridConfiguration conf;
	
	private LBBenchmark currentBench;
	private BenchmarkConfiguration benchConf;
	private ResourcePowerModel benchResults;
	private Iterator<SimulationDescription> simIt;

	public ClusterBenchmarkTask(DistributedAgent da,
			LaBoGridConfiguration conf)
			throws FileNotFoundException {
		super(da, "ClusterBenchmarkTask");

		this.conf = conf;
		
		benchConf = conf.getMiddlewareConfiguration().getLoadBalancingConfiguration().getBenchmarkConfiguration();

		ExperienceDescription expDesc = conf.getExperienceDescription();
		simIt = expDesc.simDescIterator();
		
		benchResults = new ResourcePowerModel(da.getHostName());

	}

	@Override
	protected void handleWorkerEvent(Object o) throws Exception {
		if(o instanceof BenchmarkFinished) {
			handleBenchmarkFinished();
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	@Override
	protected void handleWorkerMessage(WorkerMessage o) throws Exception {
		throw new Exception("Unexpected message: "+o.getClass().getName());
	}

	@Override
	protected Serializable preWorkerExit() {
		if(currentBench != null)
			currentBench.kill();
		return benchResults;
	}

	@Override
	protected void init() throws Throwable {
		launchNextBenchmark();
	}

	private void launchNextBenchmark() throws LBException {
		SimulationDescription simDesc = simIt.next();
		LBConfiguration lbConf =
			conf.getLBConfiguration(simDesc.getLBConfigurationId());
		ProcessingChainDescription procDesc =
			conf.getProcessingChain(simDesc.getProcessingChainId());
		
		BenchmarkParameters benchParams = new BenchmarkParameters(benchConf,
				lbConf.getLatticeDescription().getClassName(),
				lbConf.getSolidDescription().getClassName(),
				procDesc);
		
		String powerDesc = benchParams.getPowerDescriptor();
		while(benchResults.hasPower(powerDesc) &&
				simIt.hasNext()) {

			simDesc = simIt.next();
			lbConf =
				conf.getLBConfiguration(simDesc.getLBConfigurationId());
			procDesc =
				conf.getProcessingChain(simDesc.getProcessingChainId());
			
			benchParams = new BenchmarkParameters(benchConf,
					lbConf.getLatticeDescription().getClassName(),
					lbConf.getSolidDescription().getClassName(),
					procDesc);
			
			powerDesc = benchParams.getPowerDescriptor();
		}
		
		
		if(benchResults.hasPower(powerDesc)) {
			agentPrintMessage("All benchmarks done, stopping.");
			try {
				stop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (AgentException e) {
				e.printStackTrace();
			}
		} else {
			currentBench = new LBBenchmark(this, benchParams);
			currentBench.start();
		}
	}

	@Override
	public void printMessage(String string) {
		agentPrintMessage(string);
	}

	@Override
	public void signalBenchmarkFinished() throws InterruptedException {
		submitMessage(new BenchmarkFinished());
	}
	
	private void handleBenchmarkFinished() throws LBException {
		LinkedList<Throwable> errors = currentBench.getErrors();
		PowerModel power = currentBench.getPower();
		String desc = currentBench.getPowerDescriptor();
		
		if(errors.isEmpty()) {		
			benchResults.updatePower(desc, power);
		} else {
			agentPrintMessage(errors.getFirst());
		}
		
		try {
			currentBench.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(simIt.hasNext()) {
			launchNextBenchmark();
		} else {
			agentPrintMessage("All benchmarks done, stopping.");
			try {
				stop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (AgentException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onLocalTopologyChange(MnPeerState newState) throws Exception {
		// Do nothing
	}

}
