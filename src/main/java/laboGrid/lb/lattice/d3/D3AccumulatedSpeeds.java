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

public class D3AccumulatedSpeeds {
	private int xSize, ySize, zSize, yzSize3, zSize3;
	private double[] acc;
	
	public D3AccumulatedSpeeds(int xSize, int ySize, int zSize) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;

		yzSize3 = ySize * zSize * 3;
		zSize3 = zSize * 3;

		acc = new double[xSize * yzSize3];
	}

	public void accumulate(int x, int y, int z,
			double xSpeed, double ySpeed, double zSpeed) {
		int baseInd = x * yzSize3 + y * zSize3 + z;
		acc[baseInd + 0] += xSpeed;
		acc[baseInd + 1] += ySpeed;
		acc[baseInd + 2] += zSpeed;
	}

	public D3AccumulatedSpeedsSlice getXYAccumulatedSpeedsSlice(int z) {
		D3AccumulatedSpeedsSlice toReturn = new D3AccumulatedSpeedsSlice(xSize, ySize);
		
		for(int x = 0; x < xSize; ++x) {
			int xBaseInd = x * yzSize3;
			for(int y = 0; y < ySize; ++y) {
				int baseInd = xBaseInd + (y * zSize3 + z);
				double xSpeed = acc[baseInd + 0];
				double ySpeed = acc[baseInd + 1];
				double zSpeed = acc[baseInd + 2];
				
				toReturn.setAccumulatedSpeeds(x, y, xSpeed, ySpeed, zSpeed);
			}
		}
		
		return toReturn;
	}

	public D3AccumulatedSpeedsSlice getXZAccumulatedSpeedsSlice(int y) {
		D3AccumulatedSpeedsSlice toReturn = new D3AccumulatedSpeedsSlice(xSize, zSize);
		
		for(int x = 0; x < xSize; ++x) {
			int xBaseInd = x * yzSize3;
			for(int z = 0; z < zSize; ++z) {
				int baseInd = xBaseInd + (y * zSize3 + z);
				double xSpeed = acc[baseInd + 0];
				double ySpeed = acc[baseInd + 1];
				double zSpeed = acc[baseInd + 2];
				
				toReturn.setAccumulatedSpeeds(x, z, xSpeed, ySpeed, zSpeed);
			}
		}
		
		return toReturn;
	}

	public D3AccumulatedSpeedsSlice getYZAccumulatedSpeedsSlice(int x) {
		D3AccumulatedSpeedsSlice toReturn = new D3AccumulatedSpeedsSlice(ySize, zSize);

		int xBaseInd = x * yzSize3;
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < ySize; ++z) {
				int baseInd = xBaseInd + (y * zSize3 + z);
				double xSpeed = acc[baseInd + 0];
				double ySpeed = acc[baseInd + 1];
				double zSpeed = acc[baseInd + 2];
				
				toReturn.setAccumulatedSpeeds(y, z, xSpeed, ySpeed, zSpeed);
			}
		}
		
		return toReturn;
	}
}
