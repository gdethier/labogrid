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
package laboGrid.procChain.operators;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import laboGrid.impl.common.simulation.LBSimulation;
import laboGrid.impl.common.simulation.algorithm.BorderDataInfo;
import laboGrid.impl.common.simulation.algorithm.LBData;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.impl.common.simulation.messages.LBBufferedData;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.lb.lattice.BorderData;
import laboGrid.lb.lattice.Lattice;

import dimawo.middleware.communication.outputStream.MOSAccessorInterface;
import dimawo.middleware.distributedAgent.DAId;


public class NonBlockingBorderSender extends LBOperator {
	
	private SubLattice subLattice;
	private Lattice fluid;
	
	private class LinkInfo {
		int subId;
		MOSAccessorInterface access;
		boolean isRemote;
		LinkedList<LBData> buffer; 
	}
	private LinkInfo[] linkInfos;

	private class RemoteInfo {
		MOSAccessorInterface access;
		LinkedList<LBData> buffer;
		public RemoteInfo(MOSAccessorInterface access) {
			this.access = access;
			this.buffer = new LinkedList<LBData>();
		}
	}
	private LinkedList<RemoteInfo> buffers;
	private int localLinks;
	private String simHandlerId;

	@Override
	public void setParameters(String[] parameters) throws LBException {
		// SKIP
	}
	
	public void setLBAlgorithm(LBSimThread alg) throws LBException {
		super.setLBAlgorithm(alg);

		subLattice = alg.getSubLattice();
		fluid = alg.getFluid();
		Map<Integer, MOSAccessorInterface> consumers = alg.getNeighboringConsumers();
		
		int numOfVel = fluid.getLatticeDescriptor().getVelocitiesCount();
		linkInfos = new LinkInfo[numOfVel];
		TreeMap<DAId, RemoteInfo> buffersMap =
			new TreeMap<DAId, RemoteInfo>();
		localLinks = 0;
		for(int v = 0; v < numOfVel; ++v) {
			if(fluid.getLatticeDescriptor().isRest(v))
				continue;

			int subId = subLattice.getNeighborFromVel(v);
			if(subId >= 0) {
				LinkInfo l = new LinkInfo();
				linkInfos[v] = l;

				l.subId = subId;
				l.access = consumers.get(subId);
				if(l.access == null)
					throw new Error("No accessor set for sublattice "+subId);
				
				DAId destId = l.access.getDestinationDAId();
				l.isRemote = ! destId.equals(alg.getDestinationDAId());
				if(l.isRemote) {
					RemoteInfo remInf = buffersMap.get(destId);
					if(remInf == null) {
						remInf = new RemoteInfo(l.access);
						buffersMap.put(destId, remInf);
					}
					l.buffer = remInf.buffer;
				} else {
					++localLinks;
				}
			}
		}
		
		buffers = new LinkedList<RemoteInfo>();
		buffers.addAll(buffersMap.values());
		
		simHandlerId = LBSimulation.getHandlerId(alg.getVersion());
		simHandlerId.hashCode(); // to force hash code caching
	}

	@Override
	public void apply() throws LBException {
		int currentVersion = alg.getVersion();
		this.alg.setAwaitedSentSem(localLinks + buffers.size());
		
		fluid.fillBuffers();
		for(int i = 0; i < linkInfos.length; ++i) {
			BorderData bd = fluid.getOutcomingDensities(i);
			if(bd == null) // rest
				continue;
			
			bd.setVersion(currentVersion);
			
			LinkInfo inf = linkInfos[i];
			LBData data = new LBData(subLattice.getId(),
					inf.subId,
					currentVersion,
					new BorderDataInfo(bd, alg));

			if(inf.isRemote) {
				inf.buffer.add(data);
			} else {
				inf.access.writeNonBlockingMessage(data);
			}
		}
		
		// Send buffered data
		for(RemoteInfo ri : buffers) {
			LinkedList<LBData> toSend = new LinkedList<LBData>();
			toSend.addAll(ri.buffer);
			ri.buffer.clear();
			
			LBBufferedData data = new LBBufferedData(toSend, simHandlerId);
			data.setCallBack(alg);
			ri.access.writeNonBlockingMessage(data);
		}
	}

	@Override
	public NonBlockingBorderSender clone() {
		return new NonBlockingBorderSender();
	}

}
