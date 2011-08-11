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

import laboGrid.graphs.model.BlockModelGraph;
import laboGrid.lb.SubLattice;

public class D3BlockModelGraph extends BlockModelGraph {
	private static final long serialVersionUID = 1L;

	private SubLattice[][][] subsArray;

	public D3BlockModelGraph(int[] latticeSize, SubLattice[][][] subsArray) {
		this.latticeSize = latticeSize;
		this.subsArray = subsArray;
		
		this.qSize = new int[3];
		this.qSize[0] = subsArray.length;
		this.qSize[1] = subsArray[0].length;
		this.qSize[2] = subsArray[0][0].length;

		int numOfSubs = qSize[0] * qSize[1] * qSize[2];
		subsList = new SubLattice[numOfSubs];
		for(int i = 0; i < qSize[0]; ++i) {
			for(int j = 0; j < qSize[1]; ++j) {
				for(int k = 0; k < qSize[2]; ++k) {
					SubLattice sub = subsArray[i][j][k];
					subsList[sub.getId()] = sub;
				}
			}
		}
	}

	public SubLattice[][][] getSublatticesArray() {
		return subsArray;
	}
}
