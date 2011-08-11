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
package laboGrid.impl.central.task.messages;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import laboGrid.lb.SubLattice;

import dimawo.WorkerMessage;
import dimawo.middleware.distributedAgent.DAId;



public class LBConfigData extends WorkerMessage {

	private static final long serialVersionUID = 1L;

	private int phaseNum;
	private int version;
	private int startingIteration;

	protected Map<Integer, SubLattice> subLattices;
	protected Map<Integer, DAId> subLToDA;
	protected Set<DAId> replicationNeighbors;
	protected Set<DAId> computationNeighbors;
	
	public LBConfigData(DAId daId,
			int phaseNum,
			int startingIteration,
			Map<Integer, SubLattice> subLattices,
			Map<Integer, DAId> subLToDA,
			Set<DAId> backupStorageNeighbors,
			int version) {

		super(daId);

		this.phaseNum = phaseNum;
		this.version = version;
		this.startingIteration = startingIteration;

		this.subLattices = subLattices;
		this.subLToDA = subLToDA;
		this.replicationNeighbors = backupStorageNeighbors;
		
		computationNeighbors = new TreeSet<DAId>();
		computationNeighbors.addAll(subLToDA.values());

	}
	
	public int getPhaseNum() {

		return phaseNum;

	}
	
	public Map<Integer, DAId> getSubToDA() {
		return subLToDA;
	}

	public Map<Integer, SubLattice> getSubLattices() {
		return subLattices;
	}

	public int getStartingIteration() {
		return startingIteration;
	}

	public Set<DAId> getReplicationNeighbors() {
		return replicationNeighbors;
	}
	
	public int getVersion() {
		return version;
	}

	public Set<DAId> getComputationNeighbors() {
		return computationNeighbors;
	}

}
