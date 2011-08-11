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

import dimawo.fileTransfer.FileTransferMessageFactory;
import dimawo.fileTransfer.client.messages.ChunkMessage;
import dimawo.fileTransfer.client.messages.ErrorMessage;
import dimawo.fileTransfer.client.messages.PingClientMessage;
import dimawo.fileTransfer.server.messages.GetFileRequest;
import dimawo.fileTransfer.server.messages.GetNextChunkRequest;
import dimawo.fileTransfer.server.messages.PingServerMessage;

public class SaCaInputMessageFactory implements FileTransferMessageFactory {
	private static SaCaInputMessageFactory singleton = new SaCaInputMessageFactory();

	@Override
	public GetFileRequest newGetFileRequest(String fileUID, boolean isFileName) {
		return new SaCaGetFileRequest(fileUID, isFileName);
	}

	@Override
	public GetNextChunkRequest newGetNextChunkMessage(String fileUID) {
		return new SaCaGetNextChunkRequest(fileUID);
	}

	@Override
	public ChunkMessage newChunkMessage(String fileUID, byte[] data,
			boolean isLast) {
		return new SaCaChunkMessage(fileUID, data, isLast);
	}

	@Override
	public ErrorMessage newErrorMessage(String msg, String fileUID) {
		return new SaCaErrorMessage(msg, fileUID);
	}

	public static FileTransferMessageFactory getInstance() {
		return singleton;
	}

	@Override
	public PingClientMessage newPingClientMessage() {
		return new SaCaPingClientMessage();
	}

	@Override
	public PingServerMessage newPingServerMessage() {
		return new SaCaPingServerMessage();
	}

}
