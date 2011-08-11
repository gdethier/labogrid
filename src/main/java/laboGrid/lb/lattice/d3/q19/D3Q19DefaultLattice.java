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
package laboGrid.lb.lattice.d3.q19;

import laboGrid.lb.lattice.d3.q19.off.D3Q19FluidOffProp;

public class D3Q19DefaultLattice extends D3Q19FluidOffProp {

	private static final long serialVersionUID = 1L;

	public D3Q19DefaultLattice() {
	}

	public D3Q19DefaultLattice(int xSize, int ySize, int zSize) {
		super(xSize, ySize, zSize);
	}

	public D3Q19DefaultLattice(int[] spaceSize) {
		super(spaceSize);
	}
	
	@Override
	protected D3Q19DefaultLattice getFluidInstance(int xSize, int ySize, int zSize) {
		return new D3Q19DefaultLattice(xSize, ySize, zSize);
	}

	@Override
	public D3Q19DefaultLattice clone() {
		return new D3Q19DefaultLattice();
	}

}
