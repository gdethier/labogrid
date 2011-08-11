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
package laboGrid.impl.decentral.mtwa.events;

import laboGrid.graphs.mapping.kl.Vertex;
import dimawo.middleware.overlay.ComputerTreePosition;

public class RequestTwa {
	private int reqId;
	private long ccp;
	private Vertex[] initPart;
	private ComputerTreePosition ctPos;
	
	public RequestTwa(int reqId, long ccp, Vertex[] initPart, ComputerTreePosition ctPos) {
		this.reqId = reqId;
		this.ccp = ccp;
		this.initPart = initPart;
		this.ctPos = ctPos;
	}
	
	public int getRequestId() {
		return  reqId;
	}
	
	public ComputerTreePosition getCtPosition() {
		return ctPos;
	}
	
	public long getCcp() {
		return ccp;
	}
	
	public Vertex[] getInitialPartition() {
		return initPart;
	}
}
