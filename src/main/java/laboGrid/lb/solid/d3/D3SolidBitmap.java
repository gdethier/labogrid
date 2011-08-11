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
package laboGrid.lb.solid.d3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import laboGrid.lb.LBException;
import laboGrid.lb.solid.Solid;



public class D3SolidBitmap extends D3Solid {

	private static final long serialVersionUID = 1L;
	protected byte[] solid;
	
	public D3SolidBitmap() {
		solid = null;
	}
	
	public D3SolidBitmap(int x, int y, int z) {
		setSize(x, y, z);
	}
	
	public D3SolidBitmap(int[] spaceSize) {
		setSize(spaceSize);
	}

	public void setSize(int x, int y, int z) {
		setSize(new int[]{x, y, z});
	}
	
	public void setSize(int[] size) {
		super.setSize(size);
		solid = new byte[size[0] * size[1] * size[2]];
		Arrays.fill(solid, UNINITIALIZED);
	}
	
	public void setFluid() {
		Arrays.fill(solid, FLUID);
	}
	
	public void set(int x, int y, int z, byte value) {
		assert value == SOLID || value == FLUID;
		solid[x * ySize*zSize +
			  y * zSize +
			  z] = value;
	}

	public byte at(int x, int y, int z) {
		assert solid[x * ySize*zSize +
					  y * zSize +
					  z] != UNINITIALIZED;
		return solid[x * ySize*zSize +
					  y * zSize +
					  z];
	}

	@Override
	public D3SolidBitmapSlice getXYBooleans(int z) throws LBException {
		if(z < 0 || z >= zSize)
			throw new LBException("z out of bounds "+z);
		
		D3SolidBitmapSlice toReturn = new D3SolidBitmapSlice(xSize, ySize);
		
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				toReturn.set(x, y, at(x, y, z));
			}
		}
		
		return toReturn;
	}

	@Override
	public D3SolidBitmapSlice getXZBooleans(int y) throws LBException {
		if(y < 0 || y >= ySize)
			throw new LBException("y out of bounds "+y);
		
		D3SolidBitmapSlice toReturn = new D3SolidBitmapSlice(xSize, zSize);
		
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				toReturn.set(x, z, at(x, y, z));
			}
		}
		
		return toReturn;
	}

	@Override
	public D3SolidBitmapSlice getYZBooleans(int x) throws LBException {
		if(x < 0 || x >= xSize)
			throw new LBException("x out of bounds "+x);
		
		D3SolidBitmapSlice toReturn = new D3SolidBitmapSlice(ySize, zSize);
		
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				toReturn.set(y, z, at(x, y, z));
			}
		}
		
		return toReturn;
	}

	@Override
	public D3SolidBitmap getPartition(int[] minPoint, int[] maxPoint) throws LBException {
		
		int xFrom = minPoint[0];
		int yFrom = minPoint[1];
		int zFrom = minPoint[2];
		int xTo = maxPoint[0];
		int yTo = maxPoint[1];
		int zTo = maxPoint[2];
		D3SolidBitmap solidPart = new D3SolidBitmap(xTo - xFrom, yTo - yFrom, zTo - zFrom);
		
		for(int x = xFrom; x < xTo; ++x) {
			for(int y = yFrom; y < yTo; ++y) {
				for(int z = zFrom; z < zTo; ++z) {
					solidPart.set(x-xFrom, y-yFrom, z-zFrom, at(x, y, z));
				}
			}
		}
		return solidPart;
	}

	@Override
	public void readAsciiSolid(String fileName) throws IOException {
		FileInputStream fis = new FileInputStream(fileName);
		readAsciiSolid(fis);
		fis.close();
	}
	
	@Override
	public void readCompressedAsciiSolid(String fileName) throws IOException {
		FileInputStream fis = new FileInputStream(fileName);
		GZIPInputStream gis = new GZIPInputStream(fis);
		readAsciiSolid(gis);
		gis.close();
	}
	
	public void readAsciiSolid(InputStream in) throws IOException {
		Scanner s = new Scanner(in);
		
		int xSize,ySize,zSize;
		xSize = (int) Float.parseFloat(s.next());
		ySize = (int) Float.parseFloat(s.next());
		zSize = (int) Float.parseFloat(s.next());
		
		setSize(xSize, ySize, zSize);

		//System.out.println("m="+m);
		for(int z = 0; z < zSize; ++z) {
			for(int y = 0; y < ySize; ++y) {
				for(int x = 0; x < xSize; ++x) {
					if(Float.parseFloat(s.next()) == 0) {
//						System.out.println("Solid detected at "+IntegerVector.toString(new int[] {x,y,z}));
//						set(x, y, z, Solid.SURFACESOLID);
						set(x, y, z, Solid.FLUID);
					} else {
						set(x, y, z, Solid.SOLID);
					}
				}
			}
		}
		s.close();
//		solid.detectSurfaceSolids(ld);
	}

	public void writeAsciiSolid(String fileName) throws FileNotFoundException {
		
		PrintStream out = new PrintStream(fileName);

		out.println(xSize);
		out.println(ySize);
		out.println(zSize);
		
		//System.out.println("m="+m);
		for(int z = 0; z < zSize; ++z) {
			for(int y = 0; y < ySize; ++y) {
				for(int x = 0; x < xSize; ++x) {
					if(at(x, y, z) == Solid.FLUID) {
						out.println(0);
					} else {
						out.println(1);
					}
				}
			}
		}
		out.close();
		
//		solid.detectSurfaceSolids(ld);
		
	}

	public void setRandom() {
		Random r = new Random(System.currentTimeMillis());
		for(int i = 0; i < solid.length; ++i) {
			solid[i] = (r.nextBoolean()) ? SOLID: FLUID;
		}
	}

//	public void displayAsciiXYSlice(int i) {
//		
//		int xSize = xSize;
//		int ySize = ySize;
//
//		System.out.println("Slice "+i);
//		for(int x = 0; x < xSize; ++x) {
//			for(int y = 0; y < ySize; ++y) {
//				if(at(x, y, i) == Solid.SURFACESOLID) {
//					System.out.print('s');
//				} else if(at(x, y, i) == Solid.SOLID) {
//					System.out.print('i');
//				} else {
//					System.out.print(' ');
//				}
//			}
//			System.out.println("");
//		}
//		System.out.println("");
//	}

//	public void detectSurfaceSolids(LatticeDescriptor ld) {
//		
//		int xSize = xSize;
//		int ySize = ySize;
//		int zSize = zSize;
//		
//		for(int x = 0; x < xSize; ++x) {
//			for(int y = 0; y < ySize; ++y) {
//				for(int z = 0; z < zSize; ++z) {
//					if(at(x, y, z) == Solid.SURFACESOLID) {
//						
//						int k;
//						for(k = 0; k < ld.getVelocitiesCount(); ++k) {
//							if(at((x+xSize+ld.getVector(k)[0])%xSize,
//									(y+ySize+ld.getVector(k)[1])%ySize,
//									(z+zSize+ld.getVector(k)[2])%zSize) == Solid.FLUID) {
//								break;
//							}
//						}
//						if(k == ld.getVelocitiesCount())
//							set(x, y, z, Solid.SOLID);
//						
//					}
//				}
//			}
//		}
//	}
}
