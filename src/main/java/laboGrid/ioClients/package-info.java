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
 * Contains classes implementing the I/O clients mechanism of LaBoGrid.
 * An Input Client (IC) is a component able to retrieve initial files (input
 * files) required
 * for a LB simulation. An Output Client (OC) is a component able to
 * write result files (output files) produced by a simulation. I/O clients can be configured
 * for each simulation of an experiment. In the following, the I/O clients
 * currently implemented by LaBoGrid are introduced.
 * <p>
 * {@link laboGrid.ioClients.controller Controller I/O clients} imply that input files are retrived from computer
 * hosting the controller and output files sent to this computer. These clients
 * can only be used in the context of the {@link laboGrid.impl.central
 * centralized implementation} of LaBoGrid.
 * <p>
 * {@link laboGrid.ioClients.local Local I/O clients} read and write files directly from and to local hard disk.
 * <p>
 * {@link laboGrid.ioClients.standalone Stand-alone I/O clients} have a behavior similar to Controller I/O clients
 * by files are retrived from and sent to a computer that hosts a
 * {@link laboGrid.standalone.StandAloneDistributedAgent Stand Alone Distributed Agent}.
 * 
 * @see laboGrid.configuration.experience.SimulationDescription SimulationDescription
 * @see laboGrid.configuration.experience.IODescription IODescription
 */
package laboGrid.ioClients;
