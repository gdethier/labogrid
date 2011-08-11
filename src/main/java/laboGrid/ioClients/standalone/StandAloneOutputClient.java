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
import java.util.HashSet;
import java.util.TreeMap;

import laboGrid.impl.central.controllerAgent.outputServer.OutputDownloadFinished;
import laboGrid.ioClients.AbstractOutputClient;
import laboGrid.ioClients.OutputClientCB;
import laboGrid.ioClients.OutputClientCallBack;
import laboGrid.ioClients.OutputClientException;
import laboGrid.ioClients.PutFile;
import laboGrid.ioClients.controller.OutputClientMessage;
import laboGrid.ioClients.controller.OutputFileReady;
import laboGrid.standalone.SaCaOutputDownloadFinished;
import laboGrid.standalone.StandAloneDistributedAgent;

import dimawo.agents.AgentException;
import dimawo.agents.UnknownAgentMessage;
import dimawo.fileTransfer.server.FileProvider;
import dimawo.fileTransfer.server.FileTransferServerAgent;
import dimawo.fileTransfer.server.messages.SimpleFTPServerMessage;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;



public class StandAloneOutputClient extends AbstractOutputClient implements FileProvider {

	private DAId daId;
	private FileTransferServerAgent server;
	private TreeMap<String, File> readyFiles;
	private TreeMap<String, HashSet<OutputClientCallBack>> cbs;
	private String path;
	
	private CommunicatorInterface com;
	private DAId saServerId;
	private boolean toCa;

	public StandAloneOutputClient() {
		setAgentName("StandAloneOutputClient");

		server = new FileTransferServerAgent(this, "StandAloneOutputClientFTPServer");
		server.setFileProvider(this);

		readyFiles = new TreeMap<String, File>();
		cbs = new TreeMap<String, HashSet<OutputClientCallBack>>();
	}
	
	@Override
	public void setDistributedAgent(DistributedAgent da) {
		super.setDistributedAgent(da);
		
		this.daId = da.getDaId();
		this.com = da.getCommunicator();
		
		server.setCommunicator(com);
		server.setPrintStream(da.getFilePrefix());
	}

	@Override
	public void setParameters(String[] parameters) throws OutputClientException {
		if(parameters.length != 4)
			throw new OutputClientException("usage: <chunk size> <path> <sa host name> <sa port>");

		int chunkSize = Integer.parseInt(parameters[0]);
		server.setChunkSize(chunkSize);
		this.path = parameters[1];
		String hostName = parameters[2];
		int port = Integer.parseInt(parameters[3]);
		saServerId = StandAloneDistributedAgent.getDaId(hostName, port);
	}

	@Override
	protected void subClassHandleMessage(Object o) throws Exception {
		throw new UnknownAgentMessage(o);
	}

	@Override
	protected void handleOutputClientMessage(OutputClientMessage o)
			throws Exception {
		if(o instanceof SimpleFTPServerMessage) {
			server.submitServerMessage((SimpleFTPServerMessage) o);
		} else if(o instanceof OutputDownloadFinished) {
			handleOutputDownloadFinished((OutputDownloadFinished) o);
		} else if(o instanceof SaCaOutputDownloadFinished) {
			handleSaCaOutputDownloadFinished((SaCaOutputDownloadFinished) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	private void handleSaCaOutputDownloadFinished(SaCaOutputDownloadFinished o) {
		String fileUID = o.getFileUID();
		downloadFinished(fileUID);
	}

	@Override
	protected void handlePutFile(PutFile o) throws Exception {
		String fileID = o.getDestinationFileUID();
		File src = o.getSourceFile();
		OutputClientCallBack cb = o.getCallBack();
		
		synchronized(readyFiles) {
			readyFiles.put(fileID, src);
		}
		
		if(queue(fileID, cb)) {
			agentPrintMessage("Notifying ready file "+fileID);
			OutputFileReady ready = new OutputFileReady(daId, fileID, path, toCa);
			ready.setRecipient(saServerId);
			com.sendDatagramMessage(ready);
		}
	}

	private boolean queue(String fileID, OutputClientCallBack cb) {
		HashSet<OutputClientCallBack> set = cbs.get(fileID);
		if(set != null) {
			set.add(cb);
			return false;
		} else {
			set = new HashSet<OutputClientCallBack>();
			set.add(cb);
			cbs.put(fileID, set);
			return true;
		}
	}
	
	private HashSet<OutputClientCallBack> dequeue(String fileID) {
		return cbs.remove(fileID);
	}

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
		
		try {
			server.stop();
		} catch (InterruptedException e) {
		} catch (AgentException e) {
		}
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
		
		server.start();
	}
	
	private void handleOutputDownloadFinished(OutputDownloadFinished o) {
		String fileUID = o.getFileUID();
		downloadFinished(fileUID);
	}
	
	private void downloadFinished(String fileUID) {
		agentPrintMessage("File "+fileUID+" uploaded.");
		HashSet<OutputClientCallBack> cb = dequeue(fileUID);
		if(cb == null)
			throw new Error("No CB for file "+fileUID);
		
		for(OutputClientCallBack c : cb)
			c.outputClientPutCB(new OutputClientCB(fileUID, null));
	}

	@Override
	public File getFile(String fileUID) {
		synchronized(readyFiles) {
			return readyFiles.get(fileUID);
		}
	}
	
	public static String getOutputFileName(String path, String fileID) {
		if("".equals(path))
			return fileID;
		else
			return path +"/" + fileID;
	}
	
	public void setToCA(boolean b) {
		toCa = b;
	}
}
