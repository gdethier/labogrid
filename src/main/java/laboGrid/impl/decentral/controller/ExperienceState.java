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
package laboGrid.impl.decentral.controller;

import java.io.Serializable;

public class ExperienceState implements Serializable {
	private static final long serialVersionUID = 1L;

	private int simNum;
	private int simVersion;
	private int lastSavedIteration;
	
	public ExperienceState() {
		simNum = 0;
		simVersion = 0;
		lastSavedIteration = 0;
	}
	
	public ExperienceState(int simNum, int simVersion, int lastSavedIteration) {
		this.simNum = simNum;
		this.simVersion = simVersion;
		this.lastSavedIteration = lastSavedIteration;
	}
	
	public int gotoNextSim() {
		++simNum;
		return simNum;
	}

	public int getSimNum() {
		return simNum;
	}
	
	public int getVersion() {
		return simVersion;
	}
	
	public int getLastSavedIteration() {
		return lastSavedIteration;
	}
	
	public ExperienceState clone() {
		return new ExperienceState(simNum, simVersion, lastSavedIteration);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[ExperienceState").append('\n');
		sb.append("simNum=").append(simNum).append('\n');
		sb.append("version=").append(simVersion).append('\n');
		sb.append("lastSavedIteration=").append(lastSavedIteration).append('\n');
		sb.append("]").append('\n');
		return sb.toString();
	}

	public void setLastSavedIteration(int it) {
		this.lastSavedIteration = it;
	}

	public void setVersion(int conf) {
		simVersion = conf;
	}

	public void incrementVersion() {
		++simVersion;
	}
}
