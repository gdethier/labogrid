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

import java.io.Serializable;

import laboGrid.lb.lattice.d3.D3MacroVariables;
import laboGrid.math.IntegerVector;



public class D3MacroVarsLog implements Serializable {

	private static final long serialVersionUID = 1L;

	public int[] position;
	public D3MacroVariables vars;
	
	public D3MacroVarsLog(int[] position, D3MacroVariables vars) {

		this.position = position;
		this.vars = vars;

	}
	
	public String toString() {
		String str = "";
		str += "position= "+IntegerVector.toString(position)+", "+vars.toString();
		return str;
	}
	
}
