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

import java.io.IOException;
import java.util.Map;

import laboGrid.impl.common.simulation.algorithm.BorderDataInfo;
import laboGrid.impl.common.simulation.algorithm.BrokenDAException;
import laboGrid.impl.common.simulation.algorithm.LBData;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.lb.lattice.BorderData;
import laboGrid.lb.lattice.Lattice;
import laboGrid.procChain.ProcessingChainElement;

import dimawo.middleware.communication.outputStream.MOSAccessorInterface;


/**
 * extracts the outgoing densities from a sublattice and forwards them
 * to the destination sublattices. After the application of this operator,
 * all densities have been transmitted and output buffers may be overwritten.
 * 
 * @author dethier
 *
 */
public class BlockingBorderSender extends LBOperator {
	
	private SubLattice subLattice;
	private Lattice fluid;
	
	private class LinkInfo {
		int subId;
		MOSAccessorInterface access;
	}
	private LinkInfo[] link;
	
	@Override
	public void setParameters(String[] parameters) throws LBException {
		// SKIP
	}
	
	public void setLBAlgorithm(LBSimThread alg) throws LBException {
		super.setLBAlgorithm(alg);

		subLattice = alg.getSubLattice();
		fluid = alg.getFluid();
		Map<Integer, MOSAccessorInterface> consumers = alg.getNeighboringConsumers();
		
		int numOfVel = fluid.getLatticeDescriptor().getVelocitiesCount();
		link = new LinkInfo[numOfVel];
		for(int v = 0; v < numOfVel; ++v) {
			int subId = subLattice.getNeighborFromVel(v);
			if(subId >= 0) {
				LinkInfo l = new LinkInfo();
				link[v] = l;

				l.subId = subId;
				l.access = consumers.get(subId);
			}
		}
	}

	@Override
	public void apply() throws LBException, InterruptedException {
		int currentVersion = alg.getVersion();

		fluid.fillBuffers();
		for(int i = 0; i < link.length; ++i) {
			BorderData bd = fluid.getOutcomingDensities(i);
			if(bd == null) // rest
				continue;
			
			bd.setVersion(currentVersion);
			
			LinkInfo inf = link[i];
			LBData data = new LBData(subLattice.getId(), inf.subId,
					currentVersion,
					new BorderDataInfo(bd, alg));
			try {
				inf.access.writeBlockingMessage(data);
			} catch (IOException e) {
				throw new BrokenDAException(inf.access.getDestinationDAId());
			}
		}
	}

	@Override
	public ProcessingChainElement clone() {
		return new BlockingBorderSender();
	}

}
