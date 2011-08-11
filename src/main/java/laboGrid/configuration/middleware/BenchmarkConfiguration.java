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
package laboGrid.configuration.middleware;

import java.io.Serializable;

import laboGrid.ConfigurationException;
import laboGrid.math.IntegerVector;
import laboGrid.math.VectorWrongFormatException;

import org.w3c.dom.Element;



/**
 * Description of the benchmark used to estimate the power of a Resource. This description
 * contains a number of simulation iterations and a lattice size.
 * 
 * This class is associated to XML element <code>Benchmark</code>, part of the
 * <code>LoadBalancing</code> element, part of the <code>LaBoGridMiddleware</code>
 * element (see LaBoGridConfiguration.xsd).
 * 
 * @author Gérard Dethier
 *
 */
public class BenchmarkConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Simulation iterations. */
	private int[] iterations;
	
	/** Lattice sizes to create power model */
	private int[][] sizes;

	
	/**
	 * Constructor.
	 * 
	 * @param iterations Simulation iterations.
	 * @param size Lattice size.
	 */
	public BenchmarkConfiguration(int[] iterations, int[][] sizes) {
		this.iterations = iterations;
		this.sizes = sizes;
	}

	/**
	 * Instantiates a BenchmarkConfiguration object based on
	 * a <code>Benchmark</code> XML element.
	 * 
	 * @param e A <code>Benchmark</code> XML element.
	 * 
	 * @return A BenchmarkConfiguration object.
	 * 
	 * @throws VectorWrongFormatException If the lattice size is not given
	 * in a proper format.
	 * @throws ConfigurationException 
	 */
	public static BenchmarkConfiguration newInstance(Element e) throws VectorWrongFormatException, ConfigurationException {

		String[] params = e.getAttribute("iterations").split("[ \t\n]+");
		int[] iterations = new int[params.length];
		for(int i = 0; i < iterations.length; ++i) {
			iterations[i] = Integer.parseInt(params[i]);
		}
		params = e.getAttribute("refSizes").split("[ \t\n]+");
		int[][] sizes = new int[params.length][];
		for(int i = 0; i < sizes.length; ++i) {
			sizes[i] = IntegerVector.parseIntegerVector(params[i]);
		}
		
		if(iterations.length != sizes.length)
			throw new ConfigurationException("The number of iterations and of sizes are not equal");
		
		return new BenchmarkConfiguration(iterations, sizes);

	}


	public int getNumOfBenchmarks() {
		return iterations.length;
	}
	
	public int getIterations(int i) {
		return iterations[i];
	}

	public int[] getSize(int i) {
		return sizes[i];
	}
}
