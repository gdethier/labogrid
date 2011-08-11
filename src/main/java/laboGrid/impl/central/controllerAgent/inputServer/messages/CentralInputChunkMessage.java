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
package laboGrid.impl.central.controllerAgent.inputServer.messages;

import laboGrid.impl.common.simulation.messages.LBSimulationMessage;
import laboGrid.ioClients.InputClientMessage;
import dimawo.fileTransfer.client.messages.ChunkMessage;
import dimawo.middleware.distributedAgent.DAId;

public class CentralInputChunkMessage extends LBSimulationMessage implements
		ChunkMessage, InputClientMessage {

	private static final long serialVersionUID = 1L;

	private String fileUID;
	private byte[] data;
	private boolean isLast;
	
	
	public CentralInputChunkMessage(String fileUID, byte[] data, boolean isLast) {
		this.fileUID = fileUID;
		this.data = data;
		this.isLast = isLast;
	}

	@Override
	public DAId getServerDaId() {
		return this.getSender();
	}

	@Override
	public byte[] getData() {
		return data;
	}

	@Override
	public boolean isLast() {
		return isLast;
	}

}
