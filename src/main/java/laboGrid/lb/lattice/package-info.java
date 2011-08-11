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
/**
 * Contains classes implementing lattices (this includes lattice's data layout,
 * in-place streaming operation and boundaries copy/update operations). A
 * {@link laboGrid.lb.lattice.LatticeDescriptor descriptor}
 * is associated to
 * each lattice. This descriptor includes informations about the number of
 * dimensions of the lattice, the velocities it defines, etc.
 * <p>
 * Lattice's sites may be scanned using a {@link laboGrid.lb.lattice.BlockIterator
 * block iterator}. The block iterator ensures sites are
 * accessed by ensuring the best data locality. The order the sites are
 * scanned is therefore arbitrary.
 */
package laboGrid.lb.lattice;
