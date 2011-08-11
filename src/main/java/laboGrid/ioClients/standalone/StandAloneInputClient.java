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
package laboGrid.ioClients.standalone;


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import laboGrid.ioClients.AbstractInputClient;
import laboGrid.ioClients.GetFile;
import laboGrid.ioClients.InputClientCallBack;
import laboGrid.ioClients.InputClientException;
import laboGrid.ioClients.InputClientMessage;
import laboGrid.ioClients.controller.CentralInputGetFile;
import laboGrid.standalone.StandAloneDistributedAgent;

import dimawo.agents.AgentException;
import dimawo.agents.UnknownAgentMessage;
import dimawo.fileTransfer.client.GetFileCallBack;
import dimawo.fileTransfer.client.FileTransferClientAgent;
import dimawo.fileTransfer.client.FileTransferClientCallBack;
import dimawo.fileTransfer.client.messages.FileTransferClientMessage;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;


public class StandAloneInputClient extends AbstractInputClient implements FileTransferClientCallBack {
	private FileTransferClientAgent client;
	
	private String path;
	private DAId saServerId;
	private boolean toCa;
	
	private class Entry {
		String fileUID;
		HashSet<InputClientCallBack> cbs;
		
		public Entry() {
			cbs = new HashSet<InputClientCallBack>();
		}
	}
	private HashMap<String, Entry> cbs;

	public StandAloneInputClient() {
		setAgentName("StandAloneInputClient");
		client = new FileTransferClientAgent(this, "StandAloneInputFTPClient");
		
		cbs = new HashMap<String, Entry>();
	}

	@Override
	public void setParameters(String[] params) throws InputClientException {
		if(params.length != 3) {
			throw new InputClientException("usage: <path> <sa server host name> <sa server port>");
		}
		
		path = params[0];
		String hostName = params[1];
		int port = Integer.parseInt(params[2]);
		saServerId = StandAloneDistributedAgent.getDaId(hostName, port);
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	protected void handleGetFile(GetFile o) throws Exception {
		InputClientCallBack cb = o.getCallBack();
		String fileUID = o.getFileUID();
		String fileName = getFileName(fileUID);
		if(queue(fileUID, fileName, cb)) {
			File dest = o.getFile();
			client.getFile(new CentralInputGetFile(saServerId, fileName, true, dest, this, toCa));
		}
	}

	private String getFileName(String fileUID) {
		if("".equals(path))
			return fileUID;
		else
			return path + "/" + fileUID;
	}

	@Override
	protected void handleInputClientMessage(InputClientMessage o)
			throws Exception {
		if(o instanceof FileTransferClientMessage) {
			client.submitClientMessage((FileTransferClientMessage) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	@Override
	protected void subClassHandleMessage(Object o) throws Exception {
		if(o instanceof GetFileCallBack) {
			handleGetFileCallBack((GetFileCallBack) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
		
		try {
			client.stop();
		} catch (InterruptedException e) {
		} catch (AgentException e) {
		}
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
		client.setCommunicator(this.getDistributedAgent().getCommunicator());
		client.start();
	}
	
	private void handleGetFileCallBack(GetFileCallBack o) {
		String fileUID = o.getFileUID();
		Entry e = dequeue(fileUID);
		if(e == null)
			throw new Error("No call back for file "+fileUID);

		if(o.isSuccessful()) {
			for(InputClientCallBack c : e.cbs)
				c.inputClientGetCB(e.fileUID, o.getFile(), null);
		} else {
			for(InputClientCallBack c : e.cbs)
				c.inputClientGetCB(e.fileUID, null, new InputClientException("Unable to download file: "+o.getError()));
		}
	}
	
	private boolean queue(String fileUID, String fileName, InputClientCallBack cb) {
		Entry e = cbs.get(fileName);
		if(e != null) {
			e.cbs.add(cb);
			return false;
		} else {
			e = new Entry();
			e.fileUID = fileUID;
			e.cbs.add(cb);
			cbs.put(fileName, e);
			return true;
		}
	}

	private Entry dequeue(String fileName) {
		return cbs.remove(fileName);
	}

	@Override
	public void submitFile(GetFileCallBack cb) {
		try {
			submitMessage(cb);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

	public void setToCA(boolean b) {
		toCa = b;
	}

	@Override
	public void setDistributedAgent(DistributedAgent da) {
		super.setDistributedAgent(da);
		client.setPrintStream(da.getFilePrefix());
	}
}
