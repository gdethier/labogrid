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
package laboGrid.configuration.experience;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Description of an experiment. An experiment is a sequence of one
 * or several simulations.
 * 
 * This class is associated to XML elements <code>Experience</code>
 * (see LaBoGridConfiguration.xsd).
 * 
 * @author Gérard Dethier.
 *
 */
public class ExperienceDescription implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** An array of simulation descriptions. */
	private LinkedList<SimulationDescription> simulations;

	
	/**
	 * Constructor called by <code>newInstance</code>.
	 * 
	 * @param simulations An array of simulation descriptions.
	 */
	public ExperienceDescription(LinkedList<SimulationDescription> simulations) {

		this.simulations = simulations;

	}

	/**
	 * Instantiates an ExperienceDescription object based on
	 * an XML element.
	 * 
	 * @param item an XML element.
	 * 
	 * @return
	 */
	public static ExperienceDescription newInstance(Element item) {
		
		NodeList nodes = item.getChildNodes();

		LinkedList<SimulationDescription> sims = new LinkedList<SimulationDescription>();		
		for(int i = 0; i < nodes.getLength(); ++i) {

			Node n = nodes.item(i);
			
			if(n instanceof Element) {
				
				Element e = (Element) n;
				
				if(e.getNodeName().equals("Simulation")) {
					
//					System.out.println("Adding a new simulation.");
					addSimulation(e, sims);					
					
				} else if(e.getNodeName().equals("SimulationSequence")) {

//					System.out.println("Adding a new simulations sequence.");
					addSimulationSequence(e, sims);

				}
				
			}

		}
		return new ExperienceDescription(sims);
	}


	////////////////////
	// Public methods //
	////////////////////

	/**
	 * @param simNum The number of the wanted simulation description
	 * (first description has number 0).
	 * 
	 * @return a simulation description.
	 */
	public SimulationDescription getSimulationDescription(int simNum) {

		return simulations.get(simNum);

	}

	/**
	 * @return The number of simulations of this experience.
	 */
	public int getSimulationsCount() {

		return simulations.size();

	}
	
	public Iterator<SimulationDescription> simDescIterator() {
		return simulations.iterator();
	}
	
	/////////////////////
	// Private methods //
	/////////////////////
	
	private static void addSimulation(Element e, LinkedList<SimulationDescription> sims) {

		SimulationDescription sd = SimulationDescription.newSimulationInstance(e);
		sims.add(sd);

	}

	private static void addSimulationSequence(Element e, LinkedList<SimulationDescription> sims) {

		// Set first simulation of sequence
		Element firstSim = (Element) e.getElementsByTagName("Simulation").item(0);
		SimulationDescription sd = SimulationDescription.newSimulationInstance(firstSim);
		
//		System.out.println("Adding first simulation of sequence.");
		sims.add(sd);

		SimulationDescription lastSim = sd;
		NodeList nextSims = e.getElementsByTagName("NextSimulation");
		for(int i = 0; i < nextSims.getLength(); ++i) {
			
			Element nextSim = (Element) nextSims.item(i);

			lastSim.setKeepStateForNextSimulation(true);
			sd = SimulationDescription.newNextSimulationInstance(nextSim, sd.getLastIteration());
			
//			System.out.println("Adding next simulation starting at iteration "+sd.getLastIteration());
			sims.add(sd);

			lastSim = sd;

		}
		
//		System.out.println("Added all simulations.");

	}

}
