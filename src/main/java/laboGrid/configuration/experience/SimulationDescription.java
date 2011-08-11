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

import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.lb.lattice.LatticeDescriptor;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Describes a simulation. The description of a simulation
 * contains an LB configuration, a processing chain, a
 * starting iteration and a number of iterations. It also
 * contains the fact that the state of the simulation is reused for the next
 * simulation in the experience. An Input and an output can also be defined.
 * 
 * The LB configuration contains informations about the lattice and solid
 * to use (size, type...). The processing chain describes the operations
 * that will be applied on the lattice each iteration. In a simulation
 * description, only the ID of LB configuration and processing chain are
 * given. These IDs are used to get the associated <code>LBConfiguration</code>
 * and <code>ProcessingChainDescription</code>.
 * 
 * The starting iteration indicates if a previous simulation must be reused.
 * If the starting iteration is greater than 0, a previous state needs to be
 * restored. The state of the previous simulation of the experiment must
 * have been kept (the previous simulation had to be configured to do so).
 * If an input is defined for the simulation, the previous state is got
 * from it in priority using an InputClient described by an <code>IODescription</code>.
 * The number of iterations indicates how many times the processing chain is applied
 * to the lattice.
 * 
 * If an output is defined, the state of the simulation is saved using
 * an <code>OutputClient</code> which description is given by an
 * <code>IODescription</code>.
 * 
 * This class is associated to XML elements <code>Simulation</code> defined
 * in the context of the <code>Experience</code> element.
 * (see LaBoGridConfiguration.xsd).
 * 
 * @author Gérard Dethier
 */
public class SimulationDescription implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Starting iteration of the simulation.
	 * Previous state needs to be restored if greater than 0. */
	private int startingIteration;
	/** Number of times processing chain is applied on lattice. */
	private int iterationsCount;
	/** ID of a processing chain. */
	private String processingChain;
	/** ID of an LB configuration. */
	private String lbConf;
	/** If <tt>true</tt>, the state of the simulation is kept for next simulation. */
	private boolean keepStateForNextSimulation;
	/** An <code>InputClient</code> description (can be <tt>null</tt>). */
	private IODescription input;
	/** An <code>OutputClient</code> description (can be <tt>null</tt>). */
	private IODescription output;
	/** True if this simulation description is the first description of a sequence */
	private boolean isFirstOfSequence;

	/** Last iteration for this simulation
	 * (calculated from startingIteration and iterationsCount). */
	private int lastIteration;

	
	/**
	 * Constructor called by <code>newInstance</code>.
	 * 
	 * @param startingIteration Starting iteration.
	 * @param iterationsCount Number of iterations.
	 * @param processingChain A processing chain ID.
	 * @param lbConf An LB configuration ID.
	 * @param keepStateForNextSimulation If <tt>true</tt>, the state of the
	 * simulation is kept for next simulation.
	 * @param input An <code>InputClient</code> description (<tt>null</tt> if
	 * no input client is needed).
	 * @param output An <code>OutputClient</code> description (<tt>null</tt> if
	 * no output client is needed).
	 */
	public SimulationDescription(
			int startingIteration,
			int iterationsCount,
			String processingChain,
			String lbConf,
			IODescription input,
			IODescription output,
			boolean isFirstOfSequence) {

		this.startingIteration = startingIteration;
		this.iterationsCount = iterationsCount;
		this.processingChain = processingChain;
		this.lbConf = lbConf;
		this.keepStateForNextSimulation = false; // default value
		this.input = input;
		this.output = output;
		this.isFirstOfSequence = isFirstOfSequence;
		
		lastIteration = startingIteration + iterationsCount;

	}

	/**
	 * @param item Simulation XML element.
	 * @return <code>SimulationDescription</code> object based on given XML element. 
	 */
	public static SimulationDescription newSimulationInstance(Element item) {

		int startingIteration = Integer.parseInt(item.getAttribute("startingIteration"));
		int iterationsCount = Integer.parseInt(item.getAttribute("iterationsCount"));
		String processingChain = item.getAttribute("processingChain");
		String lbConf = item.getAttribute("lbConfiguration");
		
		NodeList nl = item.getElementsByTagName("Input");
		IODescription input = null;
		if(nl.getLength() > 0) {

			input = IODescription.newInstance((Element) nl.item(0));

		}
					
		nl = item.getElementsByTagName("Output");
		IODescription output = null;
		if(nl.getLength() > 0) {

			output = IODescription.newInstance((Element) nl.item(0));

		}
		
		return new SimulationDescription(startingIteration, iterationsCount,
				processingChain, lbConf, input, output, true);

	}
	
	public static SimulationDescription newNextSimulationInstance(
			Element item, int startingIteration) {

		int iterationsCount = Integer.parseInt(item.getAttribute("iterationsCount"));
		String processingChain = item.getAttribute("processingChain");
		String lbConf = item.getAttribute("lbConfiguration");
		
		NodeList nl = item.getElementsByTagName("Input");
		IODescription input = null;
		if(nl.getLength() > 0) {

			input = IODescription.newInstance((Element) nl.item(0));

		}
					
		nl = item.getElementsByTagName("Output");
		IODescription output = null;
		if(nl.getLength() > 0) {

			output = IODescription.newInstance((Element) nl.item(0));

		}
		
		return new SimulationDescription(startingIteration, iterationsCount,
				processingChain, lbConf, input, output, false);
		
	}
	
	/**
	 * @param latticeClass Java class of the lattice used for this simulation.
	 * @param pc Processing chain description.
	 * 
	 * @return A power descriptor string. This string is based on the type of lattice
	 * and the processing chain used for the simulation. The idea is that the
	 * simulation code complexity is only linked to the type of lattice and the
	 * processing chain.
	 */
	public static String getPowerDescriptor(String latticeClass,
			ProcessingChainDescription pc) {

		return latticeClass + pc.getPerformanceHash();

	}

	
	////////////////////
	// Public methods //
	////////////////////

	/**
	 * @return Starting iteration of this simulation.
	 */
	public int getStartingIteration() {
		
		return startingIteration;
		
	}
	
	/**
	 * @return Number of iterations of this simulation.
	 */
	public int getIterationsCount() {
		
		return iterationsCount;
		
	}
	
	/**
	 * @return ID string of the processing chain of this
	 * simulation.
	 */
	public String getProcessingChainId() {

		return processingChain;

	}
	
	/**
	 * @return ID string of the LB configuration of this
	 * simulation.
	 */
	public String getLBConfigurationId() {

		return lbConf;

	}
	
	/**
	 * Indicates if the state of this simulation is used by next simulation.
	 * 
	 * @return <tt>true</tt> if the state of the simulation is kept for next simulation.
	 * <tt>false</tt> if it can be cleared.
	 */
	public boolean getKeepStateForNextSimulation() {
		
		return keepStateForNextSimulation;
		
	}
	
	public void setKeepStateForNextSimulation(boolean newValue) {

		this.keepStateForNextSimulation = newValue;

	}
	
	/**
	 * @return IODescription of the InputClient of this simulation
	 * or <tt>null</tt> if no input was defined.
	 */
	public IODescription getInput() {
		
		return input;
		
	}
	
	/**
	 * @return IODescription of the OutputClient of this simulation
	 * or <tt>null</tt> if no output was defined.
	 */
	public IODescription getOutput() {
		
		return output;
		
	}

	/**
	 * @return Last iteration of this simulation.
	 */
	public int getLastIteration() {
		
		return lastIteration;
		
	}

	public boolean isFirstOfSequence() {
		return isFirstOfSequence;
	}

}
