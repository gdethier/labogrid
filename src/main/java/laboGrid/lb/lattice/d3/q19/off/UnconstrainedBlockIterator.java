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
package laboGrid.lb.lattice.d3.q19.off;

import laboGrid.lb.lattice.BlockIterator;
import laboGrid.lb.lattice.LatticeBlock;

public class UnconstrainedBlockIterator implements BlockIterator {

	private int blockSize;
	private int extractedSites;
	
	private double[] f;
	private int[] offs;
	
	private int xSize, ySize, zSize;
	private int xyzSize;
	private int currentX, currentY, currentZ;


	public UnconstrainedBlockIterator(double[] f, int[] offs,
			int blockSize,
			int xSize, int ySize, int zSize) {
		this.blockSize = blockSize;

		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		
		this.f = f;
		this.offs = offs;
		
		xyzSize = xSize * ySize * zSize;

		extractedSites = 0;
		
		currentX = currentY = currentZ = 0;
	}

	@Override
	public boolean hasNext() {
		return extractedSites < xyzSize;
	}

	@Override
	public LatticeBlock next() {

		int sitesToExtract = Math.min(blockSize, xyzSize - extractedSites);

		// Extract data from f
		double[] siteData = new double[sitesToExtract * 19];
		int[] sitePos = new int[sitesToExtract * 3];
		for(int q = 0; q < 19; ++q) {
			
			// As f contains actually circular arrays, copy may be operated
			// in 2 times in order to copy until the tail of the array and
			// the rest at the head.
			int off = offs[q];
			int arrayStart = q * xyzSize;
			int arrayStop = arrayStart + xyzSize;
			int start = arrayStart + (off + extractedSites) % xyzSize;
			if(arrayStop - start >= sitesToExtract) {

				int j = q;
				for(int i = 0; i < sitesToExtract; ++i, j += 19)
					siteData[j] = f[start + i];

			} else { // arrayStop - start < sitesToExtract

				int firstCopyLength = arrayStop - start;
				
				assert start + firstCopyLength == arrayStop : 
					start+","+firstCopyLength+","+arrayStop;

				int j = q;
				for(int i = 0; i < firstCopyLength; ++i, j += 19)
					siteData[j] = f[start + i];


				int secondCopyLength = sitesToExtract - firstCopyLength;					
				for(int i = 0; i < secondCopyLength; ++i, j += 19)
					siteData[j] = f[arrayStart + i];

			}
			
		}

		// Set positions
		int sitePosPos = 0;
		for(int s = 0; s < sitesToExtract; ++s) {
			
			for(;currentZ < zSize && s < sitesToExtract; ++currentZ, ++s) {
			
				sitePos[sitePosPos] = currentX;
				sitePos[sitePosPos + 1] = currentY;
				sitePos[sitePosPos + 2] = currentZ;
				
				sitePosPos += 3;
			
			}
			--s; // Because s will be incremented again

			if(currentZ == zSize) {
				currentZ = 0;
				++currentY;
				if(currentY == ySize) {
					currentY = 0;
					++currentX;
				}
			}

		}
		
		
		int blockPosition = extractedSites;
		extractedSites += sitesToExtract;
		
		return new UnconstrainedBlock(offs, f, xyzSize,
				blockPosition, siteData, sitePos);

	}

}
