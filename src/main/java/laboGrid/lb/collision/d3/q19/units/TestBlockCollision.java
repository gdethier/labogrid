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
package laboGrid.lb.collision.d3.q19.units;

import laboGrid.lb.LBException;
import laboGrid.lb.collision.CollisionOperator;
import laboGrid.lb.collision.d3.q19.D3Q19SRTBlockCollider;
import laboGrid.lb.collision.d3.q19.D3Q19SRTCollider;
import laboGrid.lb.lattice.BorderData;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.lattice.d3.q19.off.D3Q19FluidOffProp;
import laboGrid.lb.lattice.d3.q19.simple.D3Q19FluidSimpleProp;
import laboGrid.lb.pressure.d3.q19.D3Q19DoublePressureConditions;
import laboGrid.lb.solid.Solid;
import laboGrid.lb.solid.d3.D3SolidBitmap;
 
public class TestBlockCollision {

	@org.junit.Test
	public void compareSimpleAndBlockCollision() throws LBException {
		
		int timeSteps = 5;
		int latticeSize = 10;

		// Simple collision simulation
		D3Q19SRTCollider simpleColl = new D3Q19SRTCollider();
		simpleColl.setParameters(1, 0, 0, 0);

		D3Q19FluidOffProp simpleCollFluid =
			new D3Q19FluidOffProp(latticeSize, latticeSize, latticeSize);
		completeLB(simpleCollFluid, simpleColl, timeSteps);
		

		// Block collision simulation
		D3Q19SRTBlockCollider blockColl = new D3Q19SRTBlockCollider();
		blockColl.setParameters(1, 0, 0, 0, 2);
		
		D3Lattice blockSimpleCollFluid =
			new D3Q19FluidSimpleProp(latticeSize, latticeSize, latticeSize);
		completeLB(blockSimpleCollFluid, blockColl, timeSteps);
		
		D3Lattice blockOffCollFluid =
			new D3Q19FluidOffProp(latticeSize, latticeSize, latticeSize);
		completeLB(blockOffCollFluid, blockColl, timeSteps);
		
		
		// Comparison of fluids
		double[] val1 = new double[19];
		double[] val2 = new double[19];
		double[] val3 = new double[19];
		for(int x = 0; x < latticeSize; ++x) {
			for(int y = 0; y < latticeSize; ++y) {
				for(int z = 0; z < latticeSize; ++z) {
					
					simpleCollFluid.getSiteDensities(x, y, z, val1);
					blockOffCollFluid.getSiteDensities(x, y, z, val2);
					blockSimpleCollFluid.getSiteDensities(x, y, z, val3);
					
					for(int q = 0; q < 19; ++q) {
						
						double x1 = val1[q];
						double x2 = val2[q];
						double x3 = val3[q];
						if(x1 != x2) {
							throw new Error("Error for offset fluid at position "+
									"("+x+","+y+","+z+") and velocity "+q+": "+x1+"!="+x2);
						}
						
						if(x1 != x3) {
							throw new Error("Error for simple fluid at position "+
									"("+x+","+y+","+z+") and velocity "+q+": "+x1+"!="+x3);
						}
					}
				}
			}
		}
	}
	
	
	public long completeLB(D3Lattice fluid, CollisionOperator collider, int timeSteps)
	throws LBException {
		
		// Prepare simulation
		int xSize = fluid.getXSize();
		int ySize = fluid.getYSize();
		int zSize = fluid.getZSize();
		D3SolidBitmap solid = new D3SolidBitmap(xSize, ySize, zSize);
		solid.setFluid();
		
		solid.set(xSize/2, ySize/2, zSize/2, Solid.SOLID);
		solid.set(0, ySize/2, zSize/2, Solid.SOLID);
		solid.set(xSize/2, 0, zSize/2, Solid.SOLID);
		solid.set(xSize/2, ySize/2, 0, Solid.SOLID);
		
		D3Q19DoublePressureConditions bound =
			new D3Q19DoublePressureConditions(0, 1.001f, 0.999f);

		collider.setFluid(fluid);
		collider.setSolid(solid);

		bound.setFluidAndSolid(fluid, solid);
		
		fluid.setEquilibrium();


		// Simulation
		long startTime = System.currentTimeMillis();
		for(int t = 0; t < timeSteps; ++t) {

			BorderData[] out = new BorderData[19];
			for(int i = 0; i < 19; ++i)
				out[i] = fluid.getOutcomingDensities(i);

			fluid.inPlaceStream();
			fluid.invalidateEmptySites(solid);
			
			for(int i = 0; i < 19; ++i)
				if(out[i] != null) // ignore REST
					fluid.setIncomingDensities(out[i]);
			
			bound.apply();

			collider.collide();

		}
		long stopTime = System.currentTimeMillis();
		
		return stopTime - startTime;
	}

}
