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
package laboGrid.lb.lattice;

/**
 * A Lattice block is a collection of sites from the lattice. The sites from
 * the collection are not necessarily contiguous.
 * 
 * This interface contains all methods a lattice block should feature.
 * 
 * @author dethier
 */
public interface LatticeBlock {

	/**
	 * The number of sites in this block.
	 * 
	 * @return A number of sites
	 */
	public int size();

	/**
	 * The position of a site from this block in the corresponding lattice.
	 * 
	 * @param sIndex The index of a site
	 * @return Position of the site on X-axis
	 */
	public int getX(int sIndex);

	/**
	 * The position of a site from this block in the corresponding lattice.
	 * 
	 * @param sIndex The index of a site
	 * @return Position of the site on Y-axis
	 */
	public int getY(int sIndex);

	/**
	 * The position of a site from this block in the corresponding lattice.
	 * 
	 * @param sIndex The index of a site
	 * @return Position of the site on Z-axis
	 */
	public int getZ(int sIndex);

	/**
	 * The particle distribution function values for a given site of this block
	 * 
	 * @param sIndex The index of a site
	 * @param site A destination array
	 */
	public void getSiteData(int sIndex, double[] site);

	/**
	 * The particle distribution function values to set for a given site of this block
	 * 
	 * @param sIndex The index of a site
	 * @param site An array of values
	 */
	public void updateData(int sIndex, double[] site);

	/**
	 * The data associated to each site are written back in the lattice they
	 * were extracted from.
	 */
	public void updateLattice();

	/**
	 * Test equality of 2 lattice blocks, this includes site values and positions.
	 * 
	 * @param block Another Lattice Block
	 * 
	 * @return True if blocks are identical, false otherwise.
	 */
	public boolean equals(LatticeBlock block);

}
