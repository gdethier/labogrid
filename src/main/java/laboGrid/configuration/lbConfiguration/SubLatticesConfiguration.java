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
package laboGrid.configuration.lbConfiguration;

import java.io.Serializable;
import java.util.Collection;

import org.w3c.dom.Element;

/**
 * Description of the model graph generator to be used for a simulation. This description
 * contains a generator class name and a minimum number of sub-lattices to be generated.
 * 
 * The class name must be of
 * a sub-class of <code>laboGrid.graphs.model.ModelGraph</code> class.
 * The class must be instantiable with the default class loader.
 * 
 * This class is associated to XML element <code>SubLattices</code>, part of the
 * <code>LBConfiguration</code> element (see LaBoGridConfiguration.xsd).
 * 
 * @author Gérard Dethier
 */
public class SubLatticesConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Minimum number of sub-lattices to be generated. */
	private int minSubLatticesCount;
	/** Class name of the generator to use. */
	private String generatorClass;

	
	/**
	 * Constructor.
	 * 
	 * @param minSubLatticesCount Minimum number of sub-lattices to be generated.
	 * @param generatorClass Class name of the generator to use.
	 */
	public SubLatticesConfiguration(int minSubLatticesCount, String generatorClass) {
		this.minSubLatticesCount = minSubLatticesCount;
		this.generatorClass = generatorClass;
	}

	
	/**
	 * Instantiates a SubLatticesConfiguration object based on
	 * a <code>SubLattices</code> XML element.
	 * 
	 * @param item A <code>SubLattices</code> XML element.
	 * 
	 * @return A SubLatticesConfiguration object.
	 * 
	 */
	public static SubLatticesConfiguration newInstance(Element item) {
		
		int minSubCount = Integer.parseInt(item.getAttribute("minSubLatticesCount"));
		String generatorClass = item.getAttribute("generatorClass");
		
		return new SubLatticesConfiguration(minSubCount, generatorClass);
	}

	/**
	 * @return Minimum number of sub-lattices to be generated.
	 */
	public int getMinSubLatticesCount() {
		return minSubLatticesCount;
	}

	/**
	 * @return Model graph generator class name.
	 */
	public String getGeneratorClassName() {
		return generatorClass;
	}

	/**
	 * Adds the model graph generator class name to a given
	 * collection.
	 */
	public void getAllClassNames(Collection<String> names) {
		names.add(generatorClass);
	}

}
