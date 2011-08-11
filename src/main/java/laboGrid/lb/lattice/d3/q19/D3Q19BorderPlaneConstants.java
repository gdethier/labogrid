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

public interface D3Q19BorderPlaneConstants {
	public static final int SOUTH_SOUTHWEST = 0;
	public static final int SOUTH_SOUTH = 1;
	public static final int SOUTH_SOUTHEAST = 2;
	public static final int SOUTH_UPSOUTH = 3;
	public static final int SOUTH_DOWNSOUTH = 4;
	
	public static final int NORTH_NORTHWEST = 0;
	public static final int NORTH_NORTH = 1;
	public static final int NORTH_NORTHEAST = 2;
	public static final int NORTH_UPNORTH = 3;
	public static final int NORTH_DOWNNORTH = 4;
	
	public static final int WEST_NORTHWEST = 0;
	public static final int WEST_WEST = 1;
	public static final int WEST_SOUTHWEST = 2;
	public static final int WEST_UPWEST = 3;
	public static final int WEST_DOWNWEST = 4;
	
	public static final int EAST_NORTHEAST = 0;
	public static final int EAST_EAST = 1;
	public static final int EAST_SOUTHEAST = 2;
	public static final int EAST_UPEAST = 3;
	public static final int EAST_DOWNEAST = 4;
	
	public static final int DOWN_DOWNEAST = 0;
	public static final int DOWN_DOWNNORTH = 1;
	public static final int DOWN_DOWNWEST = 2;
	public static final int DOWN_DOWNSOUTH = 3;
	public static final int DOWN_DOWN = 4;
	
	public static final int UP_UPEAST = 0;
	public static final int UP_UPNORTH = 1;
	public static final int UP_UPWEST = 2;
	public static final int UP_UPSOUTH = 3;
	public static final int UP_UP = 4;
}
