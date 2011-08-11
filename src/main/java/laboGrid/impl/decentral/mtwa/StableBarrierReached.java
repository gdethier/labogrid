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
package laboGrid.impl.decentral.mtwa;

import laboGrid.impl.decentral.mtwa.messages.DecentralMtwaMasterMessage;

public class StableBarrierReached extends DecentralMtwaMasterMessage {
	private int conf;
	private Sub2Da sub2Da;

	public StableBarrierReached(int conf, Sub2Da sub2Da) {
		this.conf = conf;
		this.sub2Da = sub2Da;
	}

	public int getConf() {
		return conf;
	}
	
	public Sub2Da getSub2Da() {
		return sub2Da;
	}
}
