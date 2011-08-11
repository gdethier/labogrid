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


import org.w3c.dom.Element;


/**
 * Description of a solid to be used for a simulation. This description
 * contains a solid type, a class name and a file UID.
 * 
 * The solid type indicates if the solid file contains binary or ascii data.
 * The class name must be of
 * a sub-class of <code>laboGrid.lb.solid.Solid</code> class.
 * The class must be instantiable with the default class loader.
 * Finally, the file UID must point to a file that will be accessed using
 * the InputClient defined
 * for the simulation using the LBConfiguration containing this solid description.
 * 
 * This class is associated to XML element <code>Solid</code>, part of the
 * <code>LBConfiguration</code> element (see LaBoGridConfiguration.xsd).
 * 
 * @author Gérard Dethier
 */
public class SolidDescription implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Solid file types. */
	public enum SolidType {ascii, compressed_ascii, bin};
	
	/** File UID. */
	private String fileName;
	/** Solid type. */
	private SolidType type;
	/** Solid class name. */
	private String className;
	
	
	/**
	 * Constructor.
	 * 
	 * @param solidFile Solid file UID.
	 * @param solidType Solid type.
	 * @param className Solid class name.
	 */
	public SolidDescription(String solidFile, SolidType solidType, String className) {
		this.fileName = solidFile;
		this.type = solidType;
		this.className = className;
	}
	

	/**
	 * Instantiates a SolidDescription object based on
	 * a <code>Solid</code> XML element.
	 * 
	 * @param solid A <code>Solid</code> XML element.
	 * 
	 * @return A LatticeDescription object.
	 * 
	 * @throws LBException If an unknown solid type is specified.
	 */
	public static SolidDescription newInstance(Element solid) throws LBException {
		
		String solidFile = solid.getAttribute("fileId");
		String solidType = solid.getAttribute("type");
		String className = solid.getAttribute("class");
		
		SolidDescription.SolidType realSolidType = null;
		if("ascii".equals(solidType)) {
			realSolidType = SolidDescription.SolidType.ascii;
		} else if("compressed-ascii".equals(solidType)) {
			realSolidType = SolidDescription.SolidType.compressed_ascii;
		} else if("bin".equals(solidType)) {
			realSolidType = SolidDescription.SolidType.bin;
		} else {
			throw new LBException("Unknown solid type "+solidType);
		}

		return new SolidDescription(solidFile, realSolidType, className);

	}

	/**
	 * @return Solid type.
	 */
	public SolidType getType() {
		return type;
	}

	/**
	 * @return Solid file UID.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return Solid class name.
	 */
	public String getClassName() {
		return className;
	}

}
