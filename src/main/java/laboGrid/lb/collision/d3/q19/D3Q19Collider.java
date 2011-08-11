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
package laboGrid.lb.collision.d3.q19;

import laboGrid.lb.collision.d3.D3CollisionOperator;
import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;

public abstract class D3Q19Collider extends D3CollisionOperator {
	
	protected D3Q19FullWayBounceBack bounceBack;
	
	
	public D3Q19Collider() {
		this.bounceBack = new D3Q19FullWayBounceBack();
	}
	
	protected double getLocalDensity(double[] site) {
		return site[D3Q19LatticeDescriptor.EAST] +
			   site[D3Q19LatticeDescriptor.NORTHEAST] +
			   site[D3Q19LatticeDescriptor.NORTH] +
			   site[D3Q19LatticeDescriptor.NORTHWEST] +
			   site[D3Q19LatticeDescriptor.WEST] +
			   site[D3Q19LatticeDescriptor.SOUTHWEST] +
			   site[D3Q19LatticeDescriptor.SOUTH] +
			   site[D3Q19LatticeDescriptor.SOUTHEAST] +
			   site[D3Q19LatticeDescriptor.UP] +
			   site[D3Q19LatticeDescriptor.UPEAST] +
			   site[D3Q19LatticeDescriptor.UPNORTH] +
			   site[D3Q19LatticeDescriptor.UPWEST] +
			   site[D3Q19LatticeDescriptor.UPSOUTH] +
			   site[D3Q19LatticeDescriptor.DOWNEAST] +
			   site[D3Q19LatticeDescriptor.DOWNNORTH] +
			   site[D3Q19LatticeDescriptor.DOWNWEST] +
			   site[D3Q19LatticeDescriptor.DOWNSOUTH] +
			   site[D3Q19LatticeDescriptor.DOWN] +
			   site[D3Q19LatticeDescriptor.REST];
	}
	
}
