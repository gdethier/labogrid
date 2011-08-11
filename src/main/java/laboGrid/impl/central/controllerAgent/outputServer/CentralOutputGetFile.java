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

import laboGrid.ioClients.standalone.messages.SaCaInputMessageFactory;

import dimawo.fileTransfer.FileTransferMessageFactory;
import dimawo.fileTransfer.client.FileTransferClientCallBack;
import dimawo.fileTransfer.client.events.GetFile;
import dimawo.middleware.distributedAgent.DAId;



public class CentralOutputGetFile implements
		GetFile {
	
	private DAId serverDaId;
	private File dest;
	private String fileUID;
	private boolean isFileName;
	private FileTransferClientCallBack cb;
	private boolean toCa;
	
	public CentralOutputGetFile(DAId serverDaId, File dest, String fileUID, FileTransferClientCallBack cb) {
		this.serverDaId = serverDaId;
		this.dest = dest;
		this.fileUID = fileUID;
		this.cb = cb;
	}
	
	public CentralOutputGetFile(DAId serverDaId, File dest, String fileUID, FileTransferClientCallBack cb, boolean toCa) {
		this.serverDaId = serverDaId;
		this.dest = dest;
		this.fileUID = fileUID;
		this.cb = cb;
		this.toCa = toCa;
	}

	@Override
	public DAId getServerDaId() {
		return serverDaId;
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
	public FileTransferClientCallBack getGetFileCallBack() {
		return cb;
	}

	@Override
	public FileTransferMessageFactory getMessageFactory() {
		if(toCa)
			return SaCaInputMessageFactory.getInstance();
		else
			return CentralOutputMessageFactory.getInstance();
	}

	@Override
	public boolean isFileName() {
		return isFileName;
	}

}
