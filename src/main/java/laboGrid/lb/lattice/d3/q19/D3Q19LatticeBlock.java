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
package laboGrid.lb.lattice.d3.q19;

import java.util.Arrays;

import laboGrid.lb.lattice.LatticeBlock;



/**
 * The base D3Q19 lattice block.
 * 
 * @author dethier
 *
 */
public abstract class D3Q19LatticeBlock implements LatticeBlock {
	
	protected double[] siteData;
	protected int[] sitePos;
	protected int blockSize;

	
	public D3Q19LatticeBlock(double[] siteData, int[] sitePos) {

		this.siteData = siteData;
		this.sitePos = sitePos;

		assert siteData.length%19 == 0;
		blockSize = siteData.length / 19;

	}

	@Override
	public boolean equals(LatticeBlock block) {

		if(blockSize != block.size())
			return false;
		
		for(int i = 0; i < blockSize; ++i) {

			int thisX = getX(i);
			int otherX = block.getX(i);
			int thisY = getY(i);
			int otherY = block.getY(i);
			int thisZ = getZ(i);
			int otherZ = block.getZ(i);
			
			if(thisX != otherX ||
				thisY != otherY ||
				thisZ != otherZ) {
				return false;
			}

			double[] thisData = new double[19];
			getSiteData(i, thisData);
			double[] otherData = new double[19];
			block.getSiteData(i, otherData);
			
			if( ! Arrays.equals(thisData, otherData))
				return false;
			
		}

		return true;

	}

	@Override
	public void getSiteData(int sIndex, double[] site) {
		System.arraycopy(siteData, sIndex * 19, site, 0, 19);
	}

	@Override
	public int size() {
		return blockSize;
	}

	@Override
	public void updateData(int sIndex, double[] site) {
		System.arraycopy(site, 0, siteData, sIndex*19, 19);
	}

	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("blockSize=");
		sb.append(blockSize);
		sb.append("\nsites:\n");
		for(int i = 0; i < blockSize; ++i) {
			
			sb.append("block["+i+"] = [");
			for(int q = 0; q < 19; ++q) {
				
				sb.append(siteData[i*19 + q]);
				sb.append(",");

			}
			sb.append("]\n");

		}
		
		for(int i = 0; i < blockSize; ++i) {
			
			sb.append("pos["+i+"] = [");
			sb.append(getX(i));
			sb.append(",");
			sb.append(getY(i));
			sb.append(",");
			sb.append(getZ(i));
			sb.append("]\n");

		}
		
		return sb.toString();
		
	}
	
	@Override
	public int getX(int sIndex) {

		return sitePos[sIndex*3];

	}

	@Override
	public int getY(int sIndex) {

		return sitePos[sIndex*3 + 1];

	}

	@Override
	public int getZ(int sIndex) {

		return sitePos[sIndex*3 + 2];

	}
}
