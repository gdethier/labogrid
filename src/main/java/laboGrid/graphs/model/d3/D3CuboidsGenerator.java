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

import java.util.LinkedList;

public class D3CuboidsGenerator extends D3BlockGenerator {

	@Override
	protected int[] calculateQFactors(int minSubLatticesCount) {
		int xSize = size[0];
		int ySize = size[1];
		int zSize = size[2];

		int currentValue = minSubLatticesCount;
		int currentDivider = 2;
		LinkedList<Integer> dividers = new LinkedList<Integer>();
		while(currentValue > 1) {
			if((currentValue % currentDivider) == 0) {
				dividers.add(currentDivider);
				currentValue /= currentDivider;
			} else {
				++currentDivider;
			}
		}

		int[] optQ = agregateDividers(dividers, xSize, ySize, zSize);
		
		return optQ;
	}
	
	private int[] agregateDividers(LinkedList<Integer> dividers,
			int xSize, int ySize, int zSize) {

		int qx = 1, qy = 1, qz = 1;
		int xSubSize = xSize, ySubSize = ySize, zSubSize = zSize;
		while( ! dividers.isEmpty()) {
			
			int d = dividers.removeLast();
			
			if(xSubSize >= ySubSize && xSubSize >= zSubSize) {

				qx *= d;
				xSubSize /= d;

			} else if(ySubSize >= xSubSize && ySubSize >= zSubSize) {

				qy *= d;
				ySubSize /= d;

			} else {

				qz *= d;
				zSubSize /= d;

			}
			
		}
		
		int[] optQ = new int[3];
		optQ[0] = qx;
		optQ[1] = qy;
		optQ[2] = qz;
		
		return optQ;
		
	}

}
