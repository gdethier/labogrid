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
import laboGrid.powerModel.ResourceDataBase;
import dimawo.middleware.distributedAgent.DAId;

public class ClusterGenerator implements ResourceGraphGenerator {
	
	private DAId[] newIds;
	private PowerModel[] dasPower;

	public ClusterGenerator(ResourceDataBase resDB, String[] resName,
			String powDesc) throws Exception {
		int numOfDAs = resName.length;
		newIds = new DAId[numOfDAs];
		dasPower = new PowerModel[numOfDAs];
		
		for(int i = 0; i < numOfDAs; ++i) {
			newIds[i] = new DAId(resName[i], 50200, 1);
		
			PowerModel pw = resDB.getPowerModel(resName[i], powDesc);
			if(pw == null)
				throw new Exception("No power model available for resource "+resName[i]);
			dasPower[i] = pw;
		}
	}

	@Override
	public ResourceGraph generateResourceGraph() {
		return new ResourceGraph(newIds, dasPower);
	}

}
