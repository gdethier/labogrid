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
package laboGrid.impl.common.benchmark;

import java.util.Iterator;
import java.util.LinkedList;

import laboGrid.configuration.middleware.BenchmarkConfiguration;
import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.solid.Solid;
import laboGrid.math.IntegerVector;
import laboGrid.powerModel.PowerModel;
import laboGrid.procChain.ProcessingChain;
import laboGrid.procChain.ProcessingChainElement;

import dimawo.Reflection;


public class LBBenchmark implements Runnable {
	
	/**
	 * Benchmark thread. One thread is instantiated
	 * per CPU.
	 * 
	 * @author Gérard Dethier
	 */
	private class BenchThread extends Thread {
		
		private LBSimThread alg;
		
		private Throwable throwable;
		
		public BenchThread(LBSimThread alg) {
			super("BenchThread");
			this.alg = alg;
		}
		
		public void run() {

			try {
				alg.benchmark();
			} catch (Throwable t) {
				throwable = t;
			}

		}
		
		public Throwable getThrowable() {
			return throwable;
		}
		
	}
	
	private BenchmarkHost task;
	protected BenchmarkParameters benchParam;
	private BenchmarkConfiguration benchConf;
	private int currentBench;
	private int numOfBench;
	
	protected Solid[] solid;
	protected Lattice[] fluid;
	protected ProcessingChain[] pc;
	protected LBSimThread[] alg;
	private BenchThread[] threads;
	
	private int cpus;
	
	private PowerModel power;
	private LinkedList<Throwable> errors;
	
	private Thread benchThread;

	/**
	 * Constructor
	 * 
	 * @param params Benchmark parameters.
	 * 
	 * @throws LBException
	 */
	public LBBenchmark(BenchmarkHost task, BenchmarkParameters params) throws LBException {
		
		this.task = task;
		benchParam = params;
		benchConf = params.getBenchmarkConfiguration();
		numOfBench = benchConf.getNumOfBenchmarks();

		cpus = Runtime.getRuntime().availableProcessors();
		
		power = new PowerModel();
		
		solid = new Solid[cpus];
		fluid = new Lattice[cpus];
		pc = new ProcessingChain[cpus];
		
		errors = new LinkedList<Throwable>();

	}


	private void prepareBenchThreads() throws LBException {
		
		int[] refSize = benchConf.getSize(currentBench);
		
		try {
			
			for(int i = 0; i < cpus; ++i) {
				fluid[i] = (Lattice) Reflection.newInstance(benchParam.getLatticeClass());
				fluid[i].setSize(refSize);
				fluid[i].setEquilibrium();
			}

		} catch (Exception e) {
			throw new LBException("Could not instantiate lattice of class "+benchParam.getLatticeClass()+": "+e.toString());
		}

		try {

			for(int i = 0; i < cpus; ++i) {
				solid[i] = (Solid) Reflection.newInstance(benchParam.getSolidClass());
				solid[i].setSize(refSize);
				solid[i].setFluid();
			}

		} catch (Exception e) {
			throw new LBException("Could not instantiate lattice of class "+benchParam.getLatticeClass()+": "+e.getMessage());
		}
		
		try {
			
			for(int i = 0; i < cpus; ++i) {

				pc[i] = benchParam.getProcessingChainCopy();

			}

		} catch (Exception e) {
			throw new LBException("Could not obtain processing chain: "+e.getClass()+"->"+e.getMessage());
		}

		alg = new LBSimThread[cpus];
		for(int i = 0; i < alg.length; ++i) {

			alg[i] = new LBSimThread(fluid[i], solid[i], pc[i], benchConf.getIterations(currentBench));

			alg[i].addConsumer(0, alg[i]);
			
			Iterator<ProcessingChainElement> it = pc[i].iterator();
			while(it.hasNext()) {
				ProcessingChainElement pce = it.next();
				pce.setLBAlgorithm(alg[i]);
			}

		}
	}

	
	////////////////////
	// Public methods //
	////////////////////
	
	public PowerModel getPower() {
		return power;
	}
	
	public LinkedList<Throwable> getErrors() {
		
		return errors;
		
	}
	
	
	/////////////////////////////
	// Runnable implementation //
	/////////////////////////////
	
	private long execCurrentBench() {
		// Benchmark threads allocation
		BenchThread[] threads = new BenchThread[cpus];
		for(int i = 0; i < cpus; ++i) {
			threads[i] = new BenchThread(alg[i]);
		}

		// Launching benchmark
		long t1 = System.currentTimeMillis();
		for(int i = 0; i < cpus; ++i) {
			threads[i].start();
		}
		
		try {
			for(int i = 0; i < cpus; ++i) {
				threads[i].join();
				if(threads[i].getThrowable() != null) {
					for(int j = 0; j < cpus; ++j) {
						threads[j].interrupt();
					}
					return -1;
				}
			}
		} catch(InterruptedException e) {
			// Forward interruption to benchmark threads.
			for(int i = 0; i < cpus; ++i) {
				threads[i].interrupt();
			}
			return -1;
		}

		return System.currentTimeMillis() - t1;
	}

	@Override
	public void run() {

		benchmarkPrintMessage("Starting benchmark.");
		
		for(currentBench = 0; currentBench < numOfBench; ++currentBench) {
			try {
				prepareBenchThreads();
			} catch (LBException e) {
				signalError(e);
				break;
			}
			long benchDuration = execCurrentBench();
			if(benchDuration == -1) {
				signalErrors();
				break;
			}
			
			benchmarkPrintMessage("LB benchmark execution took "+benchDuration+" ms.");
			double iterations = benchConf.getIterations(currentBench);
			int[] size = benchConf.getSize(currentBench);
			int benchSites = IntegerVector.mult(size);
			long p = (long) (1000 * (benchSites*iterations/benchDuration) * Runtime.getRuntime().availableProcessors()); // in sites/s
			benchmarkPrintMessage("-> power="+p+" for size "+IntegerVector.toString(size));

			power.setPower(size, p);
		}
		

		try {
			task.signalBenchmarkFinished();
		} catch (InterruptedException e) {
			benchmarkPrintMessage("Benchmark was interrupted.");
		}

	}
	
	
	/////////////////////
	// Private methods //
	/////////////////////

//	private SubLattice getSubLattice(Lattice fluid, int[] size) {
//
//		LatticeDescriptor ld = fluid.getLatticeDescriptor();
//		int siteDegree = ld.getVelocitiesCount();
//		SubLattice sub = new SubLattice(ld);
//		sub.setId(0);
//		sub.setSize(size);
//		int[] position = new int[size.length];
//		Arrays.fill(position, 0);
//		sub.setPosition(position);
//		boolean[] boundary = new boolean[size.length];
//		Arrays.fill(boundary, true);
//		sub.setFromBoundary(boundary);
//		sub.setToBoundary(boundary);
//		
//		int[] neighbors = new int[siteDegree];
//		Arrays.fill(neighbors, 0);
//		sub.setNeighbors(neighbors);
//
//		return sub;
//
//	}

	private void signalError(LBException e) {
		power = null;
		errors.add(e);
	}


	private void signalErrors() {
		if(threads == null) {
			errors.add(new Exception("No thread instantiated"));
			return;
		}
			
		for(int i = 0; i < cpus; ++i) {
			if(threads[i] != null) {
				Throwable t = threads[i].getThrowable();
				if(t != null) {
					errors.add(t);
				}
			}
		}
	}


	private void benchmarkPrintMessage(String txt) {
		
		task.printMessage("[LBBenchmark] " + txt);
		
	}


	public String getPowerDescriptor() {

		return benchParam.getPowerDescriptor();

	}


	public void join() throws InterruptedException {
		
		benchThread.join();
		
	}


	public void kill() {
		
		benchThread.interrupt();
		
	}

	public void start() {

		benchThread = new Thread(this, "LBBenchmark");
		benchThread.start();

	}


}
