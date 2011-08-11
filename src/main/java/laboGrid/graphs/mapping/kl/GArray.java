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
package laboGrid.graphs.mapping.kl;

import java.util.Arrays;
import java.util.LinkedList;

public class GArray {
	
	protected class EdgeEnd implements Comparable<EdgeEnd> {
		int subID;
		GEntry adj;
		int weight;
		
		// Used only for search
		public EdgeEnd(int subID) {
			this.subID = subID;
		}
		
		public EdgeEnd(GEntry ag, int weight) {
			this.subID = ag.subID;
			this.adj = ag;
			this.weight = weight;
		}

		public EdgeEnd(int subID, int weight) {
			this.subID = subID;
			this.weight = weight;
		}

		@Override
		public int compareTo(EdgeEnd o) {
			return subID - o.subID;
		}
	}
	
	protected class GEntry implements Comparable<GEntry> {
		int index, subID;
		int intC, extC, difC;
		EdgeEnd[] adjList;

		// Only for binary search
		public GEntry(int subID) {
			this.subID = subID;
		}

		public GEntry(int subID, int index) {
			this.subID = subID;
			this.index = index;
			intC = extC = difC = 0;
		}

		@Override
		public int compareTo(GEntry o) {
			return subID - o.subID;
		}

		public EdgeEnd[] getAdjacencyList() {
			return adjList;
		}

		public void addToInternalCost(int w) {
			intC = intC + w;
		}

		public void addToExternalCost(int w) {
			extC = extC + w;
		}

		public void setAdjacencyList(EdgeEnd[] adj) {
			this.adjList = adj;
			Arrays.sort(adj);
		}
		
		public void setAdjacencyList(LinkedList<EdgeEnd> adj) {
			EdgeEnd[] ee = new EdgeEnd[adj.size()];
			adj.toArray(ee);
			setAdjacencyList(ee);
		}

		public boolean hasExternal() {
			return extC > 0;
		}

		public void updateDifCost() {
			difC = extC - intC;
		}
		
		public int getWeight(int subID) {
			int ind = Arrays.binarySearch(adjList, new EdgeEnd(subID));
			if(ind >= 0) {
				return adjList[ind].weight;
			}
			
			return 0;
		}
	}

	
	protected GEntry[] g;


	public int getSubID(int ind) {
		return g[ind].subID;
	}

	public int getWeight(int ind, int subID) {
		return g[ind].getWeight(subID);
	}

	public int getDifCost(int ind) {
		return g[ind].difC;
	}
	
	public GEntry getVertex(int ind) {
		return g[ind];
	}
	
	protected void swap(int i1, int i2) {
		if(i1 == i2)
			return;

		GEntry tmp = g[i1];
		g[i1] = g[i2];
		g[i2] = tmp;
		
		g[i1].index = i1;
		g[i2].index = i2;
	}

}

