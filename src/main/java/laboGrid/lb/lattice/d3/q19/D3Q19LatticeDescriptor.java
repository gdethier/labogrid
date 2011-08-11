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

import laboGrid.lb.LBException;
import laboGrid.lb.lattice.LatticeDescriptor;

public class D3Q19LatticeDescriptor extends LatticeDescriptor {
	private static final long serialVersionUID = 1L;
	
	private static D3Q19LatticeDescriptor singleton;
	
	public static D3Q19LatticeDescriptor getSingleton() {
		if(singleton == null) {
			singleton = new D3Q19LatticeDescriptor(8);
		}
		return singleton;
	}

	/**
	 * d'Humiere notation scheme
	 */
	public static final int
	REST = 0,
	EAST = 1, 
	WEST = 2,
	NORTH = 3,
	SOUTH = 4,
	UP = 5,
	DOWN = 6,
	NORTHEAST = 7,
	NORTHWEST = 8,
	SOUTHEAST = 9,
	SOUTHWEST = 10,
	UPEAST = 11,
	UPWEST = 12,
	DOWNEAST = 13,
	DOWNWEST = 14,
	UPNORTH = 15,
	UPSOUTH = 16,
	DOWNNORTH = 17,
	DOWNSOUTH = 18;

	static int[][] d3q19Velocities = new int[][] {
		new int[] { 0, 0, 0}, //REST
		new int[] { 1, 0, 0}, //EAST
		new int[] {-1, 0, 0}, //WEST
		new int[] { 0, 1, 0}, //NORTH
		new int[] { 0,-1, 0}, //SOUTH
		new int[] { 0, 0, 1}, //UP
		new int[] { 0, 0,-1}, //DOWN
		new int[] { 1, 1, 0}, //NORTHEAST
		new int[] {-1, 1, 0}, //NORTHWEST
		new int[] { 1,-1, 0}, //SOUTHEAST
		new int[] {-1,-1, 0}, //SOUTHWEST
		new int[] { 1, 0, 1}, //UPEAST
		new int[] {-1, 0, 1}, //UPWEST
		new int[] { 1, 0,-1}, //DOWNEAST
		new int[] {-1, 0,-1}, //DOWNWEST
		new int[] { 0, 1, 1}, //UPNORTH
		new int[] { 0,-1, 1}, //UPSOUTH
		new int[] { 0, 1,-1}, //DOWNNORTH
		new int[] { 0,-1,-1}, //DOWNSOUTH
	};

	private D3Q19LatticeDescriptor(int fieldSize) {
		super(3, 19, fieldSize, d3q19Velocities);
	}
	
	public boolean isRest(int q) {
		return q == REST;
	}
	
	public static int getOpposit(int q) throws LBException {
		switch(q) {
		case REST : return REST;
		case EAST : return WEST;
		case WEST : return EAST;
		case NORTH : return SOUTH;
		case SOUTH : return NORTH;
		case UP : return DOWN;
		case DOWN : return UP;
		case NORTHEAST : return SOUTHWEST;
		case NORTHWEST : return SOUTHEAST;
		case SOUTHEAST : return NORTHWEST;
		case SOUTHWEST : return NORTHEAST;
		case UPEAST : return DOWNWEST;
		case UPWEST : return DOWNEAST;
		case DOWNEAST : return UPWEST;
		case DOWNWEST : return UPEAST;
		case UPNORTH : return DOWNSOUTH;
		case UPSOUTH : return DOWNNORTH;
		case DOWNNORTH : return UPSOUTH;
		case DOWNSOUTH : return UPNORTH;
		default: throw new LBException("Unknonw velocity vector");
		}
	}

//	public int getInBorderCount() {
//		return 18;
//	}

	@Override
	public float getOutcomingSites(int v, int[] size) {
		if(v == UP || v == DOWN) {
			return (5.f/19.f) * size[0] * size[1];
		} else if(v == NORTH || v == SOUTH) {
			return (5.f/19.f) * size[0] * size[2];
		} else if(v == EAST || v == WEST) {
			return (5.f/19.f) * size[1] * size[2];
		} else if(v == UPNORTH ||
				v == UPSOUTH ||
				v == DOWNNORTH ||
				v == DOWNSOUTH) {
			return (1.f/19.f) * size[0];
		} else if(v == UPEAST ||
				v == UPWEST ||
				v == DOWNEAST ||
				v == DOWNWEST) {
			return (1.f/19.f) * size[1];
		} else if(v == NORTHEAST ||
				v == NORTHWEST ||
				v == SOUTHEAST ||
				v == SOUTHWEST) {
			return (1.f/19.f) * size[2];
		} else if(v == REST) {
			return 0;
		} else {
			assert false;
			return Float.NaN;
		}
	}

	@Override
	public int getOutgoingBytes(int[] size) {
		int xSize = size[0];
		int ySize = size[1];
		int zSize = size[2];
		
		int planeOut = 5*(2*(xSize * ySize) + 2*(ySize * zSize) + 2*(xSize * zSize));
		int edgeOut = 4*xSize + 4*ySize + 4*zSize;
		
		return fieldSize * (planeOut + edgeOut);
	}

	@Override
	public int getOutgoingBytes(int v, int[] size) {
		int xSize = size[0];
		int ySize = size[1];
		int zSize = size[2];

		switch(v) {
		case REST : return 0;
		case EAST : return 5*ySize*zSize*fieldSize;
		case WEST : return 5*ySize*zSize*fieldSize;
		case NORTH : return 5*xSize*zSize*fieldSize;
		case SOUTH : return 5*xSize*zSize*fieldSize;
		case UP : return 5*xSize*ySize*fieldSize;
		case DOWN : return 5*xSize*ySize*fieldSize;
		case NORTHEAST : return zSize*fieldSize;
		case NORTHWEST : return zSize*fieldSize;
		case SOUTHEAST : return zSize*fieldSize;
		case SOUTHWEST : return zSize*fieldSize;
		case UPEAST : return ySize*fieldSize;
		case UPWEST : return ySize*fieldSize;
		case DOWNEAST : return ySize*fieldSize;
		case DOWNWEST : return ySize*fieldSize;
		case UPNORTH : return xSize*fieldSize;
		case UPSOUTH : return xSize*fieldSize;
		case DOWNNORTH : return xSize*fieldSize;
		case DOWNSOUTH : return xSize*fieldSize;
		default: return -1;
		}
	}

}
