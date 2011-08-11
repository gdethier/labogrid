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
import org.w3c.dom.NodeList;


public class LaBoGridMiddlewareConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected ConfigurationSchedulerConfiguration schedConf;
	private StabilizerConfiguration stabConf;
	
	protected LBGMiddleware lbgConf;
	protected LoadBalancingConfiguration loadConf;
	protected FaultToleranceConfiguration faultConf;

	public LaBoGridMiddlewareConfiguration(
			ConfigurationSchedulerConfiguration schedConf,
			StabilizerConfiguration stabConf,
			LBGMiddleware lbgConf, LoadBalancingConfiguration loadConf,
			FaultToleranceConfiguration faultConf) {
		this.schedConf = schedConf;
		this.stabConf = stabConf;
		this.lbgConf = lbgConf;
		this.loadConf = loadConf;
		this.faultConf = faultConf;
	}
	
	public static LaBoGridMiddlewareConfiguration newInstance(Element item) throws VectorWrongFormatException, ConfigurationException {

		NodeList nlSched = item.getElementsByTagName("ConfigurationScheduler");
		NodeList nlStab = item.getElementsByTagName("Stabilizer");
		if(nlSched.getLength() == 0 && nlStab.getLength() == 0) {
			throw new ConfigurationException("No given configuration for configuration scheduler or stabilizer.");
		}
		ConfigurationSchedulerConfiguration schedConf = null;
		if(nlSched.getLength() > 0)
			schedConf = ConfigurationSchedulerConfiguration.newInstance((Element) nlSched.item(0));
		StabilizerConfiguration stabConf = null;
		if(nlStab.getLength() > 0)
			stabConf = StabilizerConfiguration.newInstance((Element) nlStab.item(0));

		Element e;
		
		e = (Element) item.getElementsByTagName("LBGMiddleware").item(0);
		LBGMiddleware lbgConf = (e != null) ? LBGMiddleware.newInstance(e) : null;
		
		LoadBalancingConfiguration loadConf = LoadBalancingConfiguration.newInstance((Element) item.getElementsByTagName("LoadBalancing").item(0));

		FaultToleranceConfiguration faultConf =
			FaultToleranceConfiguration.newInstance((Element) item.getElementsByTagName("FaultTolerance").item(0));

		return new LaBoGridMiddlewareConfiguration(schedConf, stabConf, lbgConf, loadConf, faultConf);

	}

	public LoadBalancingConfiguration getLoadBalancingConfiguration() {
		return loadConf;
	}

	public FaultToleranceConfiguration getFaultToleranceConfiguration() {
		return faultConf;
	}

	public LBGMiddleware getLBGConfiguration() {
		return lbgConf;
	}

	public ConfigurationSchedulerConfiguration getConfigurationSchedulerConf() {
		return schedConf;
	}
	
	public StabilizerConfiguration getStabilizerConfiguration() {
		return stabConf;
	}

}
