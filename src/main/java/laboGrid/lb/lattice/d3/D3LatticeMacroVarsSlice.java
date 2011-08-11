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

import java.io.Serializable;

public class D3LatticeMacroVarsSlice implements Serializable {
	private static final int numOfFields = 4;
	private static final int xSpeedOff = 0, ySpeedOff = 1, zSpeedOff = 2, densityOff = 3;
	private int iSize, jSize, jSizeF;
	private double[] data;
	
	public D3LatticeMacroVarsSlice(int iSize, int jSize) {
		this.iSize = iSize;
		this.jSize = jSize;
		
		this.jSizeF = jSize * numOfFields;
		data = new double[iSize * jSizeF];
	}

	public int getISize() {
		return iSize;
	}

	public int getJSize() {
		return jSize;
	}
	
	private int getBaseIndex(int i, int j) {
		return i * jSizeF + j * numOfFields;
	}

	public void setMacroVars(int i, int j, double xSpeed, double ySpeed,
			double zSpeed, double localDensity) {
		int baseInd = getBaseIndex(i, j);

		data[baseInd + xSpeedOff] = xSpeed;
		data[baseInd + ySpeedOff] = ySpeed;
		data[baseInd + zSpeedOff] = zSpeed;
		data[baseInd + densityOff] = localDensity;
	}

	public double getLocalDensity(int i, int j) {
		return data[getBaseIndex(i, j) + densityOff];
	}
	
	public double getXSpeed(int i, int j) {
		return data[getBaseIndex(i, j) + xSpeedOff];
	}
	
	public double getYSpeed(int i, int j) {
		return data[getBaseIndex(i, j) + ySpeedOff];
	}

	public double getZSpeed(int i, int j) {
		return data[getBaseIndex(i, j) + zSpeedOff];
	}

	public double getSquaredSpeed(int i, int j) {
		int baseInd = getBaseIndex(i, j);
		
		double xSpeed = data[baseInd + xSpeedOff];
		double ySpeed = data[baseInd + ySpeedOff];
		double zSpeed = data[baseInd + zSpeedOff];
		
		return xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed;
	}
}
