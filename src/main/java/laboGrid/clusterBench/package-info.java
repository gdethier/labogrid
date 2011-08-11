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
 * Contains the code of a distributed benchmarking application based on
 * DiMaWo framework.
 * <p>
 * Workers benchmark the computers they are executed by and send the result to
 * the master which writes them into a file. This file can directly be used
 * by LaBoGrid in order to balance load during the execution of a distributed
 * LB simulation.
 * <p>
 * @see laboGrid.CentralLBTaskFactory
 * @see laboGrid.LaBoGridFactory
 */
package laboGrid.clusterBench;
