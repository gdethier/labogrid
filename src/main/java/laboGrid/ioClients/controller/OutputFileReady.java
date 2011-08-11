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
package laboGrid.ioClients.controller;

import laboGrid.impl.central.controllerAgent.outputServer.CentralOutputServerMessage;
import laboGrid.standalone.StandAloneIoServerMessage;
import dimawo.middleware.distributedAgent.DAId;

public class OutputFileReady extends CentralOutputServerMessage implements
StandAloneIoServerMessage {

	private static final long serialVersionUID = 1L;
	
	private String fileID;
	private String path;
	private boolean toCa;

	public OutputFileReady(DAId sourceDaId, String fileID, String path) {
		super(sourceDaId);

		this.fileID = fileID;
		this.path = path;
	}
	
	public OutputFileReady(DAId sourceDaId, String fileID, String path, boolean toCa) {
		super(sourceDaId);

		this.fileID = fileID;
		this.path = path;
		this.toCa = toCa;
	}
	
	public String getFileID() {
		return fileID;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getOutputFileName() {
		return ControllerOutputClient.getOutputFileName(path, fileID);
	}

	public boolean getToCa() {
		return toCa;
	}

}
