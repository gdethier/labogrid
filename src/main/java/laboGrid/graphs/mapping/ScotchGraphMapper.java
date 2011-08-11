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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.TreeSet;

import laboGrid.ConfigurationException;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.resource.ResourceGraph;
import laboGrid.lb.SubLattice;



public class ScotchGraphMapper extends WrapMapper {
	
	private String tmpFile = "/tmp/sourceGridFile.gra";
	private boolean weightGridGraph;

	@Override
	protected void generateAppGraphFile(String appFileName, ModelGraph mGraph)
			throws IOException {
		
		// Application graph construction
		SubLattice[] subs = mGraph.getSubLattices();
		int nVertices = subs.length;
		
		int nEdges = 0; // to be computed
		
		// Counting edges
		// WARNING : Scotch works with directed graphs -> twice the number
		// of arcs in the undirected graph !
		for(int i = 0; i < subs.length; ++i) {
			SubLattice sub = subs[i];
			for(int j = 0; j < sub.getNeighborsCount(); ++j) {
//				int neighId = sub.getNeighborFromIndex(j);
//				if(neighId > i && neighId != i) {
					++nEdges;
//				} else { // else the edge has already been counted
//					assert subs[neighId].hasNeighbor(i);
//				}
			}
		}
		
		// Generating application graph file
		PrintStream appStream = new PrintStream(new FileOutputStream(appFileName));

		appStream.println("0");
		
		appStream.print(nVertices);
		appStream.print(' ');
		appStream.println(nEdges);
		
		appStream.print("0 "); // base numbering, 0 in our case
		appStream.print("0"); // 0 = No vertex label
		appStream.print("1"); // 0 = No edge weight
		appStream.println("0"); // 0 = No vertex weight

		for(int i = 0; i < subs.length; ++i) {
			appStream.print(' '); // white space as in examples
			SubLattice sub = subs[i];
			int numOfNeigh = sub.getNeighborsCount();
			appStream.print(numOfNeigh);
			appStream.print(' ');
			
			for(int j = 0; j < numOfNeigh; ++j) {
				int neighId = sub.getNeighborFromIndex(j);
				int weight = sub.getEdgeWeight(j);
				
				appStream.print(weight);
				appStream.print(' ');
				appStream.print(neighId);
				
				if(j < sub.getNeighborsCount() - 1)
					appStream.print(' ');
			}
			
			if(i < subs.length - 1)
				appStream.println();
		}
		
		appStream.close();
	}

	@Override
	protected void generateGridGraphFile(String gridFileName,
			ModelGraph mGraph, ResourceGraph rGraph) throws IOException {
		// Grid graph construction
		int[] refSize = mGraph.getSubLattice(0).getSize();
		int nVertices = rGraph.getDasCount();
		// WARNING : Scotch works with directed graphs -> twice the number
		// of arcs in the undirected graph !
		int nEdges = (nVertices*(nVertices - 1)); // Complete graph
		
		// Generating application graph file
		PrintStream gridStream = new PrintStream(new FileOutputStream(tmpFile));
		
		gridStream.println("0");

		gridStream.print(nVertices);
		gridStream.print(' ');
		gridStream.println(nEdges);
		
		gridStream.print("0 "); // base numbering, 0 in our case

		gridStream.print("0"); // 0 = No vertex label
		gridStream.print("0"); // 0 = No edge weight
		if(weightGridGraph)
			gridStream.println("1");
		else
			gridStream.println("0");

		for(int i = 0; i < nVertices; ++i) {
//			gridStream.print(' '); // white space as in examples
			
			if(weightGridGraph) {
				long power = rGraph.getPower(i, refSize);
				gridStream.print(power);
				gridStream.print(' ');
			}

			int numOfNeigh = nVertices - 1;
			gridStream.print(numOfNeigh);
			gridStream.print(' ');
			
			for(int j = 0; j < nVertices; ++j) {
				if(i != j) {
					gridStream.print(j);
					gridStream.print(' ');
				}
			}
			
			gridStream.println();
		}
		gridStream.println();
		
		gridStream.close();
		
		
		// Generate the decomposition-defined architecture file
		generateDDAFile(tmpFile, gridFileName);
	}

	private void generateDDAFile(String graphFile, String destFile) throws IOException {
		String execPath = getExecutablePath();
		
		String callString = execPath + "/amk_grf "+graphFile+" "+destFile;
		Process p = Runtime.getRuntime().exec(callString);
		int exitCode;
		try {
			exitCode = p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}

		if(exitCode != 0) {
			if(exitCode == 139)
				throw new IOException("amk_grf segmentation fault");
			
			System.out.println("amk_grf error:");
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line = reader.readLine();
			while(line != null) {
				System.out.println(line);
				line = reader.readLine();
			}
			
			System.out.println("amk_grf output:");
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			line = reader.readLine();
			while(line != null) {
				System.out.println(line);
				line = reader.readLine();
			}

			throw new IOException("amk_grf process exited with error code "+exitCode);
		}
	}

	@Override
	protected GraphMapping parseResultFile(String resultFileName,
			ModelGraph mGraph, ResourceGraph rGraph) throws IOException {
		int nSubs = mGraph.getSubLatticesCount();
		int nDAs = rGraph.getDasCount();
		
		int[] sub2DA = new int[nSubs];
		TreeSet<Integer>[] da2Sub = new TreeSet[nDAs];
		for(int i = 0; i < nDAs; ++i)
			da2Sub[i] = new TreeSet<Integer>();


		Scanner scan = new Scanner(new File(resultFileName));

		// First line contains the number of lines in the file
		int nLines = Integer.parseInt(scan.next());
		if(nLines != nSubs)
			throw new IOException("The number of lines does not correspond to the number of sublattices");

		for(int i = 0; i < nSubs; ++i) {
			int subID = Integer.parseInt(scan.next());
			int daID = Integer.parseInt(scan.next());
			sub2DA[subID] = daID;
			da2Sub[daID].add(subID);
		}
		
		return new GraphMapping(da2Sub, sub2DA);
	}

	@Override
	protected String getCommand(String execPath, String appFile,
			String gridFile, String resultFile) {
		return execPath + "/gmap " + appFile + " " + gridFile + " " + resultFile;
	}
	
	@Override
	public void setParameters(String[] args) throws ConfigurationException {
		if(args.length != 2) {
			throw new ConfigurationException("Usage: <execPath> <weightGridFile>");
		}
		
		String execPath = args[0];
		boolean weightGridGraph = Boolean.parseBoolean(args[1]);
		
		setParameters(execPath, weightGridGraph);
	}

	public void setParameters(String execPath, boolean weightGridGraph) {
		super.setParameters(execPath);
		
		this.weightGridGraph = weightGridGraph;
	}
	

}
