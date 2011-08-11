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

public class Speed implements Comparable<Speed> {

	public int u, v;
	public double speed;

	public Speed(int u, int v, double speed) {
		this.u = u;
		this.v = v;
		this.speed = speed;
	}

	public int compareTo(Speed s) {
		if(speed < s.speed)
			return -1;
		else if(speed > s.speed)
			return 1;
		return 0;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Speed) {
			Speed s = (Speed) o;
			return speed == s.speed;
		}
		return false; 
	}
	
	public int hashCode() {
		assert false : "hashCode not designed";
		return 42; // any arbitrary constant will do 
	}
}
