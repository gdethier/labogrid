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
package laboGrid.lb.collision;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.solid.Solid;

public interface CollisionOperator<FluidClass extends Lattice, SolidClass extends Solid> {

	/**
	 * Applies collision operator on all sites.
	 * @throws LBException 
	 */
	public void collide() throws LBException;

//	/**
//	 * Applies collision operator on internal sites.
//	 * @throws LBException 
//	 */
//	public void collideInternalSites() throws LBException;
	
//	/**
//	 * Applies collision operator on border sites.
//	 * @throws LBException 
//	 */
//	public void collideBorderSites() throws LBException;

	public void setParameters(String[] params) throws LBException;
	public void setFluid(FluidClass fluid);
	public void setSolid(SolidClass solid);
	
}
