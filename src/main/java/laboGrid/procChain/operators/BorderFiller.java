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

import laboGrid.impl.common.simulation.algorithm.BorderDataInfo;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.lattice.LatticeDescriptor;
import laboGrid.procChain.ProcessingChainElement;

/**
 * sets the incoming densities of a sublattice with outgoing densities
 * received from other sublattices. After the application of this operator,
 * all incoming densities are set.
 * 
 * @author dethier
 *
 */
public class BorderFiller extends LBOperator {

	private Lattice lattice;
	private LatticeDescriptor ld;
	
	public BorderFiller() {

	}
	
	@Override
	public void setParameters(String[] parameters) throws LBException {
		// SKIP
	}
	
	public void setLBAlgorithm(LBSimThread alg) throws LBException {

		super.setLBAlgorithm(alg);

		this.lattice = alg.getFluid();
		ld = lattice.getLatticeDescriptor();

	}

	@Override
	public void apply() throws LBException, InterruptedException {

		int currentVersion = alg.getVersion();

		for(int i = 0; i < ld.getVelocitiesCount(); ++i) {
			if( ! ld.isRest(i)) {
				BorderDataInfo d = alg.getReceivedBorder(i);
				while(! d.isKill() && d.getVersion() != currentVersion) {
					alg.printMessage("Ignore wrong version "+d.getVersion()+" instead of "+currentVersion);
					d = alg.getReceivedBorder(i);
				}
				
				if(d.isKill()) {
					// sim-thread has been killed
					return;
				}

				lattice.setIncomingDensities(d.getData());
				d.releaseLocalThread();
			}
		}
	}

	@Override
	public ProcessingChainElement clone() {
		return new BorderFiller();
	}

}
