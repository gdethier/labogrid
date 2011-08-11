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
package laboGrid.lb.lattice;

import java.io.Serializable;

import laboGrid.lb.LBException;



public abstract class LatticeDescriptor implements Serializable {

	private static final long serialVersionUID = 1L;

	protected int dimensions;
	protected int[][] velocities;
//	protected int[] opposits;
	protected int fieldSize;

	public LatticeDescriptor(int dim, int vel, int fieldSize, int[][] velocities) {
		dimensions = dim;
		this.velocities = velocities;
//		opposits = new int[vel];
//		for(int i = 0; i < vel; ++i) {
//			opposits[i] = -1;
//		}
		this.fieldSize = fieldSize;
	}

//	protected void initializeOpposits() {
//		for(int i = 0; i < velocities.length; ++i) {
//			
//			if(IntegerVector.isZero(velocities[i]))
//				continue;
//			
//			int j = 0;
//			while(j < velocities.length) {
//				
//				if(!IntegerVector.isZero(velocities[j]) &&
//						opposits[i] == -1 &&
//						Arrays.equals(velocities[i], IntegerVector.negate(velocities[j]))) {
//					opposits[i] = j;
//					opposits[j] = i;
//					break;
//				}
//				++j;
//			}
//		}
//	}

	public int getVelocitiesCount() {
		return velocities.length;
	}

//	public boolean hasOpposit(int v) {
//		return opposits[v] >= 0;
//	}
//
//	public int getOpposit(int v) {
//		return opposits[v];
//	}

	public int[] getVector(int v) {
		return velocities[v];
	}

	public int getDimension() {
		return dimensions;
	}
	
	/**
	 * This method indicates if there should be incoming data in the lattice 
	 * in the case of pressure conditions.
	 * 
	 * @param vel Velocity to consider
	 * @param pressureAxe Axe of the pressure (0 for x, 1 for y ...)
	 * @param position The position to consider
	 * @return false if there are incoming data, true otherwise
	 */
	public boolean noDataComeIn(int vel, int pressureAxe, boolean zeroPos, boolean finalPos, float rhoIn, float rhoOut) {
		
		int velQ = velocities[vel][pressureAxe];
		return (rhoIn != -1 && velQ == 1 && zeroPos) ||
				(rhoOut != -1 && velQ == -1 && finalPos);
		
	}
	
	/**
	 * This method indicates if a given velocity's data is going out of the lattice 
	 * in the case of pressure conditions.
	 * 
	 * @param vel Velocity to consider
	 * @param pressureAxe Axe of the pressure (0 for x, 1 for y ...)
	 * @param position The position to consider
	 * @return false if there are outgoing data, false otherwise
	 */
	public boolean noDataGoOut(int vel, int pressureAxe, boolean zeroPos, boolean finalPos, float rhoIn, float rhoOut) {
		
		int velQ = velocities[vel][pressureAxe];
		return (rhoIn != -1 && velQ == -1 && zeroPos) ||
				(rhoOut != -1 && velQ == 1 && finalPos);
		
	}

	public int getFieldSize() {
		return fieldSize;
	}
	
	public float getSiteSize() {
		return fieldSize * velocities.length;
	}

	public abstract float getOutcomingSites(int v, int[] size);
	public abstract boolean isRest(int q);
	public abstract int getOutgoingBytes(int[] size);
	public abstract int getOutgoingBytes(int v, int[] size);

}
