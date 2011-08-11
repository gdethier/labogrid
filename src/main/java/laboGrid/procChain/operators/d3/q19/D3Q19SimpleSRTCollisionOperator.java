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
package laboGrid.procChain.operators.d3.q19;

import laboGrid.lb.LBException;
import laboGrid.lb.collision.d3.q19.D3Q19SRTCollider;
import laboGrid.procChain.ProcessingChainElement;
import laboGrid.procChain.operators.d3.D3Collider;

public class D3Q19SimpleSRTCollisionOperator extends D3Collider {

	public D3Q19SimpleSRTCollisionOperator() {
		super();
	}

	public D3Q19SimpleSRTCollisionOperator(String[] parameters)
			throws LBException {
		super(parameters);
	}

	@Override
	protected D3Q19SRTCollider getCollider() {
		return new D3Q19SRTCollider();
	}

	@Override
	public ProcessingChainElement clone() {
		try {
			return new D3Q19SimpleSRTCollisionOperator(colliderParameters);
		} catch (LBException e) {
			e.printStackTrace();
			return null;
		}
	}

}
