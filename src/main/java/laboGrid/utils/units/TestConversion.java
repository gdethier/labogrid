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
package laboGrid.utils.units;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.model.d3.D3CuboidsGenerator;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.impl.common.simulation.algorithm.LBState;
import laboGrid.impl.common.simulation.algorithm.LBState.ContentType;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.lb.lattice.d3.q19.D3Q19DefaultLattice;
import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;
import laboGrid.lb.solid.Solid;
import laboGrid.lb.solid.d3.D3SolidBitmap;
import laboGrid.utils.ConvertIntoASCII;
import laboGrid.utils.D3MacroVar;

import org.junit.*;


public class TestConversion {
	private static int[] spaceSize = {32, 16, 64};
	private static int numOfSubs = 8;
	private static int iteration = 42;
	
	private D3Q19DefaultLattice lattice;
	private D3SolidBitmap solid;

	@Before
	public void generateFiles() throws LBException, IOException {
		lattice = new D3Q19DefaultLattice(spaceSize);
		solid = new D3SolidBitmap(spaceSize);

		lattice.setRandom();
		solid.setRandom();
		
		D3CuboidsGenerator mGen = new D3CuboidsGenerator();
		mGen.setParameters(spaceSize,
				numOfSubs, D3Q19LatticeDescriptor.getSingleton());
		ModelGraph mGraph = mGen.generateModelGraph();
		mGraph.write(ModelGraph.defaultModelGraphFileUID);
		
		for(int i = 0; i < mGraph.getSubLatticesCount(); ++i) {
			SubLattice sub = mGraph.getSubLattice(i);
			int[] from = sub.getMinPoint();
			int[] to = sub.getMaxPoint();

			D3SolidBitmap subSolid = solid.getPartition(from, to);
			D3Q19DefaultLattice subLattice = (D3Q19DefaultLattice) lattice.getPartition(from, to);
			LBState state = new LBState(subLattice, subSolid);
			
			File stateFile = new File(LBSimThread.getStateFileUID(i, iteration));
			stateFile.deleteOnExit(); // No need for explicit clean-up
			state.writeState(stateFile, ContentType.compress);
		}
	}
	
	@Test
	public void testXYConversion() throws FileNotFoundException, IOException, ClassNotFoundException, LBException {
		ConvertIntoASCII.convertStateFiles(".", "", "XY", 10, iteration, ContentType.compress);
		testXYSlices();
		cleanUpXYFiles();
	}

	private void cleanUpXYFiles() {
		String path = "_"+iteration;
		String filePref = path+"/xySlice_";
		cleanFiles(path, filePref, spaceSize[2]);
	}
	
	private void cleanUpXZFiles() {
		String path = "_"+iteration;
		String filePref = path+"/xzSlice_";
		cleanFiles(path, filePref, spaceSize[1]);
	}
	
	private void cleanUpYZFiles() {
		String path = "_"+iteration;
		String filePref = path+"/yzSlice_";
		cleanFiles(path, filePref, spaceSize[0]);
	}
	
	private void cleanFiles(String path, String filePref, int maxFileNum) {
		for(int i = 0; i < maxFileNum; ++i) {
			File file = new File(filePref + i + ".txt");
			file.delete();
		}
		File pathFile = new File(path);
		pathFile.delete();
		File modelFile = new File(ModelGraph.defaultModelGraphFileUID);
		modelFile.delete();
	}

	@Test
	public void testXZConversion() throws FileNotFoundException, IOException, ClassNotFoundException, LBException {
		ConvertIntoASCII.convertStateFiles(".", "", "XZ", 10, iteration, ContentType.compress);
		testXZSlices();
		cleanUpXZFiles();
	}
	
	@Test
	public void testYZConversion() throws FileNotFoundException, IOException, ClassNotFoundException, LBException {
		ConvertIntoASCII.convertStateFiles(".", "", "YZ", 10, iteration, ContentType.compress);
		testYZSlices();
		cleanUpYZFiles();
	}

	private void testXYSlices() throws FileNotFoundException {
		String prefix = "_"+iteration+"/xySlice_";
		for(int z = 0; z < spaceSize[2]; ++z) {
			FileInputStream fis = new FileInputStream(prefix+z+".txt");
			Scanner s = new Scanner(fis);
			for(int y = 0; y < spaceSize[1]; ++y) {
				for(int x = 0; x < spaceSize[0]; ++x) {
					D3MacroVar vars = new D3MacroVar();
					vars.read(s);

					checkMacroVars(x, y, z, vars);
				}
			}
		}
	}
	
	private void testXZSlices() throws FileNotFoundException {
		String prefix = "_"+iteration+"/xzSlice_";
		for(int y = 0; y < spaceSize[1]; ++y) {
			FileInputStream fis = new FileInputStream(prefix+y+".txt");
			Scanner s = new Scanner(fis);
			for(int z = 0; z < spaceSize[2]; ++z) {
				for(int x = 0; x < spaceSize[0]; ++x) {
					D3MacroVar vars = new D3MacroVar();
					vars.read(s);

					checkMacroVars(x, y, z, vars);
				}
			}
		}
	}
	
	private void testYZSlices() throws FileNotFoundException {
		String prefix = "_"+iteration+"/yzSlice_";
		for(int x = 0; x < spaceSize[0]; ++x) {
			FileInputStream fis = new FileInputStream(prefix+x+".txt");
			Scanner s = new Scanner(fis);
			for(int z = 0; z < spaceSize[2]; ++z) {
				for(int y = 0; y < spaceSize[1]; ++y) {
					D3MacroVar vars = new D3MacroVar();
					vars.read(s);

					checkMacroVars(x, y, z, vars);
				}
			}
		}
	}

	/**
	 * @param z
	 * @param y
	 * @param x
	 * @param vars
	 * @throws Error
	 */
	private void checkMacroVars(int x, int y, int z, D3MacroVar vars)
			throws Error {
		if(vars.getSolid() != (solid.at(x, y, z) != Solid.FLUID))
			throw new Error("");

		if(! vars.getSolid()) {
			if(Math.abs(lattice.getXSpeed(x, y, z) - vars.getXSpeed()) > 10e-7)
				throw new Error("");
			
			if(Math.abs(lattice.getYSpeed(x, y, z) - vars.getYSpeed()) > 10e-7)
				throw new Error("");
			
			if(Math.abs(lattice.getZSpeed(x, y, z) - vars.getZSpeed()) > 10e-7)
				throw new Error("");
		}
	}
}
