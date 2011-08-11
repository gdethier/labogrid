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
package laboGrid.impl.central.controllerAgent.experimenter.events;

import java.util.TreeMap;

import laboGrid.graphs.mapping.kl.Vertex;
import laboGrid.graphs.resource.ResourceGraph;

import dimawo.middleware.distributedAgent.DAId;



public class MTWAGraphMapping {
	
	private ResourceGraph rGraph;
	private TreeMap<DAId, Vertex[]> da2Sub;
	private DAId[] sub2Da;

	public MTWAGraphMapping(ResourceGraph rGraph, TreeMap<DAId, Vertex[]> da2Sub, DAId[] sub2Da) {
		this.rGraph = rGraph;
		this.da2Sub = da2Sub;
		this.sub2Da = sub2Da;
	}
	
	public ResourceGraph getResourceGraph() {
		return rGraph;
	}
	
	public TreeMap<DAId, Vertex[]> getDa2Sub() {
		return da2Sub;
	}
	
	public DAId[] getSub2Da() {
		return sub2Da;
	}

}
