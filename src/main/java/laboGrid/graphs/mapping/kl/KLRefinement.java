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
package laboGrid.graphs.mapping.kl;

import java.util.TreeMap;

public class KLRefinement {

	private KLPart aPart, bPart;
	
	private double pGain;
	
	private class KLPair {
		int aIndex;
		int bIndex;
		public KLPair(int aIndex, int bIndex) {
			this.aIndex = aIndex;
			this.bIndex = bIndex;
		}
	}

	
	public KLRefinement() {
		this(1.);
	}
	
	public KLRefinement(double pGain) {
		this.pGain = pGain;
	}

	public void setPartitions(KLPart a, KLPart b) {
		aPart = a;
		bPart = b;
	}

	public double refinePartitions() {
		
		GExchangeArray g = new GExchangeArray(aPart, bPart);
		
		// Compute gains sequence
		int numOfGains = Math.min(aPart.size(), bPart.size());
		numOfGains = (int) (numOfGains * pGain);
		int[] gains = new int[numOfGains];
		for(int i = 0; i < numOfGains; ++i) {

			int start1 = g.getAFirstBoundaryIndex();
			int end1 = g.getALastBoundaryIndex();
			if(start1 == end1) {
				start1 = g.getAFirstAvailableIndex();
				end1 = g.getALastAvailableIndex();
			}
			
			int start2 = g.getBFirstBoundaryIndex();
			int end2 = g.getBLastBoundaryIndex();
			if(start2 == end2) {
				start2 = g.getBFirstAvailableIndex();
				end2 = g.getBLastAvailableIndex();
			}
			
			int maxGain = computeExchangeGain(g, start1, start2);
			int maxA = start1;
			int maxB = start2;
			for(int j = start1 + 1; j < end1; ++j) {
				for(int k = start2; k < end2; ++k) {
					int gain = computeExchangeGain(g, j, k);
					if(gain > maxGain) {
						maxGain = gain;
						maxA = j;
						maxB = k;
					}
				}
			}
			
//			System.out.println("exchangeGain="+maxGain);
			
			gains[i] = maxGain;
			g.addToAHandledSet(maxA);
			g.addToBHandledSet(maxB);
			
			g.updateACostsAndBoundary();
			g.updateBCostsAndBoundary();
			
//			System.out.println("G(A)");
//			ga.printConfiguration();
//			System.out.println("G(B)");
//			gb.printConfiguration();
//			System.out.println();
			
		}
		
		// Search for maximal gain sequence
		int maxGain = gains[0];
		int maxK = 1;
		int currentGain = maxGain;
		for(int k = 1; k < gains.length; ++k) {
			currentGain = currentGain + gains[k];
			if(currentGain > maxGain) {
				maxGain = currentGain;
				maxK = k + 1;
			}
		}
		
		if(maxGain <= 0)
			return 0;
		
		// Exchange vertices between partitions
		int[] X = g.getAFirstHandledVertices(maxK);
		int[] Y = g.getBFirstHandledVertices(maxK);
		
		Vertex[] xVert = aPart.remove(X);
		Vertex[] yVert = bPart.remove(Y);
		aPart.add(yVert);
		bPart.add(xVert);
		
		return maxGain;

	}

	private int computeExchangeGain(GArray g, int aInd, int bInd) {
		int bSubID = g.getSubID(bInd);
		int w = g.getWeight(aInd, bSubID);
		int diffCA = g.getDifCost(aInd);
		int diffCB = g.getDifCost(bInd);
		
		return diffCA + diffCB - 2*w;
	}
	
	private int computeMoveGain(GArray g, int index) {
		return g.getDifCost(index);
	}

	public void migrateFromAToB(int toMove) throws Exception {
		// Move vertices from A to B
		int[] X = getVertexToMigrateFromAToB(toMove);
		Vertex[] xVert = aPart.remove(X);
		bPart.add(xVert);
	}

	public int[] getVertexToMigrateFromAToB(int toMove) throws Exception {
		if(toMove > aPart.size())
			throw new Exception("Not enough nodes in partition");

		GUniDirArray g = new GUniDirArray(aPart, bPart);

		int[] X = new int[toMove];
		TreeMap<Integer, Integer> test = new TreeMap<Integer, Integer>();
		for(int i = 0; i < toMove; ++i) {

//			g.printConfiguration();
			int start = g.getFirstBoundaryIndex();
			int end = g.getLastBoundaryIndex();
			if(start == end) {
//				System.out.println("No boundary");
				start = g.getFirstAvailableIndex();
				end = g.getLastAvailableIndex();
			} else {
//				System.out.println("Boundary contains "+(end - start)+" entries.");
			}
			
			int maxGain = computeMoveGain(g, start);
			int maxInd = start;
			for(int j = start + 1; j < end; ++j) {
				int gain = computeMoveGain(g, j);
				if(gain > maxGain) {
					maxGain = gain;
					maxInd = j;
				}
			}
			
			X[i] = g.getSubID(maxInd);
			if(test.containsKey(X[i])) {
				throw new Error("subID "+X[i]+" already added. New index="+maxInd+", old index="+test.get(X[i]));
			} else {
				test.put(X[i], maxInd);
			}
			g.addToHandledSet(maxInd);
			
			g.updateCostsAndBoundary();

		}
		
		return X;
	}
	
}
