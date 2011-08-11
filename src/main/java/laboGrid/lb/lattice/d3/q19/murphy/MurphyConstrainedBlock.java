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

import laboGrid.lb.lattice.d3.q19.D3Q19LatticeBlock;

public class MurphyConstrainedBlock extends D3Q19LatticeBlock {
	
	private double[][] f;
	private int[] offs;
	private int zSize, yzSize, xyzSize;


	public MurphyConstrainedBlock(int[] sitePos, double[] data,
			double[][] f,
			int[] offs,
			int zSize, int yzSize, int xyzSize) {
		super(data, sitePos);
		
		this.f = f;
		this.offs = offs;
		this.zSize = zSize;
		this.yzSize = yzSize;
		this.xyzSize = xyzSize;
	}

	@Override
	public void updateLattice() {
		
		for(int sIndex = 0; sIndex < blockSize;
		/* increment in loop */) {

			int posOff = sIndex * 3;
			int currentSeqX = sitePos[posOff];
			int currentSeqY = sitePos[posOff + 1];
			int zStart = sitePos[posOff + 2];
			int xOff = currentSeqX * yzSize;
			int yOff = currentSeqY * zSize;
			
			// Search sequence end
			int validSequenceEnd;
			if(sIndex == blockSize - 1) {

				validSequenceEnd = blockSize;
				posOff += 3;

			} else {

				validSequenceEnd = sIndex + 1;
				int prevZ = sitePos[posOff + 2];
				posOff += 3;
				int currentX = sitePos[posOff];
				int currentY = sitePos[posOff + 1];
				int currentZ = sitePos[posOff + 2];
				while(currentX == currentSeqX &&
						currentY == currentSeqY &&
						prevZ == currentZ -1 &&
						validSequenceEnd < blockSize) {
					currentX = sitePos[posOff];
					currentY = sitePos[posOff + 1];
					prevZ = currentZ;
					currentZ = sitePos[posOff + 2];
					
					++validSequenceEnd;
					posOff += 3;
				}

			}

			// Copy sequence back to f
			int zStop = sitePos[posOff - 1];
			for(int q = 0; q < 19; ++q) {
				
				int bOff = offs[q];
				int inFStart = bOff + xOff + yOff + zStart;
				int inFStop = bOff + xOff + yOff + zStop;
				
				assert inFStart <= inFStop : "No circular arrays";

				int siteDataOff = sIndex*19 + q;
				for(int i = inFStart; i <= inFStop; ++i) {
					
					f[q][i] = siteData[siteDataOff];
					siteDataOff += 19;

				}
			}

			// Prepare next sequence copy
			sIndex = validSequenceEnd;

		}

	}

}
