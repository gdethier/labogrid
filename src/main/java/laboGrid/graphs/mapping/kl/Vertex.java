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

import java.io.Serializable;

public class Vertex implements Comparable<Vertex>, Serializable {
	private int subID;
	private int[] adjList; // adjacent vertices IDs
	private int[] weights; // associated weights

	public Vertex(int subID) {
		this.subID = subID;
	}

	@Override
	public int compareTo(Vertex o) {
		return subID - o.subID;
	}

	public int[] getAdjacencyList() {
		return adjList;
	}

	public int[] getWeights() {
		return weights;
	}

	public void setAdjacencyList(int[] adj) {
		this.adjList = adj;
	}

	public void setWeights(int[] w) {
		this.weights = w;
	}

	public int getSubID() {
		return subID;
	}

}
