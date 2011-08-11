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
package laboGrid.graphs.mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.resource.ResourceGraph;
import laboGrid.lb.SubLattice;
import laboGrid.powerModel.PowerModel;

import dimawo.middleware.distributedAgent.DAId;



public class PaGridMapper extends WrapMapper {

	private static int ethernetLat = 120; // in µs
	private static int ethernetBW = 10; // in MB/s
	
	private String paGridResultFile;


	protected GraphMapping parseResultFile(String resultFile, ModelGraph mGraph, ResourceGraph rGraph) throws IOException {
		
		int nSubs = mGraph.getSubLatticesCount();
		int nDAs = rGraph.getDasCount();
		
		// result file is not used by pagrid !
		BufferedReader reader = new BufferedReader(new FileReader(paGridResultFile));
		int[] sub2DA = new int[nSubs];
		TreeSet<Integer>[] da2Sub = new TreeSet[nDAs];
		for(int i = 0; i < nDAs; ++i)
			da2Sub[i] = new TreeSet<Integer>();
		for(int i = 0; i < nSubs; ++i) {
			String line = reader.readLine();
			line = line.trim();
			
			if(line.equals(""))
				continue;

			int da = Integer.parseInt(line);
			sub2DA[i] = da;
			da2Sub[da].add(i);
		}
		reader.close();

		return new GraphMapping(da2Sub, sub2DA);
		
	}

	protected void generateGridGraphFile(String gridFile, ModelGraph mGraph, ResourceGraph rGraph) throws FileNotFoundException {

		DAId[] das = rGraph.getNewIds();
		int nVertices = das.length;
		int nEdges = nVertices * (nVertices - 1) / 2;

		PrintStream gridStream = new PrintStream(new FileOutputStream(gridFile));

		// First line: #vert. #edges
		gridStream.print(nVertices);
		gridStream.print(' ');
		gridStream.println(nEdges);
		
		// Second line: vi vj Rref (see PaGrid user's guide)
		int[] refSize = mGraph.getSubLattice(0).getSize();
		PowerModel[] powMod = rGraph.getDasPower(); 
		long[] powers = new long[powMod.length];
		for(int i = 0; i < powMod.length; ++i) {
			powers[i] = powMod[i].getPower(refSize);
		}
		long minPower = powers[0];
		for(int i = 0; i < powers.length; ++i) {
			if(powers[i] < minPower)
				minPower = powers[i];
		}
		
		SubLattice sub = mGraph.getSubLattice(0);
		long power = powers[0];
		double tComp = sub.getNumOfSites() / power; // time to process one sublattice
		long bw = rGraph.getBandwidth();
		double tComm = sub.getOutgoingBytes() / (double) bw;
		double Rref = tComm / tComp;
		
//		System.out.println("comBytes="+comBytes);
//		System.out.println("bw="+bw);
//		System.out.println("tComp="+tComp);
//		System.out.println("tComm="+tComm);
//		System.out.println("Rref="+Rref);
		
		gridStream.print("0 1 ");
		gridStream.println(Rref);

		String linkWStr = " 1 "+(ethernetLat * ethernetBW);
		for(int i = 0; i < das.length; ++i) {
			double p = powers[i];
			
			int wp = (int) Math.floor(p / minPower);
			
			gridStream.print(wp);
			gridStream.print(' ');
			for(int j = 0; j < das.length; ++j) {
				if(i != j) {
					// IDs
					gridStream.print(j);
					gridStream.print(' ');
					gridStream.print(linkWStr);
					gridStream.print(' ');
				}
			}

			if(i < das.length - 1)
				gridStream.println();
		}
		
		gridStream.close();
	}

	protected void generateAppGraphFile(String appFile, ModelGraph mGraph) throws FileNotFoundException {

		// Application graph construction
		SubLattice[] subs = mGraph.getSubLattices();
		int nVertices = subs.length;
		int nEdges = 0; // to be computed
		
		// Counting edges
		for(int i = 0; i < subs.length; ++i) {
			SubLattice sub = subs[i];
			for(int j = 0; j < sub.getNeighborsCount(); ++j) {
				int neighId = sub.getNeighborFromIndex(j);
				if(neighId > i && neighId != i) {
					++nEdges;
				} else { // else the edge has already been counted
					assert subs[neighId].hasNeighbor(i);
				}
			}
		}
		
		// Generating application graph file
		PrintStream appStream = new PrintStream(new FileOutputStream(appFile));

		appStream.print(nVertices);
		appStream.print(' ');
		appStream.println(nEdges);

		for(int i = 0; i < subs.length; ++i) {
			appStream.print(' '); // white space as in examples
			SubLattice sub = subs[i];
			for(int j = 0; j < sub.getNeighborsCount(); ++j) {
				int neighId = sub.getNeighborFromIndex(j);
				
				// vertices ids start from 1 !
				appStream.print(neighId + 1);
				
				if(j < sub.getNeighborsCount() - 1)
					appStream.print(' ');
			}
			
			if(i < subs.length - 1)
				appStream.println();
		}
		
		appStream.close();
	}

	@Override
	protected String getCommand(String execPath, String appFile,
			String gridFile, String resultFile) {
		File aFile = new File(appFile);
		File gFile = new File(gridFile);
		paGridResultFile = aFile.getName() + "." + gFile.getName() + ".result";

		return execPath + "/pagrid " + appFile + " " + gridFile;
	}

}
