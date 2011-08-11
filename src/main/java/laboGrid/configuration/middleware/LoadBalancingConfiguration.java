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

import laboGrid.ConfigurationException;
import laboGrid.math.VectorWrongFormatException;


import org.w3c.dom.Element;


public class LoadBalancingConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected boolean buildPowerModel;
	protected boolean mtwaEnabled;
	protected String generatorClassName;
	protected String[] generatorParameters;
	protected BenchmarkConfiguration benchConf;

	public LoadBalancingConfiguration(
			boolean buildPowerModel,
			boolean mtwaEnabled,
			String generatorClassName,
			String[] generatorParameters, BenchmarkConfiguration benchConf) {
		this.buildPowerModel = buildPowerModel;
		this.mtwaEnabled = mtwaEnabled;
		this.generatorClassName = generatorClassName;
		this.generatorParameters = generatorParameters;
		this.benchConf = benchConf;
	}

	public static LoadBalancingConfiguration newInstance(Element e) throws VectorWrongFormatException, ConfigurationException {
		boolean buildPowerModel = Boolean.parseBoolean(e.getAttribute("buildPowerModel"));
		boolean mtwaEnabled = Boolean.parseBoolean(e.getAttribute("mtwaEnabled"));
		String generatorClassName = e.getAttribute("generatorClass");
		String[] generatorParameters = e.getAttribute("generatorParameters").split("[ \t\n]+");
		
		Element e2 = (Element) e.getElementsByTagName("Benchmark").item(0);
		BenchmarkConfiguration benchConf = (e2 != null) ? BenchmarkConfiguration.newInstance(e2) : null;

		return new LoadBalancingConfiguration(buildPowerModel, mtwaEnabled, generatorClassName, generatorParameters, benchConf);
	}

	public BenchmarkConfiguration getBenchmarkConfiguration() {
		return benchConf;
	}

	public String getGeneratorClassName() {
		return generatorClassName;
	}

	public String[] getGeneratorParameters() {
		return generatorParameters;
	}
	
	public boolean buildPowerModel() {
		return buildPowerModel;
	}
	
	public boolean mtwaIsEnabled() {
		return mtwaEnabled;
	}

}
