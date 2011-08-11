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

import laboGrid.impl.central.controllerAgent.inputServer.CentralInputMessageFactory;
import laboGrid.impl.central.controllerAgent.inputServer.messages.CentralInputServerMessage;
import laboGrid.ioClients.standalone.messages.SaCaInputMessageFactory;
import laboGrid.standalone.StandAloneIoServerMessage;

import dimawo.fileTransfer.FileTransferMessageFactory;
import dimawo.fileTransfer.client.FileTransferClientCallBack;
import dimawo.fileTransfer.client.events.GetFile;
import dimawo.middleware.distributedAgent.DAId;



public class CentralInputGetFile extends CentralInputServerMessage implements
		GetFile, StandAloneIoServerMessage {

	private static final long serialVersionUID = 1L;

	private DAId ctrlId;
	private String fileUID;
	private boolean isFileName;
	private File dest;
	private FileTransferClientCallBack cb;
	private boolean toCa;

	public CentralInputGetFile(DAId ctrlId, String fileUID, boolean isFileName, File dest, FileTransferClientCallBack cb,
			boolean toCa) {
		this.ctrlId = ctrlId;
		this.fileUID = fileUID;
		this.isFileName = isFileName;
		this.dest = dest;
		this.cb = cb;
		this.toCa = toCa;
	}
	
	public CentralInputGetFile(DAId ctrlId, String fileUID, File dest, FileTransferClientCallBack cb) {
		this.ctrlId = ctrlId;
		this.fileUID = fileUID;
		this.dest = dest;
		this.cb = cb;
	}

	@Override
	public DAId getServerDaId() {
		return ctrlId;
	}

	@Override
	public File getDestFile() {
		return dest;
	}

	@Override
	public String getFileUID() {
		return fileUID;
	}

	@Override
	public FileTransferMessageFactory getMessageFactory() {
		if(toCa)
			return SaCaInputMessageFactory.getInstance();
		else
			return CentralInputMessageFactory.getInstance();
	}

	@Override
	public FileTransferClientCallBack getGetFileCallBack() {
		return cb;
	}

	@Override
	public boolean isFileName() {
		return isFileName;
	}

}
