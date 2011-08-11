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

public class LBGMiddleware implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected boolean useIt;
	protected int userAgentServerPort;
	protected int daPort;
	protected String taskJar;

	public LBGMiddleware(boolean useIt, int userAgentServerPort,
			String taskJar, int daPort) {
		this.useIt = useIt;
		this.userAgentServerPort = userAgentServerPort;
		this.taskJar = taskJar;
		this.daPort = daPort;
	}

	public static LBGMiddleware newInstance(Element e) {
		boolean useIt = Boolean.parseBoolean(e.getAttribute("useIt"));
		int userAgentServerPort = Integer.parseInt(e.getAttribute("userAgentServerPort"));
		String taskJar = e.getAttribute("taskJar");
		int daPort = Integer.parseInt(e.getAttribute("daPort"));
		return new LBGMiddleware(useIt, userAgentServerPort, taskJar, daPort);
	}

	public int getUserAgentServerPort() {
		return userAgentServerPort;
	}

	public String getJarFileName() {
		return taskJar;
	}

	public boolean isUsed() {
		return useIt;
	}

	public int getDaPort() {
		return daPort;
	}

}
