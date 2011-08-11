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
package laboGrid.procChain.loggers;

import java.io.PrintStream;



public class ComputationalRateLog extends LBLog {

	private static final long serialVersionUID = 1L;

	protected double itPerSec;

	public ComputationalRateLog(LBLogger logger, int iter, double itPerSec) {

		super(logger, iter);

		this.itPerSec = itPerSec;

	}

	public double getRate() {
		return itPerSec;
	}

	@Override
	public void printLog(PrintStream ps) {
		
		ps.println(itPerSec);
		
	}

}
