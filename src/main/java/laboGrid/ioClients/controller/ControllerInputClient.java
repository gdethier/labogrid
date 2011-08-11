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
package laboGrid.ioClients.controller;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import laboGrid.LocalFileCopy;
import laboGrid.ioClients.AbstractInputClient;
import laboGrid.ioClients.GetFile;
import laboGrid.ioClients.InputClientCallBack;
import laboGrid.ioClients.InputClientException;
import laboGrid.ioClients.InputClientMessage;

import dimawo.agents.AgentException;
import dimawo.agents.UnknownAgentMessage;
import dimawo.fileTransfer.client.GetFileCallBack;
import dimawo.fileTransfer.client.FileTransferClientAgent;
import dimawo.fileTransfer.client.FileTransferClientCallBack;
import dimawo.fileTransfer.client.messages.FileTransferClientMessage;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;
import dimawo.middleware.overlay.impl.central.CentralOverlay;




public class ControllerInputClient extends AbstractInputClient implements FileTransferClientCallBack {
	
	private boolean local;
	
	private FileTransferClientAgent client;
	private String path;
	private DAId ctrlId;
	
	private TreeMap<String, InputClientCallBack> cbs;

	public ControllerInputClient() {
		setAgentName("ControllerInputClient");

		client = new FileTransferClientAgent(this, "ControllerInputClientFTPClient");
		cbs = new TreeMap<String, InputClientCallBack>();
	}

	@Override
	public void setParameters(String[] parameters) throws InputClientException {
		path = parameters[0];
	}

	@Override
	protected void handleGetFile(GetFile o) throws Exception {
		if(local) {
			getFileLocal(o);
			return;
		}

		InputClientCallBack cb = o.getCallBack();
		String fileUID = o.getFileUID();
		if(cbs.put(fileUID, cb) != null)
			throw new Error("Callback already registered for file "+fileUID);
		
		File dest = o.getFile();
		client.getFile(new CentralInputGetFile(ctrlId, fileUID, dest, this));
	}

	private void getFileLocal(GetFile o) throws IOException {
		String fileUID = o.getFileUID();
		agentPrintMessage("File "+fileUID+" read locally.");
		File dest = o.getFile();
		String fileName = getInputFileName(path, fileUID);
		File src = new File(fileName);

		LocalFileCopy.copyFile(src, dest);
		
		InputClientCallBack cb = o.getCallBack();
		cb.inputClientGetCB(fileUID, dest, null);
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
			e.printStackTrace();
		} catch (AgentException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
		
		client.start();
	}

	@Override
	public void submitFile(GetFileCallBack cb) {
		try {
			submitMessage(cb);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void handleGetFileCallBack(GetFileCallBack o) {
		String fileUID = o.getFileUID();
		InputClientCallBack cb = cbs.remove(fileUID);
		if(cb == null)
			throw new Error("No call back for file "+fileUID);

		if(o.isSuccessful()) {
			cb.inputClientGetCB(fileUID, o.getFile(), null);
		} else {
			cb.inputClientGetCB(fileUID, null, new InputClientException("Unable to download file"));
		}
	}

	@Override
	public String getPath() {
		return path;
	}
	
	public static String getInputFileName(String path, String fileID) {
		if("".equals(path))
			return fileID;
		else
			return path +"/" + fileID;
	}

	public void setTurnToLocal(boolean b) {
		this.local = b;
	}
	
	@Override
	public void setDistributedAgent(DistributedAgent da) {
		super.setDistributedAgent(da);
		
		client.setCommunicator(da.getCommunicator());
		client.setPrintStream(da.getFilePrefix());
		
		CentralOverlay co = (CentralOverlay) da.getOverlayInterface();
		ctrlId = co.getLeaderId();
	}
}
