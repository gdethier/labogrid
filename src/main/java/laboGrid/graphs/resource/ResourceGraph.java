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
package laboGrid.graphs.resource;

import java.io.PrintStream;

import laboGrid.powerModel.PowerModel;

import dimawo.middleware.distributedAgent.DAId;



public class ResourceGraph {

	protected DAId[] newIds;
	protected PowerModel[] dasPower; // sites/s
	protected long bandwidth = 100000000 / 8; // bytes/s


	public ResourceGraph(
			DAId[] newIds,
			PowerModel[] dasPower) {
		if(newIds.length != dasPower.length)
			throw new IllegalArgumentException();
		
		this.newIds = newIds;
		this.dasPower = dasPower;
	}
	
	public PowerModel[] getDasPower() {
		return dasPower;
	}
	
	public long getPower(int daID, int[] size) {
		return dasPower[daID].getPower(size);
	}

	public long getBandwidth() {
		return bandwidth;
	}

	public int getDasCount() {
		return newIds.length;
	}
	
	public DAId[] getNewIds() {
		return newIds;
	}

	public boolean isEmpty() {
		return newIds.length == 0;
	}
	
	public void print(PrintStream out) {
		for(int daId = 0; daId < newIds.length; ++daId) {
			out.println("ID: "+newIds[daId]);
			out.println("Power: "+dasPower[daId]);
			out.println("-----");
		}
	}
}
