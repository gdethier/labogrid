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
package laboGrid.lb.velocity.d3;

import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.pressure.PressureConditions;
import laboGrid.lb.solid.Solid;
import laboGrid.lb.solid.d3.D3Solid;

public abstract class D3VelocityConditions implements PressureConditions {
	
	protected int xSize, ySize, zSize;
	protected int flowDirection;
	protected double uIn;
	protected D3Lattice d3Fluid;
	protected D3Solid d3Solid;
	
	private int siteSize;
	
	
	public D3VelocityConditions(int flowDirection,
			double uIn, D3Lattice fluid, D3Solid solid) {
		
		this.flowDirection = flowDirection;
		this.uIn = uIn;
		
		setFluidAndSolid(fluid, solid);
	}

	@Override
	public void apply() {
		
		double[] site = new double[siteSize];

		// X
		if(flowDirection == 0) {
//			System.out.println("Applying pressure X.");

			for(int y = 0; y < ySize; ++y) {
				for(int z = 0; z < zSize; ++z) {
					if(uIn > 0 && d3Solid.at(0, y, z) == Solid.FLUID) {
						d3Fluid.getSiteDensities(0, y, z, site);					
						inXVelocity(site, uIn);
						d3Fluid.setSiteDensities(0, y, z, site);
					} 
//					else {
//						System.out.println("No pressure condition on site (rhoIn="+rhoIn+"): "+IntegerVector.toString(new int[] {0, y, z}));
//					}
					if(d3Solid.at(xSize-1, y, z) == Solid.FLUID) {
						d3Fluid.getSiteDensities(xSize-2, y, z, site);
						d3Fluid.setSiteDensities(xSize-1, y, z, site);
					}
					
				}	
			}
		}
		
		// Y
		if(flowDirection == 1) {
//			System.out.println("Applying pressure Y.");
			
			for(int x = 0; x < xSize; ++x) {
				for(int z = 0; z < zSize; ++z) {
					if(uIn > 0 && d3Solid.at(x, 0, z) == Solid.FLUID) {
						d3Fluid.getSiteDensities(x, 0, z, site);
						inYVelocity(site, uIn);
						d3Fluid.setSiteDensities(x, 0, z, site);
					}
					if(d3Solid.at(x, ySize-1, z) == Solid.FLUID) {
						d3Fluid.getSiteDensities(x, ySize-2, z, site);
						d3Fluid.setSiteDensities(x, ySize-1, z, site);
					}
				}	
			}
		}

		// Z
		if(flowDirection == 2) {
//			System.out.println("Applying pressure Z.");
			
			for(int x = 0; x < xSize; ++x) {
				for(int y = 0; y < ySize; ++y) {

					if(uIn > 0 && d3Solid.at(x, y, 0) == Solid.FLUID) {
						d3Fluid.getSiteDensities(x, y, 0, site);
						inZVelocity(site, uIn);
						d3Fluid.setSiteDensities(x, y, 0, site);
					}

					if(d3Solid.at(x, y, zSize-1) == Solid.FLUID) {
						d3Fluid.getSiteDensities(x, y, zSize-2, site);
						d3Fluid.setSiteDensities(x, y, zSize-1, site);
					}
				}	
			}
		}

	}
	
	@Override
	public void setFluidAndSolid(Lattice fluid, Solid solid) {
		
		this.d3Fluid = (D3Lattice) fluid;
		this.d3Solid = (D3Solid) solid;
		
		if(solid != null) {
			
			xSize = d3Solid.getXSize();
			ySize = d3Solid.getYSize();
			zSize = d3Solid.getZSize();
		
		}
		
		if(fluid != null)
			siteSize = fluid.getLatticeDescriptor().getVelocitiesCount();

	}

	protected abstract void inXVelocity(double[] site, double uIn);
	protected abstract void inYVelocity(double[] site, double uIn);
	protected abstract void inZVelocity(double[] site, double uIn);

}
