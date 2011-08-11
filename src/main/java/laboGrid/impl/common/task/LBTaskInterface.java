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
package laboGrid.impl.common.task;

import laboGrid.impl.common.task.events.ConfigurationDone;
import laboGrid.impl.common.task.events.ReplicationDone;
import laboGrid.impl.common.task.events.SimulationFinished;
import dimawo.agents.ErrorHandler;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.distributedAgent.DistributedAgentInterface;

public interface LBTaskInterface extends ErrorHandler {
	public DistributedAgentInterface getDistributedAgent();

	public void printMessage(String string);
	public void printMessage(Throwable e);

	public void signalEndOfConfiguration(ConfigurationDone confDone);
	public void signalEndOfReplication(ReplicationDone repDone);
	public void signalSimulationFinished(SimulationFinished simDone);
}
