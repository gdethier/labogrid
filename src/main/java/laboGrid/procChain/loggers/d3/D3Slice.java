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

import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.io.Serializable;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.d3.D3LatticeMacroVarsSlice;
import laboGrid.lb.solid.d3.D3SolidBitmapSlice;
import laboGrid.lb.solid.d3.D3SolidSlice;


public class D3Slice implements Serializable {
	private static final int xSpeed = 0, ySpeed = 1, zSpeed = 2, speedMod = 3;
	protected D3LatticeMacroVarsSlice macroVars;
	protected D3SolidSlice solid;

	private int iSize, jSize;

	public D3Slice(D3LatticeMacroVarsSlice macroVars, D3SolidSlice solid) throws LBException {
		this.macroVars = macroVars;
		this.solid = solid;

		this.iSize = macroVars.getISize();
		this.jSize = macroVars.getJSize();
		
		if(iSize != solid.getISize() || jSize != solid.getJSize())
			throw new LBException("Slices' sizes do not match");
	}

	private double getSpeed(int speed, int i, int j) {
		if(speed == xSpeed)
			return macroVars.getXSpeed(i, j);
		else if(speed == ySpeed)
			return macroVars.getYSpeed(i, j);
		else if(speed == zSpeed)
			return macroVars.getZSpeed(i, j);
		else if(speed == speedMod)
			return macroVars.getSquaredSpeed(i, j);
		else
			throw new Error("");
	}
	
	private class MinMax {
		double min, max;
	}
	
	private MinMax getMinMax(int speed) {
		MinMax toReturn = new MinMax();

		toReturn.min = getSpeed(speed, 0, 0);
		toReturn.max = getSpeed(speed, 0, 0);
		for(int i = 0; i < iSize; ++i) {
			for(int j = 0; j < jSize; ++j) {

				boolean so = ! solid.isFluid(i, j);
				double sp = getSpeed(speed, i, j);
				
				if(!so && sp < toReturn.min) {
					toReturn.min = sp;
				} else if(!so && sp > toReturn.max) {
					toReturn.max = sp;
				}

			}
		}
		
		return toReturn;
	}

	protected void setImage(int speed, BufferedImage im) {
		MinMax res = getMinMax(speed);
		
		if(res.max == res.min)
			res.min = 0;
		
		double delta = res.max - res.min;
		
		for(int u = 0; u < iSize; ++u) {
			for(int v = 0; v < jSize; ++v) {
				double s = getSpeed(speed, u, v);
				if(solid.isFluid(u, v)) {
					int pValue = (int) ((s - res.min) * (250 / delta));
					int gray = (pValue << 16)+(pValue << 8) + pValue;
					im.setRGB(u, v, gray);
				} else {
					im.setRGB(u, v, 0xFFFFFFFF);
				}
			}
		}
	}
	
	protected Speed max(int speed) {
		double max = getSpeed(speed, 0, 0);
		int uMax = 0;
		int vMax = 0;
		for(int i = 0; i < iSize; ++i) {
			for(int j = 0; j < jSize; ++j) {
				double s = getSpeed(speed, i, j);
				if(s > max) {
					max = s;
					uMax = i;
					vMax = j;
				}
			}
		}
		return new Speed(uMax, vMax, max);
	}
	
	protected double mean(int speed) {
		int nb = 0;
		float toReturn = 0;
		for(int i = 0; i < iSize; ++i) {
			for(int j = 0; j < jSize; ++j) {
				if(solid.isFluid(i, j)) {
					toReturn += getSpeed(speed, i, j);
					++nb;
				}
			}
		}
		return toReturn / nb;
	}
	
	public void asciiDisplayXSpeed(PrintStream stream, int scale) {
		asciiDisplaySpeed(stream, scale, xSpeed);
	}
	
	public void asciiDisplayYSpeed(PrintStream stream, int scale) {
		asciiDisplaySpeed(stream, scale, ySpeed);
	}
	
	public void asciiDisplayZSpeed(PrintStream stream, int scale) {
		asciiDisplaySpeed(stream, scale, zSpeed);
	}
	
	public void asciiDisplaySquaredSpeed(PrintStream stream, int scale) {
		asciiDisplaySpeed(stream, scale, speedMod);
	}
	
	protected void asciiDisplaySpeed(PrintStream stream, int scale, int speed) {

		MinMax minMax = getMinMax(speed);
		if(minMax.max == minMax.min)
			minMax.min = 0;
		
		double delta = minMax.max - minMax.min;
		
		for(int j = 0; j < iSize; ++j) {
			stream.print("-");
		}
		stream.println();
		
		for(int i = 0; i < iSize; ++i) {
			for(int j = 0; j < jSize; ++j) {
				
				boolean so = ! solid.isFluid(i * scale, j * scale);
				double sp = getSpeed(speed, i * scale, j * scale);
				
				if(so) {
					stream.print("X");
				} else {
					double value = (sp - minMax.min) / delta;

					if(value < 1. / 4.) {
						stream.print(" ");
					} else if(value < 2. / 4.) {
						stream.print(".");
					} else if(value < 3. / 4.) {
						stream.print("o");
					} else {
						stream.print("O");
					} 
					
				}
			}
			stream.println("|");
		}
		for(int j = 0; j < jSize; ++j) {
			stream.print("-");
		}
		stream.println();
		
		stream.println("Legende : ");
		stream.println("[ ] = "+minMax.min);
		stream.println("[.] = "+(minMax.min + (0.5 * delta)));
		stream.println("[o] = "+(minMax.min + (0.75 * delta)));
		stream.println("[O] = "+minMax.max);
		
	}
	
	public Speed getMaxXSpeed() {
		return max(xSpeed);
	}
	
	public Speed getMaxYSpeed() {
		return max(ySpeed);
	}
	
	public Speed getMaxZSpeed() {
		return max(zSpeed);
	}
	
	public Speed getMaxSquaredSpeed() {
		return max(speedMod);
	}

	public void setZSpeedImage(BufferedImage im) {
		setImage(zSpeed, im);
	}
	
	public void setXSpeedImage(BufferedImage im) {
		setImage(xSpeed, im);
	}
	
	public void setYSpeedImage(BufferedImage im) {
		setImage(ySpeed, im);
	}
	
	public double getMeanZSpeed() {
		return mean(zSpeed);
	}
	
	public double getMeanYSpeed() {
		return mean(ySpeed);
	}
	
	public double getMeanXSpeed() {
		return mean(xSpeed);
	}

	public void printData(PrintStream ps) {
		for(int j = 0; j < jSize; ++j) {
			for(int i = 0; i < iSize; ++i) {
				ps.print(solid.isFluid(i, j));
				ps.print(' ');
				ps.print(macroVars.getXSpeed(i, j));
				ps.print(' ');
				ps.print(macroVars.getYSpeed(i, j));
				ps.print(' ');
				ps.print(macroVars.getZSpeed(i, j));
				ps.print(' ');
				ps.println(macroVars.getLocalDensity(i, j));
			}
		}
	}

}
