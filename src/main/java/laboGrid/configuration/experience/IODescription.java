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

import laboGrid.ioClients.InputClient;
import laboGrid.ioClients.InputClientException;
import laboGrid.ioClients.OutputClient;
import laboGrid.ioClients.OutputClientException;


import org.w3c.dom.Element;

import dimawo.Reflection;
import dimawo.ReflectionException;

/**
 * Describes the input/output of a simulation. Indeed, a simulation can
 * need <i>input</i> data and produce <i>output</i> data.
 * An Input/Output is defined by a client type string (local, LBG, LaBoGrid...)
 * and a parameters string which format depends on the client type.
 * 
 * This class is associated to XML elements <code>Input</code> and
 * <code>Output</code> defined in the context of a <code>Simulation</code> 
 * XML element (see LaBoGridConfiguration.xsd).
 * 
 * @author Gérard Dethier
 */
public class IODescription implements Serializable {

	private static final long serialVersionUID = 1L;

	/** IO Client type */
	private String clientClass;
	/** IO Client parameters */
	private String[] parameters;

	
	/**
	 * Constructor used by <code>newInstance</code>.
	 * 
	 * @param client An IO client type.
	 * @param parameters An IO client parameters string.
	 */
	public IODescription(String clientClass, String[] parameters) {

		this.clientClass = clientClass;
		this.parameters = parameters;

	}


	////////////////////
	// Static members //
	////////////////////
	
	/**
	 * @param item <code>Input</code>/<code>Output</code> XML element.
	 * @return IODescription instance based on given XML element.
	 */
	public static IODescription newInstance(Element item) {

		String clientClass = item.getAttribute("clientClass");
		String[] parameters = item.getAttribute("parameters").split("[ \t\n]+");

		return new IODescription(clientClass, parameters);

	}
	
	
	////////////////////
	// Public methods //
	////////////////////
	
	/**
	 * @return Client type string.
	 */
	public String getClientClass() {
		
		return clientClass;
		
	}
	
	/**
	 * @return Parameters string.
	 */
	public String[] getParameters() {

		return parameters;

	}


	public InputClient getInputClient() throws ReflectionException, InputClientException {
		InputClient in = (InputClient) Reflection.newInstance(clientClass);
		in.setParameters(parameters);
		return in;
	}
	
	public OutputClient getOutputClient() throws ReflectionException, OutputClientException {
		OutputClient out = (OutputClient) Reflection.newInstance(clientClass);
		out.setParameters(parameters);
		return out;
	}

}
