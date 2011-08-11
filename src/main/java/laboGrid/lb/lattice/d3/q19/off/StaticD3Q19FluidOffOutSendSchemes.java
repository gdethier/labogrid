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

import laboGrid.lb.lattice.BorderData;
import laboGrid.lb.lattice.d3.q19.D3Q19BorderPlaneConstants;
import laboGrid.lb.lattice.d3.q19.D3Q19LatticeDescriptor;


/**
 * Fluid data are always accessed velocity by velocity in order
 * to ensure data locality.
 * 
 * @author dethier
 *
 */
public class StaticD3Q19FluidOffOutSendSchemes {

	public static BorderData getOutcomingDensities(
			D3Q19BorderExtraction fluid, int link,
			int xSize, int ySize, int zSize) {

		if(link == D3Q19LatticeDescriptor.SOUTH) {

			double[][] out = new double[5][];

			out[D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST] =
				new double[xSize * zSize];
			fluid.extractXZPlane(D3Q19LatticeDescriptor.SOUTHWEST, 0,
					out[D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST]);
			
			out[D3Q19BorderPlaneConstants.SOUTH_SOUTH] =
				new double[xSize * zSize];
			fluid.extractXZPlane(D3Q19LatticeDescriptor.SOUTH, 0,
					out[D3Q19BorderPlaneConstants.SOUTH_SOUTH]);
			
			out[D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST] =
				new double[xSize * zSize];
			fluid.extractXZPlane(D3Q19LatticeDescriptor.SOUTHEAST, 0,
					out[D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST]);
			
			out[D3Q19BorderPlaneConstants.SOUTH_UPSOUTH] =
				new double[xSize * zSize];
			fluid.extractXZPlane(D3Q19LatticeDescriptor.UPSOUTH, 0,
					out[D3Q19BorderPlaneConstants.SOUTH_UPSOUTH]);
			
			out[D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH] =
				new double[xSize * zSize];
			fluid.extractXZPlane(D3Q19LatticeDescriptor.DOWNSOUTH, 0,
					out[D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH]);
			
			return new D3Q19OffBorderPlane(link, out);

		} else if(link == D3Q19LatticeDescriptor.NORTH) {
			
			double[][] out = new double[5][];
			
			out[D3Q19BorderPlaneConstants.NORTH_NORTHWEST] =
				new double[xSize * zSize];
			fluid.extractXZPlane(D3Q19LatticeDescriptor.NORTHWEST, ySize-1,
					out[D3Q19BorderPlaneConstants.NORTH_NORTHWEST]);
			
			out[D3Q19BorderPlaneConstants.NORTH_NORTH] =
				new double[xSize * zSize];
			fluid.extractXZPlane(D3Q19LatticeDescriptor.NORTH, ySize-1,
					out[D3Q19BorderPlaneConstants.NORTH_NORTH]);
			
			out[D3Q19BorderPlaneConstants.NORTH_NORTHEAST] =
				new double[xSize * zSize];
			fluid.extractXZPlane(D3Q19LatticeDescriptor.NORTHEAST, ySize-1,
					out[D3Q19BorderPlaneConstants.NORTH_NORTHEAST]);
			
			out[D3Q19BorderPlaneConstants.NORTH_UPNORTH] =
				new double[xSize * zSize];
			fluid.extractXZPlane(D3Q19LatticeDescriptor.UPNORTH, ySize-1,
					out[D3Q19BorderPlaneConstants.NORTH_UPNORTH]);
			
			out[D3Q19BorderPlaneConstants.NORTH_DOWNNORTH] =
				new double[xSize * zSize];
			fluid.extractXZPlane(D3Q19LatticeDescriptor.DOWNNORTH, ySize-1,
					out[D3Q19BorderPlaneConstants.NORTH_DOWNNORTH]);
			
			return new D3Q19OffBorderPlane(link, out);

		} else if(link == D3Q19LatticeDescriptor.WEST) {
			
			double[][] out = new double[5][];
			
			out[D3Q19BorderPlaneConstants.WEST_NORTHWEST] =
				new double[ySize * zSize];
			fluid.extractYZPlane(D3Q19LatticeDescriptor.NORTHWEST, 0,
					out[D3Q19BorderPlaneConstants.WEST_NORTHWEST]);
			
			out[D3Q19BorderPlaneConstants.WEST_WEST] =
				new double[ySize * zSize];
			fluid.extractYZPlane(D3Q19LatticeDescriptor.WEST, 0,
					out[D3Q19BorderPlaneConstants.WEST_WEST]);
			
			out[D3Q19BorderPlaneConstants.WEST_SOUTHWEST] =
				new double[ySize * zSize];
			fluid.extractYZPlane(D3Q19LatticeDescriptor.SOUTHWEST, 0,
					out[D3Q19BorderPlaneConstants.WEST_SOUTHWEST]);
			
			out[D3Q19BorderPlaneConstants.WEST_UPWEST] =
				new double[ySize * zSize];
			fluid.extractYZPlane(D3Q19LatticeDescriptor.UPWEST, 0,
					out[D3Q19BorderPlaneConstants.WEST_UPWEST]);
			
			out[D3Q19BorderPlaneConstants.WEST_DOWNWEST] =
				new double[ySize * zSize];
			fluid.extractYZPlane(D3Q19LatticeDescriptor.DOWNWEST, 0,
					out[D3Q19BorderPlaneConstants.WEST_DOWNWEST]);
			
			return new D3Q19OffBorderPlane(link, out);

		} else if(link == D3Q19LatticeDescriptor.EAST) {
			
			double[][] out = new double[5][];
			
			out[D3Q19BorderPlaneConstants.EAST_NORTHEAST] =
				new double[ySize * zSize];
			fluid.extractYZPlane(D3Q19LatticeDescriptor.NORTHEAST, xSize-1,
					out[D3Q19BorderPlaneConstants.EAST_NORTHEAST]);
			
			out[D3Q19BorderPlaneConstants.EAST_EAST] =
				new double[ySize * zSize];
			fluid.extractYZPlane(D3Q19LatticeDescriptor.EAST, xSize-1,
					out[D3Q19BorderPlaneConstants.EAST_EAST]);
			
			out[D3Q19BorderPlaneConstants.EAST_SOUTHEAST] =
				new double[ySize * zSize];
			fluid.extractYZPlane(D3Q19LatticeDescriptor.SOUTHEAST, xSize-1,
					out[D3Q19BorderPlaneConstants.EAST_SOUTHEAST]);
			
			out[D3Q19BorderPlaneConstants.EAST_UPEAST] =
				new double[ySize * zSize];
			fluid.extractYZPlane(D3Q19LatticeDescriptor.UPEAST, xSize-1,
					out[D3Q19BorderPlaneConstants.EAST_UPEAST]);
			
			out[D3Q19BorderPlaneConstants.EAST_DOWNEAST] =
				new double[ySize * zSize];
			fluid.extractYZPlane(D3Q19LatticeDescriptor.DOWNEAST, xSize-1,
					out[D3Q19BorderPlaneConstants.EAST_DOWNEAST]);
			
			return new D3Q19OffBorderPlane(link, out);

		} else if(link == D3Q19LatticeDescriptor.DOWN) {
			
			double[][] out = new double[5][];
			
			out[D3Q19BorderPlaneConstants.DOWN_DOWNEAST] =
				new double[xSize * ySize];
			fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNEAST, 0,
					out[D3Q19BorderPlaneConstants.DOWN_DOWNEAST]);
			
			out[D3Q19BorderPlaneConstants.DOWN_DOWNNORTH] =
				new double[xSize * ySize];
			fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNNORTH, 0,
					out[D3Q19BorderPlaneConstants.DOWN_DOWNNORTH]);
			
			out[D3Q19BorderPlaneConstants.DOWN_DOWNWEST] =
				new double[xSize * ySize];
			fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNWEST, 0,
					out[D3Q19BorderPlaneConstants.DOWN_DOWNWEST]);
			
			out[D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH] =
				new double[xSize * ySize];
			fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNSOUTH, 0,
					out[D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH]);
			
			out[D3Q19BorderPlaneConstants.DOWN_DOWN] =
				new double[xSize * ySize];
			fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWN, 0,
					out[D3Q19BorderPlaneConstants.DOWN_DOWN]);
			
			return new D3Q19OffBorderPlane(link, out);


		} else if(link == D3Q19LatticeDescriptor.UP) {
			
			double[][] out = new double[5][];
			
			out[D3Q19BorderPlaneConstants.UP_UPEAST] =
				new double[xSize * ySize];
			fluid.extractXYPlane(D3Q19LatticeDescriptor.UPEAST, zSize - 1,
					out[D3Q19BorderPlaneConstants.UP_UPEAST]);
			
			out[D3Q19BorderPlaneConstants.UP_UPNORTH] =
				new double[xSize * ySize];
			fluid.extractXYPlane(D3Q19LatticeDescriptor.UPNORTH, zSize - 1,
					out[D3Q19BorderPlaneConstants.UP_UPNORTH]);
			
			out[D3Q19BorderPlaneConstants.UP_UPWEST] =
				new double[xSize * ySize];
			fluid.extractXYPlane(D3Q19LatticeDescriptor.UPWEST, zSize - 1,
					out[D3Q19BorderPlaneConstants.UP_UPWEST]);
			
			out[D3Q19BorderPlaneConstants.UP_UPSOUTH] =
				new double[xSize * ySize];
			fluid.extractXYPlane(D3Q19LatticeDescriptor.UPSOUTH, zSize - 1,
					out[D3Q19BorderPlaneConstants.UP_UPSOUTH]);
			
			out[D3Q19BorderPlaneConstants.UP_UP] =
				new double[xSize * ySize];
			fluid.extractXYPlane(D3Q19LatticeDescriptor.UP, zSize - 1,
					out[D3Q19BorderPlaneConstants.UP_UP]);
			
			return new D3Q19OffBorderPlane(link, out);

		} else if(link == D3Q19LatticeDescriptor.DOWNNORTH) {
			
			double[] out = new double[xSize];
			fluid.extractXLine(D3Q19LatticeDescriptor.DOWNNORTH, ySize - 1, 0,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.DOWNSOUTH) {
			
			double[] out = new double[xSize];
			fluid.extractXLine(D3Q19LatticeDescriptor.DOWNSOUTH, 0, 0,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.DOWNWEST) {
			
			double[] out = new double[ySize];
			fluid.extractYLine(D3Q19LatticeDescriptor.DOWNWEST, 0, 0,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.DOWNEAST) {
			
			double[] out = new double[ySize];
			fluid.extractYLine(D3Q19LatticeDescriptor.DOWNEAST, xSize-1, 0,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.UPSOUTH) {
			
			double[] out = new double[xSize];
			fluid.extractXLine(D3Q19LatticeDescriptor.UPSOUTH, 0, zSize-1,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.UPNORTH) {
			
			double[] out = new double[xSize];
			fluid.extractXLine(D3Q19LatticeDescriptor.UPNORTH, ySize-1, zSize-1,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.UPWEST) {
			
			double[] out = new double[ySize];
			fluid.extractYLine(D3Q19LatticeDescriptor.UPWEST, 0, zSize - 1,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.UPEAST) {
			
			double[] out = new double[ySize];
			fluid.extractYLine(D3Q19LatticeDescriptor.UPEAST, xSize - 1, zSize - 1,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.SOUTHWEST) {
			
			double[] out = new double[zSize];
			fluid.extractZLine(D3Q19LatticeDescriptor.SOUTHWEST, 0, 0,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.NORTHWEST) {
			
			double[] out = new double[zSize];
			fluid.extractZLine(D3Q19LatticeDescriptor.NORTHWEST, 0, ySize - 1,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.SOUTHEAST) {
			
			double[] out = new double[zSize];
			fluid.extractZLine(D3Q19LatticeDescriptor.SOUTHEAST, xSize - 1, 0,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		} else if(link == D3Q19LatticeDescriptor.NORTHEAST) {
			
			double[] out = new double[zSize];
			fluid.extractZLine(D3Q19LatticeDescriptor.NORTHEAST, xSize - 1, ySize - 1,
					out);
			
			return new D3Q19OffBorderLine(link, out);
			
		}
		
		return null;
		
	}


	public static BorderData getOutcomingDensities(D3Q19BorderExtraction fluid,
			int link, int xSize, int ySize, int zSize,
			BorderData[] border) {
		
		D3Q19OffBorderPlane plane;
		D3Q19OffBorderLine line;

		switch(link) {
		
		case D3Q19LatticeDescriptor.SOUTH :
			
			plane =
				(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.SOUTH];
			
			fluid.extractXZPlane(D3Q19LatticeDescriptor.SOUTHWEST, 0,
					plane.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST));

			fluid.extractXZPlane(D3Q19LatticeDescriptor.SOUTH, 0,
					plane.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTH));
			
			fluid.extractXZPlane(D3Q19LatticeDescriptor.SOUTHEAST, 0,
					plane.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST));
			
			fluid.extractXZPlane(D3Q19LatticeDescriptor.UPSOUTH, 0,
					plane.getData(D3Q19BorderPlaneConstants.SOUTH_UPSOUTH));
			
			fluid.extractXZPlane(D3Q19LatticeDescriptor.DOWNSOUTH, 0,
					plane.getData(D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH));
			
			return plane;

		case D3Q19LatticeDescriptor.NORTH :
			
			plane =
				(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.NORTH];
			
			fluid.extractXZPlane(D3Q19LatticeDescriptor.NORTHWEST, ySize-1,
					plane.getData(D3Q19BorderPlaneConstants.NORTH_NORTHWEST));
			
			fluid.extractXZPlane(D3Q19LatticeDescriptor.NORTH, ySize-1,
					plane.getData(D3Q19BorderPlaneConstants.NORTH_NORTH));
			
			fluid.extractXZPlane(D3Q19LatticeDescriptor.NORTHEAST, ySize-1,
					plane.getData(D3Q19BorderPlaneConstants.NORTH_NORTHEAST));
			
			fluid.extractXZPlane(D3Q19LatticeDescriptor.UPNORTH, ySize-1,
					plane.getData(D3Q19BorderPlaneConstants.NORTH_UPNORTH));
			
			fluid.extractXZPlane(D3Q19LatticeDescriptor.DOWNNORTH, ySize-1,
					plane.getData(D3Q19BorderPlaneConstants.NORTH_DOWNNORTH));
			
			return plane;

		case D3Q19LatticeDescriptor.WEST :
			
			plane =
				(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.WEST];
			
			fluid.extractYZPlane(D3Q19LatticeDescriptor.NORTHWEST, 0,
					plane.getData(D3Q19BorderPlaneConstants.WEST_NORTHWEST));
			
			fluid.extractYZPlane(D3Q19LatticeDescriptor.WEST, 0,
					plane.getData(D3Q19BorderPlaneConstants.WEST_WEST));
			
			fluid.extractYZPlane(D3Q19LatticeDescriptor.SOUTHWEST, 0,
					plane.getData(D3Q19BorderPlaneConstants.WEST_SOUTHWEST));
			
			fluid.extractYZPlane(D3Q19LatticeDescriptor.UPWEST, 0,
					plane.getData(D3Q19BorderPlaneConstants.WEST_UPWEST));
			
			fluid.extractYZPlane(D3Q19LatticeDescriptor.DOWNWEST, 0,
					plane.getData(D3Q19BorderPlaneConstants.WEST_DOWNWEST));
			
			return plane;

		case D3Q19LatticeDescriptor.EAST :
			
			plane =
				(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.EAST];
			
			fluid.extractYZPlane(D3Q19LatticeDescriptor.NORTHEAST, xSize-1,
					plane.getData(D3Q19BorderPlaneConstants.EAST_NORTHEAST));
			
			fluid.extractYZPlane(D3Q19LatticeDescriptor.EAST, xSize-1,
					plane.getData(D3Q19BorderPlaneConstants.EAST_EAST));
			
			fluid.extractYZPlane(D3Q19LatticeDescriptor.SOUTHEAST, xSize-1,
					plane.getData(D3Q19BorderPlaneConstants.EAST_SOUTHEAST));
			
			fluid.extractYZPlane(D3Q19LatticeDescriptor.UPEAST, xSize-1,
					plane.getData(D3Q19BorderPlaneConstants.EAST_UPEAST));
			
			fluid.extractYZPlane(D3Q19LatticeDescriptor.DOWNEAST, xSize-1,
					plane.getData(D3Q19BorderPlaneConstants.EAST_DOWNEAST));
			
			return plane;

		case D3Q19LatticeDescriptor.DOWN :
			
			plane =
				(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.DOWN];
			
			fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNEAST, 0,
					plane.getData(D3Q19BorderPlaneConstants.DOWN_DOWNEAST));
			
			fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNNORTH, 0,
					plane.getData(D3Q19BorderPlaneConstants.DOWN_DOWNNORTH));
			
			fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNWEST, 0,
					plane.getData(D3Q19BorderPlaneConstants.DOWN_DOWNWEST));
			
			fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNSOUTH, 0,
					plane.getData(D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH));
			
			fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWN, 0,
					plane.getData(D3Q19BorderPlaneConstants.DOWN_DOWN));
			
			return plane;


		case D3Q19LatticeDescriptor.UP :
			
			plane =
				(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.UP];
			
			fluid.extractXYPlane(D3Q19LatticeDescriptor.UPEAST, zSize - 1,
					plane.getData(D3Q19BorderPlaneConstants.UP_UPEAST));
			
			fluid.extractXYPlane(D3Q19LatticeDescriptor.UPNORTH, zSize - 1,
					plane.getData(D3Q19BorderPlaneConstants.UP_UPNORTH));
			
			fluid.extractXYPlane(D3Q19LatticeDescriptor.UPWEST, zSize - 1,
					plane.getData(D3Q19BorderPlaneConstants.UP_UPWEST));
			
			fluid.extractXYPlane(D3Q19LatticeDescriptor.UPSOUTH, zSize - 1,
					plane.getData(D3Q19BorderPlaneConstants.UP_UPSOUTH));
			
			fluid.extractXYPlane(D3Q19LatticeDescriptor.UP, zSize - 1,
					plane.getData(D3Q19BorderPlaneConstants.UP_UP));
			
			return plane;

		case D3Q19LatticeDescriptor.DOWNNORTH :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.DOWNNORTH];
			
			fluid.extractXLine(D3Q19LatticeDescriptor.DOWNNORTH, ySize - 1, 0,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.DOWNSOUTH :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.DOWNSOUTH];
			
			fluid.extractXLine(D3Q19LatticeDescriptor.DOWNSOUTH, 0, 0,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.DOWNWEST :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.DOWNWEST];
			
			fluid.extractYLine(D3Q19LatticeDescriptor.DOWNWEST, 0, 0,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.DOWNEAST :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.DOWNEAST];
			
			fluid.extractYLine(D3Q19LatticeDescriptor.DOWNEAST, xSize-1, 0,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.UPSOUTH :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.UPSOUTH];
			
			fluid.extractXLine(D3Q19LatticeDescriptor.UPSOUTH, 0, zSize-1,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.UPNORTH :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.UPNORTH];
			
			fluid.extractXLine(D3Q19LatticeDescriptor.UPNORTH, ySize-1, zSize-1,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.UPWEST :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.UPWEST];
			
			fluid.extractYLine(D3Q19LatticeDescriptor.UPWEST, 0, zSize - 1,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.UPEAST :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.UPEAST];
			
			fluid.extractYLine(D3Q19LatticeDescriptor.UPEAST, xSize - 1, zSize - 1,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.SOUTHWEST :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.SOUTHWEST];
			
			fluid.extractZLine(D3Q19LatticeDescriptor.SOUTHWEST, 0, 0,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.NORTHWEST :

			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.NORTHWEST];
			
			fluid.extractZLine(D3Q19LatticeDescriptor.NORTHWEST, 0, ySize - 1,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.SOUTHEAST :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.SOUTHEAST];
			
			fluid.extractZLine(D3Q19LatticeDescriptor.SOUTHEAST, xSize - 1, 0,
					line.getData());
			
			return line;
			
		case D3Q19LatticeDescriptor.NORTHEAST :
			
			line =
				(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.NORTHEAST];
			
			fluid.extractZLine(D3Q19LatticeDescriptor.NORTHEAST, xSize - 1, ySize - 1,
					line.getData());
			
			return line;
			
		default :
			return null;
		
		}

	}
	
	
	public static void fillBuffers(D3Q19BorderExtraction fluid,
			int xSize, int ySize, int zSize,
			BorderData[] border) {
		
		D3Q19OffBorderPlane plane;
		D3Q19OffBorderLine line;

		plane =
			(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.SOUTH];
		
		fluid.extractXZPlane(D3Q19LatticeDescriptor.SOUTHWEST, 0,
				plane.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTHWEST));

		fluid.extractXZPlane(D3Q19LatticeDescriptor.SOUTH, 0,
				plane.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTH));
		
		fluid.extractXZPlane(D3Q19LatticeDescriptor.SOUTHEAST, 0,
				plane.getData(D3Q19BorderPlaneConstants.SOUTH_SOUTHEAST));
		
		fluid.extractXZPlane(D3Q19LatticeDescriptor.UPSOUTH, 0,
				plane.getData(D3Q19BorderPlaneConstants.SOUTH_UPSOUTH));
		
		fluid.extractXZPlane(D3Q19LatticeDescriptor.DOWNSOUTH, 0,
				plane.getData(D3Q19BorderPlaneConstants.SOUTH_DOWNSOUTH));
		
		
		plane =
			(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.NORTH];
		
		fluid.extractXZPlane(D3Q19LatticeDescriptor.NORTHWEST, ySize-1,
				plane.getData(D3Q19BorderPlaneConstants.NORTH_NORTHWEST));
		
		fluid.extractXZPlane(D3Q19LatticeDescriptor.NORTH, ySize-1,
				plane.getData(D3Q19BorderPlaneConstants.NORTH_NORTH));
		
		fluid.extractXZPlane(D3Q19LatticeDescriptor.NORTHEAST, ySize-1,
				plane.getData(D3Q19BorderPlaneConstants.NORTH_NORTHEAST));
		
		fluid.extractXZPlane(D3Q19LatticeDescriptor.UPNORTH, ySize-1,
				plane.getData(D3Q19BorderPlaneConstants.NORTH_UPNORTH));
		
		fluid.extractXZPlane(D3Q19LatticeDescriptor.DOWNNORTH, ySize-1,
				plane.getData(D3Q19BorderPlaneConstants.NORTH_DOWNNORTH));
		
		
		plane =
			(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.WEST];
		
		fluid.extractYZPlane(D3Q19LatticeDescriptor.NORTHWEST, 0,
				plane.getData(D3Q19BorderPlaneConstants.WEST_NORTHWEST));
		
		fluid.extractYZPlane(D3Q19LatticeDescriptor.WEST, 0,
				plane.getData(D3Q19BorderPlaneConstants.WEST_WEST));
		
		fluid.extractYZPlane(D3Q19LatticeDescriptor.SOUTHWEST, 0,
				plane.getData(D3Q19BorderPlaneConstants.WEST_SOUTHWEST));
		
		fluid.extractYZPlane(D3Q19LatticeDescriptor.UPWEST, 0,
				plane.getData(D3Q19BorderPlaneConstants.WEST_UPWEST));
		
		fluid.extractYZPlane(D3Q19LatticeDescriptor.DOWNWEST, 0,
				plane.getData(D3Q19BorderPlaneConstants.WEST_DOWNWEST));
		
		
		plane =
			(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.EAST];
		
		fluid.extractYZPlane(D3Q19LatticeDescriptor.NORTHEAST, xSize-1,
				plane.getData(D3Q19BorderPlaneConstants.EAST_NORTHEAST));
		
		fluid.extractYZPlane(D3Q19LatticeDescriptor.EAST, xSize-1,
				plane.getData(D3Q19BorderPlaneConstants.EAST_EAST));
		
		fluid.extractYZPlane(D3Q19LatticeDescriptor.SOUTHEAST, xSize-1,
				plane.getData(D3Q19BorderPlaneConstants.EAST_SOUTHEAST));
		
		fluid.extractYZPlane(D3Q19LatticeDescriptor.UPEAST, xSize-1,
				plane.getData(D3Q19BorderPlaneConstants.EAST_UPEAST));
		
		fluid.extractYZPlane(D3Q19LatticeDescriptor.DOWNEAST, xSize-1,
				plane.getData(D3Q19BorderPlaneConstants.EAST_DOWNEAST));
		
		
		plane =
			(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.DOWN];
		
		fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNEAST, 0,
				plane.getData(D3Q19BorderPlaneConstants.DOWN_DOWNEAST));
		
		fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNNORTH, 0,
				plane.getData(D3Q19BorderPlaneConstants.DOWN_DOWNNORTH));
		
		fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNWEST, 0,
				plane.getData(D3Q19BorderPlaneConstants.DOWN_DOWNWEST));
		
		fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWNSOUTH, 0,
				plane.getData(D3Q19BorderPlaneConstants.DOWN_DOWNSOUTH));
		
		fluid.extractXYPlane(D3Q19LatticeDescriptor.DOWN, 0,
				plane.getData(D3Q19BorderPlaneConstants.DOWN_DOWN));
		
		
		plane =
			(D3Q19OffBorderPlane) border[D3Q19LatticeDescriptor.UP];
		
		fluid.extractXYPlane(D3Q19LatticeDescriptor.UPEAST, zSize - 1,
				plane.getData(D3Q19BorderPlaneConstants.UP_UPEAST));
		
		fluid.extractXYPlane(D3Q19LatticeDescriptor.UPNORTH, zSize - 1,
				plane.getData(D3Q19BorderPlaneConstants.UP_UPNORTH));
		
		fluid.extractXYPlane(D3Q19LatticeDescriptor.UPWEST, zSize - 1,
				plane.getData(D3Q19BorderPlaneConstants.UP_UPWEST));
		
		fluid.extractXYPlane(D3Q19LatticeDescriptor.UPSOUTH, zSize - 1,
				plane.getData(D3Q19BorderPlaneConstants.UP_UPSOUTH));
		
		fluid.extractXYPlane(D3Q19LatticeDescriptor.UP, zSize - 1,
				plane.getData(D3Q19BorderPlaneConstants.UP_UP));
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.DOWNNORTH];
		
		fluid.extractXLine(D3Q19LatticeDescriptor.DOWNNORTH, ySize - 1, 0,
				line.getData());
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.DOWNSOUTH];
		
		fluid.extractXLine(D3Q19LatticeDescriptor.DOWNSOUTH, 0, 0,
				line.getData());
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.DOWNWEST];
		
		fluid.extractYLine(D3Q19LatticeDescriptor.DOWNWEST, 0, 0,
				line.getData());
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.DOWNEAST];
		
		fluid.extractYLine(D3Q19LatticeDescriptor.DOWNEAST, xSize-1, 0,
				line.getData());
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.UPSOUTH];
		
		fluid.extractXLine(D3Q19LatticeDescriptor.UPSOUTH, 0, zSize-1,
				line.getData());
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.UPNORTH];
		
		fluid.extractXLine(D3Q19LatticeDescriptor.UPNORTH, ySize-1, zSize-1,
				line.getData());
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.UPWEST];
		
		fluid.extractYLine(D3Q19LatticeDescriptor.UPWEST, 0, zSize - 1,
				line.getData());
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.UPEAST];
		
		fluid.extractYLine(D3Q19LatticeDescriptor.UPEAST, xSize - 1, zSize - 1,
				line.getData());
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.SOUTHWEST];
		
		fluid.extractZLine(D3Q19LatticeDescriptor.SOUTHWEST, 0, 0,
				line.getData());
		

		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.NORTHWEST];
		
		fluid.extractZLine(D3Q19LatticeDescriptor.NORTHWEST, 0, ySize - 1,
				line.getData());
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.SOUTHEAST];
		
		fluid.extractZLine(D3Q19LatticeDescriptor.SOUTHEAST, xSize - 1, 0,
				line.getData());
		
		
		line =
			(D3Q19OffBorderLine) border[D3Q19LatticeDescriptor.NORTHEAST];
		
		fluid.extractZLine(D3Q19LatticeDescriptor.NORTHEAST, xSize - 1, ySize - 1,
				line.getData());
			
	}
	
}
