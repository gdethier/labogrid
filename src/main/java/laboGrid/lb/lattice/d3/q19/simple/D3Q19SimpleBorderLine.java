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
package laboGrid.lb.lattice.d3.q19.simple;

import java.util.Arrays;

import laboGrid.lb.lattice.BorderData;


public class D3Q19SimpleBorderLine extends BorderData {

	private static final long serialVersionUID = 1L;
	
	private double[] data;

	
	public D3Q19SimpleBorderLine() {
		super(-1);
	}

	public D3Q19SimpleBorderLine(int link, double[] data) {
		super(link);
		
		this.data = data;
	}

	public double[] getData() {
		return data;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if( ! super.equals(o))
			return false;
		
		if( ! (o instanceof D3Q19SimpleBorderLine))
			return false;

		D3Q19SimpleBorderLine line = (D3Q19SimpleBorderLine) o;
		return Arrays.equals(data, line.data);

	}

	@Override
	public BorderData clone() {
		throw new Error("unimplemented");
	}

}
