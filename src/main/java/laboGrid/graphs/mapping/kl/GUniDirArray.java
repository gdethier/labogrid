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
import java.util.TreeSet;

public class GUniDirArray extends GArray {

	/** 0 <= x <= o <= g.length */
	public int x, o;

	public GUniDirArray(KLPart A, KLPart B) {
		int aSize = A.size();
		g = new GEntry[aSize];
		x = o = 0; // O and X are empty
		
		// Fill G
		for(int i = 0; i < aSize; ++i) {
			Vertex a = A.getByIndex(i);
			GEntry v = new GEntry(a.getSubID(), i);
			g[i] = v;
		} // G is sorted because A is.
		
		// Set adjacency lists of G's entries and update costs
		for(int i = 0; i < g.length; ++i) {
			GEntry e = g[i];
			Vertex a = A.getByIndex(i);
			int[] adj = a.getAdjacencyList();
			int[] weights = a.getWeights();

			// Build adjacency list
			LinkedList<EdgeEnd> list = new LinkedList<EdgeEnd>();
			for(int j = 0; j < adj.length; ++j) {
				int subID = adj[j];
				int weight = weights[j];
				
				GEntry ag = get(subID); // G is still sorted
				if(ag != null) {
					list.add(new EdgeEnd(ag, weight));
					e.addToInternalCost(weight);
				} else if(B.contains(subID)) {
					list.add(new EdgeEnd(subID, weight));
					e.addToExternalCost(weight);
				} // else -> SKIP
			}
			
			e.setAdjacencyList(list);
			e.updateDifCost();
		}
		
		// Build O
		for(int i = 0; i < g.length; ++i) {
			GEntry e = g[i];
			if(e.hasExternal()) {
				addToBoundary(i);
			}
		}
		
	}

	private void addToBoundary(int i) {
		if(i < o)
			return; // entry already in boundary
		
		if(i > o)
			swap(o, i);
		// else i == o -> SKIP
		++o;
	}

	private GEntry get(int subID) {
		int ind = Arrays.binarySearch(g, new GEntry(subID));
		if(ind >= 0)
			return g[ind];
		return null;
	}
	
	public int getFirstBoundaryIndex() {
		return x;
	}

	public int getLastBoundaryIndex() {
		return o;
	}

	public int size() {
		return g.length;
	}

	public int getFirstAvailableIndex() {
		return x;
	}

	public int getLastAvailableIndex() {
		return g.length;
	}

	public void addToHandledSet(int i) {
		if(i < x)
			throw new IllegalArgumentException("already handled");
		
		swap(x, i);

		if(x < o && i >= o) {
			// x was in boundary and was removed from it,
			// it must be inserted back into boundary
			swap(o, i);
			++o;
		} else if(x == o)
			++o;
		++x;
	}

	public void updateCostsAndBoundary() {
		GEntry last = g[x - 1]; // Last handled vertex
		EdgeEnd[] adj = last.getAdjacencyList();
		for(int i = 0; i < adj.length; ++i) {
			EdgeEnd end = adj[i];
			GEntry endEntry = end.adj;
			if(endEntry == null || endEntry.index < x)
				// end is in B or X
				continue;
			
			int endIndex = endEntry.index;
			
			updateCosts(endEntry);

			if(endEntry.hasExternal() && endIndex < o) {
				// SKIP
			} else if(endEntry.hasExternal() && endIndex >= o) {
				swap(endIndex, o);
				++o;
			} else if(! endEntry.hasExternal() && endIndex < o) {
				swap(endIndex, o - 1);
				--o;
			} else { //! endEntry.hasExternal() && endIndex >= o
				// SKIP
			}
		}
	}
	
	private void updateCosts(GEntry n) {
		n.intC = 0;
		n.extC = 0;
		for(int i = 0; i < n.adjList.length; ++i) {
			EdgeEnd end = n.adjList[i];
			GEntry e = end.adj;
			if(e == null || e.index < x) {
				n.extC = n.extC + end.weight;
			} else {
				n.intC = n.intC + end.weight;
			}
		}
		n.updateDifCost();
	}

	public void printConfiguration() {
		System.out.println("x="+x+", o="+o+", g.length="+g.length);
	}
}
