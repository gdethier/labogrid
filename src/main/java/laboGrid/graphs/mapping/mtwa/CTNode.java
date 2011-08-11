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
import java.util.LinkedList;

import dimawo.middleware.distributedAgent.DAId;


public class CTNode {
	private int daIndex;
	private DAId daId;
	
	private long ccp, aCcp;
	private int nSubs, aNSubs;
	private double quota, aQuota;
	
	private MigrationScheduler sched;
	
	private CTNode parent;
	private CTNode[] children;
	private int subtreeSize; // in nodes
	
	// Diffusion scheme variables
	private double difNSubs;
	
	public CTNode(int daIndex, int nChildren) {
		this.daIndex = daIndex;
		parent = null;
		children = new CTNode[nChildren];
		
		sched = new MigrationScheduler(nChildren);
	}

	public void setChild(int i, CTNode n) {
		children[i] = n;
	}

	public void setParent(CTNode n) {
		this.parent = n;
	}

	public void getBottomUpSchedule(LinkedList<CTNode> sched) {
		for(int i = 0; i < children.length; ++i) {
			CTNode child = children[i];
			if(child != null) {
				child.getBottomUpSchedule(sched);
			}
		}
		
		sched.addLast(this);
	}

	public int getDaIndex() {
		return daIndex;
	}

	public void setCcp(long ccp) {
		this.ccp = ccp;
	}

	private int getSubtreeSize() {
		return subtreeSize;
	}

	private long getAggregatedCcp() {
		return aCcp;
	}

	public void setNumOfSublattices(int subs) {
		this.nSubs = subs;
	}

	public void setAggregatedLabels() {
		aCcp = ccp;
		aNSubs = nSubs;
		subtreeSize = 1;
		for(int i = 0; i < children.length; ++i) {
			CTNode child = children[i];
			if(child != null) {
				aCcp += child.getAggregatedCcp();
				aNSubs += child.getAggregatedNSubs();
				subtreeSize += child.getSubtreeSize();
			}
		}
	}

	private int getAggregatedNSubs() {
		return aNSubs;
	}

	public double getAverageLoad() {
		return aNSubs / (double) aCcp;
	}

	public void setSubtreeQuotas(double avgLoad) {
		quota = avgLoad * ccp;
		aQuota = avgLoad * aCcp;
		
		for(int i = 0; i < children.length; ++i) {
			CTNode child = children[i];
			if(child != null)
				child.setSubtreeQuotas(avgLoad);
		}
	}

	public double getAggregatedQuota() {
		return aQuota;
	}

	public void scheduleSubtreeMigrations() {
		if(parent != null) {
			sched.setAggregatedVariables(aNSubs, aQuota);
		}

		for(int i = 0; i < children.length; ++i) {
			CTNode child = children[i];
			if(child != null) {
				double cAQuota = child.getAggregatedQuota();
				int cANSubs = child.getAggregatedNSubs();
				
				sched.setChildAggregatedVariables(i, cANSubs, cAQuota);
				
				child.scheduleSubtreeMigrations();
			}
		}
		
		sched.schedule();
	}

	public int getSubsToSendToParent() {
		return sched.getSendToParent();
	}

	public CTNode getParent() {
		return parent;
	}

	public int getNumOfChildren() {
		return children.length;
	}

	public int getSubsToSendToChild(int i) {
		return sched.getSendToChild(i);
	}

	public void setSubsToSendToParent(int num) {
		sched.setSendToParent(num);
	}
	
	public void setSubsToRecvFromChild(int childIndex, int num) {
		sched.setRecvFromChild(childIndex, num);
	}

	public void setSubsToRecvFromParent(int num) {
		sched.setRecvFromParent(num);
	}

	public CTNode getChild(int i) {
		return children[i];
	}

	public void setSubsToSendToChild(int i, int num) {
		sched.setSendToChild(i, num);	
	}

	public double getQuota() {
		return quota;
	}

	public void listSubtreeSources(LinkedList<CTNode> sources) {
		if(isSourceOnly())
			sources.add(this);
		
		for(int i = 0; i < children.length; ++i) {
			CTNode child = children[i];
			if(child != null)
				child.listSubtreeSources(sources);
		}
	}
	
	public boolean isSourceOnly() {
		return sched.isSourceOnly();
	}

	public int getChildIndex(int daIndex) {
		for(int i = 0; i < children.length; ++i) {
			CTNode child = children[i];
			if(child != null && child.daIndex == daIndex)
				return i;
		}
		
		return -1;
	}

	public void printSubtree(PrintStream out) {
		out.println("daIndex="+daIndex);
		if(parent != null) {
			out.println("parentIndex="+parent.getDaIndex());
		} else {
			out.println("parent=null");
		}
		for(int i = 0; i < children.length; ++i) {
			CTNode child = children[i];
			if(child != null) {
				out.println("childIndex["+i+"]="+child.getDaIndex());
			} else {
				out.println("child["+i+"]=null");
			}
		}
		out.println("_________________________");
		
		for(int i = 0; i < children.length; ++i) {
			CTNode child = children[i];
			if(child != null) {
				child.printSubtree(out);
			}
		}
	}

	public void correctMigrations() {
		sched.correctSchedule(nSubs, quota);
	}

	public void addSubs(int num) {
		nSubs += num;
	}
	
	public void remSubs(int num) {
		nSubs -= num;
	}

	public void listSubtree(LinkedList<CTNode> list) {
		getBottomUpSchedule(list);
	}

	public void setDaId(DAId daId) {
		this.daId = daId;
	}

	public DAId getDaId() {
		return daId;
	}

	public void listSubtreeTopDown(LinkedList<CTNode> list) {
		list.add(this);
		for(int i = 0; i < children.length; ++i) {
			CTNode child = children[i];
			if(child != null) {
				child.listSubtreeTopDown(list);
			}
		}
	}

	public long getCCP() {
		return ccp;
	}

	public double updateDiffusionVar(int nRound, double att) {
//		int read = nRound%2;
//		int write = (read + 1)%2;
		
		double[] difArr = new double[1 + children.length];
		
		int nVal = 0;
		if(parent != null) {
			++nVal;
			double difSubs = ((ccp / (double) parent.ccp) * parent.difNSubs) - difNSubs;
//			if((difSubs > 0 && difSubs > parent.getDiffusionSubs(read)) ||
//				(difSubs < 0 && difSubs > getDiffusionSubs(write)))
//				difSubs = 0;
			difArr[0] = difSubs;
		}
		
		for(int i = 0; i < children.length; ++i) {
			CTNode child = children[i];
			if(child != null) {
				++nVal;
				double difSubs = ((ccp / (double) child.ccp) * child.difNSubs) - difNSubs;
//				if((difSubs > 0 && difSubs > child.getDiffusionSubs(read)) ||
//					(difSubs < 0 && difSubs > getDiffusionSubs(write)))
//						difSubs = 0;
				difArr[i + 1] = difSubs;
			}
		}
		
		if(parent != null) {
			double difSubs = att * (difArr[0] / nVal);
			parent.difNSubs -= difSubs;
			difNSubs += difSubs;
		}
		
		for(int i = 1; i < difArr.length; ++i) {
			if(children[i - 1] != null) {
				double difSubs = att * (difArr[i] / nVal);
				children[i - 1].difNSubs -= difSubs;
				difNSubs += difSubs;
			}
		}
		
		double load = getDiffusionLoad();
//		System.out.println("load="+load);
		return load;
	}
	
//	private double getDiffusionVar(int i) {
//		return difAvgLoad[i];
//	}
	
	private double getDiffusionLoad() {
		return difNSubs / ccp;
	}

	public void initDiffusionVar() {
//		double load = nSubs / (double) ccp;
		difNSubs = nSubs;
	}

}
