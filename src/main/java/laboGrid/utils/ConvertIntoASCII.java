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
package laboGrid.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.model.d3.D3BlockModelGraph;
import laboGrid.impl.common.simulation.algorithm.LBState.ContentType;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.math.IntegerVector;
import laboGrid.math.VectorWrongFormatException;

import org.xml.sax.SAXException;



/**
 * This class converts sublattices' state (portable binary format)
 * into ASCII files. The lattice is sliced and there is one file per
 * slice. Each line of an ASCII file represents a particular site and
 * has following format:  
 * <p> 
 * ux uy uz press solid accux accuy accuz
 * <p>
 * where:
 * <ul>
 * <li>ui is the speed regarding axe i</li>
 * <li>press is (1./3.) of local density</li>
 * <li>solid is 1 if solid and 0 otherwise</li> 
 * <li>accui is the accumulated speed regarding axe i (accumulation must have
 * been enabled in simulation configuration, it is not the case by default
 * see {@link laboGrid.procChain.operators.d3.D3SpeedAccumulator}).</li>
 * </ul>
 * <p>
 * If slices have size (I,J) where I is the number of rows and J the number
 * of columns, then site at position (i,j) in slice is at line (I*j + i) in the
 * ASCII file (this data layout has been chosen in order to be compatible
 * with MATLAB's way of storing matrices in ASCII format).
 */
public class ConvertIntoASCII {

	/**
	 * args[0] Path to input data
	 * args[1] Prefix to output data path
	 * args[2] Slices type (XY,YZ)
	 * args[3] Slices to process at a time. (controls memory consumption)
	 * args[4] Iteration to consider
	 * args[5] State files are compressed or not
	 * @param args
	 * @throws LBException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ClassNotFoundException 
	 * @throws VectorWrongFormatException 
	 */
	public static void main(String[] args) throws SAXException, IOException, LBException, ClassNotFoundException, VectorWrongFormatException {
		
		if(args.length != 6) {
			System.err.println("Usage : <path to input> <path to output> <slice type> <slices to process at a time> <iteration> <state files are compressed (raw, mixed or compress)>");
			System.exit(-1);
		}
		
		String inputPath = args[0];
		String outputPrefix = args[1];
		String sliceType = args[2];
		int slicesToProcess = Integer.parseInt(args[3]);
		int iteration = Integer.parseInt(args[4]);
		ContentType compressedFiles = ContentType.valueOf(args[5]);

		convertStateFiles(inputPath, outputPrefix, sliceType, slicesToProcess,
				iteration, compressedFiles);
		
	}

	/**
	 * @param inputPath
	 * @param outputPrefix
	 * @param sliceType
	 * @param slicesToProcess
	 * @param iteration
	 * @param compressedFiles
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws LBException
	 * @throws FileNotFoundException
	 */
	public static void convertStateFiles(String inputPath,
			String outputPrefix, String sliceType, int slicesToProcess,
			int iteration, ContentType compressedFiles) throws IOException,
			ClassNotFoundException, LBException, FileNotFoundException {
		// Check output directory
		String outputDirName = outputPrefix+"_"+iteration;
		File outputDir = new File(outputDirName);
		if(!outputDir.exists() && !outputDir.mkdirs()) {
			throw new IOException("Could not create directory "+outputDirName);
		}

		// Read input data
		D3BlockModelGraph mGraph = (D3BlockModelGraph) ModelGraph.read(inputPath+"/"+ModelGraph.defaultModelGraphFileUID);
		int[] latticeSize = mGraph.getLatticeSize();
		
		
		System.out.println("Sub-lattices organisation: ");
		SubLattice[] subLattices = mGraph.getSubLattices();
		for(int i = 0; i < subLattices.length; ++i) {
			System.out.println("Sub-Lattice "+i+" has position "+IntegerVector.toString(subLattices[i].getQPosition())+", real position "+IntegerVector.toString(subLattices[i].getPosition())+" and size "+IntegerVector.toString(subLattices[i].getSize()));
		}

		int xSize = latticeSize[0];
		int ySize = latticeSize[1];
		int zSize = latticeSize[2];
		
		long t1 = System.currentTimeMillis();
		
		if(sliceType.equals("XY")) {
			
			for(int i = 0; i < zSize; i+=slicesToProcess) {
				System.out.println("* Creating XY slices for positions "+i+" to "+(Math.min((i+slicesToProcess), zSize)-1));
				D3AggregatedSlice[] s =
					D3MacroVarExtractor.getXYCompleteSlice(i,
							Math.min((i + slicesToProcess), zSize),
							inputPath, iteration, mGraph, compressedFiles);

				for(int j = 0; j < s.length; ++j) {
					FileOutputStream fos = new FileOutputStream(outputDirName+"/xySlice_"+(i+j)+".txt");
					PrintStream ps = new PrintStream(fos);
					for(int v = 0; v < ySize; ++v) {
						for(int u = 0; u < xSize; ++u) {
							D3MacroVar mv = s[j].getMacroVars(u, v);
							mv.print(ps);
						}
					}
				}
			}

		} else if(sliceType.equals("YZ")) {
			
			for(int i = 0; i < xSize; i+=slicesToProcess) {
				System.out.println("* Creating YZ slices for positions "+i+" to "+(Math.min((i+slicesToProcess), xSize)-1));
				D3AggregatedSlice[] s =
					D3MacroVarExtractor.getYZCompleteSlice(i,
							Math.min((i+slicesToProcess), xSize),
							inputPath, iteration, mGraph, compressedFiles);

				for(int j = 0; j < s.length; ++j) {
					FileOutputStream fos = new FileOutputStream(outputDirName+"/yzSlice_"+(i+j)+".txt");
					PrintStream ps = new PrintStream(fos);
					for(int v = 0; v < zSize; ++v) {
						for(int u = 0; u < ySize; ++u) {
							D3MacroVar mv = s[j].getMacroVars(u, v);
							mv.print(ps);
						}
					}
				}
			}

		} else if(sliceType.equals("XZ")) {
			
			for(int i = 0; i < ySize; i+=slicesToProcess) {
				System.out.println("* Creating XZ slices for positions "+i+" to "+(Math.min((i+slicesToProcess), ySize)-1));
				D3AggregatedSlice[] s =
					D3MacroVarExtractor.getXZCompleteSlice(i,
							Math.min((i+slicesToProcess), ySize),
							inputPath, iteration, mGraph, compressedFiles);

				for(int j = 0; j < s.length; ++j) {
					FileOutputStream fos = new FileOutputStream(outputDirName+"/xzSlice_"+(i+j)+".txt");
					PrintStream ps = new PrintStream(fos);
					for(int v = 0; v < zSize; ++v) {
						for(int u = 0; u < xSize; ++u) {
							D3MacroVar mv = s[j].getMacroVars(u, v);
							mv.print(ps);
						}
					}
				}
			}

		} else {
			throw new LBException("Unknown slice type "+sliceType);
		}
		
		long t2 = System.currentTimeMillis();
		System.out.println("It took "+(t2-t1)/1000+"s to convert data.");
	}

}
