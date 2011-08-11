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
package laboGrid.graphs.mapping.mtwa;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import laboGrid.ConfigurationException;
import laboGrid.graphs.GenerationException;
import laboGrid.graphs.mapping.GraphMapper;
import laboGrid.graphs.mapping.GraphMapping;
import laboGrid.graphs.mapping.kl.KLPart;
import laboGrid.graphs.mapping.kl.KLRefinement;
import laboGrid.graphs.mapping.kl.Vertex;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.resource.ResourceGraph;
import laboGrid.lb.SubLattice;



public class ModifiedTWAMapper implements GraphMapper {

	private GraphMapping initialMapping;
	private boolean useDiffusion = false;
	private double difAtt, relPrec;
	private int diffusionRounds = 0;
	
	public ModifiedTWAMapper() {
	}
	
	public ModifiedTWAMapper(boolean useDiffusion, double difAtt, double relPrec) {
		this.useDiffusion = useDiffusion;
		this.difAtt = difAtt;
		this.relPrec = relPrec;
	}
	
	public ModifiedTWAMapper(GraphMapping initialMapping) {
		this.initialMapping = initialMapping;
	}
	
	public ModifiedTWAMapper(GraphMapping initialMapping, boolean useDiffusion, double difAtt, double relPrec) {
		this.initialMapping = initialMapping;
		this.useDiffusion = useDiffusion;
		this.difAtt = difAtt;
		this.relPrec = relPrec;
	}


	@Override
	public GraphMapping map(ResourceGraph rGraph, ModelGraph mGraph)
			throws GenerationException {
		ComputerTree ct = ComputerTree.newInstance(rGraph);
//		ct.print(System.out);
		
		// Set initial mapping if not given
		int numOfSubs = mGraph.getSubLatticesCount();
		int numOfDAs = rGraph.getDasCount();
		if(initialMapping == null) {
			// All model graph nodes are associated to root DA
			CTNode root = ct.getRoot();
			int index = root.getDaIndex();
			
			TreeSet<Integer>[] da2Sub = new TreeSet[numOfDAs];
			int[] sub2Da = new int[numOfSubs];
			TreeSet<Integer> set = new TreeSet<Integer>();
			da2Sub[index] = set;
			for(int i = 0; i < numOfSubs; ++i) {
				set.add(i);
			}
			
			Arrays.fill(sub2Da, index);
			
			initialMapping = new GraphMapping(da2Sub, sub2Da);
		}
		
		// Label CT
		int[] refSize = mGraph.getSubLattice(0).getSize();
		ct.labelNodes(refSize, rGraph, initialMapping);
		if(useDiffusion) {
			diffusionRounds = ct.scheduleMigrationsDiffusion(difAtt, relPrec);
//			System.out.println("rounds="+diffusionRounds);
//			System.out.println("difAvgLoad="+ct.getAverageLoad());
//			System.out.println("twaAvgLoad="+ct.getRoot().getAverageLoad());
		} else
			ct.scheduleMigrationsTWA();
		
		// Create global vertices array
		Vertex[] vert = new Vertex[numOfSubs];
		for(int i = 0; i < vert.length; ++i) {
			Vertex v = new Vertex(i);
			vert[i] = v;
			SubLattice sub = mGraph.getSubLattice(i);
			int numOfNeigh = sub.getNeighborsCount();
			int[] adj = new int[numOfNeigh];
			sub.getNeighbors(adj);
			int[] weights = new int[numOfNeigh];
			sub.getWeights(weights);
			
			v.setAdjacencyList(adj);
			v.setWeights(weights);
		}
		
		// Migrate sublattices
		Set<Integer>[] initDa2Sub = initialMapping.getDa2Sub();
		Vertex[][] parts = new Vertex[numOfDAs][];
		for(int i = 0; i < parts.length; ++i) {
			Set<Integer> initSet = initDa2Sub[i];
			if(initSet != null) {
				Vertex[] subs = new Vertex[initSet.size()];
				parts[i] = subs;
				int k = 0;
				for(Iterator<Integer> it = initSet.iterator(); it.hasNext();) {
					int subID = it.next();
					subs[k] = vert[subID];
					++k;
				}
			} else {
				Vertex[] subs = new Vertex[0];
				parts[i] = subs;
			}
		}

		LinkedList<CTNode> sources = new LinkedList<CTNode>();
		ct.listSources(sources);
		while( ! sources.isEmpty()) {
			
			for(Iterator<CTNode> it = sources.iterator(); it.hasNext();) {
				CTNode n = it.next();
				int srcIndex = n.getDaIndex();
				
//				System.out.println("correctMigrations");
				n.correctMigrations();
				
				// Migrate from source to parent
				int toParent = n.getSubsToSendToParent();
				if(toParent > 0) {
					CTNode parent = n.getParent();
					int parentIndex = parent.getDaIndex();
					
					Vertex[] srcPart = parts[srcIndex];
					Vertex[] parentPart = parts[parentIndex];
					
//					int srcQuota = (int) Math.ceil(n.getQuota());
//					int maxNodes = Math.min(toParent, srcPart.length - srcQuota);
					int maxNodes = toParent;
					
					if(maxNodes > 0) {
						
//						System.out.println("Moving "+maxNodes+" subs from "+srcIndex+" to "+parentIndex);

						KLPart a = new KLPart(srcPart);
						KLPart b = new KLPart(parentPart);

						KLRefinement ref = new KLRefinement();
						ref.setPartitions(a, b);
						try {
							ref.migrateFromAToB(maxNodes);
						} catch (Exception e) {
							throw new GenerationException(e);
						}

						parts[srcIndex] = a.getVertices();
						parts[parentIndex] = b.getVertices();
					
					}

					n.setSubsToSendToParent(0);
					n.remSubs(maxNodes);
					int childIndex = parent.getChildIndex(n.getDaIndex());
					parent.setSubsToRecvFromChild(childIndex, 0);
					parent.addSubs(maxNodes);
				}
				
				// Migrate from source to children
				int nChildren = n.getNumOfChildren();
				for(int i = 0; i < nChildren; ++i) {
					int toChild = n.getSubsToSendToChild(i);
					if(toChild > 0) {
						CTNode child = n.getChild(i);
						int childIndex = child.getDaIndex();

						Vertex[] srcPart = parts[srcIndex];
						Vertex[] childPart = parts[childIndex];
						
//						int srcQuota = (int) Math.ceil(n.getQuota());
//						int maxNodes = Math.min(toChild, srcPart.length - srcQuota);
						int maxNodes = toChild;
						
						if(maxNodes > 0) {
							
//							System.out.println("Moving "+maxNodes+" subs from "+srcIndex+" to "+childIndex);
							
							KLPart a = new KLPart(srcPart);
							KLPart b = new KLPart(childPart);
							
							KLRefinement ref = new KLRefinement();
							ref.setPartitions(a, b);
							try {
								ref.migrateFromAToB(maxNodes);
							} catch (Exception e) {
								throw new GenerationException(e);
							}

							parts[srcIndex] = a.getVertices();
							parts[childIndex] = b.getVertices();
						}
						
						n.setSubsToSendToChild(i, 0);
						n.remSubs(maxNodes);
						child.setSubsToRecvFromParent(0); // No more to receive
						child.addSubs(maxNodes);
					}
					
				}
				
				if(n.isSourceOnly())
					throw new Error("Argh");
			}
			
			sources.clear();
			ct.listSources(sources);
		}
		
		
		// Build new graph mapping
		TreeSet<Integer>[] da2Subs = new TreeSet[parts.length];
		int[] sub2Da = new int[numOfSubs];
		for(int i = 0; i < parts.length; ++i) {
			Vertex[] part = parts[i];
			if(part != null && part.length > 0) {
				TreeSet<Integer> subs = new TreeSet<Integer>();
				da2Subs[i] = subs;
				
				for(int j = 0; j < part.length; ++j) {
					int subID = part[j].getSubID();
					subs.add(subID);
					sub2Da[subID] = i;
				}
			}
		}

		return new GraphMapping(da2Subs, sub2Da);
	}

	@Override
	public void setParameters(String[] params) throws ConfigurationException {
		if(params.length > 0)
			throw new ConfigurationException("No parameters required");
	}
	
	public int getNumOfDiffusionRounds() {
		return diffusionRounds;
	}

}
