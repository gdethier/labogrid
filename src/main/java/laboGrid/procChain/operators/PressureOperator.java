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

public abstract class PressureOperator<FluidClass extends Lattice, SolidClass extends Solid> extends LBOperator {

	protected int flowDirection;
	protected float rhoIn, rhoOut;
	
	protected PressureConditions pressureConditions;
	
	public PressureOperator() {}
	
	protected PressureOperator(int flowDirection, float rhoIn, float rhoOut) {
		this.flowDirection = flowDirection;
		this.rhoIn = rhoIn;
		this.rhoOut = rhoOut;
	}
	
	public void setLBAlgorithm(LBSimThread alg) throws LBException {

		super.setLBAlgorithm(alg);

		SubLattice sub = alg.getSubLattice();
		float rhoIn = -1;
		float rhoOut = -1;
		if(sub.isBoundaryFrom(flowDirection)) {
			rhoIn = this.rhoIn;
//			System.out.println("[PressureOperator] rhoIn="+rhoIn+" for sub-lattice "+sub.getId());
		}
		if(sub.isBoundaryTo(flowDirection)) {
			rhoOut = this.rhoOut;
//			System.out.println("[PressureOpaerator] rhoOut="+rhoOut+" for sub-lattice "+sub.getId());
		}
		FluidClass checkedFluid = (FluidClass) alg.getFluid();
		SolidClass checkedSolid = (SolidClass) alg.getSolid();
		pressureConditions = getPressureConditions(flowDirection, rhoIn, rhoOut, checkedFluid, checkedSolid);
	}

	@Override
	public void apply() throws LBException, InterruptedException {

		pressureConditions.apply();

	}

	@Override
	public void setParameters(String[] parameters) throws LBException {
		try {
			flowDirection = Integer.parseInt(parameters[0]);
		} catch(NumberFormatException e) {
			throw new LBException("[Pressure Operator] Could not parse flow direction "+e.getMessage());
		}
		try {
			rhoIn = Float.parseFloat(parameters[1]);
		} catch(NumberFormatException e) {
			throw new LBException("[Pressure Operator] Could not parse rhoIn "+e.getMessage());
		}
		try {
			rhoOut = Float.parseFloat(parameters[2]);
		} catch(NumberFormatException e) {
			throw new LBException("[Pressure Operator] Could not parse rhoOut "+e.getMessage());
		}
	}
	
	protected abstract PressureConditions getPressureConditions(int flowDirection,
			float rhoIn, float rhoOut, FluidClass fluid, SolidClass solid);

}
