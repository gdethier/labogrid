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

import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.solid.d3.D3Solid;
import laboGrid.procChain.loggers.LBLogger;
import laboGrid.procChain.loggers.Log;

public class D3MassicFlowLogger extends LBLogger {
	
	// Logger parameters
	protected int flowDirection;

	// Other members
	protected D3Lattice fluid;
	protected D3Solid solid;
	protected int iFrom;
	protected int consideredSites;
	
	public D3MassicFlowLogger() {}
	
	protected D3MassicFlowLogger(D3MassicFlowLogger other) {

		super(other);

		this.flowDirection = other.flowDirection;

	}
	
	public int getIFrom() {
		
		return iFrom;
		
	}
	
	public int getConsideredSites() {

		return consideredSites;

	}
	
	@Override
	public void setParameters(String[] params) {

		this.flowDirection = Integer.parseInt(params[0]);

	}
	
	@Override
	public void setLBAlgorithm(LBSimThread alg) throws LBException {

		super.setLBAlgorithm(alg);

		iFrom = alg.getSubLattice().getPosition()[flowDirection];
		fluid = (D3Lattice) alg.getFluid();
		solid = (D3Solid) alg.getSolid();
		
		int[] latticeSize = fluid.getSize();
		consideredSites = 1;
		for(int i = 0; i < latticeSize.length; ++i) {
			consideredSites *= latticeSize[i];
		}
	}

	@Override
	public Log getLog(int iteration) {
		double[] qMassicFlow = null;
		try {
			if(flowDirection == 0) {
				qMassicFlow = D3MassicFlowFunctor.calculateXQMassicFlow(fluid, solid);
			} else if(flowDirection == 1) {
				qMassicFlow = D3MassicFlowFunctor.calculateYQMassicFlow(fluid, solid);
			} else if(flowDirection == 2) {
				qMassicFlow = D3MassicFlowFunctor.calculateZQMassicFlow(fluid, solid);
			} else {
				assert false : "Invalid flow direction "+flowDirection;
			}
		} catch (LBException e) {
			e.printStackTrace();
		}

		return new MassicFlowLog(this, iteration, qMassicFlow);

	}

	@Override
	public D3MassicFlowLogger clone() {

		return new D3MassicFlowLogger(this);

	}

}
