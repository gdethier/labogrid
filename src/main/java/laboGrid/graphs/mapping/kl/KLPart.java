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

public class KLPart {
	/** This array is always sorted in increasing order of sublattice ID */
	private Vertex[] part;
	
	
	public KLPart(Vertex[] part) {
		this.part = part;
		Arrays.sort(part);
	}

	public int size() {
		return part.length;
	}

	public Vertex getByIndex(int i) {
		return part[i];
	}

	public Vertex getBySubID(int subID) {
		int ind = Arrays.binarySearch(part, new Vertex(subID));
		if(ind >= 0)
			return part[ind];
		return null;
	}

	public boolean contains(int subID) {
		return getBySubID(subID) != null;
	}

	public Vertex[] remove(int[] x) {
		Arrays.sort(x);
		Vertex[] old = part;
		Vertex[] removed = new Vertex[x.length];
		part = new Vertex[part.length - x.length];
		int indRemoved = 0;
		int indPart = 0;
		for(int i = 0; i < old.length; ++i) {
			Vertex v = old[i];
			int subID = v.getSubID();
			if(Arrays.binarySearch(x, subID) < 0) {
				part[indPart] = v;
				++indPart;
			} else {
				removed[indRemoved] = v;
				++indRemoved;
			}
		}
		
		if(indRemoved < removed.length || indPart < part.length) {
			throw new Error("All values were not removed");
		}

		return removed;
	}

	public void add(Vertex[] vertices) {
		Vertex[] old = part;
		part = new Vertex[old.length + vertices.length];
		for(int i = 0; i < old.length; ++i) {
			part[i] = old[i];
		}
		for(int i = 0; i < vertices.length; ++i) {
			part[old.length + i] = vertices[i];
		}
		
		Arrays.sort(part);
	}
	
	public Vertex[] getVertices() {
		return part;
	}

	public void checkValidity() throws Exception {
		// Search for doubloon
		for(int i = 0; i < part.length - 1; ++i) {
			if(part[i].getSubID() == part[i + 1].getSubID())
				throw new Exception("Partition contains doubloons");
		}
	}

}
