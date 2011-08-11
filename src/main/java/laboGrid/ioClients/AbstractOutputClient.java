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
package laboGrid.ioClients;

import java.io.File;
import java.io.IOException;

import laboGrid.ioClients.controller.OutputClientMessage;

import dimawo.agents.LoggingAgent;
import dimawo.middleware.distributedAgent.DistributedAgent;



public abstract class AbstractOutputClient
extends LoggingAgent
implements OutputClient {
	
	private DistributedAgent da;
	
	
	public AbstractOutputClient() {
		super();
		
		this.setAgentName("AbstractOutputClient");
	}
	
	public DistributedAgent getDistributedAgent() {
		return da;
	}
	
	@Override
	public void setDistributedAgent(DistributedAgent da) {
		this.da = da;
		this.setErrorHandler(da);
		this.setPrintStream(da.getFilePrefix());
	}

	@Override
	protected void handleMessage(Object o) throws Throwable {
		if(o instanceof PutFile) {
			handlePutFile((PutFile) o);
		} else if(o instanceof OutputClientMessage) {
			handleOutputClientMessage((OutputClientMessage) o);
		} else {
			subClassHandleMessage(o);
		}
	}

	@Override
	public void submitOutputClientMessage(OutputClientMessage msg) {
		try {
			submitMessage(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void put(File src, String destFileUID, OutputClientCallBack cb)
			throws OutputClientException, IOException {
		if(src == null || destFileUID == null)
			throw new Error("Source file or UID is null");
		
		try {
			submitMessage(new PutFile(src, destFileUID, cb));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected abstract void subClassHandleMessage(Object o) throws Exception;
	protected abstract void handleOutputClientMessage(OutputClientMessage o) throws Exception;
	protected abstract void handlePutFile(PutFile o) throws Exception;

}
