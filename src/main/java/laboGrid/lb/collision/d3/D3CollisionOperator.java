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
package laboGrid.lb.collision.d3;

import laboGrid.lb.LBException;
import laboGrid.lb.collision.CollisionOperator;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.solid.Solid;
import laboGrid.lb.solid.d3.D3Solid;

public abstract class D3CollisionOperator implements CollisionOperator<D3Lattice, D3Solid> {
	
	protected D3Lattice d3Fluid;
	protected D3Solid d3Solid;
	
	protected int xSize, ySize, zSize;
	
	protected double[] site;


	public D3CollisionOperator() {
		site = new double[19];
	}


//	@Override
//	public void collide() throws LBException {
//		collide(0, xSize, 0, ySize, 0, zSize);
//	}


//	@Override
//	public void collideBorderSites() throws LBException {
//		
//		int xFrom, xTo, yFrom, yTo, zFrom, zTo;
//
//
//		// Entire plane z=0
//		xFrom = yFrom = zFrom = 0;
//		xTo = xSize;
//		yTo = ySize;
//		zTo = 1;
//		collide(xFrom, xTo, yFrom, yTo, zFrom, zTo);
//
//
//		// Entire plane z=zSize-1
//		xFrom = yFrom = 0;
//		zFrom = zSize - 1;
//		xTo = xSize;
//		yTo = ySize;
//		zTo = zSize;
//		collide(xFrom, xTo, yFrom, yTo, zFrom, zTo);
//
//		
//		// Partial plane x=0
//		xFrom = yFrom = 0;
//		zFrom = 1;
//		xTo = 1;
//		yTo = ySize;
//		zTo = zSize-1;
//		collide(xFrom, xTo, yFrom, yTo, zFrom, zTo);
//
//
//		// Partial plane x=xSize-1
//		xFrom = xSize-1;
//		yFrom = 0;
//		zFrom = 1;
//		xTo = xSize;
//		yTo = ySize;
//		zTo = zSize-1;
//		collide(xFrom, xTo, yFrom, yTo, zFrom, zTo);
//
//
//		// Partial plane y=0
//		xFrom = 1;
//		yFrom = 0;
//		zFrom = 1;
//		xTo = xSize-1;
//		yTo = 1;
//		zTo = zSize-1;
//		collide(xFrom, xTo, yFrom, yTo, zFrom, zTo);
//
//
//		// Partial plane y=xSize-1
//		xFrom = 1;
//		yFrom = ySize-1;
//		zFrom = 1;
//		xTo = xSize-1;
//		yTo = ySize;
//		zTo = zSize-1;
//		collide(xFrom, xTo, yFrom, yTo, zFrom, zTo);
//		
//	}

//	@Override
//	public void collideInternalSites() throws LBException {
//		collide(1, xSize-1, 1, ySize-1, 1, zSize-1);
//	}

	@Override
	public void setFluid(D3Lattice fluid) {
		this.d3Fluid = fluid;
		if(fluid != null) {
			xSize = fluid.getXSize();
			ySize = fluid.getYSize();
			zSize = fluid.getZSize();
		} else {
			xSize = ySize = zSize = 0;
		}
	}


	@Override
	public void setSolid(D3Solid solid) {
		this.d3Solid = solid;
	}

	@Override
	public void collide() throws LBException {
		
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				for(int z = 0; z < zSize; ++z) {

					d3Fluid.getSiteDensities(x, y, z, site);
					
					// Testing code
					for(int i = 0; i < 19; ++i) {
						if(Double.isNaN(site[i])) {
							throw new LBException("before collide NaN at vel. "+i);
						}
					}
					// end of testing code
					
					double[] tmpSite = site.clone();
					collide(d3Solid.at(x,y,z) == Solid.FLUID);

					// Testing code
					for(int i = 0; i < 19; ++i) {
						if(Double.isNaN(site[i])) {
							for(int j = 0; j < 19; ++j) {
								System.out.print(tmpSite[i]+" ");
							}
							System.out.println();
							for(int j = 0; j < 19; ++j) {
								System.out.print(site[j]+" ");
							}
							System.out.println();
							throw new LBException("Nan in collision at "+i+","+x+","+y+","+z);
						}
					}
					// end of testing code

					d3Fluid.setSiteDensities(x,y,z, site);
				}

			}
		}
	}
	
	protected abstract void collide(boolean siteIsFluid);

}
