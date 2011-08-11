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
package laboGrid.impl.central.controllerAgent.outputServer;

import laboGrid.impl.common.simulation.messages.LBSimulationMessage;
import laboGrid.ioClients.controller.OutputClientMessage;
import dimawo.middleware.distributedAgent.DAId;

public class OutputDownloadFinished extends LBSimulationMessage implements OutputClientMessage {

	private static final long serialVersionUID = 1L;
	private String fileUID;

	public OutputDownloadFinished() {
	}

	public OutputDownloadFinished(DAId t, String fileUID) {
		super(t);
		
		this.fileUID = fileUID;
	}
	
	public String getFileUID() {
		return fileUID;
	}

}
