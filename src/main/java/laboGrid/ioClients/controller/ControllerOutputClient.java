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
import laboGrid.impl.central.controllerAgent.outputServer.OutputDownloadFinished;
import laboGrid.ioClients.AbstractOutputClient;
import laboGrid.ioClients.OutputClientCB;
import laboGrid.ioClients.OutputClientCallBack;
import laboGrid.ioClients.OutputClientException;
import laboGrid.ioClients.PutFile;

import dimawo.agents.AgentException;
import dimawo.agents.UnknownAgentMessage;
import dimawo.fileTransfer.server.FileProvider;
import dimawo.fileTransfer.server.FileTransferServerAgent;
import dimawo.fileTransfer.server.messages.SimpleFTPServerMessage;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgent;





public class ControllerOutputClient extends AbstractOutputClient implements FileProvider {
	
	private DAId daId;
	private boolean local; // true -> client running on controller
	
	private FileTransferServerAgent server;
	private TreeMap<String, File> readyFiles;
	private TreeMap<String, OutputClientCallBack> cbs;
	
//	private int chunkSize;
	private String path;
//	private LinkedList<PutFile> queue;
//	
//	private boolean uploading;
//	private PutFile currentUpload;
//	private FileInputStream currentInputStream;

	public ControllerOutputClient() {
		setAgentName("ControllerOutputClient");

		server = new FileTransferServerAgent(this, "ControllerOutputClientFTPServer");
		server.setFileProvider(this);

		readyFiles = new TreeMap<String, File>();
		cbs = new TreeMap<String, OutputClientCallBack>();
		
//		queue = new LinkedList<PutFile>();
//		
//		uploading = false;
	}
	
	@Override
	public void setDistributedAgent(DistributedAgent da) {
		super.setDistributedAgent(da);
		
		this.daId = da.getDaId();
		
		server.setCommunicator(da.getCommunicator());
		server.setPrintStream(da.getFilePrefix());
	}

	@Override
	protected void handleOutputClientMessage(OutputClientMessage o)
			throws Exception {
		if(o instanceof SimpleFTPServerMessage) {
			server.submitServerMessage((SimpleFTPServerMessage) o);
		} else if(o instanceof OutputDownloadFinished) {
			handleOutputDownloadFinished((OutputDownloadFinished) o);
		} else {
			throw new UnknownAgentMessage(o);
		}

	}

//	private void handleOutputNextChunkQuery(OutputNextChunkQuery o) throws IOException, InterruptedException {
//		int dataSize = Math.min(chunkSize, currentInputStream.available());
//		byte[] data = new byte[dataSize]; 
//		currentInputStream.read(data, 0, dataSize);
//		boolean lastChunk = dataSize < chunkSize;
//		
//		da.sendMessageToController(new OutputFileChunk(daId, data, lastChunk));
//		
//		if(lastChunk) {
//			currentInputStream.close();
//			currentUpload.signalEndOfUpload();
//			initNextUpload();
//		}
//	}

//	private void initNextUpload() throws FileNotFoundException, InterruptedException {
//		if(queue.isEmpty()) {
//			uploading = false;
//		} else {
//			PutFile pf = queue.removeLast();
//			
//			initUpload(pf);
//		}
//	}

//	private void initUpload(PutFile pf) throws FileNotFoundException, InterruptedException {
//		String fileID = pf.getDestinationFileUID();
//		
//		agentPrintMessage("Notifying ready file "+fileID);
//		
//		AbstractDistributedAgent da = getDistributedAgent();
//		da.sendMessageToController(
//				new OutputFileReady(daId, fileID, path));
//	}

	private void handleOutputDownloadFinished(OutputDownloadFinished o) {
		String fileUID = o.getFileUID();
		agentPrintMessage("File "+fileUID+" uploaded.");
		OutputClientCallBack cb = cbs.remove(fileUID);
		if(cb == null)
			throw new Error("No CB for file "+fileUID);
		
		cb.outputClientPutCB(new OutputClientCB(fileUID, null));
	}

	@Override
	protected void handlePutFile(PutFile o) throws Exception {
		if(local) {
			putFileLocal(o);
			return;
		}

		String fileID = o.getDestinationFileUID();
		File src = o.getSourceFile();
		OutputClientCallBack cb = o.getCallBack();
		
		synchronized(readyFiles) {
			readyFiles.put(fileID, src);
		}
		
		if(cbs.put(fileID, cb) != null)
			throw new Error("Callback already registered for file "+fileID);
		
		agentPrintMessage("Notifying ready file "+fileID);
		DistributedAgent da = getDistributedAgent();
		da.getOverlayInterface().getLeaderElectionInterface().sendMessageToLeader(
				new OutputFileReady(daId, fileID, path));
	}

	private void putFileLocal(PutFile o) throws IOException {
		String fileUID = o.getDestinationFileUID();
		agentPrintMessage("File "+fileUID+" written locally.");
		String fileName = getOutputFileName(path, fileUID);
		File src = o.getSourceFile();
		File dest = new File(fileName);

		LocalFileCopy.copyFile(src, dest);
		
		OutputClientCallBack cb = o.getCallBack();
		cb.outputClientPutCB(new OutputClientCB(fileUID, null));
	}

	@Override
	protected void subClassHandleMessage(Object o) throws Exception {
		throw new UnknownAgentMessage(o);
	}

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
		
		try {
			server.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (AgentException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
		
		server.start();
	}

	@Override
	public void setParameters(String[] parameters) throws OutputClientException {
		if(parameters.length != 2)
			throw new OutputClientException("Wrong arguments number: "+parameters.length);
		int chunkSize = Integer.parseInt(parameters[0]);
		server.setChunkSize(chunkSize);
		this.path = parameters[1];
	}

	@Override
	public File getFile(String fileUID) {
		synchronized(readyFiles) {
			return readyFiles.get(fileUID);
		}
	}

	public void setTurnToLocal(boolean on) {
		local = on;
	}
	
	public static String getOutputFileName(String path, String fileID) {
		if("".equals(path))
			return fileID;
		else
			return path +"/" + fileID;
	}

}
