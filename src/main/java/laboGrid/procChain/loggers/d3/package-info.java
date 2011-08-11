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
 * Provides loggers specific to 3D lattices:
 * <ul>
 *   <li>{@link laboGrid.procChain.loggers.d3.D3MassicFlowLogger D3MassicFlowLogger}
 * logs the massic flow for a particular sublattice i.e. the sum of the
 * multiplication of the speed according with the density measured at each site
 * of the sublattice.</il>
 * <li>{@link laboGrid.procChain.loggers.d3.D3PointsLogger D3PointsLogger}
 * logs the macro variables (see {@link be.ulg.montefiore.laboGrid.lb.lattice.d3.D3MacroVariables D3MacroVariables} class)
 * associated to a list of positions in the lattice.</il>
 * <li>{@link laboGrid.procChain.loggers.d3.D3SliceLogger D3SliceLogger}
 * logs the macro variables associated to a slice of the lattice.</il>
 * </ul>
 */
package laboGrid.procChain.loggers.d3;
