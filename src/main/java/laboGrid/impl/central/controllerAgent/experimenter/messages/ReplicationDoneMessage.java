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
package laboGrid.impl.central.controllerAgent.experimenter.messages;

import dimawo.middleware.distributedAgent.DAId;

public class ReplicationDoneMessage extends ExperimenterMessage {

	private static final long serialVersionUID = 1L;
	protected int count, iteration, version;
	protected long backupTime;
	
	public ReplicationDoneMessage(DAId sourceDaId, int count, int iteration,
			int version, long backupTime) {

		super(sourceDaId);

		this.count = count;
		this.iteration = iteration;
		this.version = version;
		this.backupTime = backupTime;
	}

	public int getIteration() {
		return iteration;
	}

	public int getCount() {
		return count;
	}
	
	public int getVersion() {
		return version;
	}
	
	public long getBackupTime() {
		return backupTime;
	}

}
