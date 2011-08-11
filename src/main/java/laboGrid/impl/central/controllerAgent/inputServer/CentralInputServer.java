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
package laboGrid.impl.central.controllerAgent.inputServer;

import java.io.File;

import laboGrid.impl.central.controllerAgent.LBControllerAgent;
import laboGrid.impl.central.controllerAgent.experimenter.Experimenter;
import laboGrid.impl.central.controllerAgent.inputServer.messages.CentralInputServerMessage;

import dimawo.agents.AgentException;
import dimawo.agents.LoggingAgent;
import dimawo.agents.UnknownAgentMessage;
import dimawo.fileTransfer.server.FileProvider;
import dimawo.fileTransfer.server.FileTransferServerAgent;
import dimawo.fileTransfer.server.messages.SimpleFTPServerMessage;
import dimawo.middleware.communication.Communicator;
import dimawo.middleware.communication.CommunicatorInterface;


public class CentralInputServer extends LoggingAgent {
	
	private LBControllerAgent ctrl;
	private CommunicatorInterface com;
	
	private FileTransferServerAgent server;
	
	
	public CentralInputServer(LBControllerAgent ctrl) {
		super(ctrl, "CentralInputServer");
		
		setPrintStream(ctrl.getHostingDA().getFilePrefix());
		
		this.ctrl = ctrl;
		com = ctrl.getHostingDA().getCommunicator();
		
		server = new FileTransferServerAgent(this, "CentralInputServerFTPServer");
		server.setCommunicator(com);
		server.setPrintStream(ctrl.getHostingDA().getFilePrefix());
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
	protected void handleMessage(Object o) throws Throwable {
		if(o instanceof SimpleFTPServerMessage) {
			server.submitServerMessage((SimpleFTPServerMessage) o);
		} else {
			throw new UnknownAgentMessage(o);
		}
	}

	public void setFileProvider(FileProvider prov) {
		server.setFileProvider(prov);
	}

	public void submitCentralInputServerMessage(CentralInputServerMessage msg) {
		try {
			submitMessage(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
