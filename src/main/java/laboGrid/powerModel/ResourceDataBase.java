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
package laboGrid.powerModel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

import laboGrid.math.VectorWrongFormatException;

import dimawo.middleware.distributedAgent.DAId;



public class ResourceDataBase implements Serializable {

	private static final long serialVersionUID = 1L;

	protected TreeMap<String, ResourcePowerModel> fromAddress;
	protected TreeMap<DAId, ResourcePowerModel> fromDaId;
	
	public ResourceDataBase() {
		
		fromAddress = new TreeMap<String, ResourcePowerModel>();
		fromDaId = new TreeMap<DAId, ResourcePowerModel>();
		
	}

	public ResourcePowerModel addDa(DAId daId) {

		String address = daId.getHostName();

		ResourcePowerModel rd = fromAddress.get(address);
		if(rd == null) {

			rd = new ResourcePowerModel(address);
			fromAddress.put(address, rd);

		}

		rd.setDaId(daId);
		ResourcePowerModel old = fromDaId.put(daId, rd);
		
		assert old == null;

		return rd;

	}

	public boolean hasPower(int daId, String powDesc) {

		ResourcePowerModel rd = fromDaId.get(daId);
		return rd.hasPower(powDesc);

	}

	/**
	 * Removes the association of given DA to its associated resource. The power
	 * profile of the resource is kept for potential future use.
	 * 
	 * @param daId The identification number of the DA.
	 * 
	 * @return True if there was an association of given DA with a resource,
	 * false otherwise.
	 */
	public boolean removeDa(DAId daId) {
		
		ResourcePowerModel rd = fromDaId.remove(daId);
		if(rd != null)
			rd.setDaId(null);
		return (rd != null);
		
	}
	
	/**
	 * Removes the association of given DAs to their associated resource. The power
	 * profile of each resource is kept for potential future use.
	 * 
	 * @param dasToRemove A list of DA IDs.
	 * 
	 * @return The IDs of the DAs without association.
	 */
	public LinkedList<DAId> removeDas(LinkedList<DAId> dasToRemove) {
		
		LinkedList<DAId> noAssociation = new LinkedList<DAId>();
		
		Iterator<DAId> it = dasToRemove.iterator();
		while(it.hasNext()) {
			
			DAId daId = it.next();
			
			if(fromDaId.remove(daId) == null) {
				
				noAssociation.add(daId);
				
			}
			
		}
		
		return noAssociation;
		
	}

	/**
	 * Lists the IDs of the DAs whose associated Resource has not already been
	 * benchmarked for the given power descriptor. 
	 * 
	 * @param powerDescriptor The power descriptor.
	 * 
	 * @return A list of DA IDs.
	 */
	public LinkedList<DAId> listDaIdsToBenchmark(String powerDescriptor) {
		
		LinkedList<DAId> ids = new LinkedList<DAId>();
		
		Iterator<Entry<DAId, ResourcePowerModel>> it = fromDaId.entrySet().iterator();
		while(it.hasNext()) {
			
			Entry<DAId, ResourcePowerModel> e = it.next();
			DAId daId = e.getKey();
			ResourcePowerModel desc = e.getValue();

			if( ! desc.hasPower(powerDescriptor)) {

				ids.add(daId);

			}
			
		}
		
		return ids;
	}

	/**
	 * Adds power information for a Resource identified by the DA it is
	 * currently running.
	 * 
	 * @param daId The identification number of the DA the Resource is running.
	 * @param powerDesc The power descriptor.
	 * @param power The added power.
	 * 
	 * @return The power previously associated to the Resource for the given
	 * power descriptor.
	 */
	public void updateResourcePower(DAId daId, String powerDesc,
			PowerModel power) {

		ResourcePowerModel rd = fromDaId.get(daId);
		rd.updatePower(powerDesc, power);

	}
	
	public void addResourcePower(DAId daId, ResourcePowerModel resDesc) {

		fromDaId.put(daId, resDesc);
		fromAddress.put(daId.getHostName(), resDesc);

	}

	/**
	 * Lists the Resources that have a power information for a given
	 * power descriptor.
	 * 
	 * @param powerDesc The power descriptor.
	 * @return A linked list containing the Resource descriptors of
	 * Resources having a power information for the given power descriptor.
	 */
	public LinkedList<ResourcePowerModel> listBenchmarkedResources(
			String powerDesc) {
		
		LinkedList<ResourcePowerModel> list = new LinkedList<ResourcePowerModel>();
		
		Iterator<ResourcePowerModel> it = fromDaId.values().iterator();
		while(it.hasNext()) {
			
			ResourcePowerModel rd = it.next();
			
			if(rd.hasPower(powerDesc)) {
				list.add(rd);
			}

		}

		return list;

	}

	/**
	 * Gives the number of available DAs.
	 * 
	 * @return The number of available DAs.
	 */
	public int availableDasCount() {

		return fromDaId.size();

	}

	public LinkedList<ResourcePowerModel> listAvailableResources() {
		LinkedList<ResourcePowerModel> list =
			new LinkedList<ResourcePowerModel>();
		
		list.addAll(fromDaId.values());
		
		return list;
	}
	
	public void writeText(String outputFileName) throws IOException {
		
		PrintStream ps = new PrintStream(outputFileName);
		
		Iterator<Entry<String, ResourcePowerModel>> resIt = fromAddress.entrySet().iterator();
		while(resIt.hasNext()) {
			Entry<String, ResourcePowerModel> e = resIt.next();
			String addr = e.getKey();
			ResourcePowerModel resDesc = e.getValue();
			
			ps.println(addr);
			resDesc.print(ps);
		}
		
	}
	
	
	public void readText(String inputFileName) throws IOException {
		Scanner scan = new Scanner(new File(inputFileName));
		while(scan.hasNext()) {
			String addr = scan.next();
			
			ResourcePowerModel resDesc = new ResourcePowerModel(addr);
			try {
				resDesc.read(scan);
			} catch (VectorWrongFormatException e) {
				throw new IOException("Could not read resource DB file.", e);
			}
			fromAddress.put(addr, resDesc);
		}
	}
	
	public PowerModel getPowerModel(String hostName, String powDesc) {
		ResourcePowerModel resMod = fromAddress.get(hostName);
		if(resMod == null)
			return null;
		return resMod.getPower(powDesc);
	}

	public LinkedList<ResourcePowerModel> listAllResources() {
		LinkedList<ResourcePowerModel> list =
			new LinkedList<ResourcePowerModel>();
		
		list.addAll(fromAddress.values());
		
		return list;
	}

	public ResourcePowerModel getResourcePowerModel(String hostName) {
		return fromAddress.get(hostName);
	}

}
