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
 * Encompasses all graph related tools (representation, partitioning,
 * partition refinment, etc.).
 * Application graph and Resource graph are represented by classes from packages
 * {@link laboGrid.graphs.model} and
 * {@link laboGrid.graphs.resource} respectively.
 * <p>
 * Package {@link laboGrid.graphs.mapping} contains tools
 * used to map an Application graph onto a Resource graph (used for load
 * balancing).
 * <p>
 * Package {@link laboGrid.graphs.replication} contains
 * the description and tools related to Replication graph (computed in the
 * context of fault-tolerance in the centralized implementation of LaBoGrid).
 * 
 * @see laboGrid.impl.central
 */
package laboGrid.graphs;
