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
package laboGrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import laboGrid.clusterBench.ClusterBenchmarkCtrl;
import laboGrid.clusterBench.ClusterBenchmarkTask;
import laboGrid.configuration.LaBoGridConfiguration;

import dimawo.DiMaWoException;
import dimawo.MasterAgent;
import dimawo.MasterWorkerFactory;
import dimawo.WorkerAgent;
import dimawo.middleware.distributedAgent.DistributedAgent;


public class ClusterBenchmarkFactory implements MasterWorkerFactory {
	
	private LaBoGridConfiguration conf;
	private int nDAs;
	private String outputFileName;
	
	@Override
	public void setParameters(String[] factArgs) throws DiMaWoException {
		if(factArgs.length != 3)
			throw new DiMaWoException("Wrong argument number: "+factArgs.length);
		try {
			conf = LaBoGridConfiguration.readXmlFile(factArgs[0]);
		} catch (Exception e) {
			throw new DiMaWoException("Could not read configuration file", e);
		}
		
		nDAs = Integer.parseInt(factArgs[1]);
		outputFileName = factArgs[2];
	}

	@Override
	public MasterAgent getMasterAgent(
			DistributedAgent da) throws DiMaWoException {
		try {
			return new ClusterBenchmarkCtrl(da, conf, nDAs, outputFileName);
		} catch (IOException e) {
			throw new DiMaWoException("Could not instantiate controller", e);
		}
	}

	@Override
	public WorkerAgent getWorkerAgent(DistributedAgent da)
			throws DiMaWoException {
		try {
			return new ClusterBenchmarkTask(da, conf);
		} catch (FileNotFoundException e) {
			throw new DiMaWoException("Could not instantiate task", e);
		}
	}

}
