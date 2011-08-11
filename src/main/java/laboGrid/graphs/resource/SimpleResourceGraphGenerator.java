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
package laboGrid.graphs.resource;

import laboGrid.powerModel.PowerModel;
import dimawo.middleware.distributedAgent.DAId;

public class SimpleResourceGraphGenerator implements ResourceGraphGenerator {
	
	private PowerModel power; // sites/s
	private int num;
	
	public SimpleResourceGraphGenerator(PowerModel power, int num) {
		this.power = power;
		this.num = num;
	}

	@Override
	public ResourceGraph generateResourceGraph() {
		PowerModel[] dasPower = new PowerModel[num];
		DAId[] newIds = new DAId[num];
		for(int i = 0; i < num; ++i) {
			newIds[i] = new DAId("hostName", 42, i);
			dasPower[i] = power;
		}
		
		return new ResourceGraph(newIds, dasPower);
	}

}
