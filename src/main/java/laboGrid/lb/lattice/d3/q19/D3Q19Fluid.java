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
package laboGrid.lb.lattice.d3.q19;

import java.util.Random;

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.LatticeDescriptor;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.lattice.d3.D3LatticeMacroVarsSlice;
import laboGrid.lb.lattice.d3.D3MacroVariables;
import laboGrid.lb.solid.Solid;
import laboGrid.lb.solid.d3.D3Solid;



public abstract class D3Q19Fluid extends D3Lattice {
	private static final long serialVersionUID = 1L;
	
	public D3Q19Fluid() {
		super();
	}
	
	public D3Q19Fluid(int xSize, int ySize, int zSize) {
		super(xSize, ySize, zSize);
	}

	public D3Q19Fluid(int[] spaceSize) {
		super(spaceSize);
	}

	@Override
	public void getSiteDensities(int x, int y, int z, double[] dest) {
		for(int i = 0; i < 19; ++i) {
			dest[i] = getDensity(i,x,y,z);
		}
	}

	public void setSiteDensities(int x, int y, int z, double[] src) {
		for(int i = 0; i < 19; ++i)
			setDensity(i,x,y,z, src[i]);
	}

	

	public D3Q19Fluid getPartition(int[] minPoint, int[] maxPoint)
			throws LBException {
		int xFrom = minPoint[0];
		int yFrom = minPoint[1];
		int zFrom = minPoint[2];
		int xTo = maxPoint[0];
		int yTo = maxPoint[1];
		int zTo = maxPoint[2];
		D3Q19Fluid dataPart = getFluidInstance(xTo - xFrom, yTo - yFrom, zTo - zFrom);

		for(int x = xFrom; x < xTo; ++x) {
			for(int y = yFrom; y < yTo; ++y) {
				for(int z = zFrom; z < zTo; ++z) {
					for(int c = 0; c < 19; ++c) {
						dataPart.setDensity(c,x-xFrom,y-yFrom,z-zFrom, getDensity(c,x,y,z));
					}
				}
			}
		}

		return dataPart;
	}


	public void invalidateEmptySites(Solid solid) {
		D3Solid d3Solid = (D3Solid) solid;
		
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				if(d3Solid.at(0, y, z) == Solid.FLUID) {
					setDensity(D3Q19LatticeDescriptor.EAST, 0, y, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.NORTHEAST, 0, y, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.SOUTHEAST, 0, y, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.UPEAST, 0, y, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.DOWNEAST, 0, y, z, Double.NaN);
				}
			}
		}

		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				if(d3Solid.at(x, 0, z) == Solid.FLUID) {
					setDensity(D3Q19LatticeDescriptor.NORTH, x, 0, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.NORTHWEST, x, 0, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.NORTHEAST, x, 0, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.UPNORTH, x, 0, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.DOWNNORTH, x, 0, z, Double.NaN);
				}
			}
		}
		
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				if(d3Solid.at(xSize-1, y, z) == Solid.FLUID) {
					setDensity(D3Q19LatticeDescriptor.WEST, xSize-1, y, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.SOUTHWEST, xSize-1, y, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.NORTHWEST, xSize-1, y, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.UPWEST, xSize-1, y, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.DOWNWEST, xSize-1, y, z, Double.NaN);
				}
			}
		}
		
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				if(d3Solid.at(x, ySize-1, z) == Solid.FLUID) {
					setDensity(D3Q19LatticeDescriptor.SOUTH, x, ySize-1, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.SOUTHEAST, x, ySize-1, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.SOUTHWEST, x, ySize-1, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.UPSOUTH, x, ySize-1, z, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.DOWNSOUTH, x, ySize-1, z, Double.NaN);
				}
			}
		}
		
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				if(d3Solid.at(x, y, 0) == Solid.FLUID) {
					setDensity(D3Q19LatticeDescriptor.UP, x, y, 0, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.UPSOUTH, x, y, 0, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.UPNORTH, x, y, 0, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.UPWEST, x, y, 0, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.UPEAST, x, y, 0, Double.NaN);
				}
			}
		}

		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				if(d3Solid.at(x, y, zSize-1) == Solid.FLUID) {
					setDensity(D3Q19LatticeDescriptor.DOWN, x, y, zSize-1, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.DOWNNORTH, x, y, zSize-1, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.DOWNSOUTH, x, y, zSize-1, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.DOWNEAST, x, y, zSize-1, Double.NaN);
					setDensity(D3Q19LatticeDescriptor.DOWNWEST, x, y, zSize-1, Double.NaN);
				}
			}
		}
	}

	public boolean isRestLink(int link) {
		return link == D3Q19LatticeDescriptor.REST;
	}

	public void setEquilibrium() throws LBException {
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				for(int z = 0; z < zSize; ++z) {
					setDensity(D3Q19LatticeDescriptor.EAST,x,y,z,1./18.);
					setDensity(D3Q19LatticeDescriptor.NORTH,x,y,z,1./18.);
					setDensity(D3Q19LatticeDescriptor.WEST,x,y,z,1./18.);
					setDensity(D3Q19LatticeDescriptor.SOUTH,x,y,z,1./18.);
					setDensity(D3Q19LatticeDescriptor.UP,x,y,z,1./18.);
					setDensity(D3Q19LatticeDescriptor.DOWN,x,y,z,1./18.);
					setDensity(D3Q19LatticeDescriptor.NORTHEAST,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.NORTHWEST,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.SOUTHWEST,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.SOUTHEAST,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.UPEAST,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.UPNORTH,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.UPWEST,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.UPSOUTH,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.DOWNEAST,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.DOWNNORTH,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.DOWNWEST,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.DOWNSOUTH,x,y,z,1./36.);
					setDensity(D3Q19LatticeDescriptor.REST,x,y,z,1./3.);
				}
			}
		}
	}

	public void setRandom() throws LBException {
		Random g = new Random();
		
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				for(int z = 0; z < zSize; ++z) {
					for(int k = 0; k < 19; ++k) {
						setDensity(k,x,y,z,g.nextDouble());
					}
				}
			}
		}
	}

	public D3Q19LatticeSlice getXYSlice(int z) throws LBException {
		if(z >= zSize || z < 0)
			throw new LBException("z out of bounds "+z);

		D3Q19LatticeSlice toReturn = new D3Q19LatticeSlice(xSize, ySize);
		double[] siteData = new double[19];
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				getSiteDensities(x, y, z, siteData);
				toReturn.setSite(x, y, siteData);
			}
		}
		
		return toReturn;
	}
	
	public D3Q19LatticeSlice getYZSlice(int x) throws LBException {
		if(x >= xSize || x < 0)
			throw new LBException("x out of bounds "+x);

		D3Q19LatticeSlice toReturn = new D3Q19LatticeSlice(ySize, zSize);
		double[] siteData = new double[19];
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				getSiteDensities(x, y, z, siteData);
				toReturn.setSite(y, z, siteData);
			}
		}
		
		return toReturn;
	}	
	
	public D3Q19LatticeSlice getXZSlice(int y) throws LBException {
		if(y >= ySize || y < 0)
			throw new LBException("y out of bounds "+y);

		D3Q19LatticeSlice toReturn = new D3Q19LatticeSlice(xSize, zSize);
		double[] siteData = new double[19];
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				getSiteDensities(x, y, z, siteData);
				toReturn.setSite(x, z, siteData);
			}
		}
		
		return toReturn;
	}
	
	public static double getLocalDensity(double[] site) {
		return	site[D3Q19LatticeDescriptor.EAST] +
			site[D3Q19LatticeDescriptor.NORTHEAST] +
			site[D3Q19LatticeDescriptor.NORTH] +
			site[D3Q19LatticeDescriptor.NORTHWEST] +
			site[D3Q19LatticeDescriptor.WEST] +
			site[D3Q19LatticeDescriptor.SOUTHWEST] +
			site[D3Q19LatticeDescriptor.SOUTH] +
			site[D3Q19LatticeDescriptor.SOUTHEAST] +
			site[D3Q19LatticeDescriptor.UP] +
			site[D3Q19LatticeDescriptor.UPEAST] +
			site[D3Q19LatticeDescriptor.UPNORTH] +
			site[D3Q19LatticeDescriptor.UPWEST] +
			site[D3Q19LatticeDescriptor.UPSOUTH] +
			site[D3Q19LatticeDescriptor.DOWNEAST] +
			site[D3Q19LatticeDescriptor.DOWNNORTH] +
			site[D3Q19LatticeDescriptor.DOWNWEST] +
			site[D3Q19LatticeDescriptor.DOWNSOUTH] +
			site[D3Q19LatticeDescriptor.DOWN] +
			site[D3Q19LatticeDescriptor.REST];
	}
	
	public static double getXSpeed(double[] site) {
		
		return (1./getLocalDensity(site)) * 
			(site[D3Q19LatticeDescriptor.EAST] +
				site[D3Q19LatticeDescriptor.NORTHEAST] +
				site[D3Q19LatticeDescriptor.SOUTHEAST] +
				site[D3Q19LatticeDescriptor.UPEAST] +
				site[D3Q19LatticeDescriptor.DOWNEAST] -
				site[D3Q19LatticeDescriptor.WEST] -
				site[D3Q19LatticeDescriptor.NORTHWEST] -
				site[D3Q19LatticeDescriptor.SOUTHWEST] -
				site[D3Q19LatticeDescriptor.UPWEST] -
				site[D3Q19LatticeDescriptor.DOWNWEST]);
	}
	
	public static double getYSpeed(double[] site) {
		
		return (1./getLocalDensity(site)) * (
			site[D3Q19LatticeDescriptor.NORTHEAST] + 
			site[D3Q19LatticeDescriptor.NORTH] + 
			site[D3Q19LatticeDescriptor.NORTHWEST] + 
			site[D3Q19LatticeDescriptor.UPNORTH] + 
			site[D3Q19LatticeDescriptor.DOWNNORTH] -
			site[D3Q19LatticeDescriptor.SOUTHWEST] -
			site[D3Q19LatticeDescriptor.SOUTH] -
			site[D3Q19LatticeDescriptor.SOUTHEAST] -
			site[D3Q19LatticeDescriptor.UPSOUTH] -
			site[D3Q19LatticeDescriptor.DOWNSOUTH]);
	}
	
	public static double getZSpeed(double[] site) {
		
		return (1./getLocalDensity(site)) * 
			(site[D3Q19LatticeDescriptor.UP] + 
			site[D3Q19LatticeDescriptor.UPEAST] + 
			site[D3Q19LatticeDescriptor.UPNORTH] + 
			site[D3Q19LatticeDescriptor.UPWEST] + 
			site[D3Q19LatticeDescriptor.UPSOUTH] -
			site[D3Q19LatticeDescriptor.DOWNEAST] -
			site[D3Q19LatticeDescriptor.DOWNNORTH] -
			site[D3Q19LatticeDescriptor.DOWNWEST] -
			site[D3Q19LatticeDescriptor.DOWNSOUTH] -
			site[D3Q19LatticeDescriptor.DOWN]);
	}
	
	public double getXSpeed(int x, int y, int z) {
		double[] site = new double[19];
		getSiteDensities(x, y, z, site);
		return getXSpeed(site);
	}

	public double getYSpeed(int x, int y, int z) {
		double[] site = new double[19];
		getSiteDensities(x, y, z, site);
		return getYSpeed(site);
	}
	
	public double getZSpeed(int x, int y, int z) {
		double[] site = new double[19];
		getSiteDensities(x, y, z, site);
		return getZSpeed(site);
	}
	
	public D3LatticeMacroVarsSlice getXYMacroVars(int z) {
		D3LatticeMacroVarsSlice toReturn = new D3LatticeMacroVarsSlice(xSize, ySize);

		double[] site = new double[19];
		for(int x = 0; x < xSize; ++x) {
			for(int y = 0; y < ySize; ++y) {
				getSiteDensities(x, y, z, site);
				double xSpeed = getXSpeed(site);
				double ySpeed = getYSpeed(site);
				double zSpeed = getZSpeed(site);
				double localDensity = getLocalDensity(site);
				
				toReturn.setMacroVars(x, y, xSpeed, ySpeed, zSpeed, localDensity);
			}
		}
		
		return toReturn;
	}
	
	public D3LatticeMacroVarsSlice getYZMacroVars(int x) {
		D3LatticeMacroVarsSlice toReturn = new D3LatticeMacroVarsSlice(ySize, ySize);

		double[] site = new double[19];
		for(int y = 0; y < ySize; ++y) {
			for(int z = 0; z < zSize; ++z) {
				getSiteDensities(x, y, z, site);
				double xSpeed = getXSpeed(site);
				double ySpeed = getYSpeed(site);
				double zSpeed = getZSpeed(site);
				double localDensity = getLocalDensity(site);
				
				toReturn.setMacroVars(y, z, xSpeed, ySpeed, zSpeed, localDensity);
			}
		}
		
		return toReturn;
	}

	public D3LatticeMacroVarsSlice getXZMacroVars(int y) {
		D3LatticeMacroVarsSlice toReturn = new D3LatticeMacroVarsSlice(xSize, zSize);

		double[] site = new double[19];
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				getSiteDensities(x, y, z, site);
				double xSpeed = getXSpeed(site);
				double ySpeed = getYSpeed(site);
				double zSpeed = getZSpeed(site);
				double localDensity = getLocalDensity(site);
				
				toReturn.setMacroVars(x, z, xSpeed, ySpeed, zSpeed, localDensity);
			}
		}
		
		return toReturn;
	}

	public double getLocalDensity(int x, int y, int z) {

		double[] site = new double[19];
		getSiteDensities(x, y, z, site);
		return getLocalDensity(site);

	}

	@Override
	public D3MacroVariables getMacroVariables(int x, int y, int z) {
		double xSpeed = getXSpeed(x, y, z);
		double ySpeed = getYSpeed(x, y, z);
		double zSpeed = getZSpeed(x, y, z);
		double dloc = getLocalDensity(x, y, z);
		
		return new D3MacroVariables(xSpeed, ySpeed, zSpeed, dloc);

	}
	
	@Override
	public boolean equals(Object o) {
		
		if(! (o instanceof D3Q19Fluid))
			return false;
		
		D3Q19Fluid other = (D3Q19Fluid) o;
		
		for(int x = 0; x < xSize; ++x)
			for(int y = 0; y < ySize; ++y)
				for(int z = 0; z < zSize; ++z)
					for(int q = 0; q < 19; ++q) {
						double val1 = getDensity(q, x, y, z);
						double val2 = other.getDensity(q, x, y, z);
						if(val1 != val2) {
							System.out.println(val1+"<>"+val2);
							return false;
						}
					}
		return true;

	}
	
	public LatticeDescriptor getLatticeDescriptor() {
		return D3Q19LatticeDescriptor.getSingleton();
	}

	protected abstract D3Q19Fluid getFluidInstance(int xSize, int ySize, int zSize);

}
