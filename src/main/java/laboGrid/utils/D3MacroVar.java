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
package laboGrid.utils;

import java.io.PrintStream;
import java.util.Scanner;

public class D3MacroVar {
	
	protected double xSpeed;
	protected double ySpeed;
	protected double zSpeed;
	protected double density;
	protected boolean solid;
	
	protected double accXSpeed;
	protected double accYSpeed;
	protected double accZSpeed;
	
	public D3MacroVar() {}
	
	public D3MacroVar(double xSpeed, double ySpeed, double zSpeed, double density, boolean solid,
			double accXSpeed, double accYSpeed, double accZSpeed) {

		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.zSpeed = zSpeed;
		this.density = density;
		this.solid = solid;
		
		this.accXSpeed = accXSpeed;
		this.accYSpeed = accYSpeed;
		this.accZSpeed = accZSpeed;

	}

	public double getXSpeed() {
		return xSpeed;
	}
	
	public double getYSpeed() {
		return ySpeed;
	}
	
	public double getZSpeed() {
		return zSpeed;
	}

	public double getDensity() {
		return density;
	}

	public boolean getSolid() {
		return solid;
	}
	
	public double getAccXSpeed() {
		return accXSpeed;
	}
	
	public double getAccYSpeed() {
		return accYSpeed;
	}
	
	public double getAccZSpeed() {
		return accZSpeed;
	}

	public void print(PrintStream ps) {
		if(solid) {
//			ps.println("0 0 0 "+(1./3.)+" 1 0 0 0");
			ps.println("0 0 0 "+(1./3.)+" 1");
		} else {
			ps.print(xSpeed+" ");
			ps.print(ySpeed+" ");
			ps.print(zSpeed+" ");
			ps.print(((1./3.)*density)+" ");
			ps.println("0 ");
//			ps.print(accXSpeed+" ");
//			ps.print(accYSpeed+" ");
//			ps.println(accZSpeed);
		}		
	}

	public void read(Scanner s) {
		xSpeed = Double.parseDouble(s.next());
		ySpeed = Double.parseDouble(s.next());
		zSpeed = Double.parseDouble(s.next());
		density = 3 * Double.parseDouble(s.next());
		solid = "1".equals(s.next()) ? true : false;
//		accXSpeed = Double.parseDouble(s.next());
//		accYSpeed = Double.parseDouble(s.next());
//		accZSpeed = Double.parseDouble(s.next());
	}
}
