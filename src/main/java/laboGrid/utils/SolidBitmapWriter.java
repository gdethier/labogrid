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

import laboGrid.lb.solid.d3.D3SolidBitmap;
import laboGrid.math.IntegerVector;

public class SolidBitmapWriter {

	/**
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length != 3) {
			
			System.out.println("Usage: <size vector> <solidType (ascii, bin)> <file name>");
			System.exit(-1);
			
		}
		
		int[] latticeSize = IntegerVector.parseIntegerVector(args[0]);
		String solidType = args[1];
		String fileName = args[2];
		
		int xSize, ySize, zSize;
		
		xSize = latticeSize[0];
		ySize = latticeSize[1];
		zSize = latticeSize[2];
		
		// Generating bitmaps
		D3SolidBitmap qSolid = new D3SolidBitmap(xSize, ySize, zSize);
		qSolid.setFluid();
		//SolidFillers.partialCircularPipe(qSolid,xSize, ySize, from[0], to[0], from[1], to[1]);
		SolidFillers.partialRectangularPipe(qSolid,xSize, ySize, 0, xSize, 0, ySize);
		
		// Writing file
		if("ascii".equals(solidType))
			qSolid.writeAsciiSolid(fileName);
		else if("bin".equals(solidType))
			qSolid.writeBinSolid(fileName);
		else
			throw new Exception("Unknown solid type: "+solidType);
			
	}
}
