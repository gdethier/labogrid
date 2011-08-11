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

import laboGrid.graphs.model.AbstractBlockGenerator;
import laboGrid.graphs.model.BlockModelGraph;
import laboGrid.lb.SubLattice;

public abstract class D3BlockGenerator extends AbstractBlockGenerator {

	@Override
	protected BlockModelGraph quantifyLattice(int[] qSize) {
		int lbXSize, lbYSize, lbZSize;
		lbXSize = size[0];
		lbYSize = size[1];
		lbZSize = size[2];
		
		int qxSize = qSize[0];
		int qySize = qSize[1];
		int qzSize = qSize[2];
		
		int xStep = lbXSize/qxSize;
		int yStep = lbYSize/qySize;
		int zStep = lbZSize/qzSize;
		
//		System.out.println("Space quantification parameters : ");
//		System.out.println("qSize : ("+qxSize+","+qySize+","+qzSize+")");
//		System.out.println("qStep : ("+xStep+","+yStep+","+zStep+")");
		
		SubLattice[][][] qLattice = new SubLattice[qxSize][qySize][qzSize];
		
		// Initializing LB nodes infos
		int rx = lbXSize%xStep;
		int ry = lbYSize%yStep;
		int rz = lbZSize%zStep;
		int nodeId = 0;
		int currentX = 0;
		int[] xSize = new int[qxSize];
		int[] ySize = new int[qySize];
		int[] zSize = new int[qzSize];
		for(int i = 0; i < qxSize; ++i) {
			if(rx == 0) {
				xSize[i] = xStep;
			} else {
				xSize[i] = xStep+1;
				--rx;
			}
		}
		for(int i = 0; i < qySize; ++i) {
			if(ry == 0) {
				ySize[i] = yStep;
			} else {
				ySize[i] = yStep+1;
				--ry;
			}
		}
		for(int i = 0; i < qzSize; ++i) {
			if(rz == 0) {
				zSize[i] = zStep;
			} else {
				zSize[i] = zStep+1;
				--rz;
			}
		}

		for(int i = 0; i < qxSize; ++i) {
			int currentY = 0;
			for(int j = 0; j < qySize; ++j) {
				int currentZ = 0;
				for(int k = 0; k < qzSize; ++k) {
					qLattice[i][j][k] = new SubLattice(latticeDesc);
					int[] nodeSize = new int[] {xSize[i], ySize[j], zSize[k]};
					int[] nodePosition = new int[] {currentX, currentY, currentZ};
					int[] nodeQPosition = new int[] {i, j, k};
					
					qLattice[i][j][k].setSize(nodeSize);
					qLattice[i][j][k].setId(nodeId);
					qLattice[i][j][k].setPosition(nodePosition);
					qLattice[i][j][k].setQPosition(nodeQPosition);
					
					qLattice[i][j][k].setFromBoundary(new boolean[] {i == 0, j == 0, k == 0});
					qLattice[i][j][k].setToBoundary(new boolean[] {i == qxSize-1, j == qySize-1, k == qzSize-1});
					
//					System.out.println("Sub-Lattice "+qLattice[i][j][k].getId()+" has position "+IntegerVector.toString(nodeQPosition)+" in qSpace (size="+IntegerVector.toString(qLattice[i][j][k].getSize())+")");

					++nodeId;
					currentZ += zSize[k];
				}
				currentY += ySize[j];
			}
			currentX += xSize[i];
		}

//		int subLatticesCount = qxSize * qySize * qzSize;
//		SubLattice[] subLattices = new SubLattice[subLatticesCount];
//		for(int x = 0; x < qxSize; ++x) {
//			for(int y = 0; y < qySize; ++y) {
//				for(int z = 0; z < qzSize; ++z) {
//					SubLattice s = qLattice[x][y][z];
//					subLattices[s.getId()] = s;
//				}
//			}
//		}
		
		for(int x = 0; x < qxSize; ++x) {
			for(int y = 0; y < qySize; ++y) {
				for(int z = 0; z < qzSize; ++z) {

					SubLattice currentNodeInfo = qLattice[x][y][z];
					int[] neighbors = new int[latticeDesc.getVelocitiesCount()];
					for(int q = 0; q < latticeDesc.getVelocitiesCount(); ++q) {

						if(latticeDesc.isRest(q))
							continue;

						int[] vel = latticeDesc.getVector(q);
						SubLattice infoNeighbor = qLattice[(x + vel[0]+qxSize)%qxSize][(y + vel[1]+qySize)%qySize][(z + vel[2]+qzSize)%qzSize];
						
						neighbors[q] = infoNeighbor.getId();
//						infoNeighbor.setNeighborOf(q, currentNodeInfo.getId());

					}
					
					currentNodeInfo.setNeighbors(neighbors);
				}
			}
		}

		D3BlockModelGraph mGraph = new D3BlockModelGraph(size, qLattice);
		return mGraph;
	}

}
