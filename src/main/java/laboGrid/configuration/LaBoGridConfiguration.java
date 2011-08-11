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
package laboGrid.configuration;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import laboGrid.ConfigurationException;
import laboGrid.configuration.experience.ExperienceDescription;
import laboGrid.configuration.experience.IODescription;
import laboGrid.configuration.experience.SimulationDescription;
import laboGrid.configuration.lbConfiguration.LBConfiguration;
import laboGrid.configuration.middleware.LaBoGridMiddlewareConfiguration;
import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.ioClients.InputClient;
import laboGrid.ioClients.OutputClient;
import laboGrid.lb.LBException;
import laboGrid.math.VectorWrongFormatException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dimawo.Reflection;
import dimawo.ReflectionException;

public class LaBoGridConfiguration {
	
	private ExperienceDescription experience;
	private TreeMap<String, LBConfiguration> lbConfs;
	private TreeMap<String, ProcessingChainDescription> procChains;
	private LaBoGridMiddlewareConfiguration middleConf;
	
	public LaBoGridConfiguration(
			ExperienceDescription experience,
			TreeMap<String, LBConfiguration> lbConfs,
			TreeMap<String, ProcessingChainDescription> procChains,
			LaBoGridMiddlewareConfiguration middleConf) {

		this.lbConfs = lbConfs;
		this.experience = experience;
		this.procChains = procChains;
		this.middleConf = middleConf;

	}
	
	public static LaBoGridConfiguration readXmlFile(String configFileName) throws IOException, SAXException, LBException {

		Document doc = parseXmlFile(configFileName);

		NodeList n = doc.getElementsByTagName("Experiment");
		ExperienceDescription experiment = ExperienceDescription.newInstance((Element) n.item(0));
		
		TreeMap<String, LBConfiguration> lbConfs = getLBConfigurations(doc);
		
		n = doc.getElementsByTagName("ProcessingChain");
		TreeMap<String, ProcessingChainDescription> procChain = getProcessingChains(n);
		
		n = doc.getElementsByTagName("LaBoGridMiddleware");
		LaBoGridMiddlewareConfiguration laboMiddleware;
		try {

			laboMiddleware = LaBoGridMiddlewareConfiguration.newInstance((Element) n.item(0));

		} catch (Exception e) {

			throw new LBException("Could not read LaBoGridMiddleware element.", e);

		}
		
		LaBoGridConfiguration conf =
			new LaBoGridConfiguration(experiment, lbConfs, procChain, laboMiddleware);
		
		doSemanticCheck(conf);
		
		return conf;

	}

	private static void doSemanticCheck(LaBoGridConfiguration conf) throws LBException {

		checkClassesAvailability(conf);

		ExperienceDescription exp = conf.getExperienceDescription();
		
		int simCount = exp.getSimulationsCount();
		SimulationDescription prevSim = null;
		SimulationDescription currSim = exp.getSimulationDescription(0);
		
		for(int i = 1; i < simCount; ++i) {
			
			prevSim = currSim;
			currSim = exp.getSimulationDescription(i);
			
			if(prevSim.getKeepStateForNextSimulation()) {
				
				// currSim depends on prevSim
				if(prevSim.getLastIteration() != currSim.getStartingIteration()) {
					
					throw new LBException("Starting iteration of experience "+i+
							" is not equal to last iteration of experience "+(i-1));
					
				}
				
				// TODO : Check LBConfiguration of 2 consecutive simulations.
				
			}
			
		}

	}

	private static void checkClassesAvailability(LaBoGridConfiguration conf) throws LBException {
		
		// Check IO clients
		Iterator<SimulationDescription> simIt = conf.experience.simDescIterator();
		while(simIt.hasNext()) {
			SimulationDescription simDesc = simIt.next();
			
			IODescription input = simDesc.getInput();
			if(input != null) {
				String inputClass = input.getClientClass();
				Object o;
				try {
					o = Reflection.newInstance(inputClass);
				} catch (ReflectionException e) {
					throw new LBException("Cannot instantiate input client "+inputClass, e);
				}
				if(! (o instanceof InputClient)) {
					throw new LBException("Class "+inputClass+" is not an input client.");
				}
			}
			
			IODescription output = simDesc.getOutput();
			if(output != null) {
				String outputClass = output.getClientClass();
				Object o;
				try {
					o = Reflection.newInstance(outputClass);
				} catch (ReflectionException e) {
					throw new LBException("Cannot instantiate output client "+outputClass, e);
				}
				if(! (o instanceof OutputClient)) {
					throw new LBException("Class "+outputClass+" is not an output client.");
				}
			}
		}

		// Check LBConfigurations
		Iterator<LBConfiguration> confIt = conf.lbConfs.values().iterator();
		while(confIt.hasNext()) {
			
			LBConfiguration lbConf = confIt.next();
			
			String latticeClass = lbConf.getLatticeDescription().getClassName();
			try {

				Reflection.newInstance(latticeClass);

			} catch (Exception e) {

				throw new LBException("Lattice class "+latticeClass+
						" is not instantiable.", e);

			}
			
			String solidClass = lbConf.getSolidDescription().getClassName();
			try {

				Reflection.newInstance(solidClass);

			} catch (Exception e) {

				throw new LBException("Solid class "+latticeClass+
						" is not instantiable.", e);

			}
			
			String genClass = lbConf.getSubLatticesConfiguration().getGeneratorClassName();
			try {

				Reflection.newInstance(genClass);

			} catch (Exception e) {

				throw new LBException("Sub-lattices generator "+latticeClass+
						" is not instantiable.", e);

			}
			
		}
		
		// Check processing chains
		Iterator<ProcessingChainDescription> procIt = conf.procChains.values().iterator();
		while(procIt.hasNext()) {
			
			ProcessingChainDescription procDesc = procIt.next();
			
			LinkedList<String> classes = new LinkedList<String>();
			procDesc.getAllClassNames(classes);
			
			Iterator<String> classIt = classes.iterator();
			while(classIt.hasNext()) {
				
				String className = classIt.next();
				
				try {

					Reflection.newInstance(className);

				} catch (Exception e) {

					throw new LBException("Processing chain element "+className+
							" is not instantiable.", e);

				}
				
			}
			
		}
		
	}

	private static Document parseXmlFile(String fileName) throws SAXException, IOException {

		DocumentBuilderFactory dBf = DocumentBuilderFactory.newInstance();
		dBf.setValidating(true);
		dBf.setNamespaceAware(true);

		dBf.setAttribute(
			    "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
			    "http://www.w3.org/2001/XMLSchema");
		
		DocumentBuilder dB = null;
		try {
			dB = dBf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.out.println("Could not create document builder : "+e.toString());
			System.exit(-1);
		}
		
		dB.setErrorHandler(new LaBoGridErrorHandler());
		return dB.parse(fileName);

	}

	private static TreeMap<String, ProcessingChainDescription> getProcessingChains(NodeList n) throws LBException {
		TreeMap<String, ProcessingChainDescription> chains = new TreeMap<String, ProcessingChainDescription>();
		
		for(int i = 0; i < n.getLength(); ++i) {
			Node m = n.item(i);
			if(m instanceof Element) {
				ProcessingChainDescription pc = ProcessingChainDescription.newInstance((Element) n.item(i));
				chains.put(pc.getId(), pc);
			}
		}
		
		return chains;
	}
	
	private static TreeMap<String, LBConfiguration> getLBConfigurations(Document doc) throws LBException {
		
		TreeMap<String, LBConfiguration> lbConfs = new TreeMap<String, LBConfiguration>();
		
		NodeList n = doc.getElementsByTagName("LBConfiguration");
		for(int i = 0; i < n.getLength(); ++i) {

			LBConfiguration lbConf;
			try {

				lbConf = LBConfiguration.newInstance((Element) n.item(i));

			} catch (Exception e) {

				throw new LBException("Could not read LBConfiguration element: "+e.getMessage());

			}

			lbConfs.put(lbConf.getId(), lbConf);

		}

		return lbConfs;

	}

	public LBConfiguration getLBConfiguration(String id) {

		return lbConfs.get(id);

	}

	public ExperienceDescription getExperienceDescription() {

		return experience;

	}

	public ProcessingChainDescription getProcessingChain(String name) {

		return procChains.get(name);

	}

	public LaBoGridMiddlewareConfiguration getMiddlewareConfiguration() {

		return middleConf;

	}

	public Iterator<ProcessingChainDescription> getProcessingChainsIterator() {
		return procChains.values().iterator();
	}

}
