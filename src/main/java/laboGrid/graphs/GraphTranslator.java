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
package laboGrid.graphs;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import laboGrid.graphs.mapping.GraphMapping;
import laboGrid.graphs.mapping.mtwa.CTNode;
import laboGrid.graphs.mapping.mtwa.ComputerTree;
import laboGrid.graphs.replication.ReplicationGraph;

import dimawo.middleware.distributedAgent.DAId;



public class GraphTranslator {

	protected DAId[] newIds;
	
	public GraphTranslator(DAId[] newIds) {
		this.newIds = newIds;
	}
	
	public void translateComputationGraph(GraphMapping cGraph,
			Map<DAId, Set<Integer>> da2Sub, DAId[] sub2Da) {
		
		assert da2Sub.isEmpty();
		Set<Integer>[] da2SubToTranslate = cGraph.getDa2Sub();
		
		for(int i = 0; i < newIds.length; ++i) {
			DAId currentDaId = newIds[i];
			Set<Integer> subs = da2SubToTranslate[i];
			if(subs != null && !subs.isEmpty()) {
				da2Sub.put(currentDaId, subs);
				Iterator<Integer> it = subs.iterator();
				while(it.hasNext()) {
					int subId = it.next();
					sub2Da[subId] = currentDaId;
				}
			} else {
				da2Sub.put(currentDaId, new TreeSet<Integer>()); // So this DA will be shutted down.
			}
		}
		
	}
	
	public void translateBackupGraph(ReplicationGraph bGraph,
			Map<DAId, Set<DAId>> transRepGraph) {

		Set<Integer>[] storageNeighbors = bGraph.getReplicationNeighbors();
		for(int i = 0; i < newIds.length; ++i) {

			Iterator<Integer> nIt = storageNeighbors[i].iterator();
			Set<DAId> realNeighbours = new TreeSet<DAId>();
			while(nIt.hasNext()) {
				int n = nIt.next();
				if(n != -1)
					realNeighbours.add(newIds[n]);
			}
			transRepGraph.put(newIds[i], realNeighbours);
		}

	}

	public void translateComputerTree(ComputerTree ct) {
		for(Iterator<CTNode> it = ct.iterator(); it.hasNext();) {
			CTNode node = it.next();
			int id = node.getDaIndex();
			DAId newId = newIds[id];
			node.setDaId(newId);
		}
	}

	public void genCentralBackupGraph(DAId hostDaId,
			Map<DAId, Set<DAId>> bGraph) {
		TreeSet<DAId> s = new TreeSet<DAId>();
		s.add(hostDaId);
		for(int i = 0; i < newIds.length; ++i) {
			
			bGraph.put(newIds[i], s);
			
		}
	}
	
}
