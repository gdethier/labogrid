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
package laboGrid.impl.common.simulation.algorithm;

import laboGrid.impl.common.simulation.messages.LBSimulationMessage;

/**
 * Inter-sub-lattices message.
 * 
 * @author Gérard Dethier
 *
 */
public class LBSimThreadMessage extends LBSimulationMessage {

	private static final long serialVersionUID = 1L;

	protected int version;
	protected int from, to;
	
	public LBSimThreadMessage(int from, int to, int version) {
		this.from = from;
		this.to = to;
		this.version = version;
	}
	
	public int getVersion() {
		return version;
	}
	
	public int getSubLFrom() {
		return from;
	}
	
	public int getSubLTo() {
		return to;
	}
}
