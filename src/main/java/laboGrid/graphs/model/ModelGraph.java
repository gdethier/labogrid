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
package laboGrid.graphs.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import laboGrid.lb.SubLattice;



public abstract class ModelGraph implements Serializable {
	public static final String defaultModelGraphFileUID = "mGraph.dat";

	private static final long serialVersionUID = 1L;

	protected int[] latticeSize;
	protected SubLattice[] subsList;
	
	public ModelGraph() {
	}
	
	public ModelGraph(int[] latticeSize, SubLattice[] subLattices) {
		this.latticeSize = latticeSize;
		this.subsList = subLattices;
	}
	
	public SubLattice[] getSubLattices() {
		return subsList;
	}
	
	public void setSubLattices(SubLattice[] subsList) {
		this.subsList = subsList;
	}
	
	public int getSubLatticesCount() {
		return subsList.length;
	}

	public SubLattice getSubLattice(int subLId) {
		return subsList[subLId];
	}
	
	public int[] getLatticeSize() {
		return latticeSize;
	}
	
	public void write(String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.close();
	}
	
	public static ModelGraph read(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(fileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		ModelGraph mGraph = (ModelGraph) ois.readObject();
		ois.close();
		return mGraph;
	}
}
