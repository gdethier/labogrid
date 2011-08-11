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

import java.util.LinkedList;

import dimawo.middleware.overlay.ComputerTreePosition;



public class SyncDa2Subs {
	private LinkedList<Da2Subs> partialDa2Subs;
	private int toReceiveFromChildren;
	private boolean fromRootSet;
	
	public SyncDa2Subs(ComputerTreePosition ctPos) {
		partialDa2Subs = new LinkedList<Da2Subs>();
		toReceiveFromChildren = ctPos.getNumOfChildren();
		fromRootSet = false;
	}
	
	public void addRootDa2Subs(Da2Subs da2Subs) {
		if(fromRootSet)
			throw new Error("Root da2subs already added");
		fromRootSet = true;
		partialDa2Subs.add(da2Subs);
	}
	
	public void addChildDa2Subs(LinkedList<Da2Subs> da2Subs) {
		if(toReceiveFromChildren == 0) {
			throw new Error("All da2subs from children already added");
		}
		--toReceiveFromChildren;
		partialDa2Subs.addAll(da2Subs);
	}
	
	public boolean isComplete() {
		return fromRootSet && toReceiveFromChildren == 0;
	}

	public LinkedList<Da2Subs> getDa2Subs() {
		return partialDa2Subs;
	}
}
