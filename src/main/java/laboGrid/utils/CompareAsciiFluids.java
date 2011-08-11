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
import java.util.Scanner;

import laboGrid.math.IntegerVector;



public class CompareAsciiFluids {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if(args.length != 4) {
			System.err.println("Usage : <latticeSize> <path to input1> <path to input1> <slice type>");
			System.exit(-1);
		}
		
		int[] latticeSize = IntegerVector.parseIntegerVector(args[0]);
		String path1 = args[1];
		String path2 = args[2];
		String sliceType = args[3];
		
		int xSize = latticeSize[0];
		int ySize = latticeSize[1];
		int zSize = latticeSize[2];
		
		if(sliceType.equals("XY")) {
			for(int i = 0; i < zSize; ++i) {
				File sliceFile1 = new File(path1+"/xySlice_"+i+".txt");
				File sliceFile2 = new File(path2+"/xySlice_"+i+".txt");
				
				Scanner scanFile1 = new Scanner(sliceFile1);
				Scanner scanFile2 = new Scanner(sliceFile2);
				
				for(int v = 0; v < ySize; ++v) {
					for(int u = 0; u < xSize; ++u) {

						float ux1 = Float.parseFloat(scanFile1.next());
						float uy1 = Float.parseFloat(scanFile1.next());
						float uz1 = Float.parseFloat(scanFile1.next());
						float dloc1 = Float.parseFloat(scanFile1.next());
						boolean solid1 = Integer.parseInt(scanFile1.next()) == 1;
						
						float ux2 = Float.parseFloat(scanFile2.next());
						float uy2 = Float.parseFloat(scanFile2.next());
						float uz2 = Float.parseFloat(scanFile2.next());
						float dloc2 = Float.parseFloat(scanFile2.next());
						boolean solid2 = Integer.parseInt(scanFile2.next()) == 1;

						if(ux1 != ux2 ||
							uy1 != uy2 ||
							uz1 != uz2 ||
							dloc1 != dloc2 ||
							solid1 != solid2) {
							System.out.println(IntegerVector.toString(new int[] {u,v,i})+": "+
									ux1+","+uy1+","+uz1+","+dloc1+","+solid1+ " <> " +
									ux2+","+uy2+","+uz2+","+dloc2+","+solid2);
						}
						
					}
				}
			}
		}
	}

}
