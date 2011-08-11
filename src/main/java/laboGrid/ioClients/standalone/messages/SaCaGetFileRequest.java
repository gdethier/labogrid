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
package laboGrid.ioClients.standalone.messages;

import laboGrid.ioClients.controller.OutputClientMessage;
import laboGrid.standalone.StandAloneIoServerMessage;
import dimawo.fileTransfer.FileTransferMessageFactory;
import dimawo.fileTransfer.server.messages.GetFileRequest;
import dimawo.master.messages.MasterMessage;
import dimawo.middleware.distributedAgent.DAId;

public class SaCaGetFileRequest extends MasterMessage implements
		GetFileRequest, StandAloneIoServerMessage, OutputClientMessage {
	private String fileUID;
	private boolean isFileName;

	public SaCaGetFileRequest(String fileUID, boolean isFileName) {
		this.fileUID = fileUID;
		this.isFileName = isFileName;
	}

	@Override
	public DAId getClientDaId() {
		return getSender();
	}

	@Override
	public String getFileUID() {
		return fileUID;
	}

	@Override
	public FileTransferMessageFactory getMessageFactory() {
		return SaCaInputMessageFactory.getInstance();
	}

	@Override
	public boolean fileNameIsGiven() {
		return isFileName;
	}

}