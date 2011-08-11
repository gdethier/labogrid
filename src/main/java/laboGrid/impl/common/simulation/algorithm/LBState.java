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
package laboGrid.impl.common.simulation.algorithm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.solid.Solid;



public class LBState {
	
	public enum ContentType {raw, compress, mixed};

	protected Lattice lattice;
	protected Solid solid;
	
	public LBState() {
		this.lattice = null;
		this.solid = null;
	}
	
	public LBState(Lattice l, Solid s) {
		this.lattice = l;
		this.solid = s;
	}
	
	public Lattice getLattice() {
		return lattice;
	}
	
	public Solid getSolid() {
		return solid;
	}
	
	public void writeState(File file, ContentType type) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		
		if(type.equals(ContentType.raw)) {
			
			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			oos.writeObject(lattice);
			oos.writeObject(solid);
			oos.close();

		} else if(type.equals(ContentType.compress)) {
			
			GZIPOutputStream gos = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gos);
//			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(gos));
			oos.writeObject(lattice);
			oos.writeObject(solid);
			oos.close();
			
		} else if(type.equals(ContentType.mixed)){ // mixed

			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			oos.writeObject(lattice);
			oos.flush();

			GZIPOutputStream gos = new GZIPOutputStream(fos);
			oos = new ObjectOutputStream(gos);
//			oos = new ObjectOutputStream(new BufferedOutputStream(gos));
			oos.writeObject(solid);
			oos.close();

		} else {
			throw new Error("Unknown content type: "+type);
		}
	}
	
	public void readState(File file, ContentType type) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		
		if(type.equals(ContentType.raw)) {
			
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));
			lattice = (Lattice) ois.readObject();
			solid = (Solid) ois.readObject();
			ois.close();

		} else if(type.equals(ContentType.compress)) {
			
			GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(fis));
			ObjectInputStream ois = new ObjectInputStream(gis);
			lattice = (Lattice) ois.readObject();
			solid = (Solid) ois.readObject();
			ois.close();
			
		} else { // mixed

			ObjectInputStream ois = new ObjectInputStream(fis);
			lattice = (Lattice) ois.readObject();

			GZIPInputStream gis = new GZIPInputStream(fis);
			ois = new ObjectInputStream(gis);
			solid = (Solid) ois.readObject();
			ois.close();

		}
	}

}
