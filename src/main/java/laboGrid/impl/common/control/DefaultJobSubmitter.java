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
package laboGrid.impl.common.control;

import laboGrid.graphs.model.ModelGraph;
import laboGrid.impl.central.controllerAgent.NewDA;
import dimawo.agents.AbstractAgent;
import dimawo.agents.UnknownAgentMessage;
import dimawo.middleware.commonEvents.BrokenDA;
import dimawo.middleware.distributedAgent.DiscoveryServiceInterface;


public class DefaultJobSubmitter extends AbstractAgent {

	private int jobsToRequest;

	private int maxJobs;
	private int requested;
	private int registered;
	
	private DiscoveryServiceInterface mInt;


	public DefaultJobSubmitter(AbstractAgent parent, int jobsToRequest,
			DiscoveryServiceInterface mInt) {
		super(parent, "DefaultJobSubmitter");
		this.jobsToRequest = jobsToRequest;

		maxJobs = 0;
		requested = 0;
		registered = 0;
		
		this.mInt = mInt;
	}


	@Override
	protected void handleMessage(Object o) throws Exception {
		if(o instanceof BrokenDA) {
			--registered;
		} else if(o instanceof NewDA) {
			++registered;
		} else if(o instanceof ModelGraph) {
			ModelGraph mGraph = (ModelGraph) o;
			maxJobs = mGraph.getSubLatticesCount();
		} else {
			throw new UnknownAgentMessage(o);
		}

		if(requested <= registered &&
				registered < maxJobs) {
			requested += jobsToRequest;
			mInt.requestResources(jobsToRequest);
		}
	}

	public void signalNewDA() {
		try {
			submitMessage(new NewDA());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void signalBrokenDA() {
		try {
			submitMessage(new BrokenDA(null));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void signalNewModelGraph(ModelGraph mGraph) {
		try {
			submitMessage(mGraph);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void exit() {
		agentPrintMessage("exit");
	}


	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
	}

}
