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

import laboGrid.lb.lattice.BorderData;
import laboGrid.lb.lattice.d3.q19.D3Q19BorderPlaneConstants;
import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;

/**
 * Basic implementation of outgoing values extraction.
 * 
 * @author dethier
 *
 */
public class StaticD3Q19FluidBaseOutSendSchemes {
	
	private static void fillSouthPlane(double[][][] tmp, int xSize, int zSize, double[][][][] fluid) {
		
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				tmp[x][z][D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST] = fluid[x][0][z][D3Q19LatticeDescriptor.SOUTHWEST];
				tmp[x][z][D3Q19BorderPlaneConstants.SOUTH_SOUTH] = fluid[x][0][z][D3Q19LatticeDescriptor.SOUTH];
				tmp[x][z][D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST] = fluid[x][0][z][D3Q19LatticeDescriptor.SOUTHEAST];
				tmp[x][z][D3Q19BorderPlaneConstants.SOUTH_UPSOUTH] = fluid[x][0][z][D3Q19LatticeDescriptor.UPSOUTH];
				tmp[x][z][D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH] = fluid[x][0][z][D3Q19LatticeDescriptor.DOWNSOUTH];
			}	
		}
		
	}
	
	
	private static void fillNorthPlane(double[][][] tmp, int y, int xSize, int zSize, double[][][][] fluid) {
		
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				tmp[x][z][D3Q19BorderPlaneConstants.NORTH_NORTHEAST] = fluid[x][y][z][D3Q19LatticeDescriptor.NORTHEAST];
				tmp[x][z][D3Q19BorderPlaneConstants.NORTH_NORTH] = fluid[x][y][z][D3Q19LatticeDescriptor.NORTH];
				tmp[x][z][D3Q19BorderPlaneConstants.NORTH_NORTHWEST] = fluid[x][y][z][D3Q19LatticeDescriptor.NORTHWEST];
				tmp[x][z][D3Q19BorderPlaneConstants.NORTH_UPNORTH] = fluid[x][y][z][D3Q19LatticeDescriptor.UPNORTH];
				tmp[x][z][D3Q19BorderPlaneConstants.NORTH_DOWNNORTH] = fluid[x][y][z][D3Q19LatticeDescriptor.DOWNNORTH];
			}	
		}
		
	}
	
	
	private static void fillWestPlane(double[][][] tmp, int ySize, int zSize, double[][][][] fluid) {
		
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				tmp[y][z][D3Q19BorderPlaneConstants.WEST_NORTHWEST] = fluid[0][y][z][D3Q19LatticeDescriptor.NORTHWEST];
				tmp[y][z][D3Q19BorderPlaneConstants.WEST_WEST] = fluid[0][y][z][D3Q19LatticeDescriptor.WEST];
				tmp[y][z][D3Q19BorderPlaneConstants.WEST_SOUTHWEST] = fluid[0][y][z][D3Q19LatticeDescriptor.SOUTHWEST];
				tmp[y][z][D3Q19BorderPlaneConstants.WEST_UPWEST] = fluid[0][y][z][D3Q19LatticeDescriptor.UPWEST];
				tmp[y][z][D3Q19BorderPlaneConstants.WEST_DOWNWEST] = fluid[0][y][z][D3Q19LatticeDescriptor.DOWNWEST];
			}	
		}
		
	}
	
	
	private static void fillEastPlane(double[][][] tmp, int x, int ySize, int zSize, double[][][][] fluid) {
		
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				tmp[y][z][D3Q19BorderPlaneConstants.EAST_EAST] = fluid[x][y][z][D3Q19LatticeDescriptor.EAST];
				tmp[y][z][D3Q19BorderPlaneConstants.EAST_NORTHEAST] = fluid[x][y][z][D3Q19LatticeDescriptor.NORTHEAST];
				tmp[y][z][D3Q19BorderPlaneConstants.EAST_SOUTHEAST] = fluid[x][y][z][D3Q19LatticeDescriptor.SOUTHEAST];
				tmp[y][z][D3Q19BorderPlaneConstants.EAST_UPEAST] = fluid[x][y][z][D3Q19LatticeDescriptor.UPEAST];
				tmp[y][z][D3Q19BorderPlaneConstants.EAST_DOWNEAST] = fluid[x][y][z][D3Q19LatticeDescriptor.DOWNEAST];
			}	
		}
		
	}
	
	
	private static void fillDownPlane(double[][][] tmp, int xSize, int ySize, double[][][][] fluid) {
		
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				tmp[x][y][D3Q19BorderPlaneConstants.DOWN_DOWNEAST] = fluid[x][y][0][D3Q19LatticeDescriptor.DOWNEAST];
				tmp[x][y][D3Q19BorderPlaneConstants.DOWN_DOWNNORTH] = fluid[x][y][0][D3Q19LatticeDescriptor.DOWNNORTH];
				tmp[x][y][D3Q19BorderPlaneConstants.DOWN_DOWNWEST] = fluid[x][y][0][D3Q19LatticeDescriptor.DOWNWEST];
				tmp[x][y][D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH] = fluid[x][y][0][D3Q19LatticeDescriptor.DOWNSOUTH];
				tmp[x][y][D3Q19BorderPlaneConstants.DOWN_DOWN] = fluid[x][y][0][D3Q19LatticeDescriptor.DOWN];
			}	
		}
		
	}
	
	
	private static void fillUpPlane(double[][][] tmp, int z, int xSize, int ySize, double[][][][] fluid) {
		
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				tmp[x][y][D3Q19BorderPlaneConstants.UP_UP] = fluid[x][y][z][D3Q19LatticeDescriptor.UP];
				tmp[x][y][D3Q19BorderPlaneConstants.UP_UPEAST] = fluid[x][y][z][D3Q19LatticeDescriptor.UPEAST];
				tmp[x][y][D3Q19BorderPlaneConstants.UP_UPNORTH] = fluid[x][y][z][D3Q19LatticeDescriptor.UPNORTH];
				tmp[x][y][D3Q19BorderPlaneConstants.UP_UPWEST] = fluid[x][y][z][D3Q19LatticeDescriptor.UPWEST];
				tmp[x][y][D3Q19BorderPlaneConstants.UP_UPSOUTH] = fluid[x][y][z][D3Q19LatticeDescriptor.UPSOUTH];
			}
		}
		
	}
	
	
//	public static BorderData getOutcomingDensities(
//			double[][][][] fluid, int link,
//			int xSize, int ySize, int zSize) throws LBException {
//
//		if(link == D3Q19LatticeDescriptor.SOUTH) {
//
//			double[][][] tmp = new double[xSize][zSize][5];
//			fillSouthPlane(tmp, xSize, zSize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//			
//		} else if(link == D3Q19LatticeDescriptor.NORTH) {
//			
//			double[][][] tmp = new double[xSize][zSize][5];
//			fillNorthPlane(tmp, ySize - 1, xSize, zSize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//
//		} else if(link == D3Q19LatticeDescriptor.WEST) {
//			
//			double[][][] tmp = new double[ySize][zSize][5];
//			fillWestPlane(tmp, ySize, zSize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//			
//		} else if(link == D3Q19LatticeDescriptor.EAST) {
//			
//			double[][][] tmp = new double[ySize][zSize][5];
//			fillEastPlane(tmp, xSize - 1, ySize, zSize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//			
//		} else if(link == D3Q19LatticeDescriptor.DOWN) {
//			
//			double[][][] tmp = new double[xSize][ySize][5];
//			fillDownPlane(tmp, xSize, ySize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//			
//		} else if(link == D3Q19LatticeDescriptor.UP) {
//		
//			double[][][] tmp = new double[xSize][ySize][5];
//			fillUpPlane(tmp, zSize - 1, xSize, ySize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//			
//		} else if(link == D3Q19LatticeDescriptor.DOWNNORTH) {
//
//			double[] tmp = new double[xSize];
//			for(int x = 0; x < xSize; ++x) {
//				tmp[x] = fluid[x][ySize - 1][0][D3Q19LatticeDescriptor.DOWNNORTH];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//			
//		} else if(link == D3Q19LatticeDescriptor.DOWNSOUTH) {
//			
//			double[] tmp = new double[xSize];
//			for(int x = 0; x < xSize; ++x) {
//				tmp[x] = fluid[x][0][0][D3Q19LatticeDescriptor.DOWNSOUTH];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.DOWNWEST) {
//			
//			double[] tmp = new double[ySize];
//			for(int y = 0; y < ySize; ++y) {
//				tmp[y] = fluid[0][y][0][D3Q19LatticeDescriptor.DOWNWEST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//			
//		} else if(link == D3Q19LatticeDescriptor.DOWNEAST) {
//			double[] tmp = new double[ySize];
//			for(int y = 0; y < ySize; ++y) {
//				tmp[y] = fluid[xSize - 1][y][0][D3Q19LatticeDescriptor.DOWNEAST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.UPSOUTH) {
//			double[] tmp = new double[xSize];
//			for(int x = 0; x < xSize; ++x) {
//				tmp[x] = fluid[x][0][zSize - 1][D3Q19LatticeDescriptor.UPSOUTH];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.UPNORTH) {
//			double[] tmp = new double[xSize];
//			for(int x = 0; x < xSize; ++x) {
//				tmp[x] = fluid[x][ySize - 1][zSize - 1][D3Q19LatticeDescriptor.UPNORTH];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.UPWEST) {
//			double[] tmp = new double[ySize];
//			for(int y = 0; y < ySize; ++y) {
//				tmp[y] = fluid[0][y][zSize - 1][D3Q19LatticeDescriptor.UPWEST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.UPEAST) {
//			double[] tmp = new double[ySize];
//			for(int y = 0; y < ySize; ++y) {
//				tmp[y] = fluid[xSize - 1][y][zSize - 1][D3Q19LatticeDescriptor.UPEAST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.SOUTHWEST) {
//			double[] tmp = new double[zSize];
//			for(int z = 0; z < zSize; ++z) {
//				tmp[z] = fluid[0][0][z][D3Q19LatticeDescriptor.SOUTHWEST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.NORTHWEST) {
//			double[] tmp = new double[zSize];
//			for(int z = 0; z < zSize; ++z) {
//				tmp[z] = fluid[0][ySize - 1][z][D3Q19LatticeDescriptor.NORTHWEST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.SOUTHEAST) {
//			double[] tmp = new double[zSize];
//			for(int z = 0; z < zSize; ++z) {
//				tmp[z] = fluid[xSize - 1][0][z][D3Q19LatticeDescriptor.SOUTHEAST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.NORTHEAST) {
//			double[] tmp = new double[zSize];
//			for(int z = 0; z < zSize; ++z) {
//				tmp[z] = fluid[xSize - 1][ySize - 1][z][D3Q19LatticeDescriptor.NORTHEAST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		}
//		
//		throw new LBException("No data to extract fpr link "+link);
//	}


//	public static BorderData getOutcomingDensities(double[][][][] fluid,
//			int link, int xSize, int ySize, int zSize,
//			double[][][][] planes, double[][] lines) throws LBException {
//
//
//		if(link == D3Q19LatticeDescriptor.SOUTH) {
//
//			double[][][] tmp = planes[link];
//			fillSouthPlane(tmp, xSize, zSize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//
//		} else if(link == D3Q19LatticeDescriptor.NORTH) {
//			double[][][] tmp = planes[link];
//			fillNorthPlane(tmp, ySize - 1, xSize, zSize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.WEST) {
//			double[][][] tmp = planes[link];
//			fillWestPlane(tmp, ySize, zSize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.EAST) {
//			double[][][] tmp = planes[link];
//			fillEastPlane(tmp, xSize - 1, ySize, zSize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.DOWN) {
//			double[][][] tmp = planes[link];
//			fillDownPlane(tmp, xSize, ySize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.UP) {
//			double[][][] tmp = planes[link];
//			fillUpPlane(tmp, zSize -1, xSize, ySize, fluid);
//			return new D3Q19SimpleBorderPlane(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.DOWNNORTH) {
//			double[] tmp = lines[link];
//			for(int x = 0; x < xSize; ++x) {
//				tmp[x] = fluid[x][ySize - 1][0][D3Q19LatticeDescriptor.DOWNNORTH];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.DOWNSOUTH) {
//			double[] tmp = lines[link];
//			for(int x = 0; x < xSize; ++x) {
//				tmp[x] = fluid[x][0][0][D3Q19LatticeDescriptor.DOWNSOUTH];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.DOWNWEST) {
//			double[] tmp = lines[link];
//			for(int y = 0; y < ySize; ++y) {
//				tmp[y] = fluid[0][y][0][D3Q19LatticeDescriptor.DOWNWEST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.DOWNEAST) {
//			double[] tmp = lines[link];
//			for(int y = 0; y < ySize; ++y) {
//				tmp[y] = fluid[xSize - 1][y][0][D3Q19LatticeDescriptor.DOWNEAST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.UPSOUTH) {
//			double[] tmp = lines[link];
//			for(int x = 0; x < xSize; ++x) {
//				tmp[x] = fluid[x][0][zSize - 1][D3Q19LatticeDescriptor.UPSOUTH];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.UPNORTH) {
//			double[] tmp = lines[link];
//			for(int x = 0; x < xSize; ++x) {
//				tmp[x] = fluid[x][ySize - 1][zSize - 1][D3Q19LatticeDescriptor.UPNORTH];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.UPWEST) {
//			double[] tmp = lines[link];
//			for(int y = 0; y < ySize; ++y) {
//				tmp[y] = fluid[0][y][zSize - 1][D3Q19LatticeDescriptor.UPWEST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.UPEAST) {
//			double[] tmp = lines[link];
//			for(int y = 0; y < ySize; ++y) {
//				tmp[y] = fluid[xSize - 1][y][zSize - 1][D3Q19LatticeDescriptor.UPEAST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.SOUTHWEST) {
//			double[] tmp = lines[link];
//			for(int z = 0; z < zSize; ++z) {
//				tmp[z] = fluid[0][0][z][D3Q19LatticeDescriptor.SOUTHWEST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.NORTHWEST) {
//			double[] tmp = lines[link];
//			for(int z = 0; z < zSize; ++z) {
//				tmp[z] = fluid[0][ySize - 1][z][D3Q19LatticeDescriptor.NORTHWEST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.SOUTHEAST) {
//			double[] tmp = lines[link];
//			for(int z = 0; z < zSize; ++z) {
//				tmp[z] = fluid[xSize - 1][0][z][D3Q19LatticeDescriptor.SOUTHEAST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		} else if(link == D3Q19LatticeDescriptor.NORTHEAST) {
//			double[] tmp = lines[link];
//			for(int z = 0; z < zSize; ++z) {
//				tmp[z] = fluid[xSize - 1][ySize - 1][z][D3Q19LatticeDescriptor.NORTHEAST];
//			}
//			return new D3Q19SimpleBorderLine(link, tmp);
//		}
//
//		throw new LBException("No data to extract for link "+link);
//	}


	public static void fillBuffers(double[][][][] fluid, int xSize, int ySize,
			int zSize, BorderData[] outgoingData) {
		
		D3Q19SimpleBorderPlane plane;
		D3Q19SimpleBorderLine line;
		
		double[][][] tmpPlane;
		
		plane = (D3Q19SimpleBorderPlane) outgoingData[D3Q19LatticeDescriptor.SOUTH];
		tmpPlane = plane.getData();
		fillSouthPlane(tmpPlane, xSize, zSize, fluid);

		plane = (D3Q19SimpleBorderPlane) outgoingData[D3Q19LatticeDescriptor.NORTH];
		tmpPlane = plane.getData();
		fillNorthPlane(tmpPlane, ySize - 1, xSize, zSize, fluid);

		plane = (D3Q19SimpleBorderPlane) outgoingData[D3Q19LatticeDescriptor.WEST];
		tmpPlane = plane.getData();
		fillWestPlane(tmpPlane, ySize, zSize, fluid);

		plane = (D3Q19SimpleBorderPlane) outgoingData[D3Q19LatticeDescriptor.EAST];
		tmpPlane = plane.getData();
		fillEastPlane(tmpPlane, xSize - 1, ySize, zSize, fluid);

		plane = (D3Q19SimpleBorderPlane) outgoingData[D3Q19LatticeDescriptor.DOWN];
		tmpPlane = plane.getData();
		fillDownPlane(tmpPlane, xSize, ySize, fluid);

		plane = (D3Q19SimpleBorderPlane) outgoingData[D3Q19LatticeDescriptor.UP];
		tmpPlane = plane.getData();
		fillUpPlane(tmpPlane, zSize -1, xSize, ySize, fluid);
		
		
		double[] tmpLine;

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.DOWNNORTH];
		tmpLine = line.getData();
		for(int x = 0; x < xSize; ++x) {
			tmpLine[x] = fluid[x][ySize - 1][0][D3Q19LatticeDescriptor.DOWNNORTH];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.DOWNSOUTH];
		tmpLine = line.getData();
		for(int x = 0; x < xSize; ++x) {
			tmpLine[x] = fluid[x][0][0][D3Q19LatticeDescriptor.DOWNSOUTH];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.DOWNWEST];
		tmpLine = line.getData();
		for(int y = 0; y < ySize; ++y) {
			tmpLine[y] = fluid[0][y][0][D3Q19LatticeDescriptor.DOWNWEST];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.DOWNEAST];
		tmpLine = line.getData();
		for(int y = 0; y < ySize; ++y) {
			tmpLine[y] = fluid[xSize - 1][y][0][D3Q19LatticeDescriptor.DOWNEAST];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.UPSOUTH];
		tmpLine = line.getData();
		for(int x = 0; x < xSize; ++x) {
			tmpLine[x] = fluid[x][0][zSize - 1][D3Q19LatticeDescriptor.UPSOUTH];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.UPNORTH];
		tmpLine = line.getData();
		for(int x = 0; x < xSize; ++x) {
			tmpLine[x] = fluid[x][ySize - 1][zSize - 1][D3Q19LatticeDescriptor.UPNORTH];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.UPWEST];
		tmpLine = line.getData();
		for(int y = 0; y < ySize; ++y) {
			tmpLine[y] = fluid[0][y][zSize - 1][D3Q19LatticeDescriptor.UPWEST];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.UPEAST];
		tmpLine = line.getData();
		for(int y = 0; y < ySize; ++y) {
			tmpLine[y] = fluid[xSize - 1][y][zSize - 1][D3Q19LatticeDescriptor.UPEAST];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.SOUTHWEST];
		tmpLine = line.getData();
		for(int z = 0; z < zSize; ++z) {
			tmpLine[z] = fluid[0][0][z][D3Q19LatticeDescriptor.SOUTHWEST];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.NORTHWEST];
		tmpLine = line.getData();
		for(int z = 0; z < zSize; ++z) {
			tmpLine[z] = fluid[0][ySize - 1][z][D3Q19LatticeDescriptor.NORTHWEST];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.SOUTHEAST];
		tmpLine = line.getData();
		for(int z = 0; z < zSize; ++z) {
			tmpLine[z] = fluid[xSize - 1][0][z][D3Q19LatticeDescriptor.SOUTHEAST];
		}

		line = (D3Q19SimpleBorderLine) outgoingData[D3Q19LatticeDescriptor.NORTHEAST];
		tmpLine = line.getData();
		for(int z = 0; z < zSize; ++z) {
			tmpLine[z] = fluid[xSize - 1][ySize - 1][z][D3Q19LatticeDescriptor.NORTHEAST];
		}
		
	}

}
