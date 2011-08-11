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
package laboGrid.graphs.mapping;

import java.util.Set;

import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.resource.ResourceGraph;



public class GraphMapping {
	
	protected Set<Integer>[] da2Sub;
	protected int[] sub2Da;
	
	public GraphMapping(Set<Integer>[] da2Sub, int[] sub2Da) {
		this.da2Sub = da2Sub;
		this.sub2Da = sub2Da;
	}
	
	public Set<Integer>[] getDa2Sub() {
		return da2Sub;
	}

	public int getDaCount() {
		return da2Sub.length;
	}

	public int[] getSub2Da() {
		return sub2Da;
	}
	
	public void checkCorrectness(ModelGraph mGraph, ResourceGraph rGraph) throws Exception {
		
		// 1) check structures consistance
		for(int subId = 0; subId < sub2Da.length; ++subId) {
			int daId = sub2Da[subId];
			
			if(daId < 0 || daId >= da2Sub.length)
				throw new Exception("Undefined mapping for sub "+subId+": "+daId);
			
			if(! da2Sub[daId].contains(subId))
				throw new Exception("Mapping is not consistent: "+subId+
						" is not part of partition assigned to "+daId);
		}
		
		// 2) check mapping is a bijection (1 sub <-> 1 DA)
		for(int subId = 0; subId < sub2Da.length; ++subId) {
			boolean assigned = false;
			for(int daId = 0; daId < da2Sub.length; ++daId) {
				if(da2Sub[daId] == null)
					continue;
				boolean subAssignedToDA = da2Sub[daId].contains(subId);
				if(! assigned && subAssignedToDA)
					assigned = true;
				else if(assigned && subAssignedToDA)
					throw new Exception("Multiple assignement of sub "+subId);
				// else ((! assigned && ! subAssignedToDA) ||
				// (assigned && ! subAssignedToDA)) -> SKIP
			}
		}
		
	}
}
