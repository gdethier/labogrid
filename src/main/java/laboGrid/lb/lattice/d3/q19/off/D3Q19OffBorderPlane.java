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
package laboGrid.lb.lattice.d3.q19.off;

import java.util.Arrays;

import laboGrid.lb.lattice.BorderData;


public class D3Q19OffBorderPlane extends BorderData  {

	private static final long serialVersionUID = 1L;

	private double[][] data;
	
	public D3Q19OffBorderPlane() {
		super(-1);
	}

	public D3Q19OffBorderPlane(int link, double[][] data) {
		super(link);
		this.data = data;
	}
	
	
	public D3Q19OffBorderPlane(D3Q19OffBorderPlane other) {
		super(other.link, other.iteration, other.version);
		
		data = new double[other.data.length][];
		for(int i = 0; i < data.length; ++i) {
			data[i] = new double[other.data[i].length];
			System.arraycopy(other.data[i], 0, data[i], 0, data[i].length);
		}
	}


	public double[] getData(int id) {
		return data[id];
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(super.equals(o)) {
			
			if( ! (o instanceof D3Q19OffBorderPlane))
				return false;
			
			D3Q19OffBorderPlane plane = (D3Q19OffBorderPlane) o;
			
			return Arrays.equals(data[0], plane.data[0]) &&
				Arrays.equals(data[1], plane.data[1]) &&
				Arrays.equals(data[2], plane.data[2]) &&
				Arrays.equals(data[3], plane.data[3]) &&
				Arrays.equals(data[4], plane.data[4]);
			
		}
		
		return false;
		
	}


	@Override
	public BorderData clone() {
		return new D3Q19OffBorderPlane(this);
	}
}
