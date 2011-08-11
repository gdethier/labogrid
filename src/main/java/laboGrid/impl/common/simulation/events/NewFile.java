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
package laboGrid.impl.common.simulation.events;

import java.io.File;

public class NewFile {
	
	private String fileUID;
	private File file;
	private boolean isFromInput;
	private Throwable error;

	public NewFile(String fileUID, File f, boolean isFromInput,
			Throwable error) {

		this.fileUID = fileUID;
		this.file = f;
		this.isFromInput = isFromInput;
		this.error = error;
	
	}

	public String getFileUID() {

		return fileUID;

	}

	public File getFile() {
		
		return file;
		
	}

	public boolean isFromInput() {
		return isFromInput;
	}
	
	public Throwable getError() {
		return error;
	}

}
