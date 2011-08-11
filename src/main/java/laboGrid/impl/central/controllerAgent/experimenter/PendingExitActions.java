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
package laboGrid.impl.central.controllerAgent.experimenter;

import java.util.TreeSet;

public class PendingExitActions {
	
	private boolean taskIsRunning;
	private TreeSet<String> putFiles;

	
	public PendingExitActions() {
		taskIsRunning = true;
		putFiles = new TreeSet<String>();
	}

	public void addPutFile(String fileUID) throws Exception {
		if( ! putFiles.add(fileUID))
			throw new Exception("File already put");
	}

	public boolean isEmpty() {
		return ! taskIsRunning && putFiles.isEmpty();
	}

	public void removePutFile(String fileUID) throws Exception {
		if( ! putFiles.remove(fileUID))
			throw new Exception("No removed file UID");
	}

	public void removeTask() throws Exception {
		if( ! taskIsRunning)
			throw new Exception("Task already removed");
		taskIsRunning = false;
	}

}
