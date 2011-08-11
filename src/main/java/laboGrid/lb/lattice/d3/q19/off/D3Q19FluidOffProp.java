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
package laboGrid.lb.lattice.d3.q19.off;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.BlockIterator;
import laboGrid.lb.lattice.BorderData;
import laboGrid.lb.lattice.d3.q19.D3Q19BorderPlaneConstants;
import laboGrid.lb.lattice.d3.q19.D3Q19Fluid;
import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;


public class D3Q19FluidOffProp extends D3Q19Fluid implements D3Q19BorderExtraction {

	private static final long serialVersionUID = 1L;
	
	private double[] f;
	private int[] offs;
	
	private int xySize, xzSize, yzSize, xyzSize;
	
	private BorderData[][] outgoingData;
	
	
	public D3Q19FluidOffProp() {
		super();
	}
	
	public D3Q19FluidOffProp(int xSize, int ySize, int zSize) {
		super(xSize, ySize, zSize);
	}
	
	public D3Q19FluidOffProp(int[] spaceSize) {
		super(spaceSize);
	}

	private void allocateOutputBuffers() {
		
		outgoingData = new BorderData[2][19];
		
		for(int bufPos = 0; bufPos < 2; ++bufPos) {

			// xz planes
			outgoingData[bufPos][D3Q19LatticeDescriptor.SOUTH] =
				new D3Q19OffBorderPlane(D3Q19LatticeDescriptor.SOUTH,
						new double[5][xSize*zSize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.NORTH] =
				new D3Q19OffBorderPlane(D3Q19LatticeDescriptor.NORTH,
						new double[5][xSize*zSize]);


			// yz planes
			outgoingData[bufPos][D3Q19LatticeDescriptor.WEST] =
				new D3Q19OffBorderPlane(D3Q19LatticeDescriptor.WEST,
						new double[5][ySize*zSize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.EAST] =
				new D3Q19OffBorderPlane(D3Q19LatticeDescriptor.EAST,
						new double[5][ySize*zSize]);


			// xy planes
			outgoingData[bufPos][D3Q19LatticeDescriptor.DOWN] =
				new D3Q19OffBorderPlane(D3Q19LatticeDescriptor.DOWN,
						new double[5][xSize*ySize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.UP] =
				new D3Q19OffBorderPlane(D3Q19LatticeDescriptor.UP,
						new double[5][xSize*ySize]);


			// lines
			outgoingData[bufPos][D3Q19LatticeDescriptor.DOWNNORTH] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.DOWNNORTH,
						new double[xSize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.DOWNSOUTH] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.DOWNSOUTH,
						new double[xSize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.DOWNWEST] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.DOWNWEST,
						new double[ySize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.DOWNEAST] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.DOWNEAST,
						new double[ySize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.UPSOUTH] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.UPSOUTH,
						new double[xSize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.UPNORTH] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.UPNORTH,
						new double[xSize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.UPWEST] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.UPWEST,
						new double[ySize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.UPEAST] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.UPEAST,
						new double[ySize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.SOUTHWEST] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.SOUTHWEST,
						new double[zSize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.NORTHWEST] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.NORTHWEST,
						new double[zSize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.SOUTHEAST] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.SOUTHEAST,
						new double[zSize]);

			outgoingData[bufPos][D3Q19LatticeDescriptor.NORTHEAST] =
				new D3Q19OffBorderLine(D3Q19LatticeDescriptor.NORTHEAST,
						new double[zSize]);

		}
		
	}
	
	@Override
	public void setSize(int[] size) {
		
		super.setSize(size);

		this.xySize = xSize*ySize;
		this.xzSize = xSize*zSize;
		this.yzSize = ySize*zSize;
		this.xyzSize = yzSize*xSize;

		f = new double[19 * xyzSize];
		offs = new int[19]; // all offsets equal 0
//		for(int i = 0; i < 19; ++i)
//			offs[i] = 5;

		allocateOutputBuffers();
	}

	public void setDensity(int c, int i, int j, int k, double density) {
		f[c*xyzSize + ((i*yzSize + j*zSize + k + offs[c])%xyzSize)] = density;
	}

	public double getDensity(int c, int i, int j, int k) {
		return f[c*xyzSize +
		         ((i*yzSize + j*zSize + k + offs[c]) % xyzSize)];
	}
	
	@Override
	public void inPlaceStream() {
		inPlaceStream(0);
	}

	@Override
	public void inPlaceStream(int bufPos) {
		
		assert yzSize != 0;
		assert xyzSize != 0;

		// Offset must go in the opposit direction of corresponding velocity so
		// velocity vector must be "negated".
		
		//REST (0, 0, 0): - 0
		//SKIP

		//EAST (1, 0, 0): - ySize*zSize
		offs[D3Q19LatticeDescriptor.EAST] =
			(offs[D3Q19LatticeDescriptor.EAST]
			      - yzSize + xyzSize) % xyzSize;
		
		//WEST (-1, 0, 0): + ySize*zSize
		offs[D3Q19LatticeDescriptor.WEST] =
			(offs[D3Q19LatticeDescriptor.WEST]
				+ yzSize) % xyzSize;
		
		//NORTH (0, 1, 0): - zSize
		offs[D3Q19LatticeDescriptor.NORTH] =
			(offs[D3Q19LatticeDescriptor.NORTH]
				- zSize + xyzSize) % xyzSize;
		
		//SOUTH (0,-1, 0): + zSize
		offs[D3Q19LatticeDescriptor.SOUTH] =
			(offs[D3Q19LatticeDescriptor.SOUTH]
				+ zSize) % xyzSize;
		
		//UP (0, 0, 1): - 1
		offs[D3Q19LatticeDescriptor.UP] =
			(offs[D3Q19LatticeDescriptor.UP]
				- 1 + xyzSize) % xyzSize;

		//DOWN (0, 0, -1): + 1
		offs[D3Q19LatticeDescriptor.DOWN] =
			(offs[D3Q19LatticeDescriptor.DOWN]
				+ 1) % xyzSize;

		//NORTHEAST (1, 1, 0): - ySize*zSize - zSize
		offs[D3Q19LatticeDescriptor.NORTHEAST] =
			(offs[D3Q19LatticeDescriptor.NORTHEAST]
				- yzSize - zSize + xyzSize) % xyzSize;

		//NORTHWEST (-1, 1, 0): + ySize*zSize - zSize
		offs[D3Q19LatticeDescriptor.NORTHWEST] =
			(offs[D3Q19LatticeDescriptor.NORTHWEST]
		        + yzSize - zSize + xyzSize) % xyzSize;

		//SOUTHEAST (1, -1, 0): - ySize*zSize + zSize
		offs[D3Q19LatticeDescriptor.SOUTHEAST] =
			(offs[D3Q19LatticeDescriptor.SOUTHEAST]
	            - yzSize + zSize + xyzSize) % xyzSize;

		//SOUTHWEST (-1, -1, 0): + ySize*zSize + zSize
		offs[D3Q19LatticeDescriptor.SOUTHWEST] =
			(offs[D3Q19LatticeDescriptor.SOUTHWEST]
		        + yzSize + zSize) % xyzSize;

		//UPEAST (1, 0, 1): - ySize*zSize - 1
		offs[D3Q19LatticeDescriptor.UPEAST] =
			(offs[D3Q19LatticeDescriptor.UPEAST]
		        - yzSize - 1 + xyzSize) % xyzSize;

		//UPWEST (-1, 0, 1): + ySize*zSize - 1
		offs[D3Q19LatticeDescriptor.UPWEST] =
			(offs[D3Q19LatticeDescriptor.UPWEST]
		        + yzSize - 1 + xyzSize) % xyzSize;

		//DOWNEAST (1, 0, -1): - ySize*zSize + 1
		offs[D3Q19LatticeDescriptor.DOWNEAST] =
			(offs[D3Q19LatticeDescriptor.DOWNEAST]
		        - yzSize + 1 + xyzSize) % xyzSize;

		//DOWNWEST (-1, 0, -1): + ySize*zSize + 1
		offs[D3Q19LatticeDescriptor.DOWNWEST] =
			(offs[D3Q19LatticeDescriptor.DOWNWEST]
		        + yzSize + 1) % xyzSize;

		//UPNORTH (0, 1, 1): - zSize - 1
		offs[D3Q19LatticeDescriptor.UPNORTH] =
			(offs[D3Q19LatticeDescriptor.UPNORTH]
		        - zSize - 1 + xyzSize) % xyzSize;

		//UPSOUTH (0, -1, 1): + zSize - 1
		offs[D3Q19LatticeDescriptor.UPSOUTH] =
			(offs[D3Q19LatticeDescriptor.UPSOUTH]
		        + zSize - 1 + xyzSize) % xyzSize;

		//DOWNNORTH (0, 1, -1): - zSize + 1
		offs[D3Q19LatticeDescriptor.DOWNNORTH] =
			(offs[D3Q19LatticeDescriptor.DOWNNORTH]
		        - zSize + 1 + xyzSize) % xyzSize;

		//DOWNSOUTH (0, -1, -1): + zSize + 1
		offs[D3Q19LatticeDescriptor.DOWNSOUTH] =
			(offs[D3Q19LatticeDescriptor.DOWNSOUTH]
		        + zSize + 1) % xyzSize;
		
		for(int i = 0; i < 19; ++i) {
			assert offs[i] >= 0 && offs[i] < xyzSize;
		}
		
	}

	@Override
	public BlockIterator getBlockIterator(int blockSize) {
		
		return new UnconstrainedBlockIterator(f, offs, blockSize, xSize, ySize, zSize);
		
	}
	
	
	@Override
	public BlockIterator getBlockIterator(int blockSize, int xFrom, int xTo, int yFrom, int yTo, int zFrom, int zTo) {

		return new ConstrainedBlockIterator(f, offs, zSize, yzSize, xyzSize,
				blockSize, xFrom, xTo, yFrom, yTo, zFrom, zTo);

	}
	
	
	/**
	 * Optimized code to ensure better data locality
	 */
	public void setIncomingDensities(BorderData in) throws LBException {
		
		double[] data;

		if(in.getLink() == D3Q19LatticeDescriptor.SOUTH) {

			D3Q19OffBorderPlane bd = (D3Q19OffBorderPlane) in;

			data = bd.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTH);
			insertXZPlane(D3Q19LatticeDescriptor.SOUTH,
					ySize - 1, 0, xSize, 0, zSize, data, 0); // No off

			data = bd.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST);
			insertXZPlane(D3Q19LatticeDescriptor.SOUTHEAST,
					ySize - 1, 1, xSize, 0, zSize, data, xzSize - zSize); // (x+1,0) -> -zSize

			data = bd.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST);
			insertXZPlane(D3Q19LatticeDescriptor.SOUTHWEST,
					ySize - 1, 0, xSize - 1, 0, zSize, data, zSize); // (x-1,0) -> +zSize

			data = bd.getData(D3Q19BorderPlaneConstants.SOUTH_UPSOUTH);
			insertXZPlane(D3Q19LatticeDescriptor.UPSOUTH,
					ySize - 1, 0, xSize, 1, zSize, data, xzSize - 1); // (0,z+1) -> -1

			data = bd.getData(D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH);
			insertXZPlane(D3Q19LatticeDescriptor.DOWNSOUTH,
					ySize - 1, 0, xSize, 0, zSize-1, data, 1); // (0,z-1) -> +1

		} else if(in.getLink() == D3Q19LatticeDescriptor.NORTH) {
			
			D3Q19OffBorderPlane bd = (D3Q19OffBorderPlane) in;

			data = bd.getData(D3Q19BorderPlaneConstants.NORTH_NORTH);
			insertXZPlane(D3Q19LatticeDescriptor.NORTH,
					0, 0, xSize, 0, zSize, data, 0); // No off

			data = bd.getData(D3Q19BorderPlaneConstants.NORTH_NORTHEAST);
			insertXZPlane(D3Q19LatticeDescriptor.NORTHEAST,
					0, 1, xSize, 0, zSize, data, xzSize - zSize); // (x+1,0) -> -zSize

			data = bd.getData(D3Q19BorderPlaneConstants.NORTH_NORTHWEST);
			insertXZPlane(D3Q19LatticeDescriptor.NORTHWEST,
					0, 0, xSize - 1, 0, zSize, data, zSize); // (x-1,0) -> +zSize

			data = bd.getData(D3Q19BorderPlaneConstants.NORTH_UPNORTH);
			insertXZPlane(D3Q19LatticeDescriptor.UPNORTH,
					0, 0, xSize, 1, zSize, data, xzSize - 1); // (0,z+1) -> -1

			data = bd.getData(D3Q19BorderPlaneConstants.NORTH_DOWNNORTH);
			insertXZPlane(D3Q19LatticeDescriptor.DOWNNORTH,
					0, 0, xSize, 0, zSize-1, data, 1); // (0,z-1) -> +1

		} else if(in.getLink() == D3Q19LatticeDescriptor.WEST) {
			
			D3Q19OffBorderPlane bd = (D3Q19OffBorderPlane) in;

			data = bd.getData(D3Q19BorderPlaneConstants.WEST_WEST);
			insertYZPlane(D3Q19LatticeDescriptor.WEST,
					xSize - 1, 0, ySize, 0, zSize, data, 0); // No off

			data = bd.getData(D3Q19BorderPlaneConstants.WEST_NORTHWEST);
			insertYZPlane(D3Q19LatticeDescriptor.NORTHWEST,
					xSize - 1, 1, ySize, 0, zSize, data, yzSize - zSize); // (y+1,0) -> -zSize

			data = bd.getData(D3Q19BorderPlaneConstants.WEST_SOUTHWEST);
			insertYZPlane(D3Q19LatticeDescriptor.SOUTHWEST,
					xSize - 1, 0, ySize - 1, 0, zSize, data, zSize); // (y-1,0) -> +zSize

			data = bd.getData(D3Q19BorderPlaneConstants.WEST_UPWEST);
			insertYZPlane(D3Q19LatticeDescriptor.UPWEST,
					xSize - 1, 0, ySize, 1, zSize, data, yzSize - 1); // (0,z+1) -> -1

			data = bd.getData(D3Q19BorderPlaneConstants.WEST_DOWNWEST);
			insertYZPlane(D3Q19LatticeDescriptor.DOWNWEST,
					xSize - 1, 0, ySize, 0, zSize-1, data, 1); // (0,z-1) -> +1

		} else if(in.getLink() == D3Q19LatticeDescriptor.EAST) {
			
			D3Q19OffBorderPlane bd = (D3Q19OffBorderPlane) in;

			data = bd.getData(D3Q19BorderPlaneConstants.EAST_EAST);
			insertYZPlane(D3Q19LatticeDescriptor.EAST,
					0, 0, ySize, 0, zSize, data, 0); // No off

			data = bd.getData(D3Q19BorderPlaneConstants.EAST_NORTHEAST);
			insertYZPlane(D3Q19LatticeDescriptor.NORTHEAST,
					0, 1, ySize, 0, zSize, data, yzSize - zSize); // (y+1,0) -> -zSize

			data = bd.getData(D3Q19BorderPlaneConstants.EAST_SOUTHEAST);
			insertYZPlane(D3Q19LatticeDescriptor.SOUTHEAST,
					0, 0, ySize - 1, 0, zSize, data, zSize); // (y-1,0) -> +zSize

			data = bd.getData(D3Q19BorderPlaneConstants.EAST_UPEAST);
			insertYZPlane(D3Q19LatticeDescriptor.UPEAST,
					0, 0, ySize, 1, zSize, data, yzSize - 1); // (0,z+1) -> -1

			data = bd.getData(D3Q19BorderPlaneConstants.EAST_DOWNEAST);
			insertYZPlane(D3Q19LatticeDescriptor.DOWNEAST,
					0, 0, ySize, 0, zSize - 1, data, 1); // (0,z-1) -> +1

		} else if(in.getLink() == D3Q19LatticeDescriptor.DOWN) {
			
			D3Q19OffBorderPlane bd = (D3Q19OffBorderPlane) in;

			data = bd.getData(D3Q19BorderPlaneConstants.DOWN_DOWN);
			insertXYPlane(D3Q19LatticeDescriptor.DOWN,
					zSize - 1, 0, xSize, 0, ySize, data, 0); // No off

			data = bd.getData(D3Q19BorderPlaneConstants.DOWN_DOWNEAST);
			insertXYPlane(D3Q19LatticeDescriptor.DOWNEAST,
					zSize - 1, 1, xSize, 0, ySize, data, xySize - ySize); // (x+1,0) -> -ySize

			data = bd.getData(D3Q19BorderPlaneConstants.DOWN_DOWNWEST);
			insertXYPlane(D3Q19LatticeDescriptor.DOWNWEST,
					zSize - 1, 0, xSize - 1, 0, ySize, data, ySize); // (x-1,0) -> +ySize
			
			data = bd.getData(D3Q19BorderPlaneConstants.DOWN_DOWNNORTH);
			insertXYPlane(D3Q19LatticeDescriptor.DOWNNORTH,
					zSize - 1, 0, xSize, 1, ySize, data, xySize - 1); // (0,y+1) -> -1

			data = bd.getData(D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH);
			insertXYPlane(D3Q19LatticeDescriptor.DOWNSOUTH,
					zSize - 1, 0, xSize, 0, ySize - 1, data, 1); // (0,y-1) -> +1

		} else if(in.getLink() == D3Q19LatticeDescriptor.UP) {
			
			D3Q19OffBorderPlane bd = (D3Q19OffBorderPlane) in;

			data = bd.getData(D3Q19BorderPlaneConstants.UP_UP);
			insertXYPlane(D3Q19LatticeDescriptor.UP,
					0, 0, xSize, 0, ySize, data, 0); // No off

			data = bd.getData(D3Q19BorderPlaneConstants.UP_UPEAST);
			insertXYPlane(D3Q19LatticeDescriptor.UPEAST,
					0, 1, xSize, 0, ySize, data, xySize - ySize); // (x+1,0) -> -ySize

			data = bd.getData(D3Q19BorderPlaneConstants.UP_UPWEST);
			insertXYPlane(D3Q19LatticeDescriptor.UPWEST,
					0, 0, xSize - 1, 0, ySize, data, ySize); // (x-1,0) -> +ySize
			
			data = bd.getData(D3Q19BorderPlaneConstants.UP_UPNORTH);
			insertXYPlane(D3Q19LatticeDescriptor.UPNORTH,
					0, 0, xSize, 1, ySize, data, xySize - 1); // (0,y+1) -> -1

			data = bd.getData(D3Q19BorderPlaneConstants.UP_UPSOUTH);
			insertXYPlane(D3Q19LatticeDescriptor.UPSOUTH,
					0, 0, xSize, 0, ySize - 1, data, 1); // (0,y-1) -> +1

		} else if(in.getLink() == D3Q19LatticeDescriptor.DOWNNORTH) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertXLine(D3Q19LatticeDescriptor.DOWNNORTH,
					0, zSize - 1, 0, xSize, data, 0); // No off

		} else if(in.getLink() == D3Q19LatticeDescriptor.DOWNSOUTH) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertXLine(D3Q19LatticeDescriptor.DOWNSOUTH,
					ySize - 1, zSize - 1, 0, xSize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.DOWNWEST) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertYLine(D3Q19LatticeDescriptor.DOWNWEST,
					xSize - 1, zSize - 1, 0, ySize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.DOWNEAST) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertYLine(D3Q19LatticeDescriptor.DOWNEAST,
					0, zSize - 1, 0, ySize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.UPSOUTH) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertXLine(D3Q19LatticeDescriptor.UPSOUTH,
					ySize - 1, 0, 0, xSize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.UPNORTH) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertXLine(D3Q19LatticeDescriptor.UPNORTH,
					0, 0, 0, xSize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.UPWEST) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertYLine(D3Q19LatticeDescriptor.UPWEST,
					xSize - 1, 0, 0, ySize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.UPEAST) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertYLine(D3Q19LatticeDescriptor.UPEAST,
					0, 0, 0, ySize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.SOUTHWEST) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertZLine(D3Q19LatticeDescriptor.SOUTHWEST,
					xSize - 1, ySize - 1, 0, zSize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.NORTHWEST) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertZLine(D3Q19LatticeDescriptor.NORTHWEST,
					xSize - 1, 0, 0, zSize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.SOUTHEAST) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertZLine(D3Q19LatticeDescriptor.SOUTHEAST,
					0, ySize - 1, 0, zSize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.NORTHEAST) {
			
			D3Q19OffBorderLine bd = (D3Q19OffBorderLine) in;
			data = bd.getData();

			insertZLine(D3Q19LatticeDescriptor.NORTHEAST,
					0, 0, 0, zSize, data, 0); // No off
			
		} else if(in.getLink() == D3Q19LatticeDescriptor.REST) {

			// SKIP
			
		} else {
			throw new LBException("Unknwon velocity type "+in.getLink());
		}
	}
	
	private void copyXPlaneLineToF(double[] plane, int pos,
			int fPos, int length) {

		if(pos + length <= plane.length) {

			int currentFPos = fPos;
			int currentPPos = pos;
			for(int i = 0; i < length; ++i) {
				f[currentFPos] = plane[currentPPos];
				++currentPPos;
				currentFPos += yzSize;
			}

		} else {

			int firstCopy = plane.length - pos;
			int currentFPos = fPos;
			int currentPPos = pos;
			for(int i = 0; i < firstCopy; ++i) {
				f[currentFPos] = plane[currentPPos];
				++currentPPos;
				currentFPos += yzSize;
			}

			int secondCopy = length - firstCopy;
			currentPPos = 0;
			for(int i = 0; i < secondCopy; ++i) {
				f[currentFPos] = plane[currentPPos];
				++currentPPos;
				currentFPos += yzSize;
			}

		}

	}
	
	private void insertXLine(int q, int y, int z,
			int xFrom, int xTo, double[] line, int off) {

		int qOff = q * xyzSize;
		int qStop = qOff + xyzSize;

		int firstZ = qOff + (offs[q] + (xFrom * yzSize) + (y * zSize) + z) % xyzSize;
		int lastZ = qOff + (offs[q] + ((xTo - 1) * yzSize) + (y * zSize) + z) % xyzSize;
		
		int pos = (off + xFrom) % line.length;
		
		int length = xTo - xFrom;
		if(firstZ <= lastZ) {

			copyXPlaneLineToF(line, pos, firstZ, length);

		} else {
			
			// 1. Copy from zStart to qStop
			int firstCopy = (qStop - firstZ + yzSize - 1) / yzSize;
			copyXPlaneLineToF(line, pos, firstZ, firstCopy);

			// 2. Copy from qOff to zStop
			int i = (pos + firstCopy) % line.length;
			int k = (qStop - firstZ) % yzSize;
			int j;
			if(k == 0)
				j = qOff;
			else
				j = qOff + yzSize - k;
			int secondCopy = length - firstCopy;
			copyXPlaneLineToF(line, i, j, secondCopy);
			
			assert firstCopy + secondCopy == length;
			
		}

	}


	private void insertYLine(int q, int x, int z,
			int yFrom, int yTo, double[] line, int off) {

		int qOff = q * xyzSize;
		int qStop = qOff + xyzSize;

		int firstZ = qOff + (offs[q] + (x * yzSize) + (yFrom * zSize) + z) % xyzSize;
		int lastZ = qOff + (offs[q] + (x * yzSize) + ((yTo - 1) * zSize) + z) % xyzSize;
		
		int pos = (off + yFrom) % line.length;
		
		int length = yTo - yFrom;
		if(firstZ <= lastZ) {

			copyYPlaneLineToF(line, pos, firstZ, length);

		} else {
			
			// 1. Copy from zStart to qStop
			int firstCopy = (qStop - firstZ + zSize - 1) / zSize;
			copyYPlaneLineToF(line, pos, firstZ, firstCopy);

			// 2. Copy from qOff to zStop
			int i = (pos + firstCopy) % line.length;
			int k = (qStop - firstZ) % zSize;
			int j;
			if(k == 0)
				j = qOff;
			else
				j = qOff + zSize - k;
			int secondCopy = length - firstCopy;
			copyYPlaneLineToF(line, i, j, secondCopy);
			
			assert firstCopy + secondCopy == length;
			
		}

	}
	
	
	private void insertZLine(int q, int x, int y,
			int zFrom, int zTo, double[] line, int off) {

		int qOff = q * xyzSize;
		int qStop = qOff + xyzSize;

		int firstZ = qOff + (offs[q] + (x * yzSize) + (y * zSize) + zFrom) % xyzSize;
		int lastZ = qOff + (offs[q] + (x * yzSize) + (y * zSize) + zTo) % xyzSize;

		int pos = (off + zFrom) % line.length;
		int length = zTo - zFrom;

		if(firstZ <= lastZ) {

			copyZPlaneLineToF(line, pos, firstZ, length);

		} else {

			int firstCopy = qStop - firstZ;
			copyZPlaneLineToF(line, pos, firstZ, firstCopy);
			int secondCopy = length - firstCopy;
			copyZPlaneLineToF(line, (pos + firstCopy) % line.length,
					qOff, secondCopy);

		}

	}


	private void copyZPlaneLineToF(double[] plane, int pos,
			int fPos, int length) {
		
		assert pos >= 0 && pos < plane.length : pos;
		assert length <= plane.length : length +"<>"+plane.length;

		if(pos + length <= plane.length) {

			System.arraycopy(plane, pos, f, fPos, length);

		} else {
			
			int firstCopy = plane.length - pos;
			System.arraycopy(plane, pos, f, fPos, firstCopy);
			int i = fPos + firstCopy;
			int secondCopy = length - firstCopy;
			assert i < f.length;
			assert secondCopy < plane.length;
			System.arraycopy(plane, 0, f, i, secondCopy);

		}

	}


	private void insertXZPlane(int q, int y, int xFrom, int xTo,
			int zFrom, int zTo, double[] plane, int off) {

		assert off >= 0 && off < plane.length;

		int qOff = q * xyzSize;
		int qStop = qOff + xyzSize;

		int length = zTo - zFrom;
		assert length <= zSize;
		for(int x = xFrom; x < xTo; ++x) {
			
			int zStart = qOff + (offs[q] + x * yzSize + y * zSize + zFrom) % xyzSize;
			int zStop = qOff + (offs[q] + x * yzSize + y * zSize + zTo - 1) % xyzSize;
			
			int pos = (off + x*zSize + zFrom) % plane.length;

			if(zStart < zStop) {
				
				copyZPlaneLineToF(plane, pos, zStart, length);

			} else {

				// 1. Copy from zStart to qStop
				int firstCopy = qStop - zStart;
				copyZPlaneLineToF(plane, pos, zStart, firstCopy);

				// 2. Copy from qOff to zStop
				int i = (pos + firstCopy) % plane.length;
				int secondCopy = length - firstCopy;
				copyZPlaneLineToF(plane, i, qOff, secondCopy);
				
				assert firstCopy + secondCopy == length;

			}
			
		}
		
	}

	public void extractXZPlane(int link, int y, double[] plane) {
		
		assert plane.length == xSize*zSize : link +" -> "+plane.length;

		int qOff = link * xyzSize;
		int qStop = qOff + xyzSize;
		
		int pos = 0;
		for(int x = 0; x < xSize; ++x) {
			int zStart = qOff + (offs[link] + x * yzSize + y * zSize) % xyzSize;
			int zStop = qOff + (offs[link] + x * yzSize + y * zSize + zSize - 1) % xyzSize;

			if(zStart <= zStop) {

				assert zStop - zStart + 1 == zSize;
				System.arraycopy(f, zStart, plane, pos, zSize);
				pos += zSize;

			} else {

				int firstCopy = qStop - zStart;
				System.arraycopy(f, zStart, plane, pos, firstCopy);
				pos += firstCopy;
				int secondCopy = zSize - firstCopy;
				assert secondCopy >= 0;
				System.arraycopy(f, qOff, plane, pos, secondCopy);
				pos += secondCopy;
				
				assert firstCopy + secondCopy == zSize;
				
			}
			
		}

	}

	
	private void insertYZPlane(int q, int x, int yFrom, int yTo,
			int zFrom, int zTo, double[] plane, int off) {
		
		assert plane.length == yzSize;
		assert zFrom >= 0 && zTo <= zSize;

		int qOff = q * xyzSize;
		int qStop = qOff + xyzSize;
		
		int length = zTo - zFrom;
		for(int y = yFrom; y < yTo; ++y) {

			int zStart = qOff + (offs[q] + x * yzSize + y * zSize + zFrom) % xyzSize;
			int zStop = qOff + (offs[q] + x * yzSize + y * zSize + zTo - 1) % xyzSize;
			
			int pos = (off + y*zSize + zFrom) % plane.length;
	
			if(zStart <= zStop) {

				copyZPlaneLineToF(plane, pos, zStart, length);

			} else {
				
				// 1. Copy from zStart to qStop
				int firstCopy = qStop - zStart;
				copyZPlaneLineToF(plane, pos, zStart, firstCopy);

				// 2. Copy from qOff to zStop
				int i = (pos + firstCopy) % plane.length;
				int secondCopy = length - firstCopy;
				copyZPlaneLineToF(plane, i, qOff, secondCopy);
				
				assert qOff + secondCopy - 1 == zStop;
				assert firstCopy + secondCopy == length;
				
			}
		
		}
		
	}

	public void extractYZPlane(int link, int x, double[] plane) {
		
		assert plane.length == yzSize;

		int qOff = link * xyzSize;

		int zStart = qOff + (offs[link] + x * yzSize /* + 0 * ySize + 0*/) % xyzSize;
		int zStop = qOff + (offs[link] + x * yzSize + (ySize - 1) * zSize + zSize - 1) % xyzSize;

		if(zStart <= zStop) {

			System.arraycopy(f, zStart, plane, 0, yzSize);

		} else {
			
			int qStop = qOff + xyzSize;
			int firstCopy = qStop - zStart;
			System.arraycopy(f, zStart, plane, 0, firstCopy);

			int pos = firstCopy;
			int secondCopy = yzSize - firstCopy;
			assert secondCopy >= 0;
			System.arraycopy(f, qOff, plane, pos, secondCopy);
			assert pos + secondCopy == plane.length;
			
		}
		
	}
	
	
	private void copyYPlaneLineToF(double[] plane, int pos,
			int fPos, int length) {
		
		assert pos >= 0 && pos < plane.length;
		
		if(pos + length <= plane.length) {

			int currentFPos = fPos;
			int currentPPos = pos;
			for(int i = 0; i < length; ++i) {

				f[currentFPos] = plane[currentPPos];
				++currentPPos;
				currentFPos += zSize;

			}

		} else {
			
			int firstCopy = plane.length - pos;
			int currentFPos = fPos;
			int currentPPos = pos;
			for(int i = 0; i < firstCopy; ++i) {

				f[currentFPos] = plane[currentPPos];
				++currentPPos;
				currentFPos += zSize;

			}

			int secondCopy = length - firstCopy;
			currentPPos = 0;
			for(int i = 0; i < secondCopy; ++i) {

				f[currentFPos] = plane[currentPPos];
				++currentPPos;
				currentFPos += zSize;

			}

		}
		
	}
	
	
	private void insertXYPlane(int q, int z, int xFrom, int xTo,
			int yFrom, int yTo, double[] plane, int off) {

		assert plane.length == xSize*ySize;
		
		int qOff = q * xyzSize;
		int qStop = qOff + xyzSize;
		
		for(int x = xFrom; x < xTo; ++x) {

			int firstZ = qOff + (offs[q] + (x * yzSize) + (yFrom * zSize) + z) % xyzSize;
			int lastZ = qOff + (offs[q] + (x * yzSize) + ((yTo - 1) * zSize) + z) % xyzSize;
			
			int pos = (off + x * ySize + yFrom) % plane.length;

			int length = yTo - yFrom;
			if(firstZ <= lastZ) {

				copyYPlaneLineToF(plane, pos, firstZ, length);

			} else {
			
				// 1. Copy from zStart to qStop
				int firstCopy = (qStop - firstZ + zSize - 1) / zSize;
				copyYPlaneLineToF(plane, pos, firstZ, firstCopy);

				// 2. Copy from qOff to zStop
				int i = (pos + firstCopy) % plane.length;
				int k = (qStop - firstZ) % zSize;
				int j;
				if(k == 0)
					j = qOff;
				else
					j = qOff + zSize - k;
				int secondCopy = length - firstCopy;
				copyYPlaneLineToF(plane, i, j, secondCopy);
				
				assert firstCopy + secondCopy == length;
			
			}

		}

	}


	public void extractXYPlane(int q, int z, double[] plane) {
		
		assert plane.length == xySize;
		
		int qOff = q * xyzSize;
		int qStop = qOff + xyzSize;
		
		int firstZ = qOff + (offs[q] + z) % xyzSize; // x,y == (0,0)
		int lastZ = qOff + (offs[q] + ((xSize-1) * yzSize) + ((ySize-1) * zSize) + z) % xyzSize;
		
		if(firstZ <= lastZ) {
			
			int pos = 0;
			for(int i = firstZ; i <= lastZ; i += zSize) {
				plane[pos] = f[i];
				++pos;
			}
			
		} else {
		
			int pos = 0;
			for(int i = firstZ; i < qStop; i += zSize) {
				plane[pos] = f[i];
				++pos;
			}

			int k = (qStop - firstZ)%zSize;
			int r;
			if(k == 0)
				r = qOff;
			else
				r = qOff + zSize - k;
			int i;
			for(i = r; i <= lastZ; i += zSize) {
				plane[pos] = f[i];
				++pos;
			}

		}

	}


	public void extractXLine(int q, int y, int z, double[] line) {
		
		int qOff = q * xyzSize;
		int qStop = qOff + xyzSize;

		int firstZ = qOff + (offs[q] + y * zSize + z) % xyzSize; // x == 0
		int lastZ = qOff + (offs[q] + ((xSize-1) * yzSize) + y * zSize + z) % xyzSize;
		
		
		if(firstZ <= lastZ) {

			int pos = 0;
			for(int i = firstZ; i <= lastZ; i += yzSize) {
				line[pos] = f[i];
				++pos;
			}

		} else {

			int pos = 0;
			for(int i = firstZ; i < qStop; i += yzSize) {
				line[pos] = f[i];
				++pos;
			}

			int k = (qStop - firstZ) % yzSize;
			int r;
			if(k == 0)
				r = qOff;
			else
				r = qOff + yzSize - k;
			for(int i = r; i <= lastZ; i += yzSize) {
				line[pos] = f[i];
				++pos;
			}
			assert pos == line.length;

		}

	}


	public void extractYLine(int q, int x, int z, double[] line) {
		
		int qOff = q * xyzSize;
		int qStop = qOff + xyzSize;
		
		int first = qOff + (offs[q] + x * yzSize + z) % xyzSize; // y == 0
		int last = qOff + (offs[q] + x * yzSize + (ySize-1) * zSize + z) % xyzSize; // y == 0
		
		if(first < last) {
			
			int pos = 0;
			for(int i = first; i <= last; i += zSize) {
				line[pos] = f[i];
				++pos;
			}

		} else {
			
			int pos = 0;
			for(int i = first; i < qStop; i += zSize) {
				line[pos] = f[i];
				++pos;
			}

			int k = (qStop - first)%zSize;
			int r;
			if(k == 0)
				r = qOff;
			else
				r = qOff + zSize - k;
			for(int i = r; i <= last; i += zSize) {
				line[pos] = f[i];
				++pos;
			}
			
			assert pos == line.length;
			
		}

	}

	public void extractZLine(int q, int x, int y, double[] line) {

		int qOff = q * xyzSize;
		int qStop = qOff + xyzSize;
		
		int first = qOff + (offs[q] + x * yzSize + y*zSize) % xyzSize; // z==0
		int last = qOff + (offs[q] + x * yzSize + y*zSize + zSize - 1) % xyzSize;

		if(first <= last) {
			
			System.arraycopy(f, first, line, 0, zSize);
			
		} else {

			int firstCopy = qStop - first;
			System.arraycopy(f, first, line, 0, firstCopy);
			int secondCopy = zSize - firstCopy;
			System.arraycopy(f, qOff, line, firstCopy, secondCopy);

		}
		
	}
	
	@Override
	public BorderData getOutcomingDensities(int bufPos, int link) {
		return outgoingData[bufPos][link];
	}
	
	@Override
	public BorderData getOutcomingDensities(int link) {
		return outgoingData[0][link];
	}
	
	@Override
	public void fillBuffers() {
		fillBuffers(0);
	}

	private void fillBuffers(int bufPos) {
		StaticD3Q19FluidOffOutSendSchemes.fillBuffers(this,
				xSize, ySize, zSize, outgoingData[bufPos]);
	}
	
	@Override
	protected D3Q19Fluid getFluidInstance(int xSize, int ySize, int zSize) {
		return new D3Q19FluidOffProp(xSize, ySize, zSize);
	}

	@Override
	public D3Q19FluidOffProp clone() {
		return new D3Q19FluidOffProp();
	}

}
