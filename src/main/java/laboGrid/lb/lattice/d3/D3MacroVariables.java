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
package laboGrid.lb.lattice.d3;

import java.io.Serializable;

public class D3MacroVariables implements Serializable {

	private static final long serialVersionUID = 1L;

	public double xSpeed, ySpeed, zSpeed, localDensity;
	
	public D3MacroVariables() {
	}
	
	public D3MacroVariables(double xSpeed, double ySpeed, double zSpeed,
			double localDensity) {

		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.zSpeed = zSpeed;
		this.localDensity = localDensity;
		
	}

	public String toString() {
		
		return "xSpeed= "+xSpeed+" , ySpeed= "+ySpeed+" , zSpeed= "+zSpeed+" , dloc= "+localDensity;
		
	}
	
	public boolean equals(Object o) {
		if(o instanceof D3MacroVariables) {
			D3MacroVariables vars = (D3MacroVariables) o;
			return vars.xSpeed == xSpeed && vars.ySpeed == ySpeed &&
				vars.zSpeed == zSpeed && vars.localDensity == localDensity;
		}
		
		return false;
	}

}
