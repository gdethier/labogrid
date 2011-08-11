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

import org.w3c.dom.Element;

public class StabilizerConfiguration implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int timeout;

	public StabilizerConfiguration(int timeout) {
		this.timeout = timeout;
	}
	
	public static StabilizerConfiguration newInstance(
			Element e) {
		int timeout = Integer.parseInt(e.getAttribute("timeout"));
		return new StabilizerConfiguration(timeout);
	}
	
	public int getTimeout() {
		return timeout;
	}

}
