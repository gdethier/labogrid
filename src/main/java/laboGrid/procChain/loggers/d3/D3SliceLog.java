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

import java.io.PrintStream;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.d3.D3LatticeMacroVarsSlice;
import laboGrid.lb.solid.d3.D3SolidBitmapSlice;
import laboGrid.lb.solid.d3.D3SolidSlice;
import laboGrid.procChain.loggers.LBLog;



public class D3SliceLog extends LBLog {
	private static final long serialVersionUID = 1L;

	protected String type;
	protected int[] subSlicePosition;
	protected int[] subSliceSize;
	protected int position;
	
	protected D3LatticeMacroVarsSlice macroVars;
	protected D3SolidSlice solid;

	public D3SliceLog(D3SliceLogger logger, int iteration,
			D3LatticeMacroVarsSlice macroVars,
			D3SolidSlice solid,
			String type,
			int[] subSlicePosition,
			int[] subSliceSize,
			int position) {

		super(logger, iteration);

		this.macroVars = macroVars;
		this.solid = solid;
		this.type = type;

		this.subSlicePosition = new int[2];
		if(type.equals("XY")) {
			this.subSlicePosition[0] = subSlicePosition[0];
			this.subSlicePosition[1] = subSlicePosition[1];
		} else if(type.equals("XZ")) {
			this.subSlicePosition[0] = subSlicePosition[0];
			this.subSlicePosition[1] = subSlicePosition[2];
		} else {
			this.subSlicePosition[0] = subSlicePosition[1];
			this.subSlicePosition[1] = subSlicePosition[2];
		}
		this.subSliceSize = subSliceSize;
		this.position = position;
	}

	public String getType() {
		return type;
	}

	public int getProjectionPosition() {
		return position;
	}

	public int[] getPositionInProjection() {
		return subSlicePosition;
	}

	public int[] getSubSliceSize() {
		return subSliceSize;
	}

	public D3LatticeMacroVarsSlice getMacroVars() {
		return macroVars;
	}
	
	public D3SolidSlice getSolid() {
		return solid;
	}

	@Override
	public void printLog(PrintStream ps) {
		try {
			D3Slice slice = new D3Slice(macroVars, solid);
			
			// Print iteration
			ps.println("Iteration "+this.getIteration());
			
			// Print position
			for(int i = 0; i < subSlicePosition.length - 1; ++i) {
				ps.print(subSlicePosition[i]);
				ps.print(' ');
			}
			ps.println(subSlicePosition[subSlicePosition.length - 1]);
			
			// Print size
			for(int i = 0; i < subSliceSize.length - 1; ++i) {
				ps.print(subSliceSize[i]);
				ps.print(' ');
			}
			ps.println(subSliceSize[subSliceSize.length - 1]);
			
			// Print data
			slice.printData(ps);
		} catch (LBException e) {
			e.printStackTrace(ps);
		}
	}
}
