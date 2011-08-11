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
package laboGrid.impl.common.simulation.events;

import dimawo.middleware.communication.outputStream.MOSAccessorInterface;
import dimawo.middleware.distributedAgent.DAId;

public class ConnectionEstablished {
	
	private DAId daId;
	private boolean success;
	private MOSAccessorInterface access;


	public ConnectionEstablished(DAId daId, boolean success,
			MOSAccessorInterface access) {
		this.daId = daId;
		this.success = success;
		this.access = access;
	}
	
	public DAId getDAId() {
		return daId;
	}
	
	public boolean isSuccessfull() {
		return success;
	}
	
	public MOSAccessorInterface getAccessor() {
		return access;
	}

}
