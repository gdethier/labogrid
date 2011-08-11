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

class UnconstrainedBlockIterator implements BlockIterator {

	private double[][][][] f;
	private int blockSize;
	private int xSize, ySize, zSize;
	private int currentX, currentY, currentZ;
	private int extractedSites;
	private int totalSites;


	protected UnconstrainedBlockIterator(double[][][][] f,
			int xSize, int ySize, int zSize,
			int blockSize) {
		this.f = f;
		this.blockSize = blockSize;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		currentX = currentY = currentZ = 0;
		extractedSites = 0;
		totalSites = xSize * ySize * zSize;
	}

	@Override
	public boolean hasNext() {
		return currentX < xSize && currentY < ySize && currentZ < zSize;
	}

	@Override
	public LatticeBlock next() {

		int availableSites = totalSites - extractedSites;
		int sitesToExtract = Math.min(blockSize, availableSites);
		
		double[] siteData = new double[sitesToExtract * 19];
		int[] sitePos = new int[sitesToExtract * 3];
		int currentExtract = 0;
		int siteDataPos = 0;
		int sitePosPos = 0;
		while(currentExtract < sitesToExtract) {

			for(int q = 0; q < 19; ++q)
				siteData[siteDataPos + q] = f[currentX][currentY][currentZ][q];
			sitePos[sitePosPos] = currentX;
			sitePos[sitePosPos + 1] = currentY;
			sitePos[sitePosPos + 2] = currentZ;

			++currentExtract;
			siteDataPos += 19;
			sitePosPos += 3;
			
			++currentZ;
			if(currentZ == zSize) {
				currentZ = 0;
				++currentY;
				if(currentY == ySize) {
					currentY = 0;
					++currentX;
				}
			}
			
		}
		
		extractedSites += sitesToExtract;

		return new UnconstrainedBlock(f, siteData, sitePos);
	}

}
