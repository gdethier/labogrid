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
import laboGrid.procChain.loggers.Log;

public abstract class LogClient {

	private String clientId, loggerId;

	public void setClientId(String id) {
		this.clientId = id;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public void setLoggerId(String loggerId) {
		this.loggerId = loggerId;
	}

	public String getLoggerId() {
		return loggerId;
	}

	public void putLog(Log log) {
		log.setClientId(clientId);
		log.setLoggerId(loggerId);
		putIdentifiedLog(log);
	}

	public abstract void setCommunicator(CommunicatorInterface com);
	public abstract void setParameters(String[] parameters) throws LogClientException;
	protected abstract void putIdentifiedLog(Log log);
	public abstract void close();
}
