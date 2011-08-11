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
package laboGrid.ioClients.controller.messages;

import laboGrid.impl.central.controllerAgent.inputServer.CentralInputMessageFactory;
import laboGrid.impl.central.controllerAgent.inputServer.messages.CentralInputServerMessage;
import laboGrid.standalone.StandAloneIoServerMessage;
import dimawo.fileTransfer.FileTransferMessageFactory;
import dimawo.fileTransfer.server.messages.GetFileRequest;
import dimawo.middleware.distributedAgent.DAId;

public class CentralInputGetFileRequest extends CentralInputServerMessage
		implements GetFileRequest, StandAloneIoServerMessage {

	private static final long serialVersionUID = 1L;

	private String fileUID;
	private boolean isFileName;

	public CentralInputGetFileRequest(String fileUID, boolean isFileName) {
		this.fileUID = fileUID;
		this.isFileName = isFileName;
	}

	@Override
	public String getFileUID() {
		return fileUID;
	}

	@Override
	public FileTransferMessageFactory getMessageFactory() {
		return CentralInputMessageFactory.getInstance();
	}

	@Override
	public DAId getClientDaId() {
		return this.getSender();
	}

	@Override
	public boolean fileNameIsGiven() {
		return isFileName;
	}

}
