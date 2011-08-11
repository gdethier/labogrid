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
package laboGrid.graphs.mapping;

import laboGrid.ConfigurationException;
import laboGrid.graphs.GenerationException;
import laboGrid.graphs.model.ModelGraph;
import laboGrid.graphs.resource.ResourceGraph;

public interface GraphMapper {
	/**
	 * Configuration interface when using reflection
	 * @param genParams
	 * @throws ConfigurationException 
	 */
	public void setParameters(String[] genParams) throws ConfigurationException;

	public GraphMapping map(ResourceGraph rGraph,
			ModelGraph mGraph) throws GenerationException;
}
