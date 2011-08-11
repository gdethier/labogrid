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

import laboGrid.lb.lattice.d3.q19.D3Q19LatticeBlock;

public class UnconstrainedBlock extends D3Q19LatticeBlock {
	
	private double[][][][] f;

	public UnconstrainedBlock(double[][][][] f, double[] data, int[] pos) {
		super(data, pos);
		this.f = f;
	}

	@Override
	public void updateLattice() {
		
		int sitePosPos = 0;
		int siteDataPos = 0;
		for(int s = 0; s < blockSize; ++s) {
			
			int x = sitePos[sitePosPos];
			int y = sitePos[sitePosPos + 1];
			int z = sitePos[sitePosPos + 2];

			for(int q = 0; q < 19; ++q)
				f[x][y][z][q] = siteData[siteDataPos + q];

			sitePosPos += 3;
			siteDataPos += 19;

		}
		
	}

}
