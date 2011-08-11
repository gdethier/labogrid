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

/**
 * Description of the benchmark used to estimate the power of a Resource. This description
 * contains a number of simulation iterations and a lattice size.
 * 
 * This class is associated to XML element <code>Benchmark</code>, part of the
 * <code>LoadBalancing</code> element, part of the <code>LaBoGridMiddleware</code>
 * element (see LaBoGridConfiguration.xsd).
 * 
 * @author Gérard Dethier
 *
 */
public class ConfigurationSchedulerConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected int benchTO;
	protected int reconfTO;
	protected int brokenTO;


	public ConfigurationSchedulerConfiguration(int benchTO, int reconfTO,
			int brokenTO) {
		this.benchTO = benchTO;
		this.reconfTO = reconfTO;
		this.brokenTO = brokenTO;
	}


	public static ConfigurationSchedulerConfiguration newInstance(
			Element e) {
		int benchTO = Integer.parseInt(e.getAttribute("benchTO"));
		int reconfTO = Integer.parseInt(e.getAttribute("reconfTO"));
		int brokenTO = Integer.parseInt(e.getAttribute("brokenTO"));
		
		return new ConfigurationSchedulerConfiguration(benchTO, reconfTO, brokenTO);
	}

	public long getReconfTO() {
		return reconfTO;
	}

	public long getBrokenTO() {
		return brokenTO;
	}

	public long getBenchTO() {
		return benchTO;
	}

}
