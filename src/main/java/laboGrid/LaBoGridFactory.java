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

import laboGrid.configuration.LaBoGridConfiguration;
import laboGrid.impl.decentral.controller.DecentralLBController;
import laboGrid.impl.decentral.task.DecentralLBTask;
import laboGrid.lb.LBException;
import laboGrid.powerModel.ResourceDataBase;
import laboGrid.powerModel.ResourcePowerModel;

import org.xml.sax.SAXException;

import dimawo.DiMaWoException;
import dimawo.MasterAgent;
import dimawo.MasterWorkerFactory;
import dimawo.WorkerAgent;
import dimawo.middleware.distributedAgent.DistributedAgent;


public class LaBoGridFactory implements MasterWorkerFactory {

	private LaBoGridConfiguration conf;
	private ResourceDataBase resDb;
	
	
	public LaBoGridFactory() {
		resDb = new ResourceDataBase();
	}
	
	public LaBoGridFactory(String confFileName) throws IOException, SAXException, LBException {
		resDb = new ResourceDataBase();
		conf = LaBoGridConfiguration.readXmlFile(confFileName);
	}
	
	@Override
	public void setParameters(String[] factArgs) throws DiMaWoException {
		
		if(factArgs.length < 1 || factArgs.length > 2) {
			throw new DiMaWoException("Usage: <xml file> [<ccp text file>]");
		}
		
		try {
			conf = LaBoGridConfiguration.readXmlFile(factArgs[0]);
		} catch (Exception e) {
			throw new DiMaWoException("Could not read configuration file", e);
		}
		
		if(factArgs.length > 1) {
			try {
				resDb.readText(factArgs[1]);
			} catch (IOException e) {
				throw new DiMaWoException("Could not read CCP file", e);
			}
		}
	}

	@Override
	public MasterAgent getMasterAgent(DistributedAgent da) throws DiMaWoException {

		try {

			return new DecentralLBController(da, conf);

		} catch (Exception e) {

			throw new DiMaWoException("Could not create LBControllerAgent.", e);

		}

	}

	@Override
	public WorkerAgent getWorkerAgent(DistributedAgent da) throws DiMaWoException {
		
		String hostName = da.getHostName();
		ResourcePowerModel resPow = resDb.getResourcePowerModel(hostName);
		if(resPow == null) {
			resPow = new ResourcePowerModel(hostName);
		}

		try {

			return new DecentralLBTask(da, conf, resPow, 1);

		} catch (FileNotFoundException e) {
			
			throw new DiMaWoException(e);
				
		}

	}

}
