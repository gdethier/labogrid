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
package laboGrid.simulator;

import laboGrid.LaBoGridFactory;
import dimawo.exec.GenericSimulatedBootstrapPeerLauncher;
import dimawo.simulation.DeploymentDescription;
import dimawo.simulation.ExecCommand;
import dimawo.simulation.GenericSimulatedPeerLauncher;
import dimawo.simulation.HostDescription;
import dimawo.simulation.Simulation;
import dimawo.simulation.cluster.ClusterDescription;
import dimawo.simulation.middleware.MiddlewareDescription;
import dimawo.simulation.middleware.VirtualTaskDescription;


public class EclipseSimLauncher {
	private static final int nResources = 10;
	private static final int defaultPort = 50200;
	private static final String taskFactoryClassName =
		LaBoGridFactory.class.getCanonicalName();
	private static final String[] taskFactoryParameters =
		{"src/main/sim-test/conf.xml", "src/main/sim-test/clusterDesc.txt"};
	private static String baseWorkDir;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.out.println("Usage: <workDir>");
			System.exit(-1);
		}
		
		baseWorkDir = args[0];
		
		ClusterDescription clusterDesc = initClusterDesc();
		MiddlewareDescription middleDesc = initMiddlewareDesc();
		DeploymentDescription deployDesc = initDeployDesc();

		Simulation sim = new Simulation(clusterDesc, middleDesc);
		sim.runSimulation(deployDesc);
	}
	
	private static MiddlewareDescription initMiddlewareDesc() {
		MiddlewareDescription desc = new MiddlewareDescription();
		
		// No host managed by middleware
		
		return desc;
	}

	private static ClusterDescription initClusterDesc() throws Exception {
		ClusterDescription desc = new ClusterDescription();
		
		for(int i = 0; i < nResources; ++i) {
			desc.addHostDescription(new HostDescription("Resource"+i));
		}
		
		return desc;
	}

	private static DeploymentDescription initDeployDesc() {
		DeploymentDescription desc = new DeploymentDescription();

		// Stand Alone DA
		SALauncher saLauncher = new SALauncher();
		saLauncher.setSaDaPort(defaultPort);
		saLauncher.setSaDaWorkDir(baseWorkDir+"sa/");
		
		VirtualTaskDescription saDesc = new VirtualTaskDescription("sa", saLauncher);
		desc.addProcess(new ExecCommand("Resource0", saDesc));

		// Bootstrap DA
		GenericSimulatedBootstrapPeerLauncher bootLauncher =
			new GenericSimulatedBootstrapPeerLauncher();
		bootLauncher.setMasterWorkerFactoryClassName(taskFactoryClassName);
		bootLauncher.setDaPort(50200);
		bootLauncher.setDaWorkingDirectory(baseWorkDir+"boot/");
		bootLauncher.setDaLogServerHostNameAndPort("Resource0", defaultPort);
		bootLauncher.setMasterWorkerFactoryArguments(taskFactoryParameters);

		VirtualTaskDescription bootDesc =
			new VirtualTaskDescription("bootstrap", bootLauncher);
		desc.addProcess(new ExecCommand("Resource1", bootDesc));
		
		for(int i = 2; i < nResources; ++i) {
			// Normal peers
			GenericSimulatedPeerLauncher peerLauncher =
				new GenericSimulatedPeerLauncher();
			peerLauncher.setMasterWorkerFactoryClassName(taskFactoryClassName);
			peerLauncher.setDaPort(50200);
			peerLauncher.setDaWorkingDirectory(baseWorkDir+"/"+"Resource"+i);
			peerLauncher.setDaLogServerHostNameAndPort("Resource0", defaultPort);
			peerLauncher.setCtrlHostNameAndPort("Resource1", defaultPort);
			peerLauncher.setMasterWorkerFactoryArguments(taskFactoryParameters);

			VirtualTaskDescription peerDesc =
				new VirtualTaskDescription("normal da"+(i-2), peerLauncher);
			desc.addProcess(new ExecCommand("Resource"+i, peerDesc));
		}

		return desc;
	}

}
