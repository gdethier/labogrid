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
package laboGrid.procChain.operators.d3;

import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.procChain.operators.LBOperator;

public class D3SpeedAccumulator extends LBOperator {

	protected D3Lattice d3Fluid;
	protected int xSize, ySize, zSize;

	public void setLBAlgorithm(LBSimThread alg) throws LBException {
		super.setLBAlgorithm(alg);

		d3Fluid = (D3Lattice) alg.getFluid();
		
		d3Fluid.allocateAccumulators();
		
		xSize = d3Fluid.getXSize();
		ySize = d3Fluid.getYSize();
		zSize = d3Fluid.getZSize();
	}

	@Override
	public void apply() throws LBException, InterruptedException {
//		System.out.println("D3InternalSitesCollider "+alg.getNodeId());

		d3Fluid.accumulateSpeeds();

	}

	@Override
	public void setParameters(String[] parameters) throws LBException {
		// SKIP
	}

	@Override
	public D3SpeedAccumulator clone() {
		return new D3SpeedAccumulator();
	}
	
}
