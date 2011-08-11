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
package laboGrid.lb.lattice.d3.q19.units;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.BlockIterator;
import laboGrid.lb.lattice.LatticeBlock;
import laboGrid.lb.lattice.d3.q19.off.D3Q19FluidOffProp;
import laboGrid.lb.lattice.d3.q19.simple.D3Q19FluidSimpleProp;



public class TestBlockIterators {
	
	private D3Q19FluidSimpleProp simpleFluid;
	private D3Q19FluidOffProp offFluid;
	
	@org.junit.Before
	public void init() throws LBException {
		int latticeSize = 10;

		// Generate identical fluids
		simpleFluid =
			new D3Q19FluidSimpleProp(latticeSize, latticeSize, latticeSize);

		offFluid =
			new D3Q19FluidOffProp(latticeSize, latticeSize, latticeSize);

		simpleFluid.setRandom();
		double[] tmp = new double[19];
		for(int x = 0; x < latticeSize; ++x)
			for(int y = 0; y < latticeSize; ++y)
				for(int z = 0; z < latticeSize; ++z) {
					simpleFluid.getSiteDensities(x, y, z, tmp);
					offFluid.setSiteDensities(x, y, z, tmp);
				}
	}

	@org.junit.Test
	public void testUnconstrainedBlockIteration() throws LBException {
		int latticeSize = simpleFluid.getXSize();
		int maximumSize = latticeSize * latticeSize * latticeSize;
		for(int blockSize = 1; blockSize < maximumSize; ++blockSize) {
			BlockIterator simpleIt = simpleFluid.getBlockIterator(blockSize);
			BlockIterator offIt = offFluid.getBlockIterator(blockSize);
			
			testIterators(blockSize, simpleIt, offIt);
		}
	}
	
	@org.junit.Test
	public void testConstrainedBlockIteration1() throws LBException {
		int latticeSize = simpleFluid.getXSize();
		int maximumSize = latticeSize * latticeSize * latticeSize;
		for(int blockSize = 1; blockSize < maximumSize; ++blockSize) {
			BlockIterator simpleIt = simpleFluid.getBlockIterator(blockSize,
					0, latticeSize, 0, latticeSize, 0, latticeSize);
			BlockIterator offIt = offFluid.getBlockIterator(blockSize,
					0, latticeSize, 0, latticeSize, 0, latticeSize);

			testIterators(blockSize, simpleIt, offIt);
		}
	}
	
	@org.junit.Test
	public void testConstrainedBlockIteration2() throws LBException {
		int latticeSize = simpleFluid.getXSize();
		int maximumSize = latticeSize * latticeSize * latticeSize;
		for(int blockSize = 1; blockSize < maximumSize; ++blockSize) {
			BlockIterator simpleIt = simpleFluid.getBlockIterator(blockSize,
					1, latticeSize-1, 1, latticeSize-1, 1, latticeSize-1);
			BlockIterator offIt = offFluid.getBlockIterator(blockSize,
					1, latticeSize-1, 1, latticeSize-1, 1, latticeSize-1);

			testIterators(blockSize, simpleIt, offIt);
		}
	}
	
	@org.junit.Test
	public void testUnconstrainedLatticeUpdate() throws LBException {
		int latticeSize = simpleFluid.getXSize();
		int maximumSize = latticeSize * latticeSize * latticeSize;
		for(int blockSize = 1; blockSize < maximumSize; ++blockSize) {
			BlockIterator simpleIt = simpleFluid.getBlockIterator(blockSize);
			BlockIterator offIt = offFluid.getBlockIterator(blockSize);
			
			testUpdate(blockSize, simpleIt, offIt);
			assertTrue(simpleFluid.equals(offFluid));
		}
	}

	@org.junit.Test
	public void testConstrainedLatticeUpdate1() throws LBException {
		int latticeSize = simpleFluid.getXSize();
		int maximumSize = latticeSize * latticeSize * latticeSize;
		for(int blockSize = 1; blockSize < maximumSize; ++blockSize) {
			BlockIterator simpleIt = simpleFluid.getBlockIterator(blockSize,
					0, latticeSize, 0, latticeSize, 0, latticeSize);
			BlockIterator offIt = offFluid.getBlockIterator(blockSize,
					0, latticeSize, 0, latticeSize, 0, latticeSize);

			testUpdate(blockSize, simpleIt, offIt);
			assertTrue(simpleFluid.equals(offFluid));
		}

	}
	
	@org.junit.Test
	public void testConstrainedLatticeUpdate2() throws LBException {
		int latticeSize = simpleFluid.getXSize();
		int maximumSize = latticeSize * latticeSize * latticeSize;
		for(int blockSize = 1; blockSize < maximumSize; ++blockSize) {
			BlockIterator simpleIt = simpleFluid.getBlockIterator(blockSize,
					1, latticeSize-1, 1, latticeSize-1, 1, latticeSize-1);
			BlockIterator offIt = offFluid.getBlockIterator(blockSize,
					1, latticeSize-1, 1, latticeSize-1, 1, latticeSize-1);

			testUpdate(blockSize, simpleIt, offIt);
			assertTrue(simpleFluid.equals(offFluid));
		}

	}

	private static void testIterators(int blockSize,
			BlockIterator simpleConsIt, BlockIterator offConsIt) {
		
		while(simpleConsIt.hasNext()) {
			
			assert offConsIt.hasNext();
			
			LatticeBlock simpleBlock = simpleConsIt.next();
			LatticeBlock offBlock = offConsIt.next();
			
			if( ! simpleBlock.equals(offBlock)) {

				if(simpleBlock.size() != offBlock.size()) {
					throw new Error("Blocks have not same size.");
				} else {
					
					for(int i = 0; i < blockSize; ++i) {

						int thisX = simpleBlock.getX(i);
						int otherX = offBlock.getX(i);
						int thisY = simpleBlock.getY(i);
						int otherY = offBlock.getY(i);
						int thisZ = simpleBlock.getZ(i);
						int otherZ = offBlock.getZ(i);
						
						if(thisX != otherX ||
							thisY != otherY ||
							thisZ != otherZ) {
							String msg = "Positions are different for block: "+i;
							msg += "x: "+thisX+"<>"+otherX;
							msg += "y: "+thisY+"<>"+otherY;
							msg += "z: "+thisZ+"<>"+otherZ;
							throw new Error(msg);
						}

						double[] thisData = new double[19];
						simpleBlock.getSiteData(i, thisData);
						double[] otherData = new double[19];
						offBlock.getSiteData(i, otherData);
						
						if( ! Arrays.equals(thisData, otherData)) {
							System.err.println("Values are different.");
							for(int q = 0; q < 19; ++q) {
								if(thisData[q] != otherData[q]) {
									throw new Error("q="+q+" : "+thisData[q]+"<>"+otherData[q]);
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	private static void testUpdate(int blockSize,
			BlockIterator it1, BlockIterator it2) {
		
		// Test update data for it1
		while(it1.hasNext()) {
			
			LatticeBlock simpleBlock = it1.next();
			
			double[] oldValue = new double[19];
			double[] newValue = new double[19];
			double[] values1 = new double[19];
			for(int i = 0; i < simpleBlock.size(); ++i) {

				simpleBlock.getSiteData(i, oldValue);
				for(int j = 0; j < 19; ++j)
					newValue[j] = oldValue[j] * 2;

				simpleBlock.updateData(i, newValue);
				
				// Test updateData
				simpleBlock.getSiteData(i, values1);
				
				for(int j = 0; j < 19; ++j)
					if(values1[j] != newValue[j]) {
						assert false;
					}
				
				simpleBlock.updateLattice();
			}
			
		}
		
		// Test update data for it2
		while(it2.hasNext()) {
			
			LatticeBlock simpleBlock = it2.next();
			
			double[] oldValue = new double[19];
			double[] newValue = new double[19];
			double[] values1 = new double[19];
			for(int i = 0; i < simpleBlock.size(); ++i) {

				simpleBlock.getSiteData(i, oldValue);
				for(int j = 0; j < 19; ++j)
					newValue[j] = oldValue[j] * 2;

				simpleBlock.updateData(i, newValue);
				
				// Test updateData
				simpleBlock.getSiteData(i, values1);
				
				for(int j = 0; j < 19; ++j)
					if(values1[j] != newValue[j]) {
						assert false;
					}

				simpleBlock.updateLattice();
			}
			
		}
	}

}
