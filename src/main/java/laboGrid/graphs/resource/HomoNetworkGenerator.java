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

import java.util.Iterator;
import java.util.LinkedList;

import laboGrid.powerModel.PowerModel;
import laboGrid.powerModel.ResourceDataBase;
import laboGrid.powerModel.ResourcePowerModel;

import dimawo.middleware.distributedAgent.DAId;



public class HomoNetworkGenerator implements ResourceGraphGenerator {
	
	private boolean loadBalUsed;
	private String powerDesc;
	private LinkedList<ResourcePowerModel> benchedDas;
	
	public HomoNetworkGenerator(
			boolean loadBalUsed,
			String powerDesc,
			ResourceDataBase resDB) {
		this.loadBalUsed = loadBalUsed;
		this.powerDesc = powerDesc;
		
		if(loadBalUsed)
			benchedDas = resDB.listBenchmarkedResources(powerDesc);
		else
			benchedDas = resDB.listAvailableResources();
	}

	@Override
	public ResourceGraph generateResourceGraph() {

		int benchedDasCount = benchedDas.size();
		DAId[] newIds = new DAId[benchedDasCount];
		PowerModel[] dasPower = new PowerModel[benchedDasCount];
		
		NullPowerModel pm = new NullPowerModel();

		int i = 0;
		Iterator<ResourcePowerModel> it = benchedDas.iterator();
		while(it.hasNext()) {

			ResourcePowerModel rd = it.next();
			DAId n = rd.getDaId();

			newIds[i] = n;
			
			if(loadBalUsed)
				dasPower[i] = rd.getPower(powerDesc);
			else
				dasPower[i] = pm;
			
			++i;

		}
		
		return new ResourceGraph(newIds, dasPower);

	}

}
