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
package laboGrid.lb.lattice.d3;

import laboGrid.lb.lattice.BlockIterator;
import laboGrid.lb.lattice.Lattice;


public abstract class D3Lattice extends Lattice {

	private static final long serialVersionUID = 1L;
	protected int xSize, ySize, zSize;
	protected D3AccumulatedSpeeds acc;
	
	public D3Lattice() {}

	public D3Lattice(int xSize, int ySize, int zSize) {
		this(new int[]{xSize, ySize, zSize});
	}
	
	public D3Lattice(int[] size) {
		setSize(size);
	}
	
	@Override
	public void setSize(int[] latticeSize) {
		super.setSize(latticeSize);
		
		xSize = latticeSize[0];
		ySize = latticeSize[1];
		zSize = latticeSize[2];
	}
	
	public int getXSize() {
		return xSize;
	}
	
	public int getYSize() {
		return ySize;
	}
	
	public int getZSize() {
		return zSize;
	}
	
	public void allocateAccumulators() {
		if(acc == null) {
			acc = new D3AccumulatedSpeeds(xSize, ySize, zSize);
		}
	}

	public void accumulateSpeeds() {
		for(int x = 0; x < xSize; ++x)
			for(int y = 0; y < ySize; ++y)
				for(int z = 0; z < zSize; ++z) {
					acc.accumulate(x, y, z, getXSpeed(x, y, z), getYSpeed(x, y, z), getZSpeed(x, y, z));
				}
	}

	public D3AccumulatedSpeedsSlice getXYAccumulators(int z) {
		if(acc != null)
			return acc.getXYAccumulatedSpeedsSlice(z);
		else
			return null;
	}

	public D3AccumulatedSpeedsSlice getXZAccumulators(int y) {
		if(acc != null)
			return acc.getXZAccumulatedSpeedsSlice(y);
		else
			return null;
	}

	public D3AccumulatedSpeedsSlice getYZAccumulators(int x) {
		if(acc != null)
			return acc.getYZAccumulatedSpeedsSlice(x);
		else
			return null;
	}

	public abstract double getDensity(int i, int x, int y, int z);
	public abstract void setDensity(int i, int x, int y, int z, double value);
	public abstract void getSiteDensities(int x, int y, int z, double[] dest);
	public abstract void setSiteDensities(int x, int y, int z, double[] src);

	public abstract double getXSpeed(int x, int y, int z);
	public abstract double getYSpeed(int x, int y, int z);
	public abstract double getZSpeed(int x, int y, int z);
	
	/**
	 * MarcoVars are organized as follows:
	 * 0 -> xSpeed
	 * 1 -> ySpeed
	 * 2 -> zSpeed
	 * 3 -> density
	 * @param x
	 * @return
	 */
	public abstract D3LatticeMacroVarsSlice getYZMacroVars(int x);
	public abstract D3LatticeMacroVarsSlice getXZMacroVars(int y);
	public abstract D3LatticeMacroVarsSlice getXYMacroVars(int z);
	public abstract D3MacroVariables getMacroVariables(int x, int y, int z);

	public abstract BlockIterator getBlockIterator(int blockSize);
	public abstract BlockIterator getBlockIterator(int blockSize,
			int xFrom, int xTo, int yFrom, int yTo, int zFrom, int zTo);

}
