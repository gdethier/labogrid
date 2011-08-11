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
 * Contains classes related to LB methods. The concepts of LB methods
 * represented by classes in LaBoGrid are
 * the {@link laboGrid.lb.lattice lattice},
 * the {@link laboGrid.lb.solid solid},
 * the {@link laboGrid.lb.collision collision operator},
 * {@link laboGrid.lb.pressure pressure boundary conditions} and
 * {@link laboGrid.lb.velocity velocity boundary conditions}.
 * An additional concept related to the distributed implementation of LB methods
 * had to be introduced: the
 * {@link laboGrid.lb.SubLattice sublattice}.
 */
package laboGrid.lb;
