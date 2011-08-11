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
package laboGrid.lb.lattice.d3.q19.units;

import java.text.DecimalFormat;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.BorderData;
import laboGrid.lb.lattice.d3.q19.D3Q19BorderPlaneConstants;
import laboGrid.lb.lattice.d3.q19.D3Q19Fluid;
import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;
import laboGrid.lb.lattice.d3.q19.murphy.D3Q19Murphy;
import laboGrid.lb.lattice.d3.q19.off.D3Q19FluidOffProp;
import laboGrid.lb.lattice.d3.q19.off.D3Q19OffBorderLine;
import laboGrid.lb.lattice.d3.q19.off.D3Q19OffBorderPlane;
import laboGrid.lb.lattice.d3.q19.simple.D3Q19FluidSimpleProp;
import laboGrid.lb.lattice.d3.q19.simple.D3Q19SimpleBorderLine;
import laboGrid.lb.lattice.d3.q19.simple.D3Q19SimpleBorderPlane;
import laboGrid.lb.lattice.d3.units.D3FluidComparator;
import laboGrid.lb.solid.d3.D3SolidBitmap;



public class TestEfficientExtractInsert {
	private int maxErrors;
	private D3Q19FluidSimpleProp simple;
	private D3Q19FluidOffProp off;
	private D3Q19Murphy murphy;
	private D3SolidBitmap solid;
	
	@org.junit.Before
	public void init() throws LBException {
		int xSize = 8, ySize = 16, zSize = 9;
		maxErrors = 100;

		simple = new D3Q19FluidSimpleProp(xSize, ySize, zSize);
		off = new D3Q19FluidOffProp(xSize, ySize, zSize);
		murphy = new D3Q19Murphy(xSize, ySize, zSize);
		solid = new D3SolidBitmap(xSize, ySize, zSize);
		
		solid.setFluid();
	}
	
	private void initSameLattices(D3Q19Fluid ref, D3Q19Fluid test) throws LBException {
		int xSize = ref.getXSize(), ySize = ref.getYSize(), zSize = ref.getZSize();

		ref.setRandom();
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				for(int z = 0; z < zSize; ++z) {
					for(int q = 0; q < 19; ++q) {
						test.setDensity(q, x, y, z,
								ref.getDensity(q, x, y, z));
					}
				}
			}
		}
		D3FluidComparator.checkD3Fluids(ref, test, maxErrors);
	}

	@org.junit.Test
	public void testLinksExtraction() throws LBException {
		initSameLattices(simple, off);
		testLinksExtraction(simple, off);
		initSameLattices(simple, murphy);
		testLinksExtraction(simple, murphy);
	}
	
	public void testLinksExtraction(D3Q19Fluid simple,
			D3Q19Fluid off) throws LBException {
		int xSize = simple.getXSize(), ySize = simple.getYSize(), zSize = simple.getZSize();
		
		simple.fillBuffers();
		off.fillBuffers();
		
		simple.invalidateEmptySites(solid);
		off.invalidateEmptySites(solid);

		// Test South link extraction
		D3Q19SimpleBorderPlane bds =
			(D3Q19SimpleBorderPlane) simple.getOutcomingDensities(D3Q19LatticeDescriptor.SOUTH);
		
		D3Q19OffBorderPlane bdo =
			(D3Q19OffBorderPlane) off.getOutcomingDensities(D3Q19LatticeDescriptor.SOUTH);


		double[][][] simpleData = bds.getData();
		double[] offPlane = bdo.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST);
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				if(offPlane[x*zSize + z] != simpleData[x][z][D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST]) {
					
					System.out.println("simpleData");
					DecimalFormat formatter = new DecimalFormat("0.000");
					for(int i = 0; i < xSize; ++i) {
						for(int j = 0; j < zSize; ++j) {
							System.out.print(formatter.format(simpleData[i][j][D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST])+" ");
						}
						System.out.println();
					}
					
					System.out.println("offPlane");
					for(int i = 0; i < xSize; ++i) {
						for(int j = 0; j < zSize; ++j) {
							System.out.print(formatter.format(offPlane[i*zSize + j])+" ");
						}
						System.out.println();
					}

					throw new Error("Extraction fail for SOUTH_SOUTHWEST at "+x+","+z);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTH);
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				if(offPlane[x*zSize + z] != simpleData[x][z][D3Q19BorderPlaneConstants.SOUTH_SOUTH]) {
					throw new Error("Extraction fail for SOUTH_SOUTH at "+x+","+z);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST);
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				if(offPlane[x*zSize + z] != simpleData[x][z][D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST]) {
					throw new Error("Extraction fail for SOUTH_SOUTHEAST at "+x+","+z);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.SOUTH_UPSOUTH);
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				if(offPlane[x*zSize + z] != simpleData[x][z][D3Q19BorderPlaneConstants.SOUTH_UPSOUTH]) {
					throw new Error("Extraction fail for SOUTH_UPSOUTH at "+x+","+z);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH);
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				if(offPlane[x*zSize + z] != simpleData[x][z][D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH]) {
					throw new Error("Extraction fail for SOUTH_DOWNSOUTH at "+x+","+z);
				}
			}
		}


		// Test South link insertion
		simple.setIncomingDensities(bds);
		off.setIncomingDensities(bdo);

		D3FluidComparator.checkD3Fluids(simple, off, maxErrors);
//		System.out.println("SOUTH insertion test OK.");
		
		
		
		// Test WEST link extraction
		bds =
			(D3Q19SimpleBorderPlane) simple.getOutcomingDensities(D3Q19LatticeDescriptor.WEST);
		
		bdo =
			(D3Q19OffBorderPlane) off.getOutcomingDensities(D3Q19LatticeDescriptor.WEST);


		simpleData = bds.getData();
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.WEST_NORTHWEST);
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				if(offPlane[y*zSize + z] != simpleData[y][z][0]) {
					throw new Error("Extraction fail for WEST_NORTHWEST at "+y+","+z);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.WEST_WEST);
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				if(offPlane[y*zSize + z] != simpleData[y][z][1]) {
					throw new Error("Extraction fail for WEST_WEST at "+y+","+z);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.WEST_SOUTHWEST);
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				if(offPlane[y*zSize + z] != simpleData[y][z][2]) {
					throw new Error("Extraction fail for WEST_SOUTHWEST at "+y+","+z);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.WEST_UPWEST);
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				if(offPlane[y*zSize + z] != simpleData[y][z][3]) {
					throw new Error("Extraction fail for WEST_UPWEST at "+y+","+z);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.WEST_DOWNWEST);
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				if(offPlane[y*zSize + z] != simpleData[y][z][4]) {
					throw new Error("Extraction fail for WEST_DOWNWEST at "+y+","+z);
				}
			}
		}

		// Test WEST link insertion
		simple.setIncomingDensities(bds);
		off.setIncomingDensities(bdo);

		D3FluidComparator.checkD3Fluids(simple, off, maxErrors);
		
		
		// Test DOWN link extraction
		bds =
			(D3Q19SimpleBorderPlane) simple.getOutcomingDensities(D3Q19LatticeDescriptor.DOWN);
		
		bdo =
			(D3Q19OffBorderPlane) off.getOutcomingDensities(D3Q19LatticeDescriptor.DOWN);


		simpleData = bds.getData();
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.DOWN_DOWNEAST);
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				if(offPlane[x*ySize + y] != simpleData[x][y][0]) {
					throw new Error("Extraction fail for DOWN_DOWNEAST at "+x+","+y);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.DOWN_DOWNNORTH);
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				if(offPlane[x*ySize + y] != simpleData[x][y][1]) {
					throw new Error("Extraction fail for DOWN_DOWNNORTH at "+x+","+y);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.DOWN_DOWNWEST);
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				if(offPlane[x*ySize + y] != simpleData[x][y][2]) {
					throw new Error("Extraction fail for DOWN_DOWNWEST at "+x+","+y);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH);
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				if(offPlane[x*ySize + y] != simpleData[x][y][3]) {
					throw new Error("Extraction fail for DOWN_DOWNSOUTH at "+x+","+y);
				}
			}
		}
		offPlane = bdo.getData(D3Q19BorderPlaneConstants.DOWN_DOWN);
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				if(offPlane[x*ySize + y] != simpleData[x][y][4]) {
					throw new Error("Extraction fail for DOWN_DOWN at "+x+","+y);
				}
			}
		}


		// Test DOWN link insertion
		simple.setIncomingDensities(bds);
		off.setIncomingDensities(bdo);

		D3FluidComparator.checkD3Fluids(simple, off, maxErrors);
		
		
		// Test DOWNNORTH link extraction
		D3Q19SimpleBorderLine bdsl =
			(D3Q19SimpleBorderLine) simple.getOutcomingDensities(D3Q19LatticeDescriptor.DOWNNORTH);
		
		D3Q19OffBorderLine bdol =
			(D3Q19OffBorderLine) off.getOutcomingDensities(D3Q19LatticeDescriptor.DOWNNORTH);
		
		double[] simpleLine = bdsl.getData();
		double[] offLine = bdol.getData();
		
		for(int x = 0; x < xSize; ++x) {
			if(simpleLine[x] != offLine[x]) {
				throw new Error("Extraction fail for DOWNNORTH at "+x);
			}
		}
		
		
		// Test DOWNNORTH link insertion
		simple.setIncomingDensities(bdsl);
		off.setIncomingDensities(bdol);

		D3FluidComparator.checkD3Fluids(simple, off, maxErrors);
		
		
		
		// Test DOWNWEST link extraction
		bdsl =
			(D3Q19SimpleBorderLine) simple.getOutcomingDensities(D3Q19LatticeDescriptor.DOWNWEST);
		
		bdol =
			(D3Q19OffBorderLine) off.getOutcomingDensities(D3Q19LatticeDescriptor.DOWNWEST);
		
		simpleLine = bdsl.getData();
		offLine = bdol.getData();
		
		for(int y = 0; y < ySize; ++y) {
			if(simpleLine[y] != offLine[y]) {
				throw new Error("Extraction fail for DOWNWEST at "+y);
			}
		}
		
		// Test DOWNWEST link insertion
		simple.setIncomingDensities(bdsl);
		off.setIncomingDensities(bdol);

		D3FluidComparator.checkD3Fluids(simple, off, maxErrors);
		
		
		// Test SOUTHWEST link extraction
		bdsl =
			(D3Q19SimpleBorderLine) simple.getOutcomingDensities(D3Q19LatticeDescriptor.SOUTHWEST);
		
		bdol =
			(D3Q19OffBorderLine) off.getOutcomingDensities(D3Q19LatticeDescriptor.SOUTHWEST);
		
		simpleLine = bdsl.getData();
		offLine = bdol.getData();
		
		for(int z = 0; z < zSize; ++z) {
			if(simpleLine[z] != offLine[z]) {
				throw new Error("Extraction fail for SOUTHWEST at "+z);
			}
		}
		
		
		// Test DOWNWEST link insertion
		simple.setIncomingDensities(bdsl);
		off.setIncomingDensities(bdol);

		D3FluidComparator.checkD3Fluids(simple, off, maxErrors);
	}
	
	@org.junit.Test
	public void testMurphyInPlaceStream() throws LBException {
		initSameLattices(simple, murphy);
		testInPlaceStream(simple, murphy);
	}
	
	@org.junit.Test
	public void testOurInPlaceStream() throws LBException {
		initSameLattices(simple, off);
		testInPlaceStream(simple, off);
	}
	
	public void testInPlaceStream(D3Q19Fluid ref, D3Q19Fluid test) throws LBException {
		BorderData[] rbd = new BorderData[19];
		BorderData[] tbd = new BorderData[19];
		for(int i = 0; i < 50; ++i) {
			ref.fillBuffers();
			test.fillBuffers();
			
			for(int q = 0; q < 19; ++q) {
				rbd[q] = ref.getOutcomingDensities(q);
				tbd[q] = test.getOutcomingDensities(q);
			}
			
			ref.inPlaceStream();
			test.inPlaceStream();
			
			for(int q = 0; q < 19; ++q) {
				if(rbd[q] != null)
					ref.setIncomingDensities(rbd[q]);
				if(tbd[q] != null)
					test.setIncomingDensities(tbd[q]);
			}
			
			D3FluidComparator.checkD3Fluids(ref, test, maxErrors);
		}
	}
}
