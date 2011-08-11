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
package laboGrid.impl.central.controllerAgent.outputServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import laboGrid.impl.central.controllerAgent.LBControllerAgent;
import laboGrid.ioClients.controller.OutputFileReady;
import laboGrid.ioClients.controller.OutputNextChunkQuery;

import dimawo.agents.AgentException;
import dimawo.agents.LoggingAgent;
import dimawo.agents.UncaughtThrowable;
import dimawo.agents.UnknownAgentMessage;
import dimawo.fileTransfer.client.GetFileCallBack;
import dimawo.fileTransfer.client.FileTransferClientAgent;
import dimawo.fileTransfer.client.FileTransferClientCallBack;
import dimawo.fileTransfer.client.messages.FileTransferClientMessage;
import dimawo.middleware.communication.Communicator;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;


public class CentralOutputServer extends LoggingAgent implements FileTransferClientCallBack {
	
	private LBControllerAgent ctrl;
	private DAId thisDaId;
	private CommunicatorInterface com;
	
	private FileTransferClientAgent client;

	public CentralOutputServer(LBControllerAgent ctrl) {
		super(ctrl, "CentralOutputServer");
		
		setPrintStream(ctrl.getHostingDA().getFilePrefix());
		
		this.ctrl = ctrl;
		thisDaId = ctrl.getHostingDA().getDaId();
		this.com = ctrl.getHostingDA().getCommunicator();
		
		client = new FileTransferClientAgent(this, "CentralOutputServerFTPClient");
		client.setCommunicator(com);
		client.setPrintStream(ctrl.getHostingDA().getFilePrefix());
		
//		downloading = false;
//		queue = new LinkedList<OutputFileReady>();
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
	protected void handleMessage(Object o) throws Throwable {
		
		if(o instanceof OutputFileReady) {
			handleOutputFileReady((OutputFileReady) o);
		} else if(o instanceof FileTransferClientMessage) {
			client.submitClientMessage((FileTransferClientMessage) o);
		} else if(o instanceof GetFileCallBack) {
			handleGetFileCallBack((GetFileCallBack) o);
		} else {
			throw new UnknownAgentMessage(o);
		}

	}

	private void handleGetFileCallBack(GetFileCallBack o) throws IOException {
		String fileUID = o.getFileUID();
		DAId serverDaId = o.getServerDaId();
		
		com.sendDatagramMessage(new OutputDownloadFinished(serverDaId, fileUID));
	}

//	private void handleOutputFileChunk(OutputFileChunk o) throws IOException {
//		byte[] data = o.getChunkData();
//		currentOutputStream.write(data);
//		
//		if(o.isLastChunk()) {
//			agentPrintMessage("Download for file "+currentFileID+" finished.");
//			currentOutputStream.close();
//			initNextDownload();
//		} else {
//			requestNextChunk();
//		}
//	}

//	private void initNextDownload() throws IOException {
//		if(queue.isEmpty()) {
//			downloading = false;
//			return;
//		}
//
//		OutputFileReady ofr = queue.removeLast();
//		initDownload(ofr);
//	}

//	private void initDownload(OutputFileReady ofr) throws IOException {
//		currentSourceDaId = ofr.getSourceDaId();
//		currentFileID = ofr.getFileID();
//		agentPrintMessage("Downloading ready file "+currentFileID);
//		
//		String fileName = ofr.getOutputFileName();
//		File file = new File(fileName);
//		File parent = file.getParentFile();
//		if( ! parent.mkdirs() && ! parent.exists())
//			throw new IOException("Could not create dir. arb. "+parent.getAbsolutePath());
//		
//		currentOutputStream = new FileOutputStream(fileName);
//		requestNextChunk();
//	}
	

	private void handleOutputFileReady(OutputFileReady o) throws IOException {
		
		DAId daId = o.getSourceDaId();
		String fileName = o.getOutputFileName();
		File file = new File(fileName);
		File parent = file.getParentFile();
		if( ! parent.mkdirs() && ! parent.exists())
			throw new IOException("Could not create dir. arb. "+parent.getAbsolutePath());
		
		client.getFile(new CentralOutputGetFile(daId, file, o.getFileID(), this));
		
//		if(downloading) {
//			agentPrintMessage("Queuing ready file "+o.getFileID());
//			queue.add(o);
//			return;
//		}
//		
//		downloading = true;
//		initDownload(o);
	}

//	private void requestNextChunk() throws IOException {
//		OutputNextChunkQuery msg = new OutputNextChunkQuery(currentSourceDaId);
//		if(currentSourceDaId == thisDaId)
//			ctrl.submitOutputClientMessage(msg);
//		else
//			com.sendDatagramMessage(msg);
//	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
		
		client.start();
	}

	public void submitCentralOutputServerMessage(CentralOutputServerMessage msg) {
		try {
			submitMessage(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void submitFile(GetFileCallBack cb) {
		try {
			submitMessage(cb);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
