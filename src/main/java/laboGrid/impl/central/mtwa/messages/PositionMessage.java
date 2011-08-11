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
package laboGrid.impl.central.mtwa.messages;

import laboGrid.impl.common.mtwa.messages.MTWAWorkerMessage;
import dimawo.middleware.distributedAgent.DAId;

public class PositionMessage extends MTWAWorkerMessage {

	private static final long serialVersionUID = 1L;
	
	private DAId parentId;
	private DAId[] childIds;
	private long ccp;
	private int conf;

	public PositionMessage(DAId daId) {
		super(daId);
	}

	public void setParentId(DAId id) {
		this.parentId = id;
	}
	
	public void setChildIds(DAId[] childIds) {
		this.childIds = childIds;
	}

	public void setConfiguration(int conf) {
		this.conf = conf;
	}

	public int getConfiguration() {
		return conf;
	}

	public DAId getParentId() {
		return parentId;
	}

	public DAId[] getChildIds() {
		return childIds;
	}

	public void setCCP(long ccp) {
		this.ccp = ccp;
	}
	
	public long getCCP() {
		return ccp;
	}

}
