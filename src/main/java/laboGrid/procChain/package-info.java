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
 * Contains classes representing a processing chain. A processing chain describes
 * the sequence of operations that will be executed a given number of times
 * by an instance of
 * {@link laboGrid.impl.common.simulation.algorithm.LBSimThread LBSimThread}.
 * In other words, the content of a processing chain determines the
 * sequence of instructions executed and that implement a LB simulation.
 * For example, a typical LB simulation implies following operations:
 * 
 * <ol>
 *   <li>streaming,</li>
 *   <li>boundary conditions,</li>
 *   <li>collision.</li>
 * </ol>
 * 
 * A {@link laboGrid.configuration.processingChain.ProcessingChainDescription processing chain's description}
 * is given to LaBoGrid in order to actually instantiate it in the context of a
 * flow simulation.
 * <p>
 * The elements of a processing
 * chain can be
 * {@link laboGrid.procChain.loggers loggers}
 * or
 * {@link laboGrid.procChain.operators operators}.
 * Loggers are used to regularly log
 * informations about the state of the fluid and/or flow. Operators generally
 * implement transformations of lattice's state.
 * 
 */
package laboGrid.procChain;
