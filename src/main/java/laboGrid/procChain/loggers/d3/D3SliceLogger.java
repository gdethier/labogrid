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
import laboGrid.lb.lattice.d3.D3LatticeMacroVarsSlice;
import laboGrid.lb.solid.d3.D3Solid;
import laboGrid.lb.solid.d3.D3SolidBitmapSlice;
import laboGrid.lb.solid.d3.D3SolidSlice;
import laboGrid.procChain.ProcessingChainElement;
import laboGrid.procChain.loggers.LBLogger;
import laboGrid.procChain.loggers.Log;


public class D3SliceLogger extends LBLogger {

	protected String type;
	protected int projection;
	protected int position;

	protected D3Lattice fluid;
	protected D3SolidSlice sliceSolid;

	protected int[] minPoint;
	
	public D3SliceLogger() {}
	
	protected D3SliceLogger(D3SliceLogger other) {

		super(other);

		this.type = other.type;
		setProjection(type);
		this.position = other.position;
	}

	@Override
	public void setParameters(String[] parameters) throws LBException {
		type = parameters[0];
		setProjection(type);
		position = Integer.parseInt(parameters[1]);
	}

	protected void setProjection(String type) {
		if(type.equals("XY")) {
			projection = 2;
		} else if(type.equals("XZ")) {
			projection = 1;
		} else if(type.equals("YZ")) {
			projection = 0;
		}
	}
	
	@Override
	public Log getLog(int iteration) {

		if(sliceSolid != null) {

			D3LatticeMacroVarsSlice s = null;
			if(projection == 2) {
				s = fluid.getXYMacroVars(position-minPoint[2]);
			} else if(projection == 1) {
				s = fluid.getXZMacroVars(position-minPoint[1]);
			} else if(projection == 0) {
				s = fluid.getYZMacroVars(position-minPoint[0]);
			}

			return new D3SliceLog(this, iteration, s, sliceSolid, type,
					minPoint, new int[] {s.getISize(), s.getJSize()}, position);

		} else {
			return null;
		}
	}

	public void setLBAlgorithm(LBSimThread alg) throws LBException {
		super.setLBAlgorithm(alg);
		minPoint = alg.getSubLattice().getPosition();
		
		if(positionInSubLattice(projection, minPoint, alg.getSubLattice().getSize(), position)) {
			fluid = (D3Lattice) alg.getFluid();
			D3Solid solid = (D3Solid) alg.getSolid();
			if(projection == 2) {
				sliceSolid = solid.getXYBooleans(position-minPoint[2]);
			} else if(projection == 1) {
				sliceSolid = solid.getXZBooleans(position-minPoint[1]);
			} else if(projection == 0) {
				sliceSolid = solid.getYZBooleans(position-minPoint[0]);
			}
		}
	}

	protected boolean positionInSubLattice(int projection, int[] minPoint,
			int[] size, int position) {
		int newPosition = position - minPoint[projection];
		return newPosition >= 0 && newPosition < minPoint[projection] + size[projection];
	}

	@Override
	public ProcessingChainElement clone() {

		return new D3SliceLogger(this);

	}

}
