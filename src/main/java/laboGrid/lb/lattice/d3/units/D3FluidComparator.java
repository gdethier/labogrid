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
package laboGrid.lb.lattice.d3.units;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.d3.D3Lattice;

public class D3FluidComparator {
	
	/**
	 * @param size
	 * @param refFluid
	 * @param testFluid
	 * @throws LBException
	 */
	public static void checkD3Fluids(D3Lattice refFluid, D3Lattice testFluid, int maxErrors) throws LBException {
		if(refFluid.getXSize() != testFluid.getXSize() ||
			refFluid.getYSize() != testFluid.getYSize() ||
			refFluid.getZSize() != testFluid.getZSize())
			throw new Error("Lattices do not have the same size");

		int xSize = refFluid.getXSize();
		int ySize = refFluid.getYSize();
		int zSize = refFluid.getZSize();
		
		String errorMsg = "";

		int errorsCount = 0;
		for(int x = 0; x < xSize; ++x)
			for(int y = 0; y < ySize; ++y)
				for(int z = 0; z < zSize; ++z) {

					boolean errorFound = false;
					boolean[] errors = new boolean[19];

					for(int c = 0; c < 19; ++c) {

						double refVal = refFluid.getDensity(c, x, y, z);
						double testVal = testFluid.getDensity(c, x, y, z);

						if((Double.isNaN(refVal) && ! Double.isNaN(testVal)) ||
							( ! Double.isNaN(refVal) && Double.isNaN(testVal)) ||
								(( ! Double.isNaN(refVal) && ! Double.isNaN(testVal)) && refVal != testVal)) {
//							System.out.println(refVal+" <> "+testVal);
							errorFound = true;
							errors[c] = true;
						} else {
							errors[c] = false;
						}

					}
					
					if(errorFound) {
						
						errorMsg += "\nError found at position ("+
							x+","+y+","+z+") for velocities: ";
						for(int i = 0; i < 19; ++i) {
							if(errors[i])
								errorMsg += (i+" ");
						}
						
						++errorsCount;
						
						if(errorsCount == maxErrors) {
							throw new LBException("Enough errors found: "+errorMsg);
						}
					
					}
					
				}

		if(errorsCount > 0)
			throw new LBException("Errors found: "+errorMsg);
	}

}
