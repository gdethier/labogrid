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
package laboGrid.impl.central.controllerAgent.experimenter;

import java.util.Map;
import java.util.Set;

import laboGrid.configuration.middleware.FaultToleranceConfiguration;
import laboGrid.graphs.GenerationException;
import laboGrid.graphs.GraphTranslator;
import laboGrid.graphs.mapping.GraphMapping;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.replication.ReplicationGraph;
import laboGrid.graphs.replication.ReplicationGraphGenerator;
import laboGrid.graphs.replication.ReplicationGraphHeuristicGenerator;
import laboGrid.graphs.resource.ResourceGraph;
import laboGrid.lb.lattice.LatticeDescriptor;

import dimawo.middleware.distributedAgent.DAId;



public class GraphConfiguration {

	private FaultToleranceConfiguration ftConf;
	private GraphTranslator graphTranslator;

	private ResourceGraph rGraph;
	private GraphMapping cGraph;
	
	private ReplicationGraph repGraph;


	public GraphConfiguration(
			FaultToleranceConfiguration ftConf,
			ModelGraph mGraph, ResourceGraph rGraph,
			GraphMapping cGraph,
			LatticeDescriptor latticeDesc) throws GenerationException {

		this.ftConf = ftConf;

		this.rGraph = rGraph;
		this.cGraph = cGraph;

		repGraph = null; // Only generated on demand.
		
		graphTranslator = new GraphTranslator(rGraph.getNewIds());
	}

	public ResourceGraph getResourceGraph() {

		return rGraph;

	}

	public GraphMapping getComputationGraph() {

		return cGraph;

	}
	
	public void setGraphStructures(
			DAId hostDaId,
			DAId[] sub2Da,
			Map<DAId, Set<Integer>> da2Sub,
			Map<DAId, Set<DAId>> bGraph) {
			
		graphTranslator.translateComputationGraph(cGraph, da2Sub, sub2Da);
		
		if( ! ftConf.replicationIsEnabled())
			return;

		if( ! ftConf.getCentralized()) {

			if(repGraph == null)
				repGraph = generateReplicationGraph();
			graphTranslator.translateBackupGraph(repGraph, bGraph);

		} else {
			
			graphTranslator.genCentralBackupGraph(hostDaId, bGraph);
			
		}

	}
	
	private ReplicationGraph generateReplicationGraph() {

		ReplicationGraph repGraph;
		if(ftConf.getCentralized()) {

			repGraph = null;

		} else {
			
			int backupDegree = ftConf.getBackupDegree();

			ReplicationGraphGenerator cons = new ReplicationGraphHeuristicGenerator();
			repGraph = cons.computeReplicationGraph(rGraph.getNewIds(), cGraph, backupDegree);

		}
		
		return repGraph;
		
	}

}
