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

import laboGrid.lb.lattice.d3.q19.D3Q19LatticeBlock;

public class UnconstrainedBlock extends D3Q19LatticeBlock {

	/** Position of the block in f. This position is normalized regarding 
	 * offset associated to each velocity so real starting position for
	 * velocity q is posInF + offs[q] */
	private int posInF;
	private int[] offs;
	private double[] f;
	
	private int xyzSize;
	
	public UnconstrainedBlock(
			int[] offs, double[] f, int xyzSize,
			int posInF,
			double[] siteData, int[] sitePos) {

		super(siteData, sitePos);
		
		this.posInF = posInF;

		this.offs = offs;
		this.f = f;
		this.xyzSize = xyzSize;

	}

	@Override
	public void updateLattice() {
		
		double[] velData = new double[blockSize];
		
		// Iterate on velocities
		for(int q = 0; q < 19; ++q) {
			
			int j = 0;
			for(int i = q; i < siteData.length; i += 19, ++j) {
				velData[j] = siteData[i];
			}

			// As f contains actually circular arrays, copy may be operated
			// in 2 times in order to copy until the tail of the array and
			// the rest at the head.
			int off = offs[q];
			int arrayStart = q * xyzSize;
			int arrayStop = arrayStart + xyzSize;
			int start = q * xyzSize + (off + posInF) % xyzSize;
			if(arrayStop - start >= blockSize) {

				int k = q;
				for(int i = 0; i < blockSize; ++i, k+=19)
					f[start + i] = siteData[k];

			} else {

				int firstCopyLength = arrayStop - start;

				int k = q;
				for(int i = 0; i < firstCopyLength; ++i, k += 19)
					f[start + i] = siteData[k];


				int secondCopyLength = blockSize - firstCopyLength;					
				for(int i = 0; i < secondCopyLength; ++i, k += 19)
					f[arrayStart + i] = siteData[k];

			}

		}
		
	}
	
}
