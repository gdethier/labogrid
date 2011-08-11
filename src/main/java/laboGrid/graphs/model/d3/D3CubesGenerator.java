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
package laboGrid.graphs.model.d3;

public class D3CubesGenerator extends D3BlockGenerator {

	@Override
	protected int[] calculateQFactors(int minSubLatticesCount) {
		int xSize = size[0];
		int ySize = size[1];
		int zSize = size[2];

		int cubeSide = (int) Math.pow((float) xSize*ySize*zSize/minSubLatticesCount, 1.f/3);
		while((xSize/cubeSide) * (ySize/cubeSide) * (zSize/cubeSide) < minSubLatticesCount) {
			--cubeSide;
		}
		if(cubeSide <= 3) {
			cubeSide = 3;
		}
		
		int[] optQ = new int[3];
		optQ[0] = xSize/cubeSide;
		optQ[1] = ySize/cubeSide;
		optQ[2] = zSize/cubeSide;
		
		return optQ;
	}

}
