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
package laboGrid.impl.decentral.controller.messages;

import laboGrid.impl.decentral.controller.ExperienceState;
import laboGrid.impl.decentral.mtwa.Sub2Da;
import dimawo.WorkerMessage;
import dimawo.middleware.distributedAgent.DAId;

public class InitialMappingMessage extends WorkerMessage {
	
	private ExperienceState expState;
	private Sub2Da sub2Da;

	public InitialMappingMessage(ExperienceState expState, Sub2Da sub2Da) {
		this.expState = expState;
		this.sub2Da = sub2Da;
	}
	
	public ExperienceState getState() {
		return expState;
	}

	public Sub2Da getMapping() {
		return sub2Da;
	}
}
