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
package laboGrid.graphs.replication;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import laboGrid.graphs.mapping.GraphMapping;



import org.apache.commons.math.stat.descriptive.moment.Variance;

import dimawo.middleware.distributedAgent.DAId;

public class ReplicationGraphHeuristicGenerator implements ReplicationGraphGenerator {

	public ReplicationGraphHeuristicGenerator() {
		
	}
	
	public ReplicationGraph computeReplicationGraph(DAId[] das, 
			GraphMapping cGraph, int backupDegree) {
		
		// Creating Peers dynamic structure
		TreeMap<String, Set<Integer>> dynPeers = new TreeMap<String, Set<Integer>>();
		Set<Integer>[] da2Sub = cGraph.getDa2Sub();
		for(int i = 0; i < das.length; ++i) {
			DAId c = das[i];
			if(da2Sub[i] != null && ! da2Sub[i].isEmpty()) { // Da can be taken into account in backup graph
//				String resourcePeer = c.getPeerId();
				String resourcePeer = "peerId";
				Set<Integer> resources = dynPeers.get(resourcePeer);
				if(resources == null) {
					System.out.println("Detected Peer: "+resourcePeer+".");
					resources = new TreeSet<Integer>();
					dynPeers.put(resourcePeer, resources);
				}
				resources.add(i);
			}
		}
		
		if(dynPeers.size() == 1) {
			
			ReplicationGraphNaiveGenerator naiveGen = new ReplicationGraphNaiveGenerator();
			return naiveGen.computeReplicationGraph(das, cGraph, backupDegree);

		} else {

			// Convert dynamic structure into a static one
			Set<Integer>[] peers = new TreeSet[dynPeers.size()];
			Iterator<Entry<String, Set<Integer>>> it = dynPeers.entrySet().iterator();
			for(int i = 0; i < peers.length; ++i) {
				Entry<String, Set<Integer>> e = it.next();
				peers[i] = e.getValue();
			}

			return new ReplicationGraph(replicationGraph(das.length, backupDegree, peers));
		}
		
	}

	public Set<Integer>[] replicationGraph(int daCount, int backupDegree, Set<Integer>[] peers) {
		int[] peerSize = new int[peers.length];
		for(int i = 0; i < peers.length; ++i) {
			peerSize[i] = peers[i].size();
//			System.out.println("Peer "+i+" DAs: "+peerSize[i]);
		}
		
		int[][] edges = calculateEdgesMatrix(peerSize, backupDegree);
		for(int i = 0; i < peerSize.length; ++i) {
			for(int j = 0; j < peerSize.length; ++j) {
				System.out.print(edges[i][j]+" ");
			}
			System.out.println();
		}
		
		Set<Integer>[] peerBackupGraph = fromMatrixToGraph(peerSize.length, edges);
		for(int i = 0; i < peerSize.length; ++i) {
			System.out.println("Peer "+i+" neighbours ("+peerBackupGraph[i].size()+"):");
			Iterator<Integer> it = peerBackupGraph[i].iterator();
			while(it.hasNext()) {
				System.out.println("	- "+it.next());
			}
		}

		Set<Integer>[] daBackup = calculateDaReplicationGraph(daCount, edges,
				peerBackupGraph, peers, backupDegree);
		for(int i = 0; i < daBackup.length; ++i) {
			System.out.println("DA "+i+" neighbours ("+daBackup[i].size()+"):");
			Iterator<Integer> it = daBackup[i].iterator();
			while(it.hasNext()) {
				System.out.println("	- "+it.next());
			}
		}
		
		return daBackup;
	}
	
	protected static Set<Integer>[] fromMatrixToGraph(int peerCount, int[][] edges) {
		Set<Integer>[] peerBackupGraph = new TreeSet[peerCount];
		for(int j = 0; j < peerCount; ++j) {
			for(int i = 0; i < peerCount; ++i) {
				Set<Integer> neighbours = peerBackupGraph[i];
				if(neighbours == null) {
					neighbours = new TreeSet<Integer>();
					peerBackupGraph[i] = neighbours;
				}
				
				if(edges[i][j] > 0) {
					neighbours.add(j);
				}
			}
		}
		return peerBackupGraph;
	}
	
	protected static int[][] calculateEdgesMatrix(int[] peerSize, int backupDegree) {
		int[][] toReturn = initEdgesMatrix(peerSize, backupDegree);
		if(peerSize.length > 1) {
			balanceMatrix(toReturn, peerSize, 1.0E-7);
		}
		System.out.println("Mean incoming edges per Da:");
		for(int j = 0; j < toReturn.length; ++j) {
			int in = 0;
			for(int i = 0; i < toReturn.length; ++i) {
				in += toReturn[i][j];
			}
			System.out.print(in/peerSize[j]+" ");
		}
		System.out.println();
		return toReturn;
	}

	protected static void balanceMatrix(int[][] edges, int[] peerSize, double stopCriterion) {
		double[] normIn = new double[peerSize.length];
		for(int i = 0; i < peerSize.length; ++i) {
			for(int j = 0; j < peerSize.length; ++j) {
				normIn[j] += edges[i][j];
			}
		}
		for(int j = 0; j < peerSize.length; ++j) {
			normIn[j] /= peerSize[j];
		}
		Variance v = new Variance();
		double var1 = v.evaluate(normIn);
		System.out.println("Initial variance on DA incoming edges is "+var1);
		double var2 = 0;

		int k = 0;
		int maxJ = 1, minJ = 1;
		boolean updateVar1 = false;
		while((var1 - var2) >= stopCriterion) {

			// First line (to ignore element (0,0))
			double maxNorm = normIn[1], minNorm = normIn[1];
			maxJ = 1;
			minJ = 1;
			for(int j = 1; j < peerSize.length; ++j) {
				if(normIn[j] > maxNorm) {
					maxJ = j;
					maxNorm = normIn[j];
				}

				if(normIn[j] < minNorm) {
					minJ = j;
					minNorm = normIn[j];
				}
			}
			
			if(minJ != maxJ) {
				balanceRow(0, edges, peerSize, minJ, maxJ, normIn);
			}

			// Other lines
			for(int i = 1; i < peerSize.length; ++i) {
				maxNorm = normIn[0];
				minNorm = normIn[0];
				maxJ = 0;
				minJ = 0;
				for(int j = 0; j < peerSize.length; ++j) {
					if(normIn[j] > maxNorm && i != j) {
						maxJ = j;
						maxNorm = normIn[j];
					}

					if(normIn[j] < minNorm && i != j) {
						minJ = j;
						minNorm = normIn[j];
					}
				}
				
				if(minJ != maxJ) {
					balanceRow(i, edges, peerSize, minJ, maxJ, normIn);
				}
			}
			
			double tmpVar = v.evaluate(normIn);
			if(updateVar1)
				var1 = var2;
			else
				updateVar1 = true;
			var2 = tmpVar;
			
			System.out.println("After pass "+k+", variance on DA incoming edges is "+var2+" (vs. "+var1+")");
			++k;

		}
	}

	private static boolean balanceRow(int i, int[][] edges, int[] peerSize, int minJ, int maxJ,
			double[] normIn) {

		double alpha = normIn[maxJ]-normIn[minJ];
		double beta = (1./peerSize[minJ])+(1./peerSize[maxJ]);

		double delta = Math.round(Math.min(alpha/beta, edges[i][maxJ]));

		if(delta != 0) {
			normIn[maxJ] -= delta/peerSize[maxJ];
			normIn[minJ] += delta/peerSize[minJ];
			edges[i][maxJ] -= delta;
			edges[i][minJ] += delta;
			return true;
		}

		return false;
	}

	protected static int[][] initEdgesMatrix(int[] peerSize, int backupDegree) {
		int[][] toReturn = new int[peerSize.length][peerSize.length];
		if(peerSize.length == 1) {
			toReturn[0][0] = backupDegree * peerSize[0];
		} else {
			int peerCount = peerSize.length - 1; // other peers count
			for(int i = 0; i < peerSize.length; ++i) {
				int addedSlots = 0;
				int value = backupDegree * peerSize[i] / peerCount;
				int rest = (backupDegree * peerSize[i]) - (value * peerCount);
				for(int j = 0; j < peerSize.length; ++j) {
					if(i == j) {
						toReturn[i][j] = 0;
					} else if(rest == 0) {
						toReturn[i][j] = value;
						addedSlots += value;
					} else {
						toReturn[i][j] = value + 1;
						addedSlots += (value + 1);
						--rest;
					}
				}
				assert rest == 0 : rest;
				assert addedSlots == peerSize[i] * backupDegree : (peerSize[i] * backupDegree)-addedSlots;
			}
		}
		return toReturn;
	}
	
	protected static Set<Integer>[] calculateDaReplicationGraph(int daCount, int[][] edges,
			Set<Integer>[] peerBackupGraph, Set<Integer>[] peers, int backupDegree) {
		
		assert peers.length > 1;
		assert daCount > 0 : "There must be at least 1 DA in the grid!";
		
		Set<Integer>[] toReturn = new Set[daCount];
		for(int daId = 0; daId < daCount; ++daId) {
			toReturn[daId] = new TreeSet<Integer>();
		}

		Iterator<Integer>[] neighDaIts = new Iterator[peers.length];
		for(int currentPeer = 0; currentPeer < peers.length; ++currentPeer) {
			neighDaIts[currentPeer] = peers[currentPeer].iterator();
		}

		for(int currentPeer = 0; currentPeer < peers.length; ++currentPeer) {
			Iterator<Integer> currentDaIt = peers[currentPeer].iterator();
			while(currentDaIt.hasNext()) {
				Iterator<Integer> currentPeerNeighIt = peerBackupGraph[currentPeer].iterator();
				LinkedList<Integer> peerNeighToRemove = new LinkedList<Integer>();
				int currentDa = currentDaIt.next();
				for(int i = 0; i < backupDegree; ++i) {
					int currentPeerNeigh;
					if(currentPeerNeighIt.hasNext()) {
						currentPeerNeigh = currentPeerNeighIt.next();
					} else {
						currentPeerNeighIt = peerBackupGraph[currentPeer].iterator();
						currentPeerNeigh = currentPeerNeighIt.next();
					}

					/**
					 * The following loop terminates always by definition of
					 * the edges array:
					 * 	\sum_i edges[i][j] = backupDegree \times nDa[i]
					 */
					while(edges[currentPeer][currentPeerNeigh] == 0) {
						if(currentPeerNeighIt.hasNext()) {
							currentPeerNeigh = currentPeerNeighIt.next();
						} else {
							currentPeerNeighIt = peerBackupGraph[currentPeer].iterator();
							currentPeerNeigh = currentPeerNeighIt.next();
						}
					}

					// edges[currentPeer][currentPeerNeigh] > 0
					int currentDaNeigh;
					if(neighDaIts[currentPeerNeigh].hasNext()) {
						currentDaNeigh = neighDaIts[currentPeerNeigh].next();
					} else {
						neighDaIts[currentPeerNeigh] = peers[currentPeerNeigh].iterator();
						currentDaNeigh = neighDaIts[currentPeerNeigh].next();
					}

					toReturn[currentDa].add(currentDaNeigh);
					--edges[currentPeer][currentPeerNeigh];
					if(edges[currentPeer][currentPeerNeigh] == 0) {
						peerNeighToRemove.add(currentPeerNeigh);
					}
				}

				peerBackupGraph[currentPeer].removeAll(peerNeighToRemove);
			}
		}



		return toReturn;

	}
}
