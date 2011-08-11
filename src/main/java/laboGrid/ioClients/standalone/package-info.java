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
 * Contains classes implementing Stand Alone I/O clients as well as
 * the Stand Alone Distributed Agent (SADA).
 * These clients imply that input files are retrived from computer
 * hosting the SADA and output files sent to this computer.
 * <p>
 * In order to be used in the context of a particular simulation, the client
 * class field of an {@link laboGrid.configuration.experience.IODescription IODescription}
 * must have value "{@link laboGrid.ioClients.standalone.StandAloneInputClient}"
 * or "{@link laboGrid.ioClients.standalone.StandAloneOutputClient}". For
 * the parameters, see documentation related to these 2 classes.
 */
package laboGrid.ioClients.standalone;
