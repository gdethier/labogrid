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

/**
 * This message is used by an LBTask to query the current state of simulation.
 * This way, the LBTask can benchmark itself, send the result and maybe
 * be part of the Resource Graph in case of load balancing.
 * It can also be used for a next phase of the simulation.
 * 
 * @author Gérard Dethier
 *
 */
public class SimulationStateQuery extends ExperimenterMessage {

	private static final long serialVersionUID = 1L;
	
	private DAId daDesc;

	public SimulationStateQuery(DAId daDesc) {
		super(daDesc);
		
		this.daDesc = daDesc;
	}

	public DAId getDAId() {
		return daDesc;
	}

}