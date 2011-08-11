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

import laboGrid.standalone.StandAloneDistributedAgent;
import dimawo.agents.AgentException;
import dimawo.simulation.GenericSimulatedLauncher;
import dimawo.simulation.socket.SocketFactory;

public class SALauncher extends GenericSimulatedLauncher {
	private int port;
	private String workDir;
	
	private StandAloneDistributedAgent da;
	
	public void setSaDaPort(int port) {
		this.port = port;
	}
	
	public void setSaDaWorkDir(String workDir) {
		this.workDir = workDir;
	}
	
	@Override
	public void main() throws Throwable {
		try {
			da =
				new StandAloneDistributedAgent(access.getHostName(), port,
					workDir,
					new SocketFactory(access));
			
			System.out.println("Running stand-alone DA");
			da.start();
			da.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void kill() {
		access.close();
		try {
			da.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (AgentException e) {
			e.printStackTrace();
		}
	}

}
