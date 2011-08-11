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
package laboGrid.powerModel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import laboGrid.math.IntegerVector;
import laboGrid.math.VectorWrongFormatException;



public class PowerModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private class Pair implements Serializable {
		private static final long serialVersionUID = 1L;
		public int[] size;
		public long power;
		
		public Pair(int[] size, long power) {
			this.size = size;
			this.power = power;
		}
	}
	
	// #sites -> power
	private LinkedList<Pair> power;
	
	
	public PowerModel() {
		power = new LinkedList<Pair>();
	}
	
	public long replace(int[] size, long p) {
		Iterator<Pair> it = power.iterator();
		long pp = -1;
		while(it.hasNext()) {
			Pair e = it.next();
			int[] eSize = e.size;
			if(Arrays.equals(size, eSize)) {
				pp = e.power;
				it.remove();
				break;
			}
		}
		
		power.add(new Pair(size, p));
		return pp;
	}

	public long getPower(int[] size) {
		if(power.size() == 0)
			return -1;

		Iterator<Pair> it = power.iterator();
		Pair e = it.next();

		long minDistPower = e.power;
		double minDist = IntegerVector.squareDistance(size, e.size);
		while(it.hasNext()) {
			e = it.next();
			long power = e.power;
			double dist = IntegerVector.squareDistance(size, e.size);
			if(dist < minDist) {
				minDist = dist;
				minDistPower = power;
			}
		}

		return minDistPower;
	}

	public void print(PrintStream ps) {
		ps.println(power.size());
		Iterator<Pair> it = power.iterator();
		while(it.hasNext()) {
			Pair p = it.next();
			ps.print(IntegerVector.toString(p.size)+" ");
			ps.println(p.power);
		}
	}

	public void read(Scanner scan) throws VectorWrongFormatException {
		int powerListSize = Integer.parseInt(scan.next());
		for(int i = 0; i < powerListSize; ++i) {
			int[] size = IntegerVector.parseIntegerVector(scan.next());
			long p = Long.parseLong(scan.next());
			power.add(new Pair(size, p));
		}
	}

	public void update(PowerModel p) {
		power.addAll(p.power);
	}
	
	public String toString() {
		String str = "";
		for(Iterator<Pair> it = power.iterator(); it.hasNext();) {
			Pair p = it.next();
			
			str += IntegerVector.toString(p.size)+" -> "+p.power+" sites/s; ";
		}
		return str;
	}

	public void setPower(int[] size, long p) {
		power.add(new Pair(size, p));
	}
	
	public static void main(String[] args) {
		PowerModel pm = new PowerModel();
		pm.setPower(new int[]{10,10,10}, 42);
		pm.setPower(new int[]{20,20,20}, 52);
		pm.setPower(new int[]{30,30,30}, 62);
		
		System.out.println(pm.getPower(new int[]{10,10,10}));
		System.out.println(pm.getPower(new int[]{15,15,15}));
		System.out.println(pm.getPower(new int[]{22,22,22}));
		System.out.println(pm.getPower(new int[]{30,30,30}));
	}
}
