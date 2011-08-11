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

public class GExchangeArray extends GArray {
	
	// 0 <= x <= o1 <= z <= y <= o2 <= g.length
	// [0..x[ = X
	// [x..o1[ = Ox
	// [o1..z[ = A-X-Ox
	// [z..y[ = Y
	// [y..o2[ = Oy
	// [o2..g.length[ = B-Y-Oy
	private int x, o1, z, y, o2;


	public GExchangeArray(KLPart A, KLPart B) {
		int aSize = A.size();
		int bSize = B.size();
		g = new GEntry[aSize + bSize];
		z = aSize;
		x = o1 = 0;
		y = o2 = z;
		
		// Fill G
		for(int i = 0; i < aSize; ++i) {
			Vertex a = A.getByIndex(i);
			GEntry v = new GEntry(a.getSubID(), i);
			g[i] = v;
		}
		
		for(int i = 0; i < bSize; ++i) {
			Vertex b = B.getByIndex(i);
			GEntry v = new GEntry(b.getSubID(), i + z);
			g[i + z] = v;
		}


		// Adjacency for A
		for(int i = 0; i < aSize; ++i) {
			GEntry e = g[i];
			Vertex a = A.getByIndex(i);
			int[] adj = a.getAdjacencyList();
			int[] weights = a.getWeights();

			// Build adjacency list
			LinkedList<EdgeEnd> list = new LinkedList<EdgeEnd>();
			for(int j = 0; j < adj.length; ++j) {
				int subID = adj[j];
				int weight = weights[j];
				
				GEntry ag = getFromA(subID);
				if(ag != null) {
					list.add(new EdgeEnd(ag, weight));
					e.addToInternalCost(weight);
				} else {
					GEntry bg = getFromB(subID);
					if(bg != null) {
						list.add(new EdgeEnd(bg, weight));
						e.addToExternalCost(weight);
					} // else -> SKIP
				}
			}
			
			e.setAdjacencyList(list);
			e.updateDifCost();
		}
		
		// Build Ox
		for(int i = 0; i < z; ++i) {
			GEntry e = g[i];
			if(e.hasExternal()) {
				addToABoundary(i);
			}
		}
		
		
		// Adjacency for B
		for(int i = 0; i < bSize; ++i) {
			GEntry e = g[z + i];
			Vertex b = B.getByIndex(i);
			int[] adj = b.getAdjacencyList();
			int[] weights = b.getWeights();

			// Build adjacency list
			LinkedList<EdgeEnd> list = new LinkedList<EdgeEnd>();
			for(int j = 0; j < adj.length; ++j) {
				int subID = adj[j];
				int weight = weights[j];
				
				GEntry bg = getFromB(subID);
				if(bg != null) {
					list.add(new EdgeEnd(bg, weight));
					e.addToInternalCost(weight);
				} else {
					GEntry ag = getFromA(subID);
					if(ag != null) {
						list.add(new EdgeEnd(ag, weight));
						e.addToExternalCost(weight);
					} // else -> SKIP
				}
			}
			
			e.setAdjacencyList(list);
			e.updateDifCost();
		}
		
		// Build Oy
		for(int i = z; i < g.length; ++i) {
			GEntry e = g[i];
			if(e.hasExternal()) {
				addToBBoundary(i);
			}
		}
		
	}

	private void addToABoundary(int i) {
		if(i < x || i >= z)
			throw new IllegalArgumentException();

		if(i >= x && i < o1)
			return; // entry already in boundary
		
		if(i > o1)
			swap(o1, i);
		// else i == o1 -> SKIP
		++o1;
	}
	
	private void addToBBoundary(int i) {
		if(i < y)
			throw new IllegalArgumentException();

		if(i >= y && i < o2)
			return; // entry already in boundary
		
		if(i > o2)
			swap(o2, i);
		// else i == o2 -> SKIP
		++o2;
	}
	
	private GEntry getFromA(int subID) {
		int ind = Arrays.binarySearch(g, 0, z, new GEntry(subID));
		if(ind >= 0)
			return g[ind];
		return null;
	}

	private GEntry getFromB(int subID) {
		int ind = Arrays.binarySearch(g, z, g.length, new GEntry(subID));
		if(ind >= 0)
			return g[ind];
		return null;
	}
	
	public int getAFirstBoundaryIndex() {
		return x;
	}

	public int getALastBoundaryIndex() {
		return o1;
	}

	public int getAFirstAvailableIndex() {
		return x;
	}

	public int getALastAvailableIndex() {
		return z;
	}
	
	public int getBFirstBoundaryIndex() {
		return y;
	}

	public int getBLastBoundaryIndex() {
		return o2;
	}

	public int getBFirstAvailableIndex() {
		return y;
	}

	public int getBLastAvailableIndex() {
		return g.length;
	}

	public void addToAHandledSet(int i) {
		if(i < x || i >= z)
			throw new IllegalArgumentException("i="+i+", x="+x+", z="+z);
		
		swap(x, i);
		
		if(x < o1 && i >= o1) {
			// x was in boundary and was removed from it,
			// it must be inserted back into boundary
			swap(o1, i);
			++o1;
		} else if(x == o1)
			++o1;
		++x;
	}
	
	public void addToBHandledSet(int i) {
		if(i < y)
			throw new IllegalArgumentException();
		
		swap(y, i);

		if(y < o2 && i >= o2) {
			// x was in boundary and was removed from it,
			// it must be inserted back into boundary
			swap(o2, i);
			++o2;
		}else if(y == o2)
			++o2;
		++y;
	}

	public void updateACostsAndBoundary() {
		GEntry last = g[x - 1]; // Last handled vertex
		EdgeEnd[] adj = last.getAdjacencyList();
		for(int i = 0; i < adj.length; ++i) {
			EdgeEnd end = adj[i];
			GEntry endEntry = end.adj;
			int endInd = endEntry.index;
			if(endInd < x || (endInd >= z && endInd < y) || (endInd >= y))
				// end is in X, Y or B
				continue;
			
			updateACost(endEntry);
			if(endEntry.hasExternal() && endInd < o1) {
				// SKIP
			} else if(endEntry.hasExternal() && (endInd >= o1)) {
				swap(endEntry.index, o1);
				++o1;
			} else if(! endEntry.hasExternal() && (endInd < o1)) {
				swap(endEntry.index, o1 - 1);
				--o1;
			} else {
				// SKIP
			}
		}
	}

	private void updateACost(GEntry e) {
		e.intC = 0;
		e.extC = 0;
		EdgeEnd[] adj = e.adjList;
		for(int i = 0; i < adj.length; ++i) {
			EdgeEnd end = adj[i];
			GEntry other = end.adj;
			int otherInd = other.index;
			int w = end.weight;
			
			if((otherInd >= x && otherInd < z) || // in A-X
					(otherInd >= z && otherInd < y)) { // in Y
				e.intC = e.intC + w;
			} else if(otherInd >= z) {
				e.extC = e.extC + w;
			}
		}
		e.difC = e.extC - e.intC;
	}
	
	public void updateBCostsAndBoundary() {
		GEntry last = g[y - 1]; // Last handled vertex
		EdgeEnd[] adj = last.getAdjacencyList();
		for(int i = 0; i < adj.length; ++i) {
			EdgeEnd end = adj[i];
			GEntry endEntry = end.adj;
			int endInd = endEntry.index;
			if(endInd < x || (endInd >= z && endInd < y) || endInd < z)
				// end is in X, Y or A
				continue;
			
			updateBCost(endEntry);
			if(endEntry.hasExternal() && (endInd < o2)) {
				// SKIP
			} else if(endEntry.hasExternal() && (endInd >= o2)) {
				swap(endEntry.index, o2);
				++o2;
			} else if(! endEntry.hasExternal() && (endInd < o2)) {
				swap(endEntry.index, o2 - 1);
				--o2;
			} else {
				// SKIP
			}
		}
	}

	private void updateBCost(GEntry e) {
		e.intC = 0;
		e.extC = 0;
		EdgeEnd[] adj = e.adjList;
		for(int i = 0; i < adj.length; ++i) {
			EdgeEnd end = adj[i];
			GEntry other = end.adj;
			int otherInd = other.index;
			int w = end.weight;
			
			if((otherInd >= y) || // in B-Y
					(otherInd < x)) { // in X
				e.intC = e.intC + w;
			} else {
				e.extC = e.extC + w;
			}
		}
		e.difC = e.extC - e.intC;
	}

	public int[] getAFirstHandledVertices(int maxK) {
		if(maxK > x)
			throw new IllegalArgumentException();
		int[] ids = new int[maxK];
		for(int i = 0; i < ids.length; ++i) {
			ids[i] = g[i].subID;
		}
		return ids;
	}
	
	public int[] getBFirstHandledVertices(int maxK) {
		if(maxK > (y - z))
			throw new IllegalArgumentException();
		int[] ids = new int[maxK];
		for(int i = 0; i < ids.length; ++i) {
			ids[i] = g[z + i].subID;
		}
		return ids;
	}

	public void printConfiguration() {
		System.out.println("x="+x+", o1="+o1+"z="+z+", y="+y+", o2="+o2+", g.length="+g.length);
	}

}
