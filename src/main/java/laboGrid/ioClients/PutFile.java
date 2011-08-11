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
package laboGrid.ioClients;

import java.io.File;

public class PutFile {
	
	private File src;
	private String destFileUID;
	private OutputClientCallBack cb;


	public PutFile(File src, String destFileUID, OutputClientCallBack cb) {
		this.src = src;
		this.destFileUID = destFileUID;
		this.cb = cb;
	}
	
	public File getSourceFile() {
		return src;
	}
	
	public String getDestinationFileUID() {
		return destFileUID;
	}
	
	public OutputClientCallBack getCallBack() {
		return cb;
	}

	public void signalEndOfUpload() {
		cb.outputClientPutCB(new OutputClientCB(destFileUID, null));
	}

}
