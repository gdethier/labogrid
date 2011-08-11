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

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.BlockIterator;
import laboGrid.lb.lattice.LatticeBlock;
import laboGrid.lb.solid.Solid;

public class D3Q19SRTBlockCollider extends D3Q19SRTCollider {

	protected int blockSize;
	
	private int x, y, z;
	

	public D3Q19SRTBlockCollider() {
		blockSize = 1;
	}


	public void setParameters(String[] params) throws LBException {
		
		if(params.length != 5)
			throw new LBException("Usage: <omega> <xAcc> <yAcc> <zAcc> <blockSize>");
		
		omega = Double.parseDouble(params[0]);
		xAccel = Double.parseDouble(params[1]);
		yAccel = Double.parseDouble(params[2]);
		zAccel = Double.parseDouble(params[3]);
		
		blockSize = Integer.parseInt(params[4]);
		
	}
	
	public void setParameters(double omega, double xAccel, double yAccel, double zAccel, int blockSize) {
		this.blockSize = blockSize;
		this.omega = omega;
		this.xAccel = xAccel;
		this.yAccel = yAccel;
		this.zAccel = zAccel;
	}
	
	
//	@Override
//	protected void collide(int xFrom, int xTo,
//			int yFrom, int yTo,
//			int zFrom, int zTo) throws LBException {
//		
//		BlockIterator it = d3Fluid.getBlockIterator(blockSize, xFrom, xTo,
//				yFrom, yTo, zFrom, zTo);
//		collide(it);
//
//	}
	
	
	/**
	 * Method is overidden to use unconstrained block iterator.
	 */
	@Override
	public void collide() throws LBException {
		
		BlockIterator it = d3Fluid.getBlockIterator(blockSize);
		collide(it);

	}
	
	
	private void collide(BlockIterator it) throws LBException {
		
		while(it.hasNext()) {

			LatticeBlock block = it.next();

			for(int sIndex = 0; sIndex < block.size(); ++sIndex) {
				
				x = block.getX(sIndex);
				y = block.getY(sIndex);
				z = block.getZ(sIndex);

				block.getSiteData(sIndex, site);
				collide(d3Solid.at(x,y,z) == Solid.FLUID);
				block.updateData(sIndex, site); // Updates data for current site
			}

			block.updateLattice(); // Updates lattice with new block data
		}
		
	}

}
