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

import java.util.Iterator;
import java.util.LinkedList;

import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.math.IntegerVector;
import laboGrid.math.VectorWrongFormatException;
import laboGrid.procChain.loggers.LBLogger;
import laboGrid.procChain.loggers.Log;




public class D3PointsLogger extends LBLogger {

	private static final long serialVersionUID = 1L;

	protected D3Lattice fluid;
	protected int[] minPoint;
	protected int[] maxPoint;
	protected LinkedList<int[]> points;

	public D3PointsLogger() {

		points = new LinkedList<int[]>();

	}

	protected D3PointsLogger(D3PointsLogger other) {

		super(other);

		points = new LinkedList<int[]>();
		points.addAll(other.points);

	}

	@Override
	public void setParameters(String[] params) throws LBException {

		try {

			for(int i = 0; i < params.length; ++i) {

				int[] point = IntegerVector.parseIntegerVector(params[i]);
				points.add(point);
	
			}
		
		} catch (VectorWrongFormatException e) {

			throw new LBException("Could not read point list: "+e.getMessage());

		}

	}
	
	public void setLBAlgorithm(LBSimThread alg) throws LBException {

		super.setLBAlgorithm(alg);

		minPoint = alg.getSubLattice().getMinPoint();
		maxPoint = alg.getSubLattice().getMaxPoint();
		fluid = (D3Lattice) alg.getFluid();

		Iterator<int[]> it = points.iterator();
		while(it.hasNext()) {

			int[] point = it.next();

			if( ! isPointIn(point, minPoint, maxPoint)) {

				it.remove();

			} else {
				
				System.out.println("D3PointsLogger on "+alg.getNodeId()+" will log point "+IntegerVector.toString(point));
				
			}

		}

	}

	@Override
	public Log getLog(int currentIteration) {

		if(points.isEmpty()) {

			return null;

		}

		D3PointsLog log = new D3PointsLog(this, currentIteration);

		Iterator<int[]> it = points.iterator();
		while(it.hasNext()) {

			int[] point = it.next();
			int[] inQuantumPos = IntegerVector.sub(point, minPoint);
			int x = inQuantumPos[0];
			int y = inQuantumPos[1];
			int z = inQuantumPos[2];
			
			log.add(point, fluid.getMacroVariables(x, y, z));

		}

		return log;

	}

	protected boolean isPointIn(int[] point, int[] minPoint, int[] maxPoint) {
		return point[0] >= minPoint[0] && point[0] < maxPoint[0] &&
			point[1] >= minPoint[1] && point[1] < maxPoint[1] &&
			point[2] >= minPoint[2] && point[2] < maxPoint[2];
	}

	@Override
	public D3PointsLogger clone() {

		return new D3PointsLogger(this);

	}

}
