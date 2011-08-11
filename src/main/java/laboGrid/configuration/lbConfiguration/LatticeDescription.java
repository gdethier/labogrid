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

import laboGrid.math.IntegerVector;
import laboGrid.math.VectorWrongFormatException;


import org.w3c.dom.Element;


/**
 * Description of a lattice to be used for a simulation. This description
 * contains a size and a class name. The size argument is a vector of integer
 * (size for each dimension). The class name must be of a sub-class of the
 * <code>laboGrid.lb.lattice.Lattice</code> class. The class must be instantiable
 * with the default class loader.
 * 
 * This class is associated to XML element <code>Lattice</code>, part of the
 * <code>LBConfiguration</code> element (see LaBoGridConfiguration.xsd).
 * 
 * @author Gérard Dethier
 *
 */
public class LatticeDescription implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Size of lattice dimensions */
	protected int[] size;
	/** Java class name of the lattice (must be a sub-class of
	 * <code>laboGrid.lb.lattice.Lattice</code>) */
	protected String className;
	
	
	/**
	 * Constructor called by <code>newInstance</code>.
	 * 
	 * @param latticeSize Size of the dimensions of the lattice.
	 * @param latticeClass Lattice class name.
	 */
	public LatticeDescription(int[] latticeSize, String latticeClass) {

		this.size = latticeSize;
		this.className = latticeClass;

	}


	/**
	 * Instantiates a LatticeDescription object based on
	 * an XML element.
	 * 
	 * @param item An <code>Lattice</code> XML element.
	 * 
	 * @return A LatticeDescription object.
	 * 
	 * @throws VectorWrongFormatException If the size attribute of the XML element
	 * is not in the right format ( "(s_1, s_2, ..., s_n)" if the lattice has n dimensions;
	 * s_i being an integer).
	 */
	public static LatticeDescription newInstance(Element item) throws VectorWrongFormatException {

		int[] size = IntegerVector.parseIntegerVector(item.getAttribute("size"));
		String className = item.getAttribute("class");

		return new LatticeDescription(size, className);

	}
	

	/**
	 * @return The size of the lattice.
	 */
	public int[] getSize() {
		return size;
	}

	/**
	 * @return The name of the lattice class.
	 */
	public String getClassName() {
		return className;
	}

}
