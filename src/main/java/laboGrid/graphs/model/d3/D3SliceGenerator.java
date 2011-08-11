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

import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.model.ModelGraphGenerator;
import laboGrid.lb.SubLattice;
import laboGrid.lb.lattice.LatticeDescriptor;

public class D3SliceGenerator implements ModelGraphGenerator {
	
	private int[] latticeSize;
	private int xSize, ySize, zSize;
	private int minSublatticesCount;
	private LatticeDescriptor lattDesc;


	public D3SliceGenerator() {
	}

	@Override
	public ModelGraph generateModelGraph() {

		int zSizePerSub = zSize / minSublatticesCount;
		int rest = zSize % minSublatticesCount;

		int velCount = lattDesc.getVelocitiesCount();

		// Create sublattices
		SubLattice[] subs = new SubLattice[minSublatticesCount];

		int i;
		int currentZ = 0;
		for(i = 0; i < rest && i < minSublatticesCount; ++i) {

			SubLattice s = new SubLattice(lattDesc);
			s.setSize(new int[] {xSize, ySize, zSizePerSub + 1});
			s.setPosition(new int[]{0,0,currentZ});
			s.setQPosition(new int[]{0,0,i});
			s.setId(i);
			subs[i] = s;
			
			currentZ += zSizePerSub + 1;

		}

		for(; i < minSublatticesCount; ++i) {

			SubLattice s = new SubLattice(lattDesc);
			s.setSize(new int[] {xSize, ySize, zSizePerSub});
			s.setPosition(new int[]{0,0,currentZ});
			s.setQPosition(new int[]{0,0,i});
			s.setId(i);
			subs[i] = s;
			
			currentZ += zSizePerSub;

		}


		// Connect sublattices
		for(int t = 0; t < subs.length; ++t) {
			int[] neighbors = new int[velCount];
			for(i = 0; i < velCount; ++i) {
				if(lattDesc.isRest(i))
					continue;
				
				int[] vel = lattDesc.getVector(i);
				int zComp = vel[2];
				
				if(zComp == 0) {
					neighbors[i] = t;
				} else {
					neighbors[i] = (t + zComp + subs.length) % subs.length;
				}
			}
		}

		SubLattice[][][] subsArray = new SubLattice[1][1][];
		subsArray[0][0] = subs;
		return new D3BlockModelGraph(latticeSize, subsArray);
	}

	@Override
	public void setParameters(int[] size, int minSubLatticesCount,
			LatticeDescriptor latticeDesc) {

		this.latticeSize = size;
		
		xSize = size[0];
		ySize = size[1];
		zSize = size[2];

		this.minSublatticesCount = minSubLatticesCount;
		this.lattDesc = latticeDesc;

	}

}
