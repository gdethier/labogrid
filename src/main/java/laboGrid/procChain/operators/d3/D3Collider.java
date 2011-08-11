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
package laboGrid.procChain.operators.d3;

import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.lb.collision.CollisionOperator;
import laboGrid.lb.lattice.d3.D3Lattice;
import laboGrid.lb.solid.d3.D3Solid;
import laboGrid.procChain.operators.LBOperator;

public abstract class D3Collider extends LBOperator {
	
	protected CollisionOperator<D3Lattice, D3Solid> collider;
	protected String[] colliderParameters;

	public D3Collider() {
		collider = getCollider();
	}
	
	public D3Collider(String[] parameters) throws LBException {
		collider = getCollider();
		collider.setParameters(parameters);
	}

	@Override
	public void setParameters(String[] parameters) throws LBException {
		this.colliderParameters = parameters;
		collider.setParameters(parameters);
	}

	@Override
	public void apply() throws LBException, InterruptedException {
		collider.collide();
	}
	
	@Override
	public void setLBAlgorithm(LBSimThread alg) throws LBException {
		super.setLBAlgorithm(alg);
		
		collider.setFluid((D3Lattice) alg.getFluid());
		collider.setSolid((D3Solid) alg.getSolid());
	}

	protected abstract CollisionOperator<D3Lattice, D3Solid> getCollider();

}
