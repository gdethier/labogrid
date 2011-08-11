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
package laboGrid.impl.central.controllerAgent.resourceGraphHandler;

import laboGrid.configuration.experience.SimulationDescription;
import laboGrid.configuration.lbConfiguration.LBConfiguration;
import laboGrid.configuration.middleware.BenchmarkConfiguration;
import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.impl.common.benchmark.BenchmarkParameters;
import laboGrid.lb.LBException;
import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.lattice.LatticeDescriptor;
import dimawo.Reflection;

public class TaskDescriptor {
	
	private int seqNum;
	private LatticeDescriptor latticeDesc;

//	private BenchmarkParameters benchParam;
	private String powerDescriptor;
	
	public TaskDescriptor(int seqNum,
			LBConfiguration lbConf,
			ProcessingChainDescription pc,
			BenchmarkConfiguration benchConf)
	throws LBException {

		this.seqNum = seqNum;
		
		try {

			Lattice f = (Lattice)
				Reflection.newInstance(lbConf.getLatticeDescription().getClassName());
			latticeDesc = f.getLatticeDescriptor();

		} catch (Exception e) {

			throw new LBException("Could not set lattice descriptor.", e);

		}

		String latticeClass = lbConf.getLatticeDescription().getClassName();
		String solidClass = lbConf.getSolidDescription().getClassName();
		powerDescriptor = SimulationDescription.getPowerDescriptor(latticeClass, pc);
//		benchParam = new BenchmarkParameters(benchConf, latticeClass, solidClass, pc);

	}

	/**
	 * Returns a reference to a string describing a
	 * power context (processing chain and lattice type).
	 * 
	 * @return The power descriptor.
	 */
	public String getPowerDescriptor() {
		
		return powerDescriptor;
		
	}

//	public BenchmarkParameters getBenchmarkParams() {
//		
//		return benchParam;
//		
//	}

	public int getSequenceNumber() {
		
		return seqNum;
		
	}
	
	public LatticeDescriptor getLatticeDescriptor() {

		return latticeDesc;

	}
	
}
