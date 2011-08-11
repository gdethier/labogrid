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
package laboGrid.configuration.processingChain;


import org.w3c.dom.Element;


public class OperatorDescription extends ProcessingChainElementDescription {

	private static final long serialVersionUID = 1L;

	protected String className;
	protected String[] parameters;
	
	public OperatorDescription(String className, String[] parameters) {
		this.className = className;
		this.parameters = parameters;
	}

	public static ProcessingChainElementDescription newInstance(Element e) {
		
		String className = e.getAttribute("class");
		String parameters = e.getAttribute("parameters");
		
		String[] params = parameters.split("[ \t\n]+");
		
		return new OperatorDescription(className, params);
	}

	public String getClassName() {
		return className;
	}

	public String[] getParameters() {
		return parameters;
	}

}
