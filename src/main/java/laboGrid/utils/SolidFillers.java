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
package laboGrid.utils;

import laboGrid.lb.lattice.LatticeDescriptor;
import laboGrid.lb.solid.Solid;
import laboGrid.lb.solid.d3.D3SolidBitmap;

public class SolidFillers {
	
	public static void partialCircularPipe(D3SolidBitmap solid, int xSize, int ySize, int xFrom, int xTo, int yFrom, int yTo) {
		
		int x = xSize, y = xSize, z = solid.getZSize();
		
		float u,v;
		float a = (x*x)/4.f, b = (y*y)/4.f;
		
		for(int i = xFrom; i < xTo; ++i) {
			
			u = i - x/2;
			for(int j = yFrom; j < yTo; ++j) {
				
				v = j - y/2;
				for(int k = 0; k < z; ++k) {
					if(((u*u)/a) + ((v*v)/b) >= 1) {
//						solid.set(i-xFrom,j-yFrom,k, Solid.SURFACESOLID);	
						solid.set(i-xFrom,j-yFrom,k, Solid.SOLID);
					}
					else {
						solid.set(i-xFrom,j-yFrom,k, Solid.FLUID);	
					}
				}
				
			}
			
		}

	}

	public static void circularPipe(D3SolidBitmap solid, LatticeDescriptor ld) {
		
		partialCircularPipe(solid, solid.getXSize(), solid.getYSize(), 0, solid.getXSize(), 0, solid.getYSize());
//		solid.detectSurfaceSolids(ld);
	}
	
	
	public static void rectangularPipe(D3SolidBitmap solid, LatticeDescriptor ld) {

		partialRectangularPipe(solid, solid.getXSize(), solid.getYSize(), 0, solid.getXSize(), 0, solid.getYSize());
//		solid.detectSurfaceSolids(ld);
	}

	public static void partialRectangularPipe(D3SolidBitmap solid, int xSize, int ySize, int xFrom, int xTo, int yFrom, int yTo) {
		
		int zSize = solid.getZSize();

		if(xFrom == 0) {
			for(int y = 0; y < solid.getYSize(); ++y) {
				for(int z = 0; z < zSize; ++z) {
					solid.set(0, y, z, Solid.SOLID);
				}
			}
		}
		
		if(xTo == xSize) {
			for(int y = 0; y < solid.getYSize(); ++y) {
				for(int z = 0; z < zSize; ++z) {
					solid.set(solid.getXSize()-1, y, z, Solid.SOLID);
				}
			}
		}
		
		if(yFrom == 0) {
			for(int x = 0; x < solid.getXSize(); ++x) {
				for(int z = 0; z < zSize; ++z) {
					solid.set(x, 0, z, Solid.SOLID);
				}
			}
		}
		
		if(yTo == ySize) {
			for(int x = 0; x < solid.getXSize(); ++x) {
				for(int z = 0; z < zSize; ++z) {
					solid.set(x, solid.getYSize()-1, z, Solid.SOLID);
				}
			}
		}

	}
}
