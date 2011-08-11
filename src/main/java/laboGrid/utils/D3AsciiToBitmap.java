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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.d3.D3LatticeMacroVarsSlice;
import laboGrid.lb.solid.Solid;
import laboGrid.lb.solid.d3.D3SolidBitmapSlice;
import laboGrid.math.IntegerVector;
import laboGrid.math.VectorWrongFormatException;
import laboGrid.procChain.loggers.d3.D3Slice;




/**
 * This program converts the ascii matlab-readable
 * ASCII files to bitmap images.
 * 
 * The ASCII files contain the following data
 * 
 * ux uy uz press solid
 * 
 * ui is the speed regarding axe i
 * press is (1/3) * localDensity
 * solid is 1 if solid and 0 otherwise 
 * 
 * args[0] Lattice size (xs,ys,zs)
 * args[1] Path to input data (the ascii slices directory)
 * args[2] Output data path
 * args[3] Slices type (XY,XZ,YZ)
 * 
 * @param args
 * @throws VectorWrongFormatException 
 * @throws IOException 
 */
public class D3AsciiToBitmap {

	protected int xSize, ySize, zSize;
	protected String slicesDir;
	protected String outputDir;
	protected String sliceType;

	public D3AsciiToBitmap(int[] latticeSize, String slicesDir, String outputDir, String sliceType) {

		this.xSize = latticeSize[0];
		this.ySize = latticeSize[1];
		this.zSize = latticeSize[2];
		this.slicesDir = slicesDir;
		this.outputDir = outputDir;
		this.sliceType = sliceType;

	}
	
	public void generateBitmapFiles() throws IOException, LBException {
		
		if(sliceType.equals("XY")) {

			generateXYBitmapFiles();

		} else if(sliceType.equals("YZ")) {
			
			generateYZBitmapFiles();
			
		} else if(sliceType.equals("XZ")) {
			
			generateXZBitmapFiles();
			
		}
		
	}
	
	private void generateXZBitmapFiles() throws IOException, LBException {
		String baseName = slicesDir+"./xzSlice_";
		String baseOutName = outputDir+"./xzSlice_";
		for(int y = 0; y < ySize; ++y) {
			Scanner sc = new Scanner(new File(baseName+y+".txt"));
			D3LatticeMacroVarsSlice macroVars = new D3LatticeMacroVarsSlice(xSize, zSize);
			D3SolidBitmapSlice solid = new D3SolidBitmapSlice(xSize, zSize);
			
			for(int z = 0; z < zSize; ++z) {
				for(int x = 0; x < xSize; ++x) {
					D3MacroVar mv = readNextMacroVars(sc);
					fillArrays(x, z, macroVars, solid, mv);
				}
			}
			
			D3Slice slice = new D3Slice(macroVars, solid);
			writeImage(ySize, zSize, slice, baseOutName+y+".bmp");
		}
	}

	private void generateYZBitmapFiles() throws IOException, LBException {
		String baseName = slicesDir+"./yzSlice_";
		String baseOutName = outputDir+"./yzSlice_";
		for(int x = 0; x < ySize; ++x) {
			Scanner sc = new Scanner(new File(baseName+x+".txt"));
			D3LatticeMacroVarsSlice macroVars = new D3LatticeMacroVarsSlice(ySize, zSize);
			D3SolidBitmapSlice solid = new D3SolidBitmapSlice(ySize, zSize);
			
			for(int y = 0; y < ySize; ++y) {
				for(int z = 0; z < zSize; ++z) {
					D3MacroVar mv = readNextMacroVars(sc);
					fillArrays(y, z, macroVars, solid, mv);
				}
			}
			
			D3Slice slice = new D3Slice(macroVars, solid);
			writeImage(ySize, zSize, slice, baseOutName+x+".bmp");
		}
	}

	private void generateXYBitmapFiles() throws IOException, LBException {
		String baseName = slicesDir+"./xySlice_";
		String baseOutName = outputDir+"./xySlice_";
		for(int z = 0; z < zSize; ++z) {
			Scanner sc = new Scanner(new File(baseName+z+".txt"));
			D3LatticeMacroVarsSlice macroVars = new D3LatticeMacroVarsSlice(xSize, ySize);
			D3SolidBitmapSlice solid = new D3SolidBitmapSlice(xSize, ySize);
			
			for(int x = 0; x < xSize; ++x) {
				for(int y = 0; y < ySize; ++y) {
					D3MacroVar mv = readNextMacroVars(sc);
					fillArrays(x, y, macroVars, solid, mv);
				}
			}
			
			D3Slice slice = new D3Slice(macroVars, solid);
			writeImage(xSize, ySize, slice, baseOutName+z+".bmp");
		}
	}

	private void writeImage(int uSize, int vSize, D3Slice slice, String fileName) throws IOException {
		BufferedImage im = new BufferedImage(uSize, vSize, BufferedImage.TYPE_BYTE_GRAY);
		slice.setZSpeedImage(im);
		javax.imageio.ImageIO.write(im, "BMP", new File(fileName));
		System.out.println("Wrote bitmap file "+fileName);
	}

	private void fillArrays(int u, int v, D3LatticeMacroVarsSlice macroVars,
			D3SolidBitmapSlice solid, D3MacroVar mv) {
		macroVars.setMacroVars(u, v, mv.getXSpeed(), mv.getYSpeed(), mv.getZSpeed(), mv.getDensity());
		solid.set(u, v, mv.getSolid() ? Solid.SOLID : Solid.FLUID);
	}

	private D3MacroVar readNextMacroVars(Scanner sc) {
		double xSpeed = Double.parseDouble(sc.next());
		double ySpeed = Double.parseDouble(sc.next());
		double zSpeed = Double.parseDouble(sc.next());
		double density = 3 * Double.parseDouble(sc.next());
		int solid = Integer.parseInt(sc.next());
		double accXSpeed = Double.parseDouble(sc.next());
		double accYSpeed = Double.parseDouble(sc.next());
		double accZSpeed = Double.parseDouble(sc.next());

		return new D3MacroVar(xSpeed, ySpeed, zSpeed, density, solid  != 0,
				accXSpeed, accYSpeed, accZSpeed);
	}

	public static void main(String[] args) throws VectorWrongFormatException, IOException, LBException {
		int[] latticeSize = IntegerVector.parseIntegerVector(args[0]);
		String slicesDir = args[1];
		String outputDir = args[2];
		String sliceType = args[3];
		
		File out = new File(outputDir);
		if(!out.exists() && !out.mkdirs()) {
			throw new IOException("Could not create output directory.");
		}
		
		D3AsciiToBitmap atb = new D3AsciiToBitmap(latticeSize, slicesDir, outputDir, sliceType);
		atb.generateBitmapFiles();
	}

}
