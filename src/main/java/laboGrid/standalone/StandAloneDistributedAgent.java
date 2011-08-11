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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import laboGrid.procChain.loggers.client.LoggerMessage;

import dimawo.agents.AgentException;
import dimawo.agents.LoggingAgent;
import dimawo.middleware.communication.Communicator;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.communication.Message;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.distributedAgent.DistributedAgentInterface;
import dimawo.middleware.distributedAgent.logging.NetworkLoggerMessage;
import dimawo.middleware.fileSystem.FileSystemAgent;
import dimawo.simulation.socket.SocketFactory;



public class StandAloneDistributedAgent extends LoggingAgent implements
		DistributedAgentInterface {
	private DAId id;
	private Communicator com;
	private StandAloneIoServer server;
	private LBLogsHandler logsHandler;
	
	private PrintStream ps;

	public StandAloneDistributedAgent(String hostName, int port,
			String filePrefix,
			SocketFactory sockFact) throws IOException {
		super(null, "StandAlongDistributedAgent");
		
		this.id = getDaId(hostName, port);
		this.setPrintStream(filePrefix+"/");
		
		com = new Communicator(null, this, sockFact);
		
		server = new StandAloneIoServer(this);
		
		File pathFile = new File(filePrefix).getAbsoluteFile();
		File dirFile;
		if(pathFile.isDirectory())
			dirFile = pathFile;
		else
			dirFile = pathFile.getParentFile();
		if(! dirFile.exists()) {
			dirFile.mkdirs();
		}
		logsHandler = new LBLogsHandler(dirFile.getAbsolutePath());
		
		ps = new PrintStream(filePrefix+"logs.csv");
	}
	
	@Override
	public CommunicatorInterface getCommunicator() {
		return com;
	}

	@Override
	public int getTcpPort() {
		return id.getPort();
	}

	@Override
	public DAId getDaId() {
		return id;
	}

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
		try {
			server.stop();
			server.join();
		} catch (InterruptedException e) {
		} catch (AgentException e) {
		}
		
		try {
			com.stop();
			com.join();
		} catch (InterruptedException e) {
		}
		
		ps.close();
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
		com.start();
		server.start();
	}

	@Override
	protected void handleMessage(Object o) throws Throwable {
		if(o instanceof StandAloneIoServerMessage) {
			server.submitStandAloneIoServerMessage((StandAloneIoServerMessage) o);
		} else if(o instanceof NetworkLoggerMessage) {
			handleNetworkLoggerMessage((NetworkLoggerMessage) o);
		} else if(o instanceof LoggerMessage) {
			handleLoggerMessage((LoggerMessage) o);
		} else {
			agentPrintMessage("Ignored unknown message: "+o.getClass().getName());
		}
	}

	private void handleLoggerMessage(LoggerMessage o) throws FileNotFoundException {
		logsHandler.handle(o.getLog());
	}

	private void handleNetworkLoggerMessage(NetworkLoggerMessage o) {
		long now = System.currentTimeMillis();
		String id = o.getId();
		String msg = o.getMessage();
		ps.println(now+";"+id+";"+msg);
	}

	@Override
	public void submitIncomingMessage(Message msg) throws InterruptedException {
		submitMessage(msg);
	}

	public static DAId getDaId(String hostName, int port) {
		return new DAId(hostName, port, 0);
	}

	public static void main(String[] args) throws Exception {
		if(args.length != 3) {
			System.out.println("Usage: <host name> <port> <work. dir.>");
			System.exit(-1);
		}

		String hostName = args[0];
		int port = Integer.parseInt(args[1]);
		String filePrefix = args[2];

		StandAloneDistributedAgent da = new StandAloneDistributedAgent(
				hostName, port, filePrefix, new SocketFactory());
		
		System.out.println("Starting stand alone IO server...");
		da.start();
		da.join();
	}

	@Override
	public FileSystemAgent getFileSystemPeer() {
		return null;
	}

	@Override
	public void log(String id, String msg) {
		throw new Error("unimplemented");
	}
}
