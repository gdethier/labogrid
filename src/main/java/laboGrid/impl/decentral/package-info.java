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
 * Contains the decentralized implementation of LaBoGrid. This implementation
 * is based on a distributed master-worker architecture (DiMaWo framework).
 * <p>
 * Each DAs execute the simulation code on its associated sublattices.
 * One of the DAs (the leader) executes a Controller that implements
 * minimal control logic.
 * 
 * @see laboGrid.impl.central
 */
package laboGrid.impl.decentral;
