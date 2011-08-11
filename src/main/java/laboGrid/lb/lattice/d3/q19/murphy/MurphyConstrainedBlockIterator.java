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
package laboGrid.lb.lattice.d3.q19.murphy;

import laboGrid.lb.lattice.BlockIterator;
import laboGrid.lb.lattice.LatticeBlock;

public class MurphyConstrainedBlockIterator implements BlockIterator {

	private int blockSize;
	
	private double[][] f;
	private int[] offs;
	private int zSize, yzSize, xyzSize;
	
	private int xFrom, xTo;
	private int yFrom, yTo;
	private int zFrom, zTo;

	private int currentX, currentY, currentZ;
	private int totalSites;
	private int extractedSites;


	public MurphyConstrainedBlockIterator(
			double[][] f,
			int[] offs,
			int zSize, int yzSize, int xyzSize,
			int blockSize,
			int xFrom, int xTo,
			int yFrom, int yTo,
			int zFrom, int zTo) {

		this.blockSize = blockSize;
		
		this.f = f;
		this.offs = offs;
		this.zSize = zSize;
		this.yzSize = yzSize;
		this.xyzSize = xyzSize;
		
		this.xFrom = xFrom;
		this.xTo = xTo;
		this.yFrom = yFrom;
		this.yTo = yTo;
		this.zFrom = zFrom;
		this.zTo = zTo;
		
		currentX = xFrom;
		currentY = yFrom;
		currentZ = zFrom;

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
		
		assert sitesToExtract > 0;
		
		double[] siteData = new double[sitesToExtract * 19];
		//Debug
		for(int i = 0; i < siteData.length; ++i) {
			siteData[i] = Double.NaN;
		}
		int[] sitePos = new int[sitesToExtract * 3];
		for(int i = 0; i < sitePos.length; ++i) {
			sitePos[i] = -1;
		}

		assert currentX >= xFrom && currentX < xTo;
		assert currentY >= yFrom && currentY < yTo;
		assert currentZ >= zFrom && currentZ < zTo;

		// Extract from current valid sequence
		int sitesToExtractFromCurrentSequence = Math.min(zTo - currentZ,
				sitesToExtract);
		sitesToExtract -= sitesToExtractFromCurrentSequence;
		int zStop = currentZ + sitesToExtractFromCurrentSequence;
		assert zStop > currentZ && zStop <= zTo;
		assert currentX >= xFrom && currentX < xTo;
		assert currentY >= yFrom && currentY < yTo;
		int posOff = 0;
		for(int z = currentZ; z < zStop; ++z) {
			sitePos[posOff] = currentX;
			sitePos[posOff + 1] = currentY;
			sitePos[posOff + 2] = z;
			posOff += 3;
		}

		extractValuesForCurrentSequence(siteData, 0, zStop);

		int extractedSitesForBlock = sitesToExtractFromCurrentSequence;
		currentZ = zStop;
		if(currentZ == zTo) {
			currentZ = zFrom;
			++currentY;
			if(currentY == yTo) {
				currentY = yFrom;
				++currentX;
			}
		}

		// Iterate on valid sequences
		while(sitesToExtract > 0) {
			
			// Extract from current valid sequence
			sitesToExtractFromCurrentSequence = Math.min(zTo - currentZ,
					sitesToExtract);
			sitesToExtract -= sitesToExtractFromCurrentSequence;
			zStop = currentZ + sitesToExtractFromCurrentSequence;
			assert zStop > currentZ && zStop <= zTo;
			assert currentX >= xFrom && currentX < xTo;
			assert currentY >= yFrom && currentY < yTo;
			posOff = extractedSitesForBlock * 3;
			for(int z = currentZ; z < zStop; ++z) {
				sitePos[posOff] = currentX;
				sitePos[posOff + 1] = currentY;
				sitePos[posOff + 2] = z;
				posOff += 3;
			}

			extractValuesForCurrentSequence(siteData, extractedSitesForBlock,
					zStop);

			extractedSitesForBlock += sitesToExtractFromCurrentSequence;
			currentZ = zStop;
			if(currentZ == zTo) {
				currentZ = zFrom;
				++currentY;
				if(currentY == yTo) {
					currentY = yFrom;
					++currentX;
				}
			}

		}

		return new MurphyConstrainedBlock(sitePos, siteData, f, offs, zSize,
				yzSize, xyzSize);
	}

	/**
	 * @param siteData
	 * @param currentPosInSiteData
	 * @param zStop
	 */
	private void extractValuesForCurrentSequence(double[] siteData,
			int extractedSitesForBlock, int zStop) {

		for(int q = 0; q < 19; ++q) {
			int bOff = offs[q];
			int toExtractFrom = bOff + currentX*yzSize + currentY*zSize + currentZ;
			int toExtractTo = bOff + currentX*yzSize + currentY*zSize + zStop;
			
			assert toExtractFrom <= toExtractTo;

			// Case all values are consecutive
			int off = extractedSitesForBlock * 19 + q;
			for(int pos = toExtractFrom; pos < toExtractTo; ++pos) {

				siteData[off] = f[q][pos];
				off += 19;

			}
		}
	}
	
}
