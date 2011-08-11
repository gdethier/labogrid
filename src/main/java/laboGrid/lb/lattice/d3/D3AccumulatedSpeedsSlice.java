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
package laboGrid.lb.lattice.d3;

public class D3AccumulatedSpeedsSlice {
	private static final int xSpeedOff = 0, ySpeedOff = 1, zSpeedOff = 2;
	private int iSize, jSize, jSize3;
	private double[] acc;
	
	public D3AccumulatedSpeedsSlice(int iSize, int jSize) {
		this.iSize = iSize;
		this.jSize = jSize;
		this.jSize3 = jSize * 3;

		acc = new double[iSize  * jSize3];
	}

	public void setAccumulatedSpeeds(int i, int j,
			double aXSpeed, double aYSpeed, double aZSpeed) {
		int baseInd = i * jSize3 + j * 3;
		acc[baseInd + xSpeedOff] = aXSpeed;
		acc[baseInd + ySpeedOff] = aYSpeed;
		acc[baseInd + zSpeedOff] = aZSpeed;
	}

	public double getAccumulatedXSpeed(int i, int j) {
		return acc[i * jSize3 + j * 3 + xSpeedOff];
	}
	
	public double getAccumulatedYSpeed(int i, int j) {
		return acc[i * jSize3 + j * 3 + ySpeedOff];
	}
	
	public double getAccumulatedZSpeed(int i, int j) {
		return acc[i * jSize3 + j * 3 + zSpeedOff];
	}
}
