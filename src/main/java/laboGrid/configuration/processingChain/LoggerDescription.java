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
package laboGrid.configuration.processingChain;


import laboGrid.procChain.loggers.client.LogClient;
import laboGrid.procChain.loggers.client.LogClientException;

import org.w3c.dom.Element;

import dimawo.Reflection;
import dimawo.middleware.communication.CommunicatorInterface;

public class LoggerDescription extends ProcessingChainElementDescription {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private int rate;
	
	private String loggerClass;
	private String[] loggerParameters;

	private String clientClass;
	private String[] clientParameters;

	public LoggerDescription(
			String id,
			int rate,
			String loggerClass,
			String[] loggerParameters,
			String clientClass,
			String[] clientParameters) {

		this.id = id;
		this.rate = rate;

		this.loggerClass = loggerClass;
		this.loggerParameters = loggerParameters;

		this.clientClass = clientClass;
		this.clientParameters = clientParameters;

	}

	public static ProcessingChainElementDescription newInstance(Element e) {

		String id = e.getAttribute("id");
		int rate = Integer.parseInt(e.getAttribute("rate"));

		String loggerClass = e.getAttribute("loggerClass");
		String loggerParameters = e.getAttribute("loggerParameters");

		String clientClass = e.getAttribute("clientClass");
		String clientParameters = e.getAttribute("clientParameters");

		return new LoggerDescription(id, rate, loggerClass, loggerParameters.split("[ \t\n]+"), clientClass, clientParameters.split("[ \t\n]+"));

	}

	public String getLogId() {
		return id;
	}
	
	public int getRefreshRate() {
		return rate;
	}

	public String getLoggerClass() {
		return loggerClass;
	}

	public String[] getLoggerParameters() {
		return loggerParameters;
	}
	
	public String getClientClass() {

		return clientClass;

	}

	public String[] getClientParameters() {

		return clientParameters;

	}

	@Override
	public String getClassName() {
		return loggerClass;
	}

	public LogClient getClient(String clientId, CommunicatorInterface com) throws LogClientException {
		LogClient in;
		try {
			in = (LogClient) Reflection.newInstance(clientClass);
			in.setLoggerId(id);
			in.setClientId(clientId);
			in.setCommunicator(com);
			in.setParameters(clientParameters);
		} catch (Exception e) {
			throw new LogClientException("Could not instantiate log client", e);
		}
		return in;
	}

}
