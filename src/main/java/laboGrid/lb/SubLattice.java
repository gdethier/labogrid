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
package laboGrid.lb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import laboGrid.lb.lattice.LatticeDescriptor;
import laboGrid.math.IntegerVector;



public class SubLattice implements Serializable {

	private static final long serialVersionUID = 1;

	private int id;
	private LatticeDescriptor lDesc;

	private int[] neighbors;
	private int[] weights; // bytes going to a neighbor
	protected int[] vel2Neighbor;

	protected int[] position;
	protected int[] qPosition;
	protected int[] size;
	protected boolean[] fromBoundary; // indicates if a plane is an input
	protected boolean[] toBoundary; // indicates if a plane is an output
	
	public SubLattice(LatticeDescriptor lDesc) {

		id = -1;
		this.lDesc = lDesc;

		int numOfVel = lDesc.getVelocitiesCount();
		vel2Neighbor = new int[numOfVel];
		Arrays.fill(vel2Neighbor, -2);
		
		size = null;
		
		// Default : no boundaries
		fromBoundary = new boolean[numOfVel];
		toBoundary = new boolean[numOfVel];
		Arrays.fill(fromBoundary, false);
		Arrays.fill(toBoundary, false);
	}

	public void initAsLattice(int[] size, LatticeDescriptor lDesc) {

		id = 0;
		this.lDesc = lDesc;

		this.size = size;
		position = new int[size.length];
		qPosition = new int[size.length];
		Arrays.fill(position, 0);
		Arrays.fill(qPosition, 0);

		// Set neighbors.
		neighbors = new int[] {};
		weights = new int[] {};
		
		for(int q = 0; q < vel2Neighbor.length; ++q) {
			
			if( ! lDesc.isRest(q)) {

				vel2Neighbor[q] = 0;
//				vel2NeighborOf[q] = 0;
			
			}

		}
		
		// Set boundaries
		fromBoundary = new boolean[vel2Neighbor.length];
		toBoundary = new boolean[vel2Neighbor.length];
		
		Arrays.fill(fromBoundary, false);
		Arrays.fill(toBoundary, false);

	}
	
	public int getNeighborFromVel(int vel) {

		return vel2Neighbor[vel];

	}
	
	public void setSize(int[] newSize) {
		size = newSize;
	}
	
	public int[] getSize() {
		return size;
	}

	public void setPosition(int[] nodePosition) {
		position = nodePosition;
	}
	
	public int[] getPosition() {
		return position;
	}

	public void setQPosition(int[] nodeQPosition) {
		qPosition = nodeQPosition;
	}

	public int[] getQPosition() {
		return qPosition;
	}
	
	public String toString() {
		String s = "[LBid : "+id+",";
		s += "qPosition : "+IntegerVector.toString(qPosition)+",";
		s += "position : "+IntegerVector.toString(position)+",";
		s += "size : "+IntegerVector.toString(size)+",";
		s += "neighbors = [";
		for(int i = 0; i < vel2Neighbor.length-1; ++i) {
			s += vel2Neighbor[i] + ",";
		}
		s += vel2Neighbor[vel2Neighbor.length-1];
		s += "]";
		s += "]";
		return s;
	}

	/**
	 * Sets the neigbors of this sublattice. The size of the sublattice must be
	 * set before a call to this method.
	 * 
	 * @param ids Arrays of neighbors: ids[i] is the neighbor following velocity
	 * i. Note that the same ID can appear several times in the array. The rest
	 * velocity (if any) is ignored.
	 */
	public void setNeighbors(int[] ids) {
		if(size == null)
			throw new Error("Size must be set before neighbors");

		TreeMap<Integer, Integer> neighMap = new TreeMap<Integer, Integer>();
		for(int i = 0; i < ids.length; ++i) {
			if(lDesc.isRest(i))
				continue;
			int subID = ids[i];
			
			vel2Neighbor[i] = subID;
			
			Integer w = neighMap.get(subID);
			int newWeight;
			if(w == null) {
				newWeight = lDesc.getOutgoingBytes(i, size);
			} else {
				newWeight = w + lDesc.getOutgoingBytes(i, size);
			}
			neighMap.put(subID, newWeight);
		}
		
		neighbors = new int[neighMap.size()];
		weights = new int[neighMap.size()];
		Iterator<Entry<Integer, Integer>> it = neighMap.entrySet().iterator();
		int k = 0;
		while(it.hasNext()) {
			Entry<Integer, Integer> e = it.next();
			int subID = e.getKey();
			int w = e.getValue();
			neighbors[k] = subID;
			weights[k] = w;
			++k;
		}

	}
	
	public int getSites() {
		int sites = 1;
		for(int i = 0; i < size.length; ++i) {
			sites *= size[i];
		}
		return sites;
	}

	public boolean isBoundaryFrom(int flowDirection) {
		return fromBoundary[flowDirection];
	}
	
	public boolean isBoundaryTo(int flowDirection) {
		return toBoundary[flowDirection];
	}
	
	public void setFromBoundary(boolean[] fromBoundary) {
		this.fromBoundary = fromBoundary;
	}
	
	public void setToBoundary(boolean[] toBoundary) {
		this.toBoundary = toBoundary;
	}

	public boolean getHasNeighbor(int vel) {
		return vel2Neighbor[vel] >= 0;
	}

	public int getId() {

		return id;

	}

	public void setId(int id) {

		this.id = id;

	}

	public int getVelocitiesCount() {

		return vel2Neighbor.length;

	}

	public int[] getMinPoint() {

		return position;

	}

	public int[] getMaxPoint() {

		return IntegerVector.add(position, size);

	}

	public int getNeighborsCount() {
		return neighbors.length;
	}

	public boolean hasNeighbor(int subID) {
		return Arrays.binarySearch(neighbors, subID) >= 0;
	}

	public int getNeighborFromIndex(int k) {
		return neighbors[k];
	}

	public int getEdgeWeight(int k) {
		return weights[k];
	}

	public LatticeDescriptor getLatticeDescriptor() {
		return lDesc;
	}

	public int getOutgoingBytes() {
		return lDesc.getOutgoingBytes(size);
	}

	public void getNeighbors(int[] adj) {
		System.arraycopy(neighbors, 0, adj, 0, neighbors.length);
	}
	
	public void getWeights(int[] w) {
		System.arraycopy(weights, 0, w, 0, weights.length);
	}

	public long getNumOfSites() {
		return IntegerVector.mult(size);
	}

}
