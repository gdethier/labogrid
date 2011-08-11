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
package laboGrid.lb.lattice.d3.q19.simple;

import laboGrid.lb.lattice.BlockIterator;
import laboGrid.lb.lattice.LatticeBlock;

public class ConstrainedBlockIterator implements BlockIterator {

	private double[][][][] f;
	private int blockSize;
	private int currentPos;
	
	private int xFrom, xTo;
	private int yFrom, yTo;
	private int zFrom, zTo;

	private int currentX, currentY, currentZ;
	private int totalSites;
	private int extractedSites;


	public ConstrainedBlockIterator(
			double[][][][] f,
			int blockSize,
			int xFrom, int xTo,
			int yFrom, int yTo,
			int zFrom, int zTo,
			int yzqSize, int zqSize) {

		this.f = f;
		this.blockSize = blockSize;
		
		this.xFrom = xFrom;
		this.xTo = xTo;
		this.yFrom = yFrom;
		this.yTo = yTo;
		this.zFrom = zFrom;
		this.zTo = zTo;
		
		currentX = xFrom;
		currentY = yFrom;
		currentZ = zFrom;
		currentPos = currentX * yzqSize + currentY * zqSize + currentZ * 19;

		totalSites = (xTo - xFrom) * (yTo - yFrom) * (zTo - zFrom);
		extractedSites = 0;
		
	}

	@Override
	public boolean hasNext() {
		return extractedSites < totalSites;
	}

	@Override
	public LatticeBlock next() {
		
		int sitesToExtract = Math.min(blockSize, totalSites - extractedSites);
		extractedSites += sitesToExtract;

		double[] siteData = new double[sitesToExtract * 19];
		int[] sitePos = new int[sitesToExtract * 3];
		
		int extractedSites = 0;
		int siteDataPos = 0;
		int sitePosPos = 0;
		
		for(; currentZ < zTo && extractedSites < sitesToExtract; ++currentZ) {

			for(int q = 0; q < 19; ++q)
				siteData[siteDataPos + q] = f[currentX][currentY][currentZ][q];
			sitePos[sitePosPos] = currentX;
			sitePos[sitePosPos + 1] = currentY;
			sitePos[sitePosPos + 2] = currentZ;
			++extractedSites;
			siteDataPos += 19;
			sitePosPos += 3;

		}
		
		if(currentZ == zTo) {
			currentZ = zFrom;
			++currentY;
			if(currentY == yTo) {
				currentY = yFrom;
				++currentX;
			}
		}
		
		while(extractedSites < sitesToExtract) {

			for(int q = 0; q < 19; ++q)
				siteData[siteDataPos + q] = f[currentX][currentY][currentZ][q];
			sitePos[sitePosPos] = currentX;
			sitePos[sitePosPos + 1] = currentY;
			sitePos[sitePosPos + 2] = currentZ;
			++extractedSites;
			siteDataPos += 19;
			sitePosPos += 3;
			
			++currentZ;
			if(currentZ == zTo) {
				currentZ = zFrom;
				++currentY;
				if(currentY == yTo) {
					currentY = yFrom;
					++currentX;
				}
			}

		}

		return new UnconstrainedBlock(f, siteData, sitePos);
		
	}

}
