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
package laboGrid.lb.lattice;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import laboGrid.CompressionUtil;
import laboGrid.lb.LBException;
import laboGrid.lb.solid.Solid;



public abstract class Lattice implements Serializable {

	private static final long serialVersionUID = 1L;

	protected int[] size;
	
	public int[] getSize() {
		return size;
	}
	
	public void setSize(int[] latticeSize) {
		this.size = latticeSize;
	}

	public static Lattice readBinFluid(String fileName) throws IOException {
		Lattice toReturn = null;
		FileInputStream fis = new FileInputStream(fileName);
		try {
			toReturn = (Lattice) CompressionUtil.readGZipCompressedObject(fis);
		} catch (ClassNotFoundException e) {
			throw new IOException("Class not found : "+e.getMessage());
		}
		fis.close();
		return toReturn;
	}
	
	public void writeBinFluid(String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		CompressionUtil.writeGZipCompressedObject(this, fos);
		fos.close();
	}


	public abstract LatticeDescriptor getLatticeDescriptor();

	public abstract void setEquilibrium() throws LBException;
	public abstract void setRandom() throws LBException ;
	public abstract void inPlaceStream();
	public abstract void inPlaceStream(int bufPos);
	public abstract void invalidateEmptySites(Solid solid);

	public abstract void fillBuffers();
	public abstract BorderData getOutcomingDensities(int link) throws LBException;
	public abstract BorderData getOutcomingDensities(int bufPos, int link) throws LBException;
	public abstract void setIncomingDensities(BorderData data) throws LBException;
	
	public abstract Lattice clone();

}
