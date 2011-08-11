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

public class GetFile {
	
	private String fileUID;
	private File dest;
	private InputClientCallBack cb;


	public GetFile(String fileUID, File dest, InputClientCallBack cb) {
		this.fileUID = fileUID;
		this.dest = dest;
		this.cb = cb;
	}


	public File getFile() {
		return dest;
	}


	public String getFileUID() {
		return fileUID;
	}
	
	public InputClientCallBack getCallBack() {
		return cb;
	}

}
