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

import java.io.File;
import java.io.IOException;

import laboGrid.configuration.LaBoGridConfiguration;
import laboGrid.powerModel.ResourceDataBase;
import laboGrid.powerModel.ResourcePowerModel;

import dimawo.MasterAgent;
import dimawo.master.messages.MasterMessage;
import dimawo.master.messages.WorkerExitMessage;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;



public class ClusterBenchmarkCtrl extends MasterAgent {
	
	private int nDAs;
	private File outputFile;
	
	private ResourceDataBase benchResults;


	public ClusterBenchmarkCtrl(DistributedAgent hostingDa,
			LaBoGridConfiguration conf,
			int nDAs,
			String outputFileName)
			throws IOException {
		super(hostingDa, "ClusterBenchmarkCtrl");
		
		this.nDAs = nDAs;
		
		outputFile = new File(outputFileName);
		if(outputFile.isDirectory())
			throw new IOException("Output is a directory");
		File path = outputFile.getParentFile();
		if(path != null && ! path.mkdirs() && ! path.exists()) {
			throw new IOException("Could not create output directory");
		}
		
		benchResults = new ResourceDataBase();
		
	}

	@Override
	protected void handleMasterEvent(Object o) throws Exception {
		throw new Exception("Unexpected event: "+o.getClass().getName());
	}

	@Override
	protected void handleWorkerExit(WorkerExitMessage msg)
			throws InterruptedException, IOException {
		agentPrintMessage("Result from task "+msg.getSourceDaId());
		Object res = msg.getResult();
		ResourcePowerModel benchRes = (ResourcePowerModel) res;
		benchResults.addResourcePower(msg.getSourceDaId(), benchRes);
		
		benchResults.writeText(outputFile.getAbsolutePath());

		--nDAs;
		tryEndOfBenchmark();
	}

	@Override
	protected void handleUserDefinedAgentMessage(MasterMessage msg)
			throws Exception {
		throw new Exception("Unexpected message: "+msg.getClass().getName());
	}

	@Override
	protected void onExit() {
		agentPrintMessage("exit");
	}

	@Override
	protected void onStartup() throws Throwable {
		agentPrintMessage("startup");
	}
	
	private void tryEndOfBenchmark() throws IOException {
		if(nDAs == 0) {
			shutdown();
		}
	}

	@Override
	protected void onTopologyChange(DAId subject, ChangeType type)
			throws Exception {
		// Do nothing
	}
}
