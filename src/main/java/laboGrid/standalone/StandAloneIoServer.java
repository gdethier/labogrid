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
package laboGrid.standalone;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import laboGrid.impl.central.controllerAgent.outputServer.CentralOutputGetFile;
import laboGrid.impl.central.controllerAgent.outputServer.OutputDownloadFinished;
import laboGrid.ioClients.controller.OutputFileReady;

import dimawo.agents.AgentException;
import dimawo.agents.LoggingAgent;
import dimawo.agents.UnknownAgentMessage;
import dimawo.fileTransfer.client.GetFileCallBack;
import dimawo.fileTransfer.client.FileTransferClientAgent;
import dimawo.fileTransfer.client.FileTransferClientCallBack;
import dimawo.fileTransfer.client.messages.FileTransferClientMessage;
import dimawo.fileTransfer.server.FileProvider;
import dimawo.fileTransfer.server.FileTransferServerAgent;
import dimawo.fileTransfer.server.messages.SimpleFTPServerMessage;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.distributedAgent.DAId;


public class StandAloneIoServer extends LoggingAgent implements FileProvider, FileTransferClientCallBack {
	
	private StandAloneDistributedAgent da;
	private CommunicatorInterface com;

	private FileTransferClientAgent client;
	private FileTransferServerAgent server;
	
	private class Key implements Comparable<Key> {
		String fileUID;
		DAId daId;

		public Key(String fileUID, DAId daId) {
			this.fileUID = fileUID;
			this.daId = daId;
		}

		@Override
		public int compareTo(Key o) {
			int c = fileUID.compareTo(o.fileUID);
			if(c == 0)
				c = daId.compareTo(o.daId);
			return c;
		}
	}
	private TreeMap<Key, Boolean> toCas;
	
	
	public StandAloneIoServer(StandAloneDistributedAgent da) {
		super(da, "StandAloneIoServer");
		this.da = da;
		
		this.setPrintStream(da.getFilePrefix());
		
		client = new FileTransferClientAgent(this, "SimpleFTPClient");
		client.setPrintStream(da.getFilePrefix());
		server = new FileTransferServerAgent(this, "SimpleFTPServer");
		server.setPrintStream(da.getFilePrefix());
		
		toCas = new TreeMap<Key, Boolean>();
	}

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
		try {
			client.stop();
		} catch (InterruptedException e) {
		} catch (AgentException e) {
		}
		try {
			server.stop();
		} catch (InterruptedException e) {
		} catch (AgentException e) {
		}
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");

		com = da.getCommunicator();
		client.setCommunicator(com);
		server.setCommunicator(com);
		
		server.setFileProvider(this);

		client.start();
		server.start();
	}

	@Override
	protected void handleMessage(Object o) throws Throwable {
		if(o instanceof FileTransferClientMessage) {
			client.submitClientMessage((FileTransferClientMessage) o);
		} else if(o instanceof SimpleFTPServerMessage) {
			server.submitServerMessage((SimpleFTPServerMessage) o);
		} else if(o instanceof OutputFileReady) {
			handleOutputFileReady((OutputFileReady) o);
		} else if(o instanceof GetFileCallBack) {
			handleGetFileCallBack((GetFileCallBack) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	private void handleOutputFileReady(OutputFileReady o) throws IOException {
		DAId daId = o.getSourceDaId();
		String fileName = o.getOutputFileName();
		boolean toCa = o.getToCa();

		File file = new File(fileName);
		File parent = file.getParentFile();
		if( ! parent.mkdirs() && ! parent.exists())
			throw new IOException("Could not create dir. arb. "+parent.getAbsolutePath());
		
		toCas.put(new Key(o.getFileID(), daId), toCa);
		client.getFile(new CentralOutputGetFile(daId, file, o.getFileID(), this, toCa));
	}

	public void submitStandAloneIoServerMessage(StandAloneIoServerMessage o) {
		try {
			submitMessage(o);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public File getFile(String fileUID) {
		throw new Error("unimplemented");
	}

	@Override
	public void submitFile(GetFileCallBack cb) {
		try {
			submitMessage(cb);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void handleGetFileCallBack(GetFileCallBack o) throws IOException {
		String fileUID = o.getFileUID();
		DAId serverDaId = o.getServerDaId();
		
		boolean toCa = toCas.remove(new Key(fileUID, serverDaId));
		if(toCa)
			com.sendDatagramMessage(new SaCaOutputDownloadFinished(serverDaId, fileUID));
		else
			com.sendDatagramMessage(new OutputDownloadFinished(serverDaId, fileUID));
	}

}
