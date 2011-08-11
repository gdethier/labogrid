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

public class MigrationScheduler {
	
	private boolean hasParent;
	private boolean[] hasChild;
	
	private int aNSubs;
	private double aQuota;
	private int[] childANSubs;
	private double[] childAQuota;

	private double parentSendError;
	private int sendToParent, recvFromParent;
	private double[] childrenSendError;
	private int[] sendToChildren, recvFromChildren;

	public MigrationScheduler(int maxChildren) {
		hasParent = false;
		sendToParent = 0;
		recvFromParent = 0;
		parentSendError = 0;
		
		childANSubs = new int[maxChildren];
		childAQuota = new double[maxChildren];

		hasChild = new boolean[maxChildren]; // No child
		sendToChildren = new int[maxChildren];
		childrenSendError = new double[maxChildren];
		recvFromChildren = new int[maxChildren];
	}
	
	public void setAggregatedVariables(int aNSubs, double aQuota) {
		hasParent = true;
		this.aNSubs = aNSubs;
		this.aQuota = aQuota;
	}
	
	public void setChildAggregatedVariables(int child, int aNSubs, double aQuota) {
		hasChild[child] = true;
		childANSubs[child] = aNSubs;
		childAQuota[child] = aQuota;
	}
	
	public void schedule() {
		if(hasParent) {
			if(aNSubs > aQuota) {
				recvFromParent = 0;

				double tmp = aNSubs - aQuota;
				sendToParent = (int) Math.floor(tmp);
				if(sendToParent > 0)
					parentSendError = tmp - sendToParent;
				else
					parentSendError = 0;
			} else {
				recvFromParent = (int) Math.floor(aQuota - aNSubs);				
				sendToParent = 0;
			}
			
		} else {
			sendToParent = 0;
			parentSendError = 0;
			recvFromParent = 0;
		}

		for(int i = 0; i < hasChild.length; ++i) {
			if(hasChild[i]) {
				double cAQuota = childAQuota[i];
				int cANSubs = childANSubs[i];
				
				double tmp;
				if(cAQuota > cANSubs) {
					tmp = cAQuota - cANSubs;
					sendToChildren[i] = (int) Math.floor(tmp);
					if(sendToChildren[i] > 0)
						childrenSendError[i] = tmp - sendToChildren[i];
					else
						childrenSendError[i] = 0;
					recvFromChildren[i] = 0;
				} else {
					sendToChildren[i] = 0;
					childrenSendError[i] = 0;
					recvFromChildren[i] = (int) Math.floor(cANSubs - cAQuota);
				}
			} else {
				sendToChildren[i] = 0;
				childrenSendError[i] = 0;
				recvFromChildren[i] = 0;
			}
		}
	}
	
	public boolean isSourceOnly() {
		boolean isDest = recvFromParent > 0;
		boolean isSource = sendToParent > 0;
		for(int i = 0; i < hasChild.length; ++i) {
			isDest = isDest || (recvFromChildren[i] > 0);
			isSource = isSource || (sendToChildren[i] > 0);
		}
		
		return ! isDest && isSource;
	}

	public int getSendToParent() {
		return sendToParent;
	}

	public int getSendToChild(int i) {
		return sendToChildren[i];
	}

	public void setSendToParent(int num) {
		sendToParent = num;
	}

	public void setRecvFromChild(int childIndex, int num) {
		recvFromChildren[childIndex] = num;
	}

	public void setRecvFromParent(int num) {
		recvFromParent = num;
	}

	public void setSendToChild(int i, int num) {
		sendToChildren[i] = num;
	}
	
	public void correctSchedule(int nSubs, double quota) {
		int errorsAvailable;
		int out;

		if(parentSendError > 0) {
			errorsAvailable = 1;
			out = sendToParent;
		} else {
			errorsAvailable = 0;
			out = 0;
		}
		
		for(int i = 0; i < hasChild.length; ++i) {
			if(hasChild[i]) {
				if(childrenSendError[i] > 0) {
					out += sendToChildren[i];
					++errorsAvailable;
				}
			}
		}

//		System.out.println("quota="+quota);
//		System.out.println("errors="+errorsAvailable);
		
		int afterMig = nSubs - out;
//		System.out.println("afterMig="+afterMig);
		while(afterMig > quota && errorsAvailable > 0) {
			// try to correct output
			int maxErrorIndex = 0;
			double maxError = parentSendError;

			for(int i = 0; i < childrenSendError.length; ++i) {
				if(childrenSendError[i] > maxError) {
					maxError = childrenSendError[i];
					maxErrorIndex = i + 1;
				}
			}
			
			if(maxErrorIndex == 0) {
				++sendToParent;
				parentSendError = 0;
			} else {
				++sendToChildren[maxErrorIndex - 1];
				childrenSendError[maxErrorIndex - 1] = 0;
			}
			
			--errorsAvailable;
			--afterMig;
		}
//		System.out.println("correctedAfterMig="+afterMig);
	}

	public boolean isStable() {
		boolean isStable = (sendToParent == 0 && recvFromParent == 0);
		for(int i = 0; isStable && i < hasChild.length; ++i) {
			isStable = isStable && (sendToChildren[i] == 0 && recvFromChildren[i] == 0);
		}
		return isStable;
	}
	
}
