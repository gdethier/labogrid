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
import java.io.IOException;
import java.io.InputStreamReader;

import laboGrid.ConfigurationException;
import laboGrid.graphs.GenerationException;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.resource.ResourceGraph;



public abstract class WrapMapper implements GraphMapper {

	private static String appFile = "/tmp/appGraph.gra";
	private static String gridFile = "/tmp/gridGraph.gri";
	private static String resultFile;

	private String execPath;
	
	
	public WrapMapper() {
		File aFile = new File(appFile);
		File gFile = new File(gridFile);
		
		resultFile = "/tmp/" + aFile.getName() + "." + gFile.getName() + ".result";
	}

	@Override
	public GraphMapping map(ResourceGraph rGraph, ModelGraph mGraph)
			throws GenerationException {
		// Generate graph files
		try {
			generateAppGraphFile(appFile, mGraph);
		} catch (IOException e) {
			throw new GenerationException(e);
		}
		
		try {
			generateGridGraphFile(gridFile, mGraph, rGraph);
		} catch (IOException e) {
			throw new GenerationException(e);
		}
		
		// Call external mapper
		try {
			String callString = getCommand(execPath, appFile, gridFile, resultFile);
			Process p = Runtime.getRuntime().exec(callString);
			int exitCode = p.waitFor();
			if(exitCode != 0) {
				
				if(exitCode == 139)
					throw new GenerationException("segmentation fault");
				
				System.out.println("Mapper error:");
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = reader.readLine();
				while(line != null) {
					System.out.println(line);
					line = reader.readLine();
				}
				
				System.out.println("Mapper output:");
				reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				line = reader.readLine();
				while(line != null) {
					System.out.println(line);
					line = reader.readLine();
				}

				throw new GenerationException("Mapper process exited with error code "+exitCode);
			} else {
//				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//				String line = reader.readLine();
//				while(line != null) {
//					System.out.println(line);
//					line = reader.readLine();
//				}
			}

		} catch (IOException e) {
			throw new GenerationException(e);
		} catch (InterruptedException e) {
			throw new GenerationException(e);
		}

		try {
			GraphMapping map = parseResultFile(resultFile, mGraph, rGraph);
			return map;
		} catch (IOException e) {
			throw new GenerationException(e);
		}
	}

	@Override
	public void setParameters(String[] params) throws ConfigurationException {
		if(params.length != 1)
			throw new ConfigurationException("Wrong number of arguments: "+params.length);

		setParameters(params[0]);
	}
	
	public void setParameters(String execPath) {
		this.execPath = execPath;
	}
	
	public String getExecutablePath() {
		return execPath;
	}
	
	protected abstract String getCommand(String execFile, String appFile, String gridFile, String resultFile);
	protected abstract GraphMapping parseResultFile(String resultFileName, ModelGraph mGraph, ResourceGraph rGraph) throws IOException;
	protected abstract void generateGridGraphFile(String gridFileName, ModelGraph mGraph, ResourceGraph rGraph) throws IOException;
	protected abstract void generateAppGraphFile(String appFileName, ModelGraph mGraph) throws IOException;

}
