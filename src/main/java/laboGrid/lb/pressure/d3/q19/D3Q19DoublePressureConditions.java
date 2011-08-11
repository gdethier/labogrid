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
package laboGrid.lb.pressure.d3.q19;

import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;
import laboGrid.lb.pressure.d3.D3PressureConditions;
import laboGrid.lb.solid.d3.D3Solid;

public class D3Q19DoublePressureConditions extends D3PressureConditions {
	
	public D3Q19DoublePressureConditions(int flowDirection,
			double rhoIn, double rhoOut, D3Lattice fluid, D3Solid solid) {
		super(flowDirection, rhoIn, rhoOut, fluid, solid);
	}
	
	public D3Q19DoublePressureConditions(int flowDirection,
			double rhoIn, double rhoOut) {
		super(flowDirection, rhoIn, rhoOut);
	}
	
	protected void inXPressure(double[] data, double rhoIn) {
		
		double u = 1 - (data[D3Q19LatticeDescriptor.REST] + 
				data[D3Q19LatticeDescriptor.NORTH] + 
				data[D3Q19LatticeDescriptor.SOUTH] + 
				data[D3Q19LatticeDescriptor.UP] + 
				data[D3Q19LatticeDescriptor.DOWN] + 
				data[D3Q19LatticeDescriptor.UPNORTH] + 
				data[D3Q19LatticeDescriptor.DOWNNORTH] + 
				data[D3Q19LatticeDescriptor.UPSOUTH] + 
				data[D3Q19LatticeDescriptor.DOWNSOUTH] +
					2*(data[D3Q19LatticeDescriptor.NORTHWEST] + 
							data[D3Q19LatticeDescriptor.WEST] + 
							data[D3Q19LatticeDescriptor.SOUTHWEST] + 
							data[D3Q19LatticeDescriptor.UPWEST] + 
							data[D3Q19LatticeDescriptor.DOWNWEST]))/rhoIn;
		double c = (u*rhoIn)/6f;
	
		data[D3Q19LatticeDescriptor.EAST] = data[D3Q19LatticeDescriptor.WEST] + 2*c;
		data[D3Q19LatticeDescriptor.NORTHEAST] = data[D3Q19LatticeDescriptor.SOUTHWEST] + c;
		data[D3Q19LatticeDescriptor.SOUTHEAST] = data[D3Q19LatticeDescriptor.NORTHWEST] + c;
		data[D3Q19LatticeDescriptor.UPEAST] = data[D3Q19LatticeDescriptor.DOWNWEST] + c;
		data[D3Q19LatticeDescriptor.DOWNEAST] = data[D3Q19LatticeDescriptor.UPWEST] + c;
		
	}
	
	protected void outXPressure(double[] data, double rhoOut) {

		double u = 1 - (data[D3Q19LatticeDescriptor.REST] + 
				data[D3Q19LatticeDescriptor.NORTH] + 
				data[D3Q19LatticeDescriptor.SOUTH] + 
				data[D3Q19LatticeDescriptor.UP] + 
				data[D3Q19LatticeDescriptor.DOWN] + 
				data[D3Q19LatticeDescriptor.UPNORTH] + 
				data[D3Q19LatticeDescriptor.DOWNNORTH] + 
				data[D3Q19LatticeDescriptor.UPSOUTH] + 
				data[D3Q19LatticeDescriptor.DOWNSOUTH] +
					2*(data[D3Q19LatticeDescriptor.NORTHEAST] + 
							data[D3Q19LatticeDescriptor.EAST] + 
							data[D3Q19LatticeDescriptor.SOUTHEAST] + 
							data[D3Q19LatticeDescriptor.UPEAST] + 
							data[D3Q19LatticeDescriptor.DOWNEAST]))/rhoOut;
		double c = (u*rhoOut)/6f;
	
		data[D3Q19LatticeDescriptor.WEST] = data[D3Q19LatticeDescriptor.EAST] + 2*c;
		data[D3Q19LatticeDescriptor.SOUTHWEST] = data[D3Q19LatticeDescriptor.NORTHEAST] + c;
		data[D3Q19LatticeDescriptor.NORTHWEST] = data[D3Q19LatticeDescriptor.SOUTHEAST] + c;
		data[D3Q19LatticeDescriptor.DOWNWEST] = data[D3Q19LatticeDescriptor.UPEAST] + c;
		data[D3Q19LatticeDescriptor.UPWEST] = data[D3Q19LatticeDescriptor.DOWNEAST] + c;
		
	}
	
	protected void inYPressure(double[] data, double rhoIn) {

		double u = 1 - (data[D3Q19LatticeDescriptor.REST] + 
				data[D3Q19LatticeDescriptor.EAST] + 
				data[D3Q19LatticeDescriptor.UPEAST] + 
				data[D3Q19LatticeDescriptor.UP] + 
				data[D3Q19LatticeDescriptor.UPWEST] + 
				data[D3Q19LatticeDescriptor.WEST] + 
				data[D3Q19LatticeDescriptor.DOWNWEST] + 
				data[D3Q19LatticeDescriptor.DOWN] + 
				data[D3Q19LatticeDescriptor.DOWNEAST] +
					2*(data[D3Q19LatticeDescriptor.SOUTHWEST] + 
							data[D3Q19LatticeDescriptor.SOUTH] + 
							data[D3Q19LatticeDescriptor.SOUTHEAST] + 
							data[D3Q19LatticeDescriptor.UPSOUTH] + 
							data[D3Q19LatticeDescriptor.DOWNSOUTH]))/rhoIn;
		double c = (u*rhoIn)/6f;
	
		data[D3Q19LatticeDescriptor.NORTH] = data[D3Q19LatticeDescriptor.SOUTH] + 2*c;
		data[D3Q19LatticeDescriptor.NORTHEAST] = data[D3Q19LatticeDescriptor.SOUTHWEST] + c;
		data[D3Q19LatticeDescriptor.NORTHWEST] = data[D3Q19LatticeDescriptor.SOUTHEAST] + c;
		data[D3Q19LatticeDescriptor.UPNORTH] = data[D3Q19LatticeDescriptor.DOWNSOUTH] + c;
		data[D3Q19LatticeDescriptor.DOWNNORTH] = data[D3Q19LatticeDescriptor.UPSOUTH] + c;
		
	}
	
	protected void outYPressure(double[] data, double rhoOut) {

		double u = 1 - (data[D3Q19LatticeDescriptor.REST] +
				data[D3Q19LatticeDescriptor.EAST] +
				data[D3Q19LatticeDescriptor.UPEAST] +
				data[D3Q19LatticeDescriptor.UP] +
				data[D3Q19LatticeDescriptor.UPWEST] +
				data[D3Q19LatticeDescriptor.WEST] +
				data[D3Q19LatticeDescriptor.DOWNWEST] +
				data[D3Q19LatticeDescriptor.DOWN] +
				data[D3Q19LatticeDescriptor.DOWNEAST] +
				2*(data[D3Q19LatticeDescriptor.NORTHEAST] +
						data[D3Q19LatticeDescriptor.NORTH] +
						data[D3Q19LatticeDescriptor.NORTHWEST] +
						data[D3Q19LatticeDescriptor.UPNORTH] +
						data[D3Q19LatticeDescriptor.DOWNNORTH]))/rhoOut;
		double c = (u*rhoOut)/6f;
	
		data[D3Q19LatticeDescriptor.SOUTH] = data[D3Q19LatticeDescriptor.NORTH] + 2*c;
		data[D3Q19LatticeDescriptor.SOUTHWEST] = data[D3Q19LatticeDescriptor.NORTHEAST] + c;
		data[D3Q19LatticeDescriptor.SOUTHEAST] = data[D3Q19LatticeDescriptor.NORTHWEST] + c;
		data[D3Q19LatticeDescriptor.DOWNSOUTH] = data[D3Q19LatticeDescriptor.UPNORTH] + c;
		data[D3Q19LatticeDescriptor.UPSOUTH] = data[D3Q19LatticeDescriptor.DOWNNORTH] + c;
		
	}
	
	protected void inZPressure(double[] data, double rhoIn) {

		double u = 1 - (data[D3Q19LatticeDescriptor.REST] +
				data[D3Q19LatticeDescriptor.EAST] +
				data[D3Q19LatticeDescriptor.NORTHEAST] +
				data[D3Q19LatticeDescriptor.NORTH] +
				data[D3Q19LatticeDescriptor.NORTHWEST] +
				data[D3Q19LatticeDescriptor.WEST] +
				data[D3Q19LatticeDescriptor.SOUTHWEST] +
				data[D3Q19LatticeDescriptor.SOUTH] +
				data[D3Q19LatticeDescriptor.SOUTHEAST] +
				2*(data[D3Q19LatticeDescriptor.DOWNEAST] + 
						data[D3Q19LatticeDescriptor.DOWNWEST] +
						data[D3Q19LatticeDescriptor.DOWN] +
						data[D3Q19LatticeDescriptor.DOWNNORTH] +
						data[D3Q19LatticeDescriptor.DOWNSOUTH]))/rhoIn;
		double c = (u*rhoIn)/6f;
	
		data[D3Q19LatticeDescriptor.UP] = data[D3Q19LatticeDescriptor.DOWN] + 2*c;
		data[D3Q19LatticeDescriptor.UPEAST] = data[D3Q19LatticeDescriptor.DOWNWEST] + c;
		data[D3Q19LatticeDescriptor.UPWEST] = data[D3Q19LatticeDescriptor.DOWNEAST] + c;
		data[D3Q19LatticeDescriptor.UPNORTH] = data[D3Q19LatticeDescriptor.DOWNSOUTH] + c;
		data[D3Q19LatticeDescriptor.UPSOUTH] = data[D3Q19LatticeDescriptor.DOWNNORTH] + c;
		
	}
	
	protected void outZPressure(double[] data, double rhoOut) {

		double u = 1 - (data[D3Q19LatticeDescriptor.REST] +
				data[D3Q19LatticeDescriptor.EAST] +
				data[D3Q19LatticeDescriptor.NORTHEAST] +
				data[D3Q19LatticeDescriptor.NORTH] +
				data[D3Q19LatticeDescriptor.NORTHWEST] +
				data[D3Q19LatticeDescriptor.WEST] +
				data[D3Q19LatticeDescriptor.SOUTHWEST] +
				data[D3Q19LatticeDescriptor.SOUTH] +
				data[D3Q19LatticeDescriptor.SOUTHEAST] +
				2*(data[D3Q19LatticeDescriptor.UP] +
						data[D3Q19LatticeDescriptor.UPEAST] +
						data[D3Q19LatticeDescriptor.UPWEST] +
						data[D3Q19LatticeDescriptor.UPNORTH] +
						data[D3Q19LatticeDescriptor.UPSOUTH]))/rhoOut;
		double c = (u*rhoOut)/6f;
	
		data[D3Q19LatticeDescriptor.DOWN] = data[D3Q19LatticeDescriptor.UP] + 2*c;
		data[D3Q19LatticeDescriptor.DOWNEAST] = data[D3Q19LatticeDescriptor.UPWEST] + c;
		data[D3Q19LatticeDescriptor.DOWNWEST] = data[D3Q19LatticeDescriptor.UPEAST] + c;
		data[D3Q19LatticeDescriptor.DOWNNORTH] = data[D3Q19LatticeDescriptor.UPSOUTH] + c;
		data[D3Q19LatticeDescriptor.DOWNSOUTH] = data[D3Q19LatticeDescriptor.UPNORTH] + c;

	}

}
