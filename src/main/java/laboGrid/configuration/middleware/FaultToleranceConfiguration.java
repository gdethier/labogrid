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
package laboGrid.configuration.middleware;

import java.io.Serializable;

import laboGrid.impl.common.simulation.algorithm.LBState.ContentType;


import org.w3c.dom.Element;


public class FaultToleranceConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private boolean replicationEnabled;
	protected int backupRate;
	protected boolean centralized;
	protected int neighborsCount;
	protected int chunkSize;
	protected ContentType compressFiles;

	public FaultToleranceConfiguration(boolean replicationEnabled, int backupRate, boolean centralized, int neighborsCount,
			int chunkSize, ContentType compressFiles) {
		this.replicationEnabled = replicationEnabled;
		this.backupRate = backupRate;
		this.centralized = centralized;
		if(centralized) {
			this.neighborsCount = 1;
		} else {
			this.neighborsCount = neighborsCount;
		}
		this.chunkSize = chunkSize;
		this.compressFiles = compressFiles;
	}

	public static FaultToleranceConfiguration newInstance(Element e) {
		boolean replicationEnabled = Boolean.parseBoolean(e.getAttribute("replicationEnabled"));
		int backupRate = Integer.parseInt(e.getAttribute("backupRate"));
		boolean centralized = Boolean.parseBoolean(e.getAttribute("centralized"));
		int neighborsCount = Integer.parseInt(e.getAttribute("neighborsCount"));
		int chunkSize = Integer.parseInt(e.getAttribute("chunkSize"));
		ContentType compressFiles = ContentType.valueOf(e.getAttribute("compressFiles"));
		return new FaultToleranceConfiguration(replicationEnabled, backupRate, centralized, neighborsCount, chunkSize, compressFiles);
	}

	public int getBackupRate() {
		return backupRate;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public int getBackupDegree() {
		return neighborsCount;
	}

	public boolean getCentralized() {
		return centralized;
	}

	public ContentType getCompressFiles() {
		return compressFiles;
	}

	public boolean replicationIsEnabled() {
		return replicationEnabled;
	}
}
