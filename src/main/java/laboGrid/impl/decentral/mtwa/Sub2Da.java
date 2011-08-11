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


import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import dimawo.agents.AbstractAgent;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.overlay.Updatable;
import dimawo.middleware.overlay.Update;


public class Sub2Da implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private TreeMap<Integer, DAId> sub2Da;
	
	
	public Sub2Da() {
		sub2Da = new TreeMap<Integer, DAId>();
	}

	public Map<Integer, DAId> getMap() {
		return sub2Da;
	}

	public LinkedList<Integer> removeSubsFromDa(DAId id) {
		LinkedList<Integer> subIds = new LinkedList<Integer>();
		for(Iterator<Entry<Integer, DAId>> it = sub2Da.entrySet().iterator();
		it.hasNext();) {
			Entry<Integer, DAId> e = it.next();
			if(e.getValue().equals(id)) {
				subIds.add(e.getKey());
				it.remove();
			}
		}
		return subIds;
	}

	public void addSubsToDa(DAId daId, Collection<Integer> subIds) {
		for(Integer subId : subIds) {
			sub2Da.put(subId, daId);
		}
	}
	
	@Override
	public Sub2Da clone() {
		Sub2Da copy = new Sub2Da();
		copy.sub2Da.putAll(sub2Da);
		return copy;
	}

	public void add(int subId, DAId id) {
		sub2Da.put(subId, id);
	}

	public void print(AbstractAgent agent) {
		for(Entry<Integer, DAId> e : sub2Da.entrySet()) {
			agent.agentPrintMessage(e.getKey()+" -> "+e.getValue());
		}
	}
}
