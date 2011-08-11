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

import laboGrid.standalone.StandAloneIoServerMessage;
import dimawo.fileTransfer.client.messages.ChunkMessage;
import dimawo.middleware.distributedAgent.DAId;

public class OutputFileChunk extends CentralOutputServerMessage
implements ChunkMessage, StandAloneIoServerMessage {

	private static final long serialVersionUID = 1L;
	
	private byte[] chunkData;
	private boolean lastChunk;

	public OutputFileChunk(DAId sourceDaId, byte[] chunkData, boolean lastChunk) {
		super(sourceDaId);

		this.chunkData = chunkData;
		this.lastChunk = lastChunk;
	}
	
	public OutputFileChunk(byte[] chunkData, boolean lastChunk) {
		this.chunkData = chunkData;
		this.lastChunk = lastChunk;
	}

	public boolean isLastChunk() {
		return lastChunk;
	}

	@Override
	public byte[] getData() {
		return chunkData;
	}

	@Override
	public boolean isLast() {
		return lastChunk;
	}

	@Override
	public DAId getServerDaId() {
		return this.getSender();
	}

}
