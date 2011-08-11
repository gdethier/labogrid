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
package laboGrid.impl.decentral.mtwa;

import java.util.LinkedList;

import laboGrid.impl.common.mtwa.messages.MTWAWorkerMessage;

import dimawo.middleware.distributedAgent.DAId;


public class SubtreeDa2SubsMessage extends MTWAWorkerMessage {
	private int conf;
	private LinkedList<Da2Subs> da2Subs;

	public SubtreeDa2SubsMessage(DAId daId, int conf,
			LinkedList<Da2Subs> da2Subs) {
		super(daId);
		
		this.conf = conf;
		this.da2Subs = da2Subs;
	}
	
	public int getConf() {
		return conf;
	}

	public LinkedList<Da2Subs> getDa2Subs() {
		return da2Subs;
	}
}
