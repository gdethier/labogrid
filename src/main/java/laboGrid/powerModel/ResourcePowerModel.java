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

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

import laboGrid.math.VectorWrongFormatException;

import dimawo.middleware.distributedAgent.DAId;



public class ResourcePowerModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String hostName;
	private TreeMap<String, PowerModel> powers;
	
	private DAId id;
	
	public ResourcePowerModel(String hostName) {
		this.hostName = hostName;
		powers = new TreeMap<String, PowerModel>();
	}
	
	public boolean hasPower(String powerDesc) {
		PowerModel powerForDesc = powers.get(powerDesc);
		return powerForDesc != null;
	}

	public long updatePower(String powerDesc, int[] size, long power) {
		PowerModel powerForDesc = powers.get(powerDesc);
		if(powerForDesc == null) {
			powerForDesc = new PowerModel();
			powers.put(powerDesc, powerForDesc);
		}

		return powerForDesc.replace(size, power);
	}

	public String getHostName() {
		return hostName;
	}
	
	public DAId getDaId() {
		return id;
	}
	
	public void setDaId(DAId id) {
		this.id = id;
	}

	public long getPower(String powerDesc, int[] size) {
		
		PowerModel power = powers.get(powerDesc);
		if(powerDesc == null)
			return -1;
		
		return power.getPower(size);
	}

	public void print(PrintStream ps) {
		ps.println(powers.size());

		Iterator<Entry<String, PowerModel>> powIt =
			powers.entrySet().iterator();
		while(powIt.hasNext()) {
			Entry<String, PowerModel> pe = powIt.next();
			String powDesc = pe.getKey();
			PowerModel power = pe.getValue();
			
			ps.println(powDesc);
			power.print(ps);
		}
	}

	public void read(Scanner scan) throws VectorWrongFormatException {
		int nPowers = Integer.parseInt(scan.next());
		
		for(int i = 0; i < nPowers; ++i) {
			String powDesc = scan.next();

			PowerModel power = new PowerModel();
			power.read(scan);

			updatePower(powDesc, power);
		}
		
	}

	public void updatePower(String powDesc, PowerModel power) {
		PowerModel oldPower = powers.get(powDesc);
		if(oldPower == null) {
			powers.put(powDesc, power);
		} else {
			oldPower.update(power);
		}
	}

	public int size() {
		return powers.size();
	}

	public void merge(ResourcePowerModel provided) {
		powers.putAll(provided.powers);
	}

	public PowerModel getPower(String powerDesc) {
		return powers.get(powerDesc);
	}

}
