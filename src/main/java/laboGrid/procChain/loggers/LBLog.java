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


public abstract class LBLog extends Log {

	private static final long serialVersionUID = 1L;

	private int subId;
	private int simNum;
	private int iteration;

	public LBLog(LBLogger logger, int iteration) {

		this.subId = logger.getSubLatticeId();
		this.simNum = logger.getSimulationNumber();
		
		this.iteration = iteration;

	}
	
	public int getSubId() {

		return subId;

	}

	public int getSimNumber() {

		return simNum;

	}
	
	public int getIteration() {

		return iteration;

	}

}
