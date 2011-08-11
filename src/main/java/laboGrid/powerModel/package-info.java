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
 * Contains the implementation of a computational powers database.
 * The database is implemented by class {@link #ResourceDataBase ResourceDataBase}.
 * This database can be written to and read from a text file that can be
 * edited.
 * <p>
 * The computational power of a computer is taken into account when solving
 * the load balancing problem. In the context of LaBoGrid, an application graph
 * (representing a distributed LB simulation, see {@link laboGrid.graphs.model.ModelGraph ModelGraph} class and its subclasses) is mapped onto a resource graph
 * (representing a cluster, see {@link laboGrid.graphs.resource.ResourceGraph ResourceGraph} class)).
 * The nodes of the resource graph, representing the computers of a cluster,
 * are weighted with the computational power of the associated computer.
 * 
 * @see laboGrid.graphs.mapping
 */
package laboGrid.powerModel;
