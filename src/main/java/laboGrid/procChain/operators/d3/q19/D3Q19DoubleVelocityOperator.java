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
import laboGrid.lb.solid.d3.D3Solid;
import laboGrid.lb.velocity.d3.q19.D3Q19DoubleVelocityConditions;
import laboGrid.procChain.ProcessingChainElement;
import laboGrid.procChain.operators.VelocityOperator;

public class D3Q19DoubleVelocityOperator extends
		VelocityOperator<D3Lattice, D3Solid> {
	
	public D3Q19DoubleVelocityOperator() {
		super();
	}

	public D3Q19DoubleVelocityOperator(int flowDirection, double uIn) {
		super(flowDirection, uIn);
	}

	@Override
	protected PressureConditions getVelocityConditions(int flowDirection,
			double uIn, D3Lattice fluid, D3Solid solid) {
		return new D3Q19DoubleVelocityConditions(flowDirection, uIn, fluid, solid);
	}

	@Override
	public ProcessingChainElement clone() {
		return new D3Q19DoubleVelocityOperator(flowDirection, uIn);
	}

}
