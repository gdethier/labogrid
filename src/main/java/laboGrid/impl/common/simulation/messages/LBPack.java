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
package laboGrid.impl.common.simulation.messages;

import laboGrid.impl.common.simulation.algorithm.LBData;
import dimawo.middleware.distributedAgent.DAId;

public class LBPack extends LBSimulationMessage {

	private static final long serialVersionUID = 1L;

	protected LBData[] data;
	protected int version;
	
	public LBPack(DAId recipient) {
		super(recipient);
		data = null;
	}

	public void setData(LBData[] messages) {
		data = messages;
	}

	public LBData[] getData() {
		return data;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public int getVersion() {
		return version;
	}
}
