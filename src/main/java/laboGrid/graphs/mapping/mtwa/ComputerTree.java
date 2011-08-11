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

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import laboGrid.graphs.mapping.GraphMapping;
import laboGrid.graphs.resource.ResourceGraph;



public class ComputerTree {
	private CTNode root;
	private int size;
	private double avgLoad;
	private int nDifRounds;

	public ComputerTree() {
	}

	public static ComputerTree newInstance(ResourceGraph rGraph) {
		int nDAs = rGraph.getDasCount();
		
		ComputerTree ct = new ComputerTree();
		ct.root = ct.buildSubtree(0, nDAs);
		
		return ct;
	}

	private CTNode buildSubtree(int from, int to) {
		int numOfChildren = 2;
		
		if(from == to - 1) {
			++size;
			return new CTNode(from, numOfChildren);
		}
		
		int middle = (from + to) / 2;
		++size;
		CTNode root = new CTNode(middle, 2);
		CTNode left = null, right = null;
		if(from < middle) {
			left = buildSubtree(from, middle);
			left.setParent(root);
		}
		if(to > middle + 1) {
			right = buildSubtree(middle + 1, to);
			right.setParent(root);
		}

		root.setChild(0, left);
		root.setChild(1, right);
		
		return root;
	}
	
	public void labelNodes(int[] refSize, ResourceGraph rGraph) {
		LinkedList<CTNode> sched = new LinkedList<CTNode>();
		root.getBottomUpSchedule(sched);

		for(Iterator<CTNode> it = sched.iterator(); it.hasNext();) {
			CTNode n = it.next();
			int daIndex = n.getDaIndex();
			long ccp = rGraph.getPower(daIndex, refSize);
			n.setCcp(ccp);
		}
	}

	public void labelNodes(int[] refSize, ResourceGraph rGraph,
			GraphMapping initialMapping) {
		// 1) set aggregated labels
		LinkedList<CTNode> sched = new LinkedList<CTNode>();
		root.getBottomUpSchedule(sched);
		
		Set<Integer>[] da2Sub = initialMapping.getDa2Sub();
		for(Iterator<CTNode> it = sched.iterator(); it.hasNext();) {
			CTNode n = it.next();
			int daIndex = n.getDaIndex();
			long ccp = rGraph.getPower(daIndex, refSize);
			int work;
			if(da2Sub[daIndex] != null)
				work = da2Sub[daIndex].size();
			else
				work = 0;
			
			n.setNumOfSublattices(work);
			n.setCcp(ccp);
			
			n.setAggregatedLabels();
		}
		
	}
	
	public void scheduleMigrationsTWA() {
		// 2) set quotas and aggregated quotas
		avgLoad = root.getAverageLoad();
//		System.out.println("avgLoad="+avgLoad);
		root.setSubtreeQuotas(avgLoad);
		
		// 3) Schedule sublattices migration
		root.scheduleSubtreeMigrations();
	}
	
	private int setAvgLoadDiffusion(double att, double relPrec) {
		LinkedList<CTNode> list = new LinkedList<CTNode>();
		getTopDownSchedule(list);

		if(list.size() == 0)
			return 0;
		
		for(Iterator<CTNode> it = list.iterator(); it.hasNext();) {
			CTNode n = it.next();
			n.initDiffusionVar();
		}

		int nRound = -1;
		double minAvg;
		double maxAvg;
		double crit;
		do {
			++nRound;
			Iterator<CTNode> it = list.iterator();
			CTNode n = it.next();
			minAvg = n.updateDiffusionVar(nRound, att);
			maxAvg = minAvg;
			
			while(it.hasNext()) {
				n = it.next();
				double avg = n.updateDiffusionVar(nRound, att);
				if(avg < minAvg)
					minAvg = avg;
				if(avg > maxAvg)
					maxAvg = avg;
			}
			
			crit = (maxAvg - minAvg) / maxAvg;
		} while(minAvg < 0 || crit > relPrec);
		
		avgLoad = (minAvg + maxAvg) / 2;
		
		return nRound;
	}

	public int scheduleMigrationsDiffusion(double att, double relPrec) {
		nDifRounds = setAvgLoadDiffusion(att, relPrec);
		root.setSubtreeQuotas(avgLoad);
		
		// 3) Schedule sublattices migration
		root.scheduleSubtreeMigrations();
		return nDifRounds;
	}

	public CTNode getRoot() {
		return root;
	}

	public void listSources(LinkedList<CTNode> sources) {
		root.listSubtreeSources(sources);
	}
	
	public void print(PrintStream out) {
		root.printSubtree(out);
	}

	public Iterator<CTNode> iterator() {
		// TODO : efficient solution !
		LinkedList<CTNode> list = new LinkedList<CTNode>();
		root.listSubtree(list);
		return list.iterator();
	}

	public void getTopDownSchedule(LinkedList<CTNode> list) {
		root.listSubtreeTopDown(list);
	}

	public int size() {
		return size;
	}

	public double getAverageLoad() {
		return avgLoad;
	}

	public void setRoot(CTNode node) {
		root = node;
	}

	public void setSize(int i) {
		this.size = i;
	}

}
