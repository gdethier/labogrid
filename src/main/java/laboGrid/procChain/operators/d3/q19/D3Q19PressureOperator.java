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
package laboGrid.procChain.operators.d3.q19;

import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.pressure.PressureConditions;
import laboGrid.lb.pressure.d3.q19.D3Q19DoublePressureConditions;
import laboGrid.lb.solid.d3.D3Solid;
import laboGrid.procChain.operators.PressureOperator;

public class D3Q19PressureOperator extends PressureOperator<D3Lattice, D3Solid> {

	public D3Q19PressureOperator() {}

	public D3Q19PressureOperator(int flowDirection, float rhoIn,
			float rhoOut) {
		super(flowDirection, rhoIn, rhoOut);
	}

	@Override
	protected PressureConditions getPressureConditions(int flowDirection,
			float rhoIn, float rhoOut, D3Lattice fluid, D3Solid solid) {
		return new D3Q19DoublePressureConditions(flowDirection, rhoIn, rhoOut, fluid, solid);
	}

	@Override
	public D3Q19PressureOperator clone() {
		return new D3Q19PressureOperator(flowDirection, rhoIn, rhoOut);
	}

}
