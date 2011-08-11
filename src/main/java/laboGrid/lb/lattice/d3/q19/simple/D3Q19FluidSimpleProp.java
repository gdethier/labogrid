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
package laboGrid.lb.lattice.d3.q19.simple;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.BlockIterator;
import laboGrid.lb.lattice.BorderData;
import laboGrid.lb.lattice.d3.q19.D3Q19BorderPlaneConstants;
import laboGrid.lb.lattice.d3.q19.D3Q19Fluid;
import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;



/**
 * In this implementation of the D3Q19 lattice, a standard storage scheme
 * is used : f[x][y][z][q]. This storage scheme is optimized for the collision
 * step but only simple propagation method can be used.
 * 
 * @author dethier
 */
public class D3Q19FluidSimpleProp extends D3Q19Fluid {

	private static final long serialVersionUID = 1L;
	
	protected double[][][][] f; // x y z q
	protected int yzqSize, zqSize, xyzSize;
	
	private BorderData[][] outgoingData;
	
	
	public D3Q19FluidSimpleProp() {
		super();
	}
	
	public D3Q19FluidSimpleProp(int xSize, int ySize, int zSize) {
		super(xSize, ySize, zSize);
	}
	
	private void allocateOutputBuffers() {
		outgoingData = new BorderData[2][19];
		
		for(int bufPos = 0; bufPos < 2; ++bufPos) {

			outgoingData[bufPos][D3Q19LatticeDescriptor.SOUTH] =
				new D3Q19SimpleBorderPlane(D3Q19LatticeDescriptor.SOUTH, new double[xSize][zSize][5]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.NORTH] =
				new D3Q19SimpleBorderPlane(D3Q19LatticeDescriptor.NORTH, new double[xSize][zSize][5]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.WEST] =
				new D3Q19SimpleBorderPlane(D3Q19LatticeDescriptor.WEST, new double[ySize][zSize][5]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.EAST] =
				new D3Q19SimpleBorderPlane(D3Q19LatticeDescriptor.EAST, new double[ySize][zSize][5]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.DOWN] =
				new D3Q19SimpleBorderPlane(D3Q19LatticeDescriptor.DOWN, new double[xSize][ySize][5]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.UP] =
				new D3Q19SimpleBorderPlane(D3Q19LatticeDescriptor.UP, new double[xSize][ySize][5]);


			outgoingData[bufPos][D3Q19LatticeDescriptor.DOWNNORTH] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.DOWNNORTH, new double[xSize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.DOWNSOUTH] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.DOWNSOUTH, new double[xSize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.DOWNEAST] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.DOWNEAST, new double[ySize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.DOWNWEST] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.DOWNWEST, new double[ySize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.UPSOUTH] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.UPSOUTH, new double[xSize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.UPNORTH] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.UPNORTH, new double[xSize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.UPWEST] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.UPWEST, new double[ySize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.UPEAST] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.UPEAST, new double[ySize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.SOUTHWEST] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.SOUTHWEST, new double[zSize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.SOUTHEAST] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.SOUTHEAST, new double[zSize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.NORTHWEST] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.NORTHWEST, new double[zSize]);
			outgoingData[bufPos][D3Q19LatticeDescriptor.NORTHEAST] =
				new D3Q19SimpleBorderLine(D3Q19LatticeDescriptor.NORTHEAST, new double[zSize]);

		}
	}
	
	@Override
	public void setSize(int[] size) {

		super.setSize(size);
		
		zqSize = 19 * zSize;
		yzqSize = ySize * zqSize;
		xyzSize = xSize * ySize * zSize;
		
		f = new double[xSize][ySize][zSize][19];
		
		allocateOutputBuffers();

	}


	@Override
	public void setDensity(int q, int x, int y, int z, double density) {
		f[x][y][z][q] = density;
	}

	public double getDensity(int q, int x, int y, int z) {
		return f[x][y][z][q];
	}
	
	
	public void inPlaceStream() {
		inPlaceStream(0);
	}

	public void inPlaceStream(int bufPos) {
		for(int x = xSize-2; x >= 0; --x) { // x-
			for(int y = 0; y < ySize; ++y) { // y0
				for(int z = 0; z < zSize; ++z) // z0
					f[x + 1][y][z][D3Q19LatticeDescriptor.EAST] =
							f[x][y][z][D3Q19LatticeDescriptor.EAST];
			}
		}

		for(int x = xSize-2; x >= 0; --x) { // x-	
			for(int y = ySize-2; y >= 0; --y) { // y-
				for(int z = 0; z < zSize; ++z) { // z0
					f[x + 1][y + 1][z][D3Q19LatticeDescriptor.NORTHEAST] =
						f[x][y][z][D3Q19LatticeDescriptor.NORTHEAST];
				}
			}
		}
		
		for(int x = 0; x < xSize; ++x) { // x0
			for(int y = ySize-2; y >= 0; --y) { // y-
				for(int z = 0; z < zSize; ++z) { // z0
					f[x][y + 1][z][D3Q19LatticeDescriptor.NORTH] =
						f[x][y][z][D3Q19LatticeDescriptor.NORTH];
				}
			}
		}
		
		for(int x = 1; x < xSize; ++x) { // x+
			for(int y = ySize-2; y >= 0; --y) { // y-
				for(int z = 0; z < zSize; ++z) { // z0
					f[x - 1][y + 1][z][D3Q19LatticeDescriptor.NORTHWEST] =
						f[x][y][z][D3Q19LatticeDescriptor.NORTHWEST];
				}
			}
		}
		
		for(int x = 1; x < xSize; ++x) { // x+
			for(int y = 0; y < ySize; ++y) { // y0
				for(int z = 0; z < zSize; ++z) { // z0
					f[x - 1][y][z][D3Q19LatticeDescriptor.WEST] =
						f[x][y][z][D3Q19LatticeDescriptor.WEST];
				}
			}
		}
		
		for(int x = 1; x < xSize; ++x) { // x+
			for(int y = 1; y < ySize; ++y) { // y+
				for(int z = 0; z < zSize; ++z) { // z0
					f[x - 1][y - 1][z][D3Q19LatticeDescriptor.SOUTHWEST] =
						f[x][y][z][D3Q19LatticeDescriptor.SOUTHWEST];
				}
			}
		}
		
		for(int x = 0; x < xSize; ++x) { // x0
			for(int y = 1; y < ySize; ++y) { // y+
				for(int z = 0; z < zSize; ++z) { // z0
					f[x][y - 1][z][D3Q19LatticeDescriptor.SOUTH] =
						f[x][y][z][D3Q19LatticeDescriptor.SOUTH];
				}
			}
		}
		
		for(int x = xSize-2; x >= 0; --x) { // x-
			for(int y = 1; y < ySize; ++y) { // y+
				for(int z = 0; z < zSize; ++z) { // z0
					f[x + 1][y - 1][z][D3Q19LatticeDescriptor.SOUTHEAST] =
						f[x][y][z][D3Q19LatticeDescriptor.SOUTHEAST];
				}
			}
		}
		
		for(int x = 0; x < xSize; ++x) { // x0
			for(int y = 0; y < ySize; ++y) { // y0
				for(int z = zSize - 2; z >= 0; --z) { // z-
					f[x][y][z + 1][D3Q19LatticeDescriptor.UP] =
						f[x][y][z][D3Q19LatticeDescriptor.UP];
				}
			}
		}
			
		for(int x = xSize-2; x >= 0; --x) { // x-
			for(int y = 0; y < ySize; ++y) { // y0
				for(int z = zSize - 2; z >= 0; --z) { // z-
					f[x + 1][y][z + 1][D3Q19LatticeDescriptor.UPEAST] =
						f[x][y][z][D3Q19LatticeDescriptor.UPEAST];
				}
			}
		}
			
		for(int x = 0; x < xSize; ++x) { // x0
			for(int y = ySize-2; y >= 0; --y) { // y-
				for(int z = zSize - 2; z >= 0; --z) { // z-
					f[x][y + 1][z + 1][D3Q19LatticeDescriptor.UPNORTH] =
						f[x][y][z][D3Q19LatticeDescriptor.UPNORTH];
				}
			}
		}

		for(int x = 1; x < xSize; ++x) { // x+
			for(int y = 0; y < ySize; ++y) { // y0
				for(int z = zSize - 2; z >= 0; --z) { // z-
					f[x - 1][y][z + 1][D3Q19LatticeDescriptor.UPWEST] =
						f[x][y][z][D3Q19LatticeDescriptor.UPWEST];
				}
			}
		}

		for(int x = 0; x < xSize; ++x) { // x0
			for(int y = 1; y < ySize; ++y) { // y+
				for(int z = zSize - 2; z >= 0; --z) { // z-
					f[x][y - 1][z + 1][D3Q19LatticeDescriptor.UPSOUTH] =
						f[x][y][z][D3Q19LatticeDescriptor.UPSOUTH];
				}
			}
		}

		for(int x = xSize-2; x >= 0; --x) { // x-
			for(int y = 0; y < ySize; ++y) { // y0
				for(int z = 1; z < zSize; ++z) { // z+
					f[x + 1][y][z - 1][D3Q19LatticeDescriptor.DOWNEAST] =
						f[x][y][z][D3Q19LatticeDescriptor.DOWNEAST];
				}
			}
		}
			
		for(int x = 0; x < xSize; ++x) { // x0
			for(int y = ySize-2; y >= 0; --y) { // y-
				for(int z = 1; z < zSize; ++z) { // z+
					f[x][y + 1][z - 1][D3Q19LatticeDescriptor.DOWNNORTH] =
						f[x][y][z][D3Q19LatticeDescriptor.DOWNNORTH];
				}
			}
		}
		
		for(int x = 1; x < xSize; ++x) { // x+
			for(int y = 0; y < ySize; ++y) { // y0
				for(int z = 1; z < zSize; ++z) { // z+
					f[x - 1][y][z - 1][D3Q19LatticeDescriptor.DOWNWEST] =
						f[x][y][z][D3Q19LatticeDescriptor.DOWNWEST];
				}
			}
		}
				
		for(int x = 0; x < xSize; ++x) { // x0
			for(int y = 1; y < ySize; ++y) { // y+
				for(int z = 1; z < zSize; ++z) { // z+
					f[x][y - 1][z - 1][D3Q19LatticeDescriptor.DOWNSOUTH] =
						f[x][y][z][D3Q19LatticeDescriptor.DOWNSOUTH];
				}
			}
		}
				
		for(int x = 0; x < xSize; ++x) { // x0
			for(int y = 0; y < ySize; ++y) { // y0
				for(int z = 1; z < zSize; ++z) { // z+
					f[x][y][z - 1][D3Q19LatticeDescriptor.DOWN] =
						f[x][y][z][D3Q19LatticeDescriptor.DOWN];
				}
			}
		}
	}

	@Override
	public BlockIterator getBlockIterator(int blockSize) {
		return new UnconstrainedBlockIterator(f, xSize, ySize, zSize, blockSize);
	}

	@Override
	public BlockIterator getBlockIterator(int blockSize,
			int xFrom, int xTo, int yFrom, int yTo, int zFrom, int zTo) {
		return new ConstrainedBlockIterator(f, blockSize,
				xFrom, xTo, yFrom, yTo, zFrom, zTo,
				yzqSize, zqSize);
	}


	@Override
	public void setIncomingDensities(BorderData bd) throws LBException {

		int xe, xw;
		int yn, ys;
		int zu, zd;
		

		if(bd.getLink() == D3Q19LatticeDescriptor.SOUTH) {
			
			D3Q19SimpleBorderPlane in = (D3Q19SimpleBorderPlane) bd;
			double[][][] plane = in.getData();

			// inside plane
			xe = 2;
			xw = 0;
			for(int x = 1; x < xSize-1; ++x, ++xe, ++xw) {
				zu = 2; 
				zd = 0;
				for(int z = 1; z < zSize-1; ++z, ++zu, ++zd) {
					f[xw][ySize-1][z][D3Q19LatticeDescriptor.SOUTHWEST] = plane[x][z][D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST];
					f[x][ySize-1][z][D3Q19LatticeDescriptor.SOUTH] = plane[x][z][D3Q19BorderPlaneConstants.SOUTH_SOUTH];
					f[xe][ySize-1][z][D3Q19LatticeDescriptor.SOUTHEAST] = plane[x][z][D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST];
					f[x][ySize-1][zu][D3Q19LatticeDescriptor.UPSOUTH] = plane[x][z][D3Q19BorderPlaneConstants.SOUTH_UPSOUTH];
					f[x][ySize-1][zd][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[x][z][D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH];	
				}	
			}

			// corners
			f[0][ySize-1][zSize-1][D3Q19LatticeDescriptor.SOUTH] = plane[0][zSize-1][D3Q19BorderPlaneConstants.SOUTH_SOUTH];
			f[1][ySize-1][zSize-1][D3Q19LatticeDescriptor.SOUTHEAST] = plane[0][zSize-1][D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST];
			f[0][ySize-1][zSize-2][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[0][zSize-1][D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH];

			f[0][ySize-1][0][D3Q19LatticeDescriptor.SOUTH] = plane[0][0][D3Q19BorderPlaneConstants.SOUTH_SOUTH];
			f[1][ySize-1][0][D3Q19LatticeDescriptor.SOUTHEAST] = plane[0][0][D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST];
			f[0][ySize-1][1][D3Q19LatticeDescriptor.UPSOUTH] = plane[0][0][D3Q19BorderPlaneConstants.SOUTH_UPSOUTH];

			f[xSize-2][ySize-1][0][D3Q19LatticeDescriptor.SOUTHWEST] = plane[xSize-1][0][D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST];
			f[xSize-1][ySize-1][0][D3Q19LatticeDescriptor.SOUTH] = plane[xSize-1][0][D3Q19BorderPlaneConstants.SOUTH_SOUTH];
			f[xSize-1][ySize-1][1][D3Q19LatticeDescriptor.UPSOUTH] = plane[xSize-1][0][D3Q19BorderPlaneConstants.SOUTH_UPSOUTH];

			f[xSize-2][ySize-1][zSize-1][D3Q19LatticeDescriptor.SOUTHWEST] = plane[xSize-1][zSize-1][D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST];
			f[xSize-1][ySize-1][zSize-1][D3Q19LatticeDescriptor.SOUTH] = plane[xSize-1][zSize-1][D3Q19BorderPlaneConstants.SOUTH_SOUTH];
			f[xSize-1][ySize-1][zSize-2][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[xSize-1][zSize-1][D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH];

			// borders
			for(int z = 1; z < zSize-1; ++z) {
				f[0][ySize-1][z][D3Q19LatticeDescriptor.SOUTH] = plane[0][z][D3Q19BorderPlaneConstants.SOUTH_SOUTH];
				f[1][ySize-1][z][D3Q19LatticeDescriptor.SOUTHEAST] = plane[0][z][D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST];
				f[0][ySize-1][z+1][D3Q19LatticeDescriptor.UPSOUTH] = plane[0][z][D3Q19BorderPlaneConstants.SOUTH_UPSOUTH];
				f[0][ySize-1][z-1][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[0][z][D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH];

				f[xSize-2][ySize-1][z][D3Q19LatticeDescriptor.SOUTHWEST] = plane[xSize-1][z][D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST];
				f[xSize-1][ySize-1][z][D3Q19LatticeDescriptor.SOUTH] = plane[xSize-1][z][D3Q19BorderPlaneConstants.SOUTH_SOUTH];
				f[xSize-1][ySize-1][z+1][D3Q19LatticeDescriptor.UPSOUTH] = plane[xSize-1][z][D3Q19BorderPlaneConstants.SOUTH_UPSOUTH];
				f[xSize-1][ySize-1][z-1][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[xSize-1][z][D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH];
			}

			for(int x = 1; x < xSize-1; ++x) {
				f[x-1][ySize-1][zSize-1][D3Q19LatticeDescriptor.SOUTHWEST] = plane[x][zSize-1][D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST];
				f[x][ySize-1][zSize-1][D3Q19LatticeDescriptor.SOUTH] = plane[x][zSize-1][D3Q19BorderPlaneConstants.SOUTH_SOUTH];
				f[x+1][ySize-1][zSize-1][D3Q19LatticeDescriptor.SOUTHEAST] = plane[x][zSize-1][D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST];
				f[x][ySize-1][zSize-2][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[x][zSize-1][D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH];

				f[x-1][ySize-1][0][D3Q19LatticeDescriptor.SOUTHWEST] = plane[x][0][D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST];
				f[x][ySize-1][0][D3Q19LatticeDescriptor.SOUTH] = plane[x][0][D3Q19BorderPlaneConstants.SOUTH_SOUTH];
				f[x+1][ySize-1][0][D3Q19LatticeDescriptor.SOUTHEAST] = plane[x][0][D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST];
				f[x][ySize-1][1][D3Q19LatticeDescriptor.UPSOUTH] = plane[x][0][D3Q19BorderPlaneConstants.SOUTH_UPSOUTH];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.NORTH) {

			D3Q19SimpleBorderPlane in = (D3Q19SimpleBorderPlane) bd;
			double[][][] plane = in.getData();

			xe = 2;
			xw = 0;
			for(int x = 1; x < xSize-1; ++x, ++xe, ++xw) {
				zu = 2; 
				zd = 0;
				for(int z = 1; z < zSize-1; ++z, ++zu, ++zd) {
					f[xe][0][z][D3Q19LatticeDescriptor.NORTHEAST] = plane[x][z][D3Q19BorderPlaneConstants.NORTH_NORTHEAST];
					f[x][0][z][D3Q19LatticeDescriptor.NORTH] = plane[x][z][D3Q19BorderPlaneConstants.NORTH_NORTH];
					f[xw][0][z][D3Q19LatticeDescriptor.NORTHWEST] = plane[x][z][D3Q19BorderPlaneConstants.NORTH_NORTHWEST];
					f[x][0][zu][D3Q19LatticeDescriptor.UPNORTH] = plane[x][z][D3Q19BorderPlaneConstants.NORTH_UPNORTH];
					f[x][0][zd][D3Q19LatticeDescriptor.DOWNNORTH] = plane[x][z][D3Q19BorderPlaneConstants.NORTH_DOWNNORTH];
				}	
			}

			// corners
			f[1][0][zSize-1][D3Q19LatticeDescriptor.NORTHEAST] = plane[0][zSize-1][D3Q19BorderPlaneConstants.NORTH_NORTHEAST];
			f[0][0][zSize-1][D3Q19LatticeDescriptor.NORTH] = plane[0][zSize-1][D3Q19BorderPlaneConstants.NORTH_NORTH];
			f[0][0][zSize-2][D3Q19LatticeDescriptor.DOWNNORTH] = plane[0][zSize-1][D3Q19BorderPlaneConstants.NORTH_DOWNNORTH];

			f[1][0][0][D3Q19LatticeDescriptor.NORTHEAST] = plane[0][0][D3Q19BorderPlaneConstants.NORTH_NORTHEAST];
			f[0][0][0][D3Q19LatticeDescriptor.NORTH] = plane[0][0][D3Q19BorderPlaneConstants.NORTH_NORTH];
			f[0][0][1][D3Q19LatticeDescriptor.UPNORTH] = plane[0][0][D3Q19BorderPlaneConstants.NORTH_UPNORTH];

			f[xSize-1][0][0][D3Q19LatticeDescriptor.NORTH] = plane[xSize-1][0][D3Q19BorderPlaneConstants.NORTH_NORTH];
			f[xSize-2][0][0][D3Q19LatticeDescriptor.NORTHWEST] = plane[xSize-1][0][D3Q19BorderPlaneConstants.NORTH_NORTHWEST];
			f[xSize-1][0][1][D3Q19LatticeDescriptor.UPNORTH] = plane[xSize-1][0][D3Q19BorderPlaneConstants.NORTH_UPNORTH];

			f[xSize-1][0][zSize-1][D3Q19LatticeDescriptor.NORTH] = plane[xSize-1][zSize-1][D3Q19BorderPlaneConstants.NORTH_NORTH];
			f[xSize-2][0][zSize-1][D3Q19LatticeDescriptor.NORTHWEST] = plane[xSize-1][zSize-1][D3Q19BorderPlaneConstants.NORTH_NORTHWEST];
			f[xSize-1][0][zSize-2][D3Q19LatticeDescriptor.DOWNNORTH] = plane[xSize-1][zSize-1][D3Q19BorderPlaneConstants.NORTH_DOWNNORTH];

			// borders
			for(int z = 1; z < zSize-1; ++z) {
				f[1][0][z][D3Q19LatticeDescriptor.NORTHEAST] = plane[0][z][D3Q19BorderPlaneConstants.NORTH_NORTHEAST];
				f[0][0][z][D3Q19LatticeDescriptor.NORTH] = plane[0][z][D3Q19BorderPlaneConstants.NORTH_NORTH];
				f[0][0][z+1][D3Q19LatticeDescriptor.UPNORTH] = plane[0][z][D3Q19BorderPlaneConstants.NORTH_UPNORTH];
				f[0][0][z-1][D3Q19LatticeDescriptor.DOWNNORTH] = plane[0][z][D3Q19BorderPlaneConstants.NORTH_DOWNNORTH];

				f[xSize-1][0][z][D3Q19LatticeDescriptor.NORTH] = plane[xSize-1][z][D3Q19BorderPlaneConstants.NORTH_NORTH];
				f[xSize-2][0][z][D3Q19LatticeDescriptor.NORTHWEST] = plane[xSize-1][z][D3Q19BorderPlaneConstants.NORTH_NORTHWEST];
				f[xSize-1][0][z+1][D3Q19LatticeDescriptor.UPNORTH] = plane[xSize-1][z][D3Q19BorderPlaneConstants.NORTH_UPNORTH];
				f[xSize-1][0][z-1][D3Q19LatticeDescriptor.DOWNNORTH] = plane[xSize-1][z][D3Q19BorderPlaneConstants.NORTH_DOWNNORTH];
			}

			for(int x = 1; x < xSize-1; ++x) {
				f[x+1][0][zSize-1][D3Q19LatticeDescriptor.NORTHEAST] = plane[x][zSize-1][D3Q19BorderPlaneConstants.NORTH_NORTHEAST];
				f[x][0][zSize-1][D3Q19LatticeDescriptor.NORTH] = plane[x][zSize-1][D3Q19BorderPlaneConstants.NORTH_NORTH];
				f[x-1][0][zSize-1][D3Q19LatticeDescriptor.NORTHWEST] = plane[x][zSize-1][D3Q19BorderPlaneConstants.NORTH_NORTHWEST];
				f[x][0][zSize-2][D3Q19LatticeDescriptor.DOWNNORTH] = plane[x][zSize-1][D3Q19BorderPlaneConstants.NORTH_DOWNNORTH];

				f[x+1][0][0][D3Q19LatticeDescriptor.NORTHEAST] = plane[x][0][D3Q19BorderPlaneConstants.NORTH_NORTHEAST];
				f[x][0][0][D3Q19LatticeDescriptor.NORTH] = plane[x][0][D3Q19BorderPlaneConstants.NORTH_NORTH];
				f[x-1][0][0][D3Q19LatticeDescriptor.NORTHWEST] = plane[x][0][D3Q19BorderPlaneConstants.NORTH_NORTHWEST];
				f[x][0][1][D3Q19LatticeDescriptor.UPNORTH] = plane[x][0][D3Q19BorderPlaneConstants.NORTH_UPNORTH];

			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.WEST) {

			D3Q19SimpleBorderPlane in = (D3Q19SimpleBorderPlane) bd;
			double[][][] plane = in.getData();

			yn = 2;
			ys = 0;
			for(int y = 1; y < ySize-1; ++y, ++yn, ++ys) {
				zu = 2;
				zd = 0;
				for(int z = 1; z < zSize-1; ++z, ++zu, ++zd) {
					f[xSize-1][yn][z][D3Q19LatticeDescriptor.NORTHWEST] = plane[y][z][D3Q19BorderPlaneConstants.WEST_NORTHWEST];
					f[xSize-1][y][z][D3Q19LatticeDescriptor.WEST] = plane[y][z][D3Q19BorderPlaneConstants.WEST_WEST];
					f[xSize-1][ys][z][D3Q19LatticeDescriptor.SOUTHWEST] = plane[y][z][D3Q19BorderPlaneConstants.WEST_SOUTHWEST];
					f[xSize-1][y][zu][D3Q19LatticeDescriptor.UPWEST] = plane[y][z][D3Q19BorderPlaneConstants.WEST_UPWEST];
					f[xSize-1][y][zd][D3Q19LatticeDescriptor.DOWNWEST] = plane[y][z][D3Q19BorderPlaneConstants.WEST_DOWNWEST];
				}	
			}

			// corners
			f[xSize-1][ySize-1][0][D3Q19LatticeDescriptor.WEST] = plane[ySize-1][0][D3Q19BorderPlaneConstants.WEST_WEST];
			f[xSize-1][ySize-2][0][D3Q19LatticeDescriptor.SOUTHWEST] = plane[ySize-1][0][D3Q19BorderPlaneConstants.WEST_SOUTHWEST];
			f[xSize-1][ySize-1][1][D3Q19LatticeDescriptor.UPWEST] = plane[ySize-1][0][D3Q19BorderPlaneConstants.WEST_UPWEST];

			f[xSize-1][1][0][D3Q19LatticeDescriptor.NORTHWEST] = plane[0][0][D3Q19BorderPlaneConstants.WEST_NORTHWEST];
			f[xSize-1][0][0][D3Q19LatticeDescriptor.WEST] = plane[0][0][D3Q19BorderPlaneConstants.WEST_WEST];
			f[xSize-1][0][1][D3Q19LatticeDescriptor.UPWEST] = plane[0][0][D3Q19BorderPlaneConstants.WEST_UPWEST];

			f[xSize-1][1][zSize-1][D3Q19LatticeDescriptor.NORTHWEST] = plane[0][zSize-1][D3Q19BorderPlaneConstants.WEST_NORTHWEST];
			f[xSize-1][0][zSize-1][D3Q19LatticeDescriptor.WEST] = plane[0][zSize-1][D3Q19BorderPlaneConstants.WEST_WEST];
			f[xSize-1][0][zSize-2][D3Q19LatticeDescriptor.DOWNWEST] = plane[0][zSize-1][D3Q19BorderPlaneConstants.WEST_DOWNWEST];

			f[xSize-1][ySize-1][zSize-1][D3Q19LatticeDescriptor.WEST] = plane[ySize-1][zSize-1][D3Q19BorderPlaneConstants.WEST_WEST];
			f[xSize-1][ySize-2][zSize-1][D3Q19LatticeDescriptor.SOUTHWEST] = plane[ySize-1][zSize-1][D3Q19BorderPlaneConstants.WEST_SOUTHWEST];
			f[xSize-1][ySize-1][zSize-2][D3Q19LatticeDescriptor.DOWNWEST] = plane[ySize-1][zSize-1][D3Q19BorderPlaneConstants.WEST_DOWNWEST];


			// borders
			for(int y = 1; y < ySize-1; ++y) {
				f[xSize-1][y+1][0][D3Q19LatticeDescriptor.NORTHWEST] = plane[y][0][D3Q19BorderPlaneConstants.WEST_NORTHWEST];
				f[xSize-1][y][0][D3Q19LatticeDescriptor.WEST] = plane[y][0][D3Q19BorderPlaneConstants.WEST_WEST];
				f[xSize-1][y-1][0][D3Q19LatticeDescriptor.SOUTHWEST] = plane[y][0][D3Q19BorderPlaneConstants.WEST_SOUTHWEST];
				f[xSize-1][y][1][D3Q19LatticeDescriptor.UPWEST] = plane[y][0][D3Q19BorderPlaneConstants.WEST_UPWEST];

				f[xSize-1][y+1][zSize-1][D3Q19LatticeDescriptor.NORTHWEST] = plane[y][zSize-1][D3Q19BorderPlaneConstants.WEST_NORTHWEST];
				f[xSize-1][y][zSize-1][D3Q19LatticeDescriptor.WEST] = plane[y][zSize-1][D3Q19BorderPlaneConstants.WEST_WEST];
				f[xSize-1][y-1][zSize-1][D3Q19LatticeDescriptor.SOUTHWEST] = plane[y][zSize-1][D3Q19BorderPlaneConstants.WEST_SOUTHWEST];
				f[xSize-1][y][zSize-2][D3Q19LatticeDescriptor.DOWNWEST] = plane[y][zSize-1][D3Q19BorderPlaneConstants.WEST_DOWNWEST];
			}

			for(int z = 1; z < zSize-1; ++z) {
				f[xSize-1][ySize-1][z][D3Q19LatticeDescriptor.WEST] = plane[ySize-1][z][D3Q19BorderPlaneConstants.WEST_WEST];
				f[xSize-1][ySize-2][z][D3Q19LatticeDescriptor.SOUTHWEST] = plane[ySize-1][z][D3Q19BorderPlaneConstants.WEST_SOUTHWEST];
				f[xSize-1][ySize-1][z+1][D3Q19LatticeDescriptor.UPWEST] = plane[ySize-1][z][D3Q19BorderPlaneConstants.WEST_UPWEST];
				f[xSize-1][ySize-1][z-1][D3Q19LatticeDescriptor.DOWNWEST] = plane[ySize-1][z][D3Q19BorderPlaneConstants.WEST_DOWNWEST];

				f[xSize-1][1][z][D3Q19LatticeDescriptor.NORTHWEST] = plane[0][z][D3Q19BorderPlaneConstants.WEST_NORTHWEST];
				f[xSize-1][0][z][D3Q19LatticeDescriptor.WEST] = plane[0][z][D3Q19BorderPlaneConstants.WEST_WEST];
				f[xSize-1][0][z+1][D3Q19LatticeDescriptor.UPWEST] = plane[0][z][D3Q19BorderPlaneConstants.WEST_UPWEST];
				f[xSize-1][0][z-1][D3Q19LatticeDescriptor.DOWNWEST] = plane[0][z][D3Q19BorderPlaneConstants.WEST_DOWNWEST];

			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.EAST) {

			D3Q19SimpleBorderPlane in = (D3Q19SimpleBorderPlane) bd;
			double[][][] plane = in.getData();

			yn = 2;
			ys = 0;
			for(int y = 1; y < ySize-1; ++y, ++yn, ++ys) {
				zu = 2;
				zd = 0;
				for(int z = 1; z < zSize-1; ++z, ++zu, ++zd) {
					f[0][y][z][D3Q19LatticeDescriptor.EAST] = plane[y][z][D3Q19BorderPlaneConstants.EAST_EAST];
					f[0][yn][z][D3Q19LatticeDescriptor.NORTHEAST] = plane[y][z][D3Q19BorderPlaneConstants.EAST_NORTHEAST];
					f[0][ys][z][D3Q19LatticeDescriptor.SOUTHEAST] = plane[y][z][D3Q19BorderPlaneConstants.EAST_SOUTHEAST];
					f[0][y][zu][D3Q19LatticeDescriptor.UPEAST] = plane[y][z][D3Q19BorderPlaneConstants.EAST_UPEAST];
					f[0][y][zd][D3Q19LatticeDescriptor.DOWNEAST] = plane[y][z][D3Q19BorderPlaneConstants.EAST_DOWNEAST];
				}	
			}

			// corners
			f[0][ySize-1][0][D3Q19LatticeDescriptor.EAST] = plane[ySize-1][0][D3Q19BorderPlaneConstants.EAST_EAST];
			f[0][ySize-2][0][D3Q19LatticeDescriptor.SOUTHEAST] = plane[ySize-1][0][D3Q19BorderPlaneConstants.EAST_SOUTHEAST];
			f[0][ySize-1][1][D3Q19LatticeDescriptor.UPEAST] = plane[ySize-1][0][D3Q19BorderPlaneConstants.EAST_UPEAST];

			f[0][0][0][D3Q19LatticeDescriptor.EAST] = plane[0][0][D3Q19BorderPlaneConstants.EAST_EAST];
			f[0][1][0][D3Q19LatticeDescriptor.NORTHEAST] = plane[0][0][D3Q19BorderPlaneConstants.EAST_NORTHEAST];
			f[0][0][1][D3Q19LatticeDescriptor.UPEAST] = plane[0][0][D3Q19BorderPlaneConstants.EAST_UPEAST];

			f[0][0][zSize-1][D3Q19LatticeDescriptor.EAST] = plane[0][zSize-1][D3Q19BorderPlaneConstants.EAST_EAST];
			f[0][1][zSize-1][D3Q19LatticeDescriptor.NORTHEAST] = plane[0][zSize-1][D3Q19BorderPlaneConstants.EAST_NORTHEAST];
			f[0][0][zSize-2][D3Q19LatticeDescriptor.DOWNEAST] = plane[0][zSize-1][D3Q19BorderPlaneConstants.EAST_DOWNEAST];

			f[0][ySize-1][zSize-1][D3Q19LatticeDescriptor.EAST] = plane[ySize-1][zSize-1][D3Q19BorderPlaneConstants.EAST_EAST];
			f[0][ySize-2][zSize-1][D3Q19LatticeDescriptor.SOUTHEAST] = plane[ySize-1][zSize-1][D3Q19BorderPlaneConstants.EAST_SOUTHEAST];
			f[0][ySize-1][zSize-2][D3Q19LatticeDescriptor.DOWNEAST] = plane[ySize-1][zSize-1][D3Q19BorderPlaneConstants.EAST_DOWNEAST];

			// borders
			for(int y = 1; y < ySize-1; ++y) {
				f[0][y][0][D3Q19LatticeDescriptor.EAST] = plane[y][0][D3Q19BorderPlaneConstants.EAST_EAST];
				f[0][y+1][0][D3Q19LatticeDescriptor.NORTHEAST] = plane[y][0][D3Q19BorderPlaneConstants.EAST_NORTHEAST];
				f[0][y-1][0][D3Q19LatticeDescriptor.SOUTHEAST] = plane[y][0][D3Q19BorderPlaneConstants.EAST_SOUTHEAST];
				f[0][y][1][D3Q19LatticeDescriptor.UPEAST] = plane[y][0][D3Q19BorderPlaneConstants.EAST_UPEAST];

				f[0][y][zSize-1][D3Q19LatticeDescriptor.EAST] = plane[y][zSize-1][D3Q19BorderPlaneConstants.EAST_EAST];
				f[0][y+1][zSize-1][D3Q19LatticeDescriptor.NORTHEAST] = plane[y][zSize-1][D3Q19BorderPlaneConstants.EAST_NORTHEAST];
				f[0][y-1][zSize-1][D3Q19LatticeDescriptor.SOUTHEAST] = plane[y][zSize-1][D3Q19BorderPlaneConstants.EAST_SOUTHEAST];
				f[0][y][zSize-2][D3Q19LatticeDescriptor.DOWNEAST] = plane[y][zSize-1][D3Q19BorderPlaneConstants.EAST_DOWNEAST];
			}

			for(int z = 1; z < zSize-1; ++z) {
				f[0][ySize-1][z][D3Q19LatticeDescriptor.EAST] = plane[ySize-1][z][D3Q19BorderPlaneConstants.EAST_EAST];
				f[0][ySize-2][z][D3Q19LatticeDescriptor.SOUTHEAST] = plane[ySize-1][z][D3Q19BorderPlaneConstants.EAST_SOUTHEAST];
				f[0][ySize-1][z+1][D3Q19LatticeDescriptor.UPEAST] = plane[ySize-1][z][D3Q19BorderPlaneConstants.EAST_UPEAST];
				f[0][ySize-1][z-1][D3Q19LatticeDescriptor.DOWNEAST] = plane[ySize-1][z][D3Q19BorderPlaneConstants.EAST_DOWNEAST];

				f[0][0][z][D3Q19LatticeDescriptor.EAST] = plane[0][z][D3Q19BorderPlaneConstants.EAST_EAST];
				f[0][1][z][D3Q19LatticeDescriptor.NORTHEAST] = plane[0][z][D3Q19BorderPlaneConstants.EAST_NORTHEAST];
				f[0][0][z+1][D3Q19LatticeDescriptor.UPEAST] = plane[0][z][D3Q19BorderPlaneConstants.EAST_UPEAST];
				f[0][0][z-1][D3Q19LatticeDescriptor.DOWNEAST] = plane[0][z][D3Q19BorderPlaneConstants.EAST_DOWNEAST];
			}

		} else if(bd.getLink() == D3Q19LatticeDescriptor.DOWN) {

			D3Q19SimpleBorderPlane in = (D3Q19SimpleBorderPlane) bd;
			double[][][] plane = in.getData();

			xe = 2;
			xw = 0;
			for(int x = 1; x < xSize-1; ++x, ++xe, ++xw) {
				yn = 2;
				ys = 0;
				for(int y = 1; y < ySize-1; ++y, ++ys, ++yn) {
					f[xe][y][zSize-1][D3Q19LatticeDescriptor.DOWNEAST] = plane[x][y][D3Q19BorderPlaneConstants.DOWN_DOWNEAST];
					f[x][yn][zSize-1][D3Q19LatticeDescriptor.DOWNNORTH] = plane[x][y][D3Q19BorderPlaneConstants.DOWN_DOWNNORTH];
					f[xw][y][zSize-1][D3Q19LatticeDescriptor.DOWNWEST] = plane[x][y][D3Q19BorderPlaneConstants.DOWN_DOWNWEST];
					f[x][ys][zSize-1][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[x][y][D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH];
					f[x][y][zSize-1][D3Q19LatticeDescriptor.DOWN] = plane[x][y][D3Q19BorderPlaneConstants.DOWN_DOWN];
				}	
			}

			// corners
			f[1][ySize-1][zSize-1][D3Q19LatticeDescriptor.DOWNEAST] = plane[0][ySize-1][D3Q19BorderPlaneConstants.DOWN_DOWNEAST];
			f[0][ySize-2][zSize-1][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[0][ySize-1][D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH];
			f[0][ySize-1][zSize-1][D3Q19LatticeDescriptor.DOWN] = plane[0][ySize-1][D3Q19BorderPlaneConstants.DOWN_DOWN];

			f[1][0][zSize-1][D3Q19LatticeDescriptor.DOWNEAST] = plane[0][0][D3Q19BorderPlaneConstants.DOWN_DOWNEAST];
			f[0][1][zSize-1][D3Q19LatticeDescriptor.DOWNNORTH] = plane[0][0][D3Q19BorderPlaneConstants.DOWN_DOWNNORTH];
			f[0][0][zSize-1][D3Q19LatticeDescriptor.DOWN] = plane[0][0][D3Q19BorderPlaneConstants.DOWN_DOWN];

			f[xSize-1][1][zSize-1][D3Q19LatticeDescriptor.DOWNNORTH] = plane[xSize-1][0][D3Q19BorderPlaneConstants.DOWN_DOWNNORTH];
			f[xSize-2][0][zSize-1][D3Q19LatticeDescriptor.DOWNWEST] = plane[xSize-1][0][D3Q19BorderPlaneConstants.DOWN_DOWNWEST];
			f[xSize-1][0][zSize-1][D3Q19LatticeDescriptor.DOWN] = plane[xSize-1][0][D3Q19BorderPlaneConstants.DOWN_DOWN];

			f[xSize-2][ySize-1][zSize-1][D3Q19LatticeDescriptor.DOWNWEST] = plane[xSize-1][ySize-1][D3Q19BorderPlaneConstants.DOWN_DOWNWEST];
			f[xSize-1][ySize-2][zSize-1][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[xSize-1][ySize-1][D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH];
			f[xSize-1][ySize-1][zSize-1][D3Q19LatticeDescriptor.DOWN] = plane[xSize-1][ySize-1][D3Q19BorderPlaneConstants.DOWN_DOWN];

			// borders
			for(int y = 1; y < ySize-1; ++y) {
				f[1][y][zSize-1][D3Q19LatticeDescriptor.DOWNEAST] = plane[0][y][D3Q19BorderPlaneConstants.DOWN_DOWNEAST];
				f[0][y+1][zSize-1][D3Q19LatticeDescriptor.DOWNNORTH] = plane[0][y][D3Q19BorderPlaneConstants.DOWN_DOWNNORTH];
				f[0][y-1][zSize-1][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[0][y][D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH];
				f[0][y][zSize-1][D3Q19LatticeDescriptor.DOWN] = plane[0][y][D3Q19BorderPlaneConstants.DOWN_DOWN];

				f[xSize-1][y+1][zSize-1][D3Q19LatticeDescriptor.DOWNNORTH] = plane[xSize-1][y][D3Q19BorderPlaneConstants.DOWN_DOWNNORTH];
				f[xSize-2][y][zSize-1][D3Q19LatticeDescriptor.DOWNWEST] = plane[xSize-1][y][D3Q19BorderPlaneConstants.DOWN_DOWNWEST];
				f[xSize-1][y-1][zSize-1][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[xSize-1][y][D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH];
				f[xSize-1][y][zSize-1][D3Q19LatticeDescriptor.DOWN] = plane[xSize-1][y][D3Q19BorderPlaneConstants.DOWN_DOWN];
			}

			for(int x = 1; x < xSize-1; ++x) {
				f[x-1][ySize-1][zSize-1][D3Q19LatticeDescriptor.DOWNWEST] = plane[x][ySize-1][D3Q19BorderPlaneConstants.DOWN_DOWNWEST];
				f[x][ySize-2][zSize-1][D3Q19LatticeDescriptor.DOWNSOUTH] = plane[x][ySize-1][D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH];
				f[x][ySize-1][zSize-1][D3Q19LatticeDescriptor.DOWN] = plane[x][ySize-1][D3Q19BorderPlaneConstants.DOWN_DOWN];
				f[x+1][ySize-1][zSize-1][D3Q19LatticeDescriptor.DOWNEAST] = plane[x][ySize-1][D3Q19BorderPlaneConstants.DOWN_DOWNEAST];
				
				f[x+1][0][zSize-1][D3Q19LatticeDescriptor.DOWNEAST] = plane[x][0][D3Q19BorderPlaneConstants.DOWN_DOWNEAST];
				f[x][1][zSize-1][D3Q19LatticeDescriptor.DOWNNORTH] = plane[x][0][D3Q19BorderPlaneConstants.DOWN_DOWNNORTH];
				f[x-1][0][zSize-1][D3Q19LatticeDescriptor.DOWNWEST] = plane[x][0][D3Q19BorderPlaneConstants.DOWN_DOWNWEST];
				f[x][0][zSize-1][D3Q19LatticeDescriptor.DOWN] = plane[x][0][D3Q19BorderPlaneConstants.DOWN_DOWN];
			}

		} else if(bd.getLink() == D3Q19LatticeDescriptor.UP) {

			D3Q19SimpleBorderPlane in = (D3Q19SimpleBorderPlane) bd;
			double[][][] plane = in.getData();

			xe = 2;
			xw = 0;
			for(int x = 1; x < xSize-1; ++x, ++xe, ++xw) {
				yn = 2;
				ys = 0;
				for(int y = 1; y < ySize-1; ++y, ++ys, ++yn) {
					f[x][y][0][D3Q19LatticeDescriptor.UP] = plane[x][y][D3Q19BorderPlaneConstants.UP_UP];
					f[xe][y][0][D3Q19LatticeDescriptor.UPEAST] = plane[x][y][D3Q19BorderPlaneConstants.UP_UPEAST];
					f[x][yn][0][D3Q19LatticeDescriptor.UPNORTH] = plane[x][y][D3Q19BorderPlaneConstants.UP_UPNORTH];
					f[xw][y][0][D3Q19LatticeDescriptor.UPWEST] = plane[x][y][D3Q19BorderPlaneConstants.UP_UPWEST];
					f[x][ys][0][D3Q19LatticeDescriptor.UPSOUTH] = plane[x][y][D3Q19BorderPlaneConstants.UP_UPSOUTH];
				}	
			}

			// corners
			f[0][ySize-1][0][D3Q19LatticeDescriptor.UP] = plane[0][ySize-1][D3Q19BorderPlaneConstants.UP_UP];
			f[1][ySize-1][0][D3Q19LatticeDescriptor.UPEAST] = plane[0][ySize-1][D3Q19BorderPlaneConstants.UP_UPEAST];
			f[0][ySize-2][0][D3Q19LatticeDescriptor.UPSOUTH] = plane[0][ySize-1][D3Q19BorderPlaneConstants.UP_UPSOUTH];

			f[0][0][0][D3Q19LatticeDescriptor.UP] = plane[0][0][D3Q19BorderPlaneConstants.UP_UP];
			f[1][0][0][D3Q19LatticeDescriptor.UPEAST] = plane[0][0][D3Q19BorderPlaneConstants.UP_UPEAST];
			f[0][1][0][D3Q19LatticeDescriptor.UPNORTH] = plane[0][0][D3Q19BorderPlaneConstants.UP_UPNORTH];

			f[xSize-1][0][0][D3Q19LatticeDescriptor.UP] = plane[xSize-1][0][D3Q19BorderPlaneConstants.UP_UP];
			f[xSize-1][1][0][D3Q19LatticeDescriptor.UPNORTH] = plane[xSize-1][0][D3Q19BorderPlaneConstants.UP_UPNORTH];
			f[xSize-2][0][0][D3Q19LatticeDescriptor.UPWEST] = plane[xSize-1][0][D3Q19BorderPlaneConstants.UP_UPWEST];

			f[xSize-1][ySize-1][0][D3Q19LatticeDescriptor.UP] = plane[xSize-1][ySize-1][D3Q19BorderPlaneConstants.UP_UP];
			f[xSize-2][ySize-1][0][D3Q19LatticeDescriptor.UPWEST] = plane[xSize-1][ySize-1][D3Q19BorderPlaneConstants.UP_UPWEST];
			f[xSize-1][ySize-2][0][D3Q19LatticeDescriptor.UPSOUTH] = plane[xSize-1][ySize-1][D3Q19BorderPlaneConstants.UP_UPSOUTH];

			// borders
			for(int y = 1; y < ySize-1; ++y) {
				f[0][y][0][D3Q19LatticeDescriptor.UP] = plane[0][y][D3Q19BorderPlaneConstants.UP_UP];
				f[1][y][0][D3Q19LatticeDescriptor.UPEAST] = plane[0][y][D3Q19BorderPlaneConstants.UP_UPEAST];
				f[0][y+1][0][D3Q19LatticeDescriptor.UPNORTH] = plane[0][y][D3Q19BorderPlaneConstants.UP_UPNORTH];
				f[0][y-1][0][D3Q19LatticeDescriptor.UPSOUTH] = plane[0][y][D3Q19BorderPlaneConstants.UP_UPSOUTH];

				f[xSize-1][y][0][D3Q19LatticeDescriptor.UP] = plane[xSize-1][y][D3Q19BorderPlaneConstants.UP_UP];
				f[xSize-1][y+1][0][D3Q19LatticeDescriptor.UPNORTH] = plane[xSize-1][y][D3Q19BorderPlaneConstants.UP_UPNORTH];
				f[xSize-2][y][0][D3Q19LatticeDescriptor.UPWEST] = plane[xSize-1][y][D3Q19BorderPlaneConstants.UP_UPWEST];
				f[xSize-1][y-1][0][D3Q19LatticeDescriptor.UPSOUTH] = plane[xSize-1][y][D3Q19BorderPlaneConstants.UP_UPSOUTH];
			}

			for(int x = 1; x < xSize-1; ++x) {
				f[x][ySize-1][0][D3Q19LatticeDescriptor.UP] = plane[x][ySize-1][D3Q19BorderPlaneConstants.UP_UP];
				f[x+1][ySize-1][0][D3Q19LatticeDescriptor.UPEAST] = plane[x][ySize-1][D3Q19BorderPlaneConstants.UP_UPEAST];
				f[x-1][ySize-1][0][D3Q19LatticeDescriptor.UPWEST] = plane[x][ySize-1][D3Q19BorderPlaneConstants.UP_UPWEST];
				f[x][ySize-2][0][D3Q19LatticeDescriptor.UPSOUTH] = plane[x][ySize-1][D3Q19BorderPlaneConstants.UP_UPSOUTH];

				f[x][0][0][D3Q19LatticeDescriptor.UP] = plane[x][0][D3Q19BorderPlaneConstants.UP_UP];
				f[x+1][0][0][D3Q19LatticeDescriptor.UPEAST] = plane[x][0][D3Q19BorderPlaneConstants.UP_UPEAST];
				f[x][1][0][D3Q19LatticeDescriptor.UPNORTH] = plane[x][0][D3Q19BorderPlaneConstants.UP_UPNORTH];
				f[x-1][0][0][D3Q19LatticeDescriptor.UPWEST] = plane[x][0][D3Q19BorderPlaneConstants.UP_UPWEST];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.DOWNNORTH) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();

			for(int x = 0; x < xSize; ++x) {
				f[x][0][zSize-1][D3Q19LatticeDescriptor.DOWNNORTH] = vector[x];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.DOWNSOUTH) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int x = 0; x < xSize; ++x) {
				f[x][ySize-1][zSize-1][D3Q19LatticeDescriptor.DOWNSOUTH] = vector[x];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.DOWNWEST) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int y = 0; y < ySize; ++y) {
				f[xSize-1][y][zSize-1][D3Q19LatticeDescriptor.DOWNWEST] = vector[y];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.DOWNEAST) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int y = 0; y < ySize; ++y) {
				f[0][y][zSize-1][D3Q19LatticeDescriptor.DOWNEAST] = vector[y];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.UPSOUTH) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int x = 0; x < xSize; ++x) {
				f[x][ySize-1][0][D3Q19LatticeDescriptor.UPSOUTH] = vector[x];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.UPNORTH) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int x = 0; x < xSize; ++x) {
				f[x][0][0][D3Q19LatticeDescriptor.UPNORTH] = vector[x];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.UPWEST) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int y = 0; y < ySize; ++y) {
				f[xSize-1][y][0][D3Q19LatticeDescriptor.UPWEST] = vector[y];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.UPEAST) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int y = 0; y < ySize; ++y) {
				f[0][y][0][D3Q19LatticeDescriptor.UPEAST] = vector[y];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.SOUTHWEST) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int z = 0; z < zSize; ++z) {
				f[xSize-1][ySize-1][z][D3Q19LatticeDescriptor.SOUTHWEST] = vector[z];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.NORTHWEST) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int z = 0; z < zSize; ++z) {
				f[xSize-1][0][z][D3Q19LatticeDescriptor.NORTHWEST] = vector[z];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.SOUTHEAST) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int z = 0; z < zSize; ++z) {
				f[0][ySize-1][z][D3Q19LatticeDescriptor.SOUTHEAST] = vector[z];
			}
		} else if(bd.getLink() == D3Q19LatticeDescriptor.NORTHEAST) {

			D3Q19SimpleBorderLine in = (D3Q19SimpleBorderLine) bd;
			double[] vector = in.getData();
			for(int z = 0; z < zSize; ++z) {
				f[0][0][z][D3Q19LatticeDescriptor.NORTHEAST] = vector[z];
			}

		} else if(bd.getLink() == D3Q19LatticeDescriptor.REST) {

			// SKIP
			
		} else {

			throw new LBException("Unknwon velocity type "+bd.getLink());

		}
	}
	
	
	@Override
	protected D3Q19FluidSimpleProp getFluidInstance(int xSize, int ySize, int zSize) {
		return new D3Q19FluidSimpleProp(xSize, ySize, zSize);
	}

	@Override
	public D3Q19FluidSimpleProp clone() {
		return new D3Q19FluidSimpleProp();
	}
	
	@Override
	public BorderData getOutcomingDensities(int link) throws LBException {
		return outgoingData[0][link];
	}

	@Override
	public BorderData getOutcomingDensities(int bufPos, int link) throws LBException {
		return outgoingData[bufPos][link];
	}

	@Override
	public void fillBuffers() {
		fillBuffers(0);
	}
	
	public void fillBuffers(int bufPos) {
		StaticD3Q19FluidBaseOutSendSchemes.fillBuffers(f,
				xSize, ySize, zSize, outgoingData[bufPos]);
	}

}
