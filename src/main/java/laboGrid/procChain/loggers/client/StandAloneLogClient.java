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
package laboGrid.procChain.loggers.client;

import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;
import laboGrid.procChain.loggers.Log;
import laboGrid.standalone.StandAloneDistributedAgent;

public class StandAloneLogClient extends LogClient {
	private CommunicatorInterface com;
	private DAId saDaId;

	public StandAloneLogClient() {
	}

	@Override
	public void setParameters(String[] parameters) throws LogClientException {
		if(parameters.length != 2)
			throw new LogClientException("Wrong number of arguments. Usage: <saHost> <saPort>");

		String saHost = parameters[0];
		int saPort;
		try {
			saPort = Integer.parseInt(parameters[1]);
		} catch (NumberFormatException e) {
			throw new LogClientException("Port is not an integer. Usage: <saHost> <saPort>");
		}
		
		saDaId = StandAloneDistributedAgent.getDaId(saHost, saPort);
	}

	@Override
	protected void putIdentifiedLog(Log log) {
		com.sendDatagramMessage(new LoggerMessage(saDaId, log));
	}

	@Override
	public void close() {
		// Nothing to do.
	}

	@Override
	public void setCommunicator(CommunicatorInterface com) {
		this.com = com;
	}

}
