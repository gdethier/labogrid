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

import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;

public class PendingInitActions {

	private TreeMap<String, Object> waitingInputs;
	private TreeSet<String> waitingFS;


	public PendingInitActions() {
		waitingInputs = new TreeMap<String, Object>();
		waitingFS = new TreeSet<String>();
	}

	public Object removePendingInput(String fileUID) {
		if( ! waitingInputs.containsKey(fileUID))
			throw new NoSuchElementException("No pending input for "+fileUID);
		return waitingInputs.remove(fileUID);
	}

	public void addInputGet(String fileID, Object attach) throws Exception {
		if(waitingInputs.containsKey(fileID))
			throw new Exception("File already gotten from input "+fileID);
		waitingInputs.put(fileID, attach);
	}

	public void addFSAdd(String fileUID) throws Exception {
		if(waitingFS.contains(fileUID))
			throw new Exception("File already added: "+fileUID);
		waitingFS.add(fileUID);
	}

	public boolean removePendingAddFile(String fileUID) {
		if( ! waitingFS.contains(fileUID))
			throw new NoSuchElementException("Could not remove added file "+fileUID);
		return waitingFS.remove(fileUID);
	}

	public boolean isEmpty() {
		return waitingFS.isEmpty() && waitingInputs.isEmpty();
	}

}
