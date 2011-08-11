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

import laboGrid.procChain.loggers.LBLog;



public class MassicFlowLog extends LBLog {

	private static final long serialVersionUID = 1L;

	protected double[] massicFlow;
	protected int iFrom;
	protected int consideredSites;

	public MassicFlowLog(D3MassicFlowLogger logger, int iter, double[] massicFlow) {

		super(logger, iter);

		this.massicFlow = massicFlow;
		this.iFrom = logger.getIFrom();
		this.consideredSites = logger.getConsideredSites();

	}

	public int getiFrom() {
		return iFrom;
	}

	public int getConsideredSites() {
		return consideredSites;
	}

	public double[] getFlow() {
		return massicFlow;
	}

	@Override
	public void printLog(PrintStream ps) {

		for(int i = 0; i < massicFlow.length; ++i) {

			ps.print(massicFlow[i]+" ");

		}

	}

}
