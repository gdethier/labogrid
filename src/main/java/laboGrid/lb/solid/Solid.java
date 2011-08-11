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
package laboGrid.lb.solid;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import laboGrid.CompressionUtil;
import laboGrid.lb.LBException;



public abstract class Solid implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final byte  SOLID=0, FLUID=1, UNINITIALIZED=3;//, SURFACESOLID=2;
	protected int[] size;
	
//	public static Solid readBinSolid(String fileName) throws IOException {
//		Solid toReturn = null;
//		FileInputStream fis = new FileInputStream(fileName);
//		try {
//			toReturn = (Solid) CompressionUtil.readGZipCompressedObject(fis);
//		} catch (ClassNotFoundException e) {
//			throw new IOException("Class not found : "+e.getMessage());
//		}
//		fis.close();
//		return toReturn;
//	}
	
	public static Solid readBinSolid(String fileName) throws IOException {
		Solid toReturn = null;
		FileInputStream fis = new FileInputStream(fileName);
		GZIPInputStream gis = new GZIPInputStream(fis);
		ObjectInputStream ois = new ObjectInputStream(gis);
		try {
			toReturn = (Solid) ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException("Class not found : "+e.getMessage());
		} finally {
			ois.close();
		}
		return toReturn;
	}
	
	public void writeBinSolid(String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		CompressionUtil.writeGZipCompressedObject(this, fos);
		fos.close();
	}

	public int[] getSize() {
		return size;
	}
	
	public void setSize(int[] size) {
		this.size = size;
	}

	public abstract void setFluid();

	public abstract Solid getPartition(int[] minPoint, int[] maxPoint) throws LBException;
	public abstract void readAsciiSolid(String fileName) throws IOException;
	public abstract void readCompressedAsciiSolid(String fileName) throws IOException;
}
