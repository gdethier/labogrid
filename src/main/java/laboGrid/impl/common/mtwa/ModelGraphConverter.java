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
package laboGrid.impl.common.mtwa;

import laboGrid.graphs.mapping.kl.Vertex;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.lb.SubLattice;

public class ModelGraphConverter {

	public static Vertex[] convertModelGraph(ModelGraph mGraph) {
		int nSubs = mGraph.getSubLatticesCount();
		Vertex[] all = new Vertex[nSubs];
		for(int i = 0; i < nSubs; ++i) {
			SubLattice sub = mGraph.getSubLattice(i);
			Vertex v = new Vertex(i);
			int nNeigh = sub.getNeighborsCount();
			
			int[] adj = new int[nNeigh];
			sub.getNeighbors(adj);
			v.setAdjacencyList(adj);
			
			int[] weights = new int[nNeigh];
			sub.getWeights(weights);
			v.setWeights(weights);
			
			all[i] = v;
		}
		return all;
	}
	
}
