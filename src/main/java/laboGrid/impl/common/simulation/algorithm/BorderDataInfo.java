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

import laboGrid.lb.lattice.BorderData;

public class BorderDataInfo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private BorderData data;
	private transient LBSimThread localSrc;
	
	public BorderDataInfo() {
	}

	public BorderDataInfo(BorderData data, LBSimThread srcThread) {
		this.data = data;
		this.localSrc = srcThread;
	}
	
	public boolean isKill() {
		return data == null;
	}
	
	public int getLink() {
		return data.getLink();
	}

	public int getVersion() {
		return data.getVersion();
	}
	
	public BorderData getData() {
		return data;
	}
	
	public void releaseLocalThread() {
		if(localSrc != null) {
			localSrc.releaseAwaitedSent();
		}
	}
}
