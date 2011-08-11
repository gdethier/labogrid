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
 * Contains classes used to describe a particular LB fluid simulation.
 * A LB fluid simulation is described by an instance of class
 * {@link be.ulg.montefiore.laboGrid.configuration.LBConfiguration}.
 * It contains a description of the lattice, solid and sublattices generator
 * to use in order to execute the simulation in a parallel or distributed
 * environment.
 * 
 * @see laboGrid.lb.lattice
 * @see be.ulg.montefiore.laboGrid.lb.solid
 * @see laboGrid.graphs.model
 */
package laboGrid.configuration.lbConfiguration;
