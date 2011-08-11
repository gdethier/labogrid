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
 * Contains the centralized implementation of LaBoGrid. This implementation
 * is based on a simple master-worker architecture: workers run each a
 * Distributed Agent (DA) and the master runs a central Controller.
 * <p>
 * Each DAs execute the simulation code on its associated sublattices.
 * The Controller organizes and synchronizes the DAs during simulation's
 * execution.
 * 
 * @see laboGrid.impl.decentral
 */
package laboGrid.impl.central;
