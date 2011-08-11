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

import laboGrid.lb.LBException;
import laboGrid.math.VectorWrongFormatException;



import org.w3c.dom.Element;


/**
 * Description of LB parameters to use for a given simulation. An LB
 * simulation is described by a lattice (see <code>LatticeDescription</code>),
 * a solid (see <code>SolidDescription</code>) and a model graph generator
 * (see <code>SubLatticesConfiguration</code>). An ID string is associated to
 * LB parameters. This ID is used to link LB parameters to a simulation.
 * 
 * This class is associated to XML element <code>LBConfiguration</code>
 * (see LaBoGridConfiguration.xsd).
 * 
 * @author Gérard Dethier
 */
public class LBConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	/** ID string of LB parameters. */
	private String id;
	/** Lattice description. */
	private LatticeDescription latticeDesc;
	/** Solid description. */
	private SolidDescription solidDesc;
	/** Model graph generator description. */
	private SubLatticesConfiguration subDesc;

	
	/**
	 * Constructor.
	 * 
	 * @param id ID string.
	 * @param latticeDesc Lattice description.
	 * @param solidDesc Solid description.
	 * @param subDesc Model graph generator description.
	 */
	public LBConfiguration(
			String id,
			LatticeDescription latticeDesc,
			SolidDescription solidDesc,
			SubLatticesConfiguration subDesc) {

		this.id = id;
		this.latticeDesc = latticeDesc;
		this.solidDesc = solidDesc;
		this.subDesc = subDesc;

	}


	/**
	 * Instantiates an <code>LBConfiguration</code> object based on a given
	 * XML <code>LBConfiguration</code> element.
	 * 
	 * @param node An XML <code>LBConfiguration</code> element.
	 * 
	 * @return An LBConfiguration object.
	 * 
	 * @throws VectorWrongFormatException If the size of the lattice
	 * given in the Lattice XML element is not in the right format.
	 * 
	 * @throws LBException If the solid type given in Solid XML element is
	 * unsupported.
	 */
	public static LBConfiguration newInstance(Element node)
	throws VectorWrongFormatException, LBException {

		String id = node.getAttribute("id");

		Element lattice = (Element) node.getElementsByTagName("Lattice").item(0);
		Element solid = (Element) node.getElementsByTagName("Solid").item(0);
		Element sub = (Element) node.getElementsByTagName("SubLattices").item(0);

		LatticeDescription latticeDesc = LatticeDescription.newInstance(lattice);
		SolidDescription solidDesc = SolidDescription.newInstance(solid);
		SubLatticesConfiguration subDesc = SubLatticesConfiguration.newInstance(sub);

		return new LBConfiguration(id, latticeDesc, solidDesc, subDesc);

	}

	/**
	 * @return Lattice description.
	 */
	public LatticeDescription getLatticeDescription() {
		return latticeDesc;
	}

	/**
	 * @return Solid description.
	 */
	public SolidDescription getSolidDescription() {
		return solidDesc;
	}
	
	/**
	 * @return Model graph generator description.
	 */
	public SubLatticesConfiguration getSubLatticesConfiguration() {
		return subDesc;
	}

	/**
	 * @return LB parameters ID string.
	 */
	public String getId() {
		
		return id;
		
	}

}
