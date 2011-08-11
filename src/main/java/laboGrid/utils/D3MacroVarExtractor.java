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

import java.io.File;
import java.io.IOException;

import laboGrid.graphs.model.d3.D3BlockModelGraph;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.impl.common.simulation.algorithm.LBState;
import laboGrid.impl.common.simulation.algorithm.LBState.ContentType;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.lb.lattice.d3.D3AccumulatedSpeedsSlice;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.lattice.d3.D3LatticeMacroVarsSlice;
import laboGrid.lb.solid.d3.D3Solid;
import laboGrid.lb.solid.d3.D3SolidSlice;



public class D3MacroVarExtractor {

	public static D3AggregatedSlice[] getXYCompleteSlice(int zFrom, int zTo,
			String path, int iteration,
			D3BlockModelGraph aGraph,
			ContentType compressedFiles) throws IOException, ClassNotFoundException, LBException {
		SubLattice[][][] subsArray = aGraph.getSublatticesArray();

		int saXSize = subsArray.length;
		int saYSize = subsArray[0].length;
		int saZSize = subsArray[0][0].length;
		
		D3AggregatedSlice[] toReturn = new D3AggregatedSlice[zTo - zFrom];
		for(int i = 0; i < toReturn.length; ++i) {
			toReturn[i] = new D3AggregatedSlice(saXSize, saYSize);
		}

		for(int saZ = 0; saZ < saZSize; ++saZ) {
			int subMinZPos = subsArray[0][0][saZ].getMinPoint()[2];
			int subMaxZPos = subsArray[0][0][saZ].getMaxPoint()[2];
			int subZSize = subMaxZPos - subMinZPos;

			// Check if slides may be extracted from sublattices
			// of current subsArray slice
			if(subMinZPos > zTo || subMaxZPos < zFrom)
				continue;

			// Select portion of sublattices to exploit
			int inSubsZFrom;
			if(subMinZPos >= zFrom) {
				inSubsZFrom = 0;
			} else { // subMinZPos < zFrom
				inSubsZFrom = zFrom - subMinZPos;
			}

			int inSubsZTo;
			if(subMaxZPos >= zTo) {
				inSubsZTo = zTo - subMinZPos;
			} else { // subMaxZPos < zTo
				inSubsZTo = subZSize;
			}

			for(int saX = 0; saX < saXSize; ++saX) {
				for(int saY = 0; saY < saYSize; ++saY) {
					SubLattice nodeInfo = subsArray[saX][saY][saZ];

					File file = new File(path+"/"+LBSimThread.getStateFileUID(nodeInfo.getId(), iteration));
					LBState state = new LBState();
					state.readState(file, compressedFiles);

					D3Lattice d = (D3Lattice) state.getLattice();
					D3Solid solid = (D3Solid) state.getSolid();

					for(int z = inSubsZFrom; z < inSubsZTo; ++z) {
						D3LatticeMacroVarsSlice macroVarsSlice = d.getXYMacroVars(z);
						D3SolidSlice solidSlice = solid.getXYBooleans(z);
						D3AccumulatedSpeedsSlice accSlice = d.getXYAccumulators(z);

						toReturn[z + subMinZPos - zFrom].setMacroVars(saX, saY, macroVarsSlice);
						toReturn[z + subMinZPos - zFrom].setSolids(saX, saY, solidSlice);
						toReturn[z + subMinZPos - zFrom].setAccumulatedSpeeds(saX, saY, accSlice);
					}
				}
			}
		}
		
		return toReturn;
	}

	public static D3AggregatedSlice[] getXZCompleteSlice(int yFrom, int yTo,
			String path, int iteration,
			D3BlockModelGraph aGraph,
			ContentType compressedFiles) throws IOException, ClassNotFoundException, LBException {
		SubLattice[][][] subsArray = aGraph.getSublatticesArray();

		int saXSize = subsArray.length;
		int saYSize = subsArray[0].length;
		int saZSize = subsArray[0][0].length;
		
		D3AggregatedSlice[] toReturn = new D3AggregatedSlice[yTo - yFrom];
		for(int i = 0; i < toReturn.length; ++i) {
			toReturn[i] = new D3AggregatedSlice(saXSize, saZSize);
		}

		for(int saY = 0; saY < saYSize; ++saY) {
			int subMinYPos = subsArray[0][saY][0].getMinPoint()[1];
			int subMaxYPos = subsArray[0][saY][0].getMaxPoint()[1];
			int subYSize = subMaxYPos - subMinYPos;

			// Check if slides may be extracted from sublattices
			// of current subsArray slice
			if(subMinYPos > yTo || subMaxYPos < yFrom)
				continue;

			// Select portion of sublattices to exploit
			int inSubsYFrom;
			if(subMinYPos >= yFrom) {
				inSubsYFrom = 0;
			} else { // subMinYPos < yFrom
				inSubsYFrom = yFrom - subMinYPos;
			}

			int inSubsYTo;
			if(subMaxYPos >= yTo) {
				inSubsYTo = yTo - subMinYPos;
			} else { // subMaxYPos < yTo
				inSubsYTo = subYSize;
			}

			for(int saX = 0; saX < saXSize; ++saX) {
				for(int saZ = 0; saZ < saZSize; ++saZ) {
					SubLattice nodeInfo = subsArray[saX][saY][saZ];

					File file = new File(path+"/"+LBSimThread.getStateFileUID(nodeInfo.getId(), iteration));
					LBState state = new LBState();
					state.readState(file, compressedFiles);

					D3Lattice d = (D3Lattice) state.getLattice();
					D3Solid solid = (D3Solid) state.getSolid();

					for(int y = inSubsYFrom; y < inSubsYTo; ++y) {
						D3LatticeMacroVarsSlice macroVarsSlice = d.getXZMacroVars(y);
						D3SolidSlice solidSlice = solid.getXZBooleans(y);
						D3AccumulatedSpeedsSlice accSlice = d.getXZAccumulators(y);

						toReturn[y + subMinYPos - yFrom].setMacroVars(saX, saZ, macroVarsSlice);
						toReturn[y + subMinYPos - yFrom].setSolids(saX, saZ, solidSlice);
						toReturn[y + subMinYPos - yFrom].setAccumulatedSpeeds(saX, saZ, accSlice);
					}
				}
			}
		}
		
		return toReturn;
	}
	
	public static D3AggregatedSlice[] getYZCompleteSlice(int xFrom, int xTo,
			String path, int iteration,
			D3BlockModelGraph aGraph,
			ContentType compressedFiles) throws IOException, ClassNotFoundException, LBException {
		SubLattice[][][] subsArray = aGraph.getSublatticesArray();

		int saXSize = subsArray.length;
		int saYSize = subsArray[0].length;
		int saZSize = subsArray[0][0].length;
		
		D3AggregatedSlice[] toReturn = new D3AggregatedSlice[xTo - xFrom];
		for(int i = 0; i < toReturn.length; ++i) {
			toReturn[i] = new D3AggregatedSlice(saYSize, saZSize);
		}

		for(int saX = 0; saX < saXSize; ++saX) {
			int subMinXPos = subsArray[saX][0][0].getMinPoint()[0];
			int subMaxXPos = subsArray[saX][0][0].getMaxPoint()[0];
			int subXSize = subMaxXPos - subMinXPos;

			// Check if slides may be extracted from sublattices
			// of current subsArray slice
			if(subMinXPos > xTo || subMaxXPos < xFrom)
				continue;

			// Select portion of sublattices to exploit
			int inSubsXFrom;
			if(subMinXPos >= xFrom) {
				inSubsXFrom = 0;
			} else { // subMinXPos < xFrom
				inSubsXFrom = xFrom - subMinXPos;
			}

			int inSubsXTo;
			if(subMaxXPos >= xTo) {
				inSubsXTo = xTo - subMinXPos;
			} else { // subMaxXPos < xTo
				inSubsXTo = subXSize;
			}

			for(int saY = 0; saY < saYSize; ++saY) {
				for(int saZ = 0; saZ < saZSize; ++saZ) {
					SubLattice nodeInfo = subsArray[saX][saY][saZ];

					File file = new File(path+"/"+LBSimThread.getStateFileUID(nodeInfo.getId(), iteration));
					LBState state = new LBState();
					state.readState(file, compressedFiles);

					D3Lattice d = (D3Lattice) state.getLattice();
					D3Solid solid = (D3Solid) state.getSolid();

					for(int x = inSubsXFrom; x < inSubsXTo; ++x) {
						D3LatticeMacroVarsSlice macroVarsSlice = d.getYZMacroVars(x);
						D3SolidSlice solidSlice = solid.getYZBooleans(x);
						D3AccumulatedSpeedsSlice accSlice = d.getYZAccumulators(x);

						toReturn[x + subMinXPos - xFrom].setMacroVars(saY, saZ, macroVarsSlice);
						toReturn[x + subMinXPos - xFrom].setSolids(saY, saZ, solidSlice);
						toReturn[x + subMinXPos - xFrom].setAccumulatedSpeeds(saY, saZ, accSlice);
					}
				}
			}
		}
		
		return toReturn;
	}

}
