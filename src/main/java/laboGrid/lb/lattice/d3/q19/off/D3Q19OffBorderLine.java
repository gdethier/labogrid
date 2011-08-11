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


public class D3Q19OffBorderLine extends BorderData {

	private static final long serialVersionUID = 1L;
	
	private double[] line;
	
	
	public D3Q19OffBorderLine() {
		super(-1);
	}

	public D3Q19OffBorderLine(int link, double[] line) {
		super(link);
		
		this.line = line;
	}
	
	public D3Q19OffBorderLine(D3Q19OffBorderLine other) {
		super(other.link, other.iteration, other.version);
		line = new double[other.line.length];
		System.arraycopy(other.line, 0, line, 0, line.length);
	}

	public double[] getData() {
		return line;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(super.equals(o)) {
			
			if( ! (o instanceof D3Q19OffBorderLine))
				return false;
			
			D3Q19OffBorderLine line = (D3Q19OffBorderLine) o;
			
			return Arrays.equals(line.line, this.line);
			
		}
		
		return false;
		
	}

	@Override
	public BorderData clone() {
		return new D3Q19OffBorderLine(this);
	}

}
