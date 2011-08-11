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

import laboGrid.lb.LBException;


public class ComputationalRateLogger extends LBLogger {

	protected long lastTime;
	
	public ComputationalRateLogger() {
		lastTime = -1;
	}

	public ComputationalRateLogger(ComputationalRateLogger other) {

		super(other);

		lastTime = -1;

	}

	@Override
	protected Log getLog(int iteration) {

		if(lastTime != -1) {

			long currentTime = System.currentTimeMillis();
			long elapsedTime = currentTime - lastTime;
			lastTime = currentTime;
			double itPerSec = (double)(getRefreshRate() * 1000)/(elapsedTime);

			return new ComputationalRateLog(this, iteration, itPerSec);

		} else {

			lastTime = System.currentTimeMillis();

		}

		return new ComputationalRateLog(this, iteration, 0);

	}

	@Override
	public ComputationalRateLogger clone() {

		return new ComputationalRateLogger(this);

	}

	@Override
	protected void setParameters(String[] params) throws LBException {
		// No parameters.
	}

}
