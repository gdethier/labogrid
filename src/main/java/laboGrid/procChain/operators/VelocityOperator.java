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
package laboGrid.procChain.operators;

import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.pressure.PressureConditions;
import laboGrid.lb.solid.Solid;

public abstract class VelocityOperator<
FluidClass extends Lattice, SolidClass extends Solid> extends LBOperator {
	
	protected int flowDirection;
	protected double uIn;
	protected boolean applyIn, applyOut;

	protected PressureConditions velocityConditions;


	public VelocityOperator() {
	}
	
	protected VelocityOperator(int flowDirection, double uIn) {
		this.flowDirection = flowDirection;
		this.uIn = uIn;
	}
	
	
	public void setLBAlgorithm(LBSimThread alg) throws LBException {
		super.setLBAlgorithm(alg);
		SubLattice sub = alg.getSubLattice();
		if(sub.isBoundaryFrom(flowDirection)) {
			applyIn = true;
//			System.out.println("[PressureOperator] rhoIn="+rhoIn+" for sub-lattice "+sub.getId());
		}
		if(sub.isBoundaryTo(flowDirection)) {
			applyOut = true;
//			System.out.println("[PressureOpaerator] rhoOut="+rhoOut+" for sub-lattice "+sub.getId());
		}
		FluidClass checkedFluid = (FluidClass) alg.getFluid();
		SolidClass checkedSolid = (SolidClass) alg.getSolid();
		velocityConditions = getVelocityConditions(flowDirection, uIn,
				checkedFluid, checkedSolid);
	}


	@Override
	public void apply() throws LBException, InterruptedException {
		velocityConditions.apply();
	}


	@Override
	public void setParameters(String[] parameters) throws LBException {
		try {
			flowDirection = Integer.parseInt(parameters[0]);
		} catch(NumberFormatException e) {
			throw new LBException("[Pressure Operator] Could not parse flow direction "+e.getMessage());
		}
		try {
			uIn = Float.parseFloat(parameters[1]);
		} catch(NumberFormatException e) {
			throw new LBException("[Pressure Operator] Could not parse uIn "+e.getMessage());
		}
	}
	
	
	protected abstract PressureConditions getVelocityConditions(int flowDirection,
			double uIn, FluidClass fluid, SolidClass solid);

}
