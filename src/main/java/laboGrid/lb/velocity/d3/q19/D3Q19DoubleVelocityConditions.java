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
package laboGrid.lb.velocity.d3.q19;

import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;
import laboGrid.lb.solid.d3.D3Solid;
import laboGrid.lb.velocity.d3.D3VelocityConditions;

public class D3Q19DoubleVelocityConditions extends D3VelocityConditions {

	public D3Q19DoubleVelocityConditions(int flowDirection, double uIn, D3Lattice fluid,
			D3Solid solid) {
		super(flowDirection, uIn, fluid, solid);
	}

	@Override
	protected void inXVelocity(double[] data, double uIn) {

		double c= uIn / 6.;

		data[D3Q19LatticeDescriptor.EAST] = data[D3Q19LatticeDescriptor.WEST] + 2*c;  
		data[D3Q19LatticeDescriptor.NORTHEAST] = data[D3Q19LatticeDescriptor.SOUTHWEST] + c;
		data[D3Q19LatticeDescriptor.SOUTHEAST] = data[D3Q19LatticeDescriptor.NORTHWEST] + c;       
		data[D3Q19LatticeDescriptor.UPEAST] = data[D3Q19LatticeDescriptor.DOWNWEST] + c;       
		data[D3Q19LatticeDescriptor.DOWNEAST] = data[D3Q19LatticeDescriptor.UPWEST] + c;
		
	}

	@Override
	protected void inYVelocity(double[] data, double uIn) {
		
		double c= uIn / 6.;

		data[D3Q19LatticeDescriptor.NORTH] = data[D3Q19LatticeDescriptor.SOUTH] + 2*c;
		data[D3Q19LatticeDescriptor.NORTHEAST] = data[D3Q19LatticeDescriptor.SOUTHWEST] + c;
		data[D3Q19LatticeDescriptor.NORTHWEST] = data[D3Q19LatticeDescriptor.SOUTHEAST] + c;
		data[D3Q19LatticeDescriptor.UPNORTH] = data[D3Q19LatticeDescriptor.DOWNSOUTH] + c;
		data[D3Q19LatticeDescriptor.DOWNNORTH] = data[D3Q19LatticeDescriptor.UPSOUTH] + c;
		
	}

	@Override
	protected void inZVelocity(double[] data, double uIn) {
		
		double c= uIn / 6.;

		data[D3Q19LatticeDescriptor.UP] = data[D3Q19LatticeDescriptor.DOWN] + 2*c;
		data[D3Q19LatticeDescriptor.UPEAST] = data[D3Q19LatticeDescriptor.DOWNWEST] + c;
		data[D3Q19LatticeDescriptor.UPWEST] = data[D3Q19LatticeDescriptor.DOWNEAST] + c;
		data[D3Q19LatticeDescriptor.UPNORTH] = data[D3Q19LatticeDescriptor.DOWNSOUTH] + c;
		data[D3Q19LatticeDescriptor.UPSOUTH] = data[D3Q19LatticeDescriptor.DOWNNORTH] + c;

	}

}
