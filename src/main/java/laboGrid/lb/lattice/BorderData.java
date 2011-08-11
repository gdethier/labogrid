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
package laboGrid.lb.lattice;

import java.io.Serializable;
import java.util.concurrent.Semaphore;

public abstract class BorderData
implements Serializable {

	private static final long serialVersionUID = 1L;

	protected int link;
	protected int iteration;
	protected int version;
	
	private transient Semaphore consSync;

	
	protected BorderData(int link) {
		this.link = link;
		this.iteration = -2;
		this.version = -2;
		
		consSync = new Semaphore(0);
	}
	
	protected BorderData(int link, int iteration, int version) {
		this.link = link;
		this.iteration = iteration;
		this.version = version;
		
		consSync = new Semaphore(0);
	}
	
	public int getLink() {
		return link;
	}
	
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	
	public int getIteration() {
		return iteration;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public int getVersion() {
		return version;
	}

	@Override
	public boolean equals(Object o) {
		
		if(o == null || ! (o instanceof BorderData))
			return false;
		
		BorderData bd = (BorderData) o;
		
		return link == bd.link && iteration == bd.iteration &&
			version == bd.version;
		
	}

	public void waitBorderConsumed() {
		try {
			consSync.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void signalBorderConsumed() {
		consSync.release();
	}
	
	public abstract BorderData clone();

}
