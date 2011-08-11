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

import laboGrid.impl.central.controllerAgent.inputServer.messages.CentralInputChunkMessage;
import laboGrid.impl.central.controllerAgent.inputServer.messages.CentralInputErrorMessage;
import laboGrid.ioClients.controller.messages.CentralInputGetFileRequest;
import laboGrid.ioClients.controller.messages.CentralInputGetNextChunkMessage;
import dimawo.fileTransfer.FileTransferMessageFactory;
import dimawo.fileTransfer.client.messages.ChunkMessage;
import dimawo.fileTransfer.client.messages.ErrorMessage;
import dimawo.fileTransfer.client.messages.PingClientMessage;
import dimawo.fileTransfer.server.messages.GetFileRequest;
import dimawo.fileTransfer.server.messages.GetNextChunkRequest;
import dimawo.fileTransfer.server.messages.PingServerMessage;

public class CentralInputMessageFactory implements FileTransferMessageFactory {
	
	private static CentralInputMessageFactory singleton = new CentralInputMessageFactory();
	
	private CentralInputMessageFactory() {
	}

	@Override
	public GetFileRequest newGetFileRequest(String fileUID, boolean isFileName) {
		return new CentralInputGetFileRequest(fileUID, isFileName);
	}

	@Override
	public GetNextChunkRequest newGetNextChunkMessage(String fileUID) {
		return new CentralInputGetNextChunkMessage();
	}

	@Override
	public ChunkMessage newChunkMessage(String fileUID, byte[] data,
			boolean isLast) {
		return new CentralInputChunkMessage(fileUID, data, isLast);
	}

	@Override
	public ErrorMessage newErrorMessage(String string, String fileUID) {
		return new CentralInputErrorMessage(string, fileUID);
	}

	public static FileTransferMessageFactory getInstance() {
		return singleton;
	}

	@Override
	public PingClientMessage newPingClientMessage() {
		return new CentralInputPingClientMessage();
	}

	@Override
	public PingServerMessage newPingServerMessage() {
		return new CentralInputPingServerMessage();
	}

}
