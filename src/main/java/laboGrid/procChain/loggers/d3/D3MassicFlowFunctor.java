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
package laboGrid.procChain.loggers.d3;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.lattice.d3.D3LatticeMacroVarsSlice;
import laboGrid.lb.solid.d3.D3Solid;
import laboGrid.lb.solid.d3.D3SolidSlice;


public class D3MassicFlowFunctor {

	public static double calculateXYReynolds(D3Slice s, double omega, int dim) {
		return calculateReynolds(s.getMeanZSpeed(), omega, dim);
	}
	
	private static double calculateReynolds(double umean, double omega, int dim) {
		double v = (1. / 6.) * ((2. / omega) - 1.);
		return umean * dim / v;
	}

	public static double[] calculateZQMassicFlow(D3Lattice fluid, D3Solid solid) throws LBException {
		int xSize = fluid.getXSize();
		int ySize = fluid.getYSize();
		int zSize = fluid.getZSize();
		
		double[] toReturn = new double[zSize];
		
		for(int z = 0; z < zSize; ++z) {
			D3LatticeMacroVarsSlice s = fluid.getXYMacroVars(z);
			D3SolidSlice solid2 = solid.getXYBooleans(z);
			for(int x = 0; x < xSize; ++x) {
				for(int y = 0; y < ySize; ++y) {
					if(! solid2.isFluid(x, y)) {
						continue;
					}
					toReturn[z] += s.getLocalDensity(x, y) * s.getZSpeed(x, y);
				}
			}
		}
		
		return toReturn;
	}

	public static double calculateXZReynolds(D3Slice s, double omega, int dim) {
		return calculateReynolds(s.getMeanYSpeed(), omega, dim);
	}

	public static double calculateYZReynolds(D3Slice s, double omega, int dim) {
		return calculateReynolds(s.getMeanXSpeed(), omega, dim);
	}

	public static double[] calculateYQMassicFlow(D3Lattice fluid, D3Solid solid) throws LBException {
		int xSize = fluid.getXSize();
		int ySize = fluid.getYSize();
		int zSize = fluid.getZSize();
		
		double[] toReturn = new double[ySize];
		
		for(int y = 0; y < ySize; ++y) {
			D3LatticeMacroVarsSlice s = fluid.getXZMacroVars(y);
			D3SolidSlice solid2 = solid.getXZBooleans(y);
			for(int x = 0; x < xSize; ++x) {
				for(int z = 0; z < zSize; ++z) {
					if(! solid2.isFluid(x, z)) {
						continue;
					}
					toReturn[z] += s.getLocalDensity(x, z) * s.getYSpeed(x, z);
				}
			}
		}
		
		return toReturn;
	}

	public static double[] calculateXQMassicFlow(D3Lattice fluid, D3Solid solid) throws LBException {
		int xSize = fluid.getXSize();
		int ySize = fluid.getYSize();
		int zSize = fluid.getZSize();
		
		double[] toReturn = new double[zSize];
		
		for(int x = 0; x < xSize; ++x) {
			D3LatticeMacroVarsSlice s = fluid.getYZMacroVars(x);
			D3SolidSlice solid2 = solid.getYZBooleans(x);
			for(int y = 0; y < ySize; ++y) {
				for(int z = 0; z < zSize; ++z) {
					if(! solid2.isFluid(y, z)) {
						continue;
					}
					toReturn[z] += s.getLocalDensity(y, z) * s.getXSpeed(y, z);
				}
			}
		}
		
		return toReturn;
	}
}
