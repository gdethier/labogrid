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
package laboGrid.utils;

import laboGrid.lb.lattice.d3.D3AccumulatedSpeedsSlice;
import laboGrid.lb.lattice.d3.D3LatticeMacroVarsSlice;
import laboGrid.lb.solid.d3.D3SolidBitmapSlice;
import laboGrid.lb.solid.d3.D3SolidSlice;

public class D3AggregatedSlice {
	private int iSize, jSize;
	
	private D3LatticeMacroVarsSlice[][] macroVars;
	private D3AccumulatedSpeedsSlice[][] acc;
	private D3SolidSlice[][] solids;
	
	private int[] uIntervals, vIntervals;
	
	public D3AggregatedSlice(int iSize, int jSize) {
		this.iSize = iSize;
		this.jSize = jSize;
		
		macroVars = new D3LatticeMacroVarsSlice[iSize][jSize];
		acc = new D3AccumulatedSpeedsSlice[iSize][jSize];
		solids = new D3SolidBitmapSlice[iSize][jSize];
	}

	public void setMacroVars(int i, int j,
			D3LatticeMacroVarsSlice slice) {
		macroVars[i][j] = slice;
	}

	public void setSolids(int i, int j, D3SolidSlice slice) {
		solids[i][j] = slice;
	}

	public void setAccumulatedSpeeds(int i, int j,
			D3AccumulatedSpeedsSlice slice) {
		acc[i][j] = slice;
	}
	
	private void setUVIntervals() {
		if(uIntervals == null) {
			uIntervals = new int[iSize];
			uIntervals[0] = 0;
			for(int i = 1; i < iSize; ++i) {
				uIntervals[i] = uIntervals[i - 1] + solids[i - 1][0].getISize();
			}
		}
		
		if(vIntervals == null) {
			vIntervals = new int[jSize];
			vIntervals[0] = 0;
			for(int j = 1; j < jSize; ++j) {
				vIntervals[j] = vIntervals[j - 1] + solids[0][j - 1].getJSize();
			}
		}
	}

	public D3MacroVar getMacroVars(int u, int v) {
		setUVIntervals();

		// Find subslice containing requested position
		int low = 0, high = iSize;
		while(low + 1 != high) {
			int middle = (low + high) / 2;
			if(uIntervals[middle] <= u) {
				low = middle;
			} else {
				high = middle;
			}
		}
		int i = low;
		
		low = 0; high = jSize;
		while(low + 1 != high) {
			int middle = (low + high) / 2;
			if(vIntervals[middle] <= v) {
				low = middle;
			} else {
				high = middle;
			}
		}
		int j = low;
		
		// Extract data from subslice (i,j)
		int uPosInSubslice = u - uIntervals[i];
		int vPosInSubslice = v - vIntervals[j];
		
		double xSpeed = macroVars[i][j].getXSpeed(uPosInSubslice, vPosInSubslice);
		double ySpeed = macroVars[i][j].getYSpeed(uPosInSubslice, vPosInSubslice);
		double zSpeed = macroVars[i][j].getZSpeed(uPosInSubslice, vPosInSubslice);
		double density = macroVars[i][j].getLocalDensity(uPosInSubslice, vPosInSubslice);
		boolean solid = ! solids[i][j].isFluid(uPosInSubslice, vPosInSubslice);

		double accXSpeed = 0, accYSpeed = 0, accZSpeed = 0;
		if(acc[i][j] != null) {
			accXSpeed = acc[i][j].getAccumulatedXSpeed(uPosInSubslice, vPosInSubslice);
			accYSpeed = acc[i][j].getAccumulatedYSpeed(uPosInSubslice, vPosInSubslice);
			accZSpeed = acc[i][j].getAccumulatedZSpeed(uPosInSubslice, vPosInSubslice);
		}

		return new D3MacroVar(xSpeed, ySpeed, zSpeed, density, solid,
				accXSpeed, accYSpeed, accZSpeed);
	}
}
