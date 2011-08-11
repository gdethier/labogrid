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
package laboGrid.impl.common.benchmark;

import java.io.Serializable;

import laboGrid.configuration.middleware.BenchmarkConfiguration;
import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.lb.LBException;
import laboGrid.procChain.ProcessingChain;



public class BenchmarkParameters implements Serializable {

	private static final long serialVersionUID = 1L;

	protected BenchmarkConfiguration benchConf;
	protected String latticeClass;
	protected String solidClass;
	protected ProcessingChain procChain;
	
	private String performanceHash;
	
	public BenchmarkParameters(BenchmarkConfiguration benchConf,
			String latticeClass,
			String solidClass,
			ProcessingChainDescription procChainDesc) throws LBException {

		this.benchConf = benchConf;
		this.latticeClass = latticeClass;
		this.solidClass = solidClass;
		this.procChain = ProcessingChain.getOperatorsChain(procChainDesc, true);

		performanceHash = latticeClass + procChainDesc.getPerformanceHash();

	}

	public String getLatticeClass() {
		return latticeClass;
	}

	public ProcessingChain getProcessingChainCopy() {
		return procChain.clone();
	}

	public BenchmarkConfiguration getBenchmarkConfiguration() {
		return benchConf;
	}

	public String getSolidClass() {
		return solidClass;
	}

	public String getPowerDescriptor() {

		return performanceHash;

	}

}
