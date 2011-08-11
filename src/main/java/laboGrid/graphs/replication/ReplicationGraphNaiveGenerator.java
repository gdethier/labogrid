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
package laboGrid.graphs.replication;

import java.util.Set;
import java.util.TreeSet;

import laboGrid.graphs.mapping.GraphMapping;

import dimawo.middleware.distributedAgent.DAId;



public class ReplicationGraphNaiveGenerator implements ReplicationGraphGenerator {

	public ReplicationGraph computeReplicationGraph(DAId[] das, 
			GraphMapping cGraph, int backupDegree) {
		
		Set<Integer>[] toReturn = new Set[das.length];
		Set<Integer>[] da2Sub = cGraph.getDa2Sub();
		for(int i = 0; i < das.length; ++i) {
			if(da2Sub[i] != null) {
				Set<Integer> backupNodes = calculateReplicationNeighbors(i, backupDegree, da2Sub);
				toReturn[i] = backupNodes;
			} else {
				toReturn[i] = new TreeSet<Integer>();
			}
		}

		return new ReplicationGraph(toReturn);

	}
	
	protected Set<Integer> calculateReplicationNeighbors(int daId, int count, Set<Integer>[] da2Sub) {
		
		TreeSet<Integer> toReturn = new TreeSet<Integer>();
		
		int addedNodes = 0;
		int iterations = 0;
		int toAdd = (daId + 1)%da2Sub.length;
		while(addedNodes < count && iterations < (da2Sub.length - 1)) {
			
			if(da2Sub[toAdd] != null) {
				toReturn.add(toAdd);
				++addedNodes;
			}
			
			++iterations;
			
			toAdd = (toAdd + 1)%da2Sub.length;
		}

		return toReturn;
	}

}
