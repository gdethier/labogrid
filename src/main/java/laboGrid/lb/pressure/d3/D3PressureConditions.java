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
package laboGrid.lb.pressure.d3;

import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.pressure.PressureConditions;
import laboGrid.lb.solid.Solid;
import laboGrid.lb.solid.d3.D3Solid;

public abstract class D3PressureConditions implements PressureConditions {

	protected int flowDirection;
	protected double rhoIn, rhoOut;
	
	protected int xSize, ySize, zSize;
	protected D3Lattice d3Fluid;
	protected D3Solid d3Solid;
	
	private int siteSize;
	

	public D3PressureConditions(int flowDirection,
			double rhoIn, double rhoOut) {
		
		this.flowDirection = flowDirection;
		this.rhoIn = rhoIn;
		this.rhoOut = rhoOut;

	}
	
	
	public D3PressureConditions(int flowDirection,
			double rhoIn, double rhoOut, D3Lattice fluid, D3Solid solid) {
		
		this(flowDirection, rhoIn, rhoOut);

		setFluidAndSolid(fluid, solid);

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
	
	
	public void apply() {
		
		double[] site = new double[siteSize];

		// X
		if(flowDirection == 0) {
//			System.out.println("Applying pressure X.");

			for(int y = 0; y < ySize; ++y) {
				for(int z = 0; z < zSize; ++z) {
					if(rhoIn > 0 && d3Solid.at(0, y, z) == Solid.FLUID) {
						d3Fluid.getSiteDensities(0, y, z, site);					
						inXPressure(site, rhoIn);
						d3Fluid.setSiteDensities(0, y, z, site);
					} 
//					else {
//						System.out.println("No pressure condition on site (rhoIn="+rhoIn+"): "+IntegerVector.toString(new int[] {0, y, z}));
//					}
					if(rhoOut > 0 && d3Solid.at(xSize-1, y, z) == Solid.FLUID) {
						d3Fluid.getSiteDensities(xSize-1, y, z, site);
						outXPressure(site, rhoOut);
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
					if(rhoIn > 0 && d3Solid.at(x, 0, z) == Solid.FLUID) {
						d3Fluid.getSiteDensities(x, 0, z, site);
						inYPressure(site, rhoIn);
						d3Fluid.setSiteDensities(x, 0, z, site);
					}
					if(rhoOut > 0 && d3Solid.at(x, ySize-1, z) == Solid.FLUID) {
						d3Fluid.getSiteDensities(x, ySize-1, z, site);
						outYPressure(site, rhoOut);
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

					if(rhoIn > 0 && d3Solid.at(x, y, 0) == Solid.FLUID) {
						d3Fluid.getSiteDensities(x, y, 0, site);
						inZPressure(site, rhoIn);
						d3Fluid.setSiteDensities(x, y, 0, site);
					}

					if(rhoOut > 0 && d3Solid.at(x, y, zSize-1) == Solid.FLUID) {
						d3Fluid.getSiteDensities(x, y, zSize-1, site);
						outZPressure(site, rhoOut);
						d3Fluid.setSiteDensities(x, y, zSize-1, site);
					}
				}	
			}
		}
	}

	protected abstract void outXPressure(double[] site, double rhoOut);
	protected abstract void inXPressure(double[] site, double rhoIn);
	protected abstract void inYPressure(double[] site, double rhoIn);
	protected abstract void outYPressure(double[] site, double rhoOut);
	protected abstract void inZPressure(double[] site, double rhoIn);
	protected abstract void outZPressure(double[] site, double rhoOut);

}
