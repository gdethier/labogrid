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
import java.util.Iterator;
import java.util.LinkedList;

import laboGrid.lb.lattice.d3.D3MacroVariables;
import laboGrid.procChain.loggers.LBLog;




public class D3PointsLog extends LBLog {

	private static final long serialVersionUID = 1L;

	private LinkedList<D3MacroVarsLog> values;

	public D3PointsLog(D3PointsLogger log, int iteration) {

		super(log, iteration);

		values = new LinkedList<D3MacroVarsLog>();

	}

	public void add(int[] position, D3MacroVariables vars) {

		values.add(new D3MacroVarsLog(position, vars));

	}

	@Override
	public void printLog(PrintStream ps) {

		Iterator<D3MacroVarsLog> it = values.iterator();
		while(it.hasNext()) {

			D3MacroVarsLog log = it.next();
			ps.println(log);

		}

	}

}
