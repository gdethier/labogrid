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

import laboGrid.impl.common.simulation.algorithm.LBSimThread;
import laboGrid.lb.LBException;
import laboGrid.procChain.ProcessingChainElement;
import laboGrid.procChain.operators.BorderFiller;
import laboGrid.procChain.operators.InPlaceStream;
import laboGrid.procChain.operators.LBOperator;
import laboGrid.procChain.operators.NonBlockingBorderSender;
import laboGrid.procChain.operators.WaitBorderSent;

public class D3Q19AllInOneOperator extends LBOperator {
	private String[] collParams;
	private NonBlockingBorderSender sender;
	private InPlaceStream stream;
	private BorderFiller filler;
	private D3Q19SimpleSRTCollisionOperator coll;
	private WaitBorderSent wait;

	public D3Q19AllInOneOperator() {
		sender = new NonBlockingBorderSender();
		stream = new InPlaceStream();
		filler = new BorderFiller();
		coll = new D3Q19SimpleSRTCollisionOperator();
		wait = new WaitBorderSent();
	}

	protected D3Q19AllInOneOperator(String[] params) throws LBException {
		this();
		this.collParams = params;
		coll.setParameters(params);
	}

	@Override
	public void setParameters(String[] params) throws LBException {
		this.collParams = params;
		coll.setParameters(params);
	}
	
	@Override
	public void setLBAlgorithm(LBSimThread alg) throws LBException {
		super.setLBAlgorithm(alg);
		
		sender.setLBAlgorithm(alg);
		stream.setLBAlgorithm(alg);
		filler.setLBAlgorithm(alg);
		coll.setLBAlgorithm(alg);
		wait.setLBAlgorithm(alg);
	}

	@Override
	public void apply() throws LBException, InterruptedException {
		sender.apply();
		stream.apply();
		filler.apply();
		coll.apply();
		wait.apply();
	}

	@Override
	public ProcessingChainElement clone() {
		try {
			return new D3Q19AllInOneOperator(collParams);
		} catch (LBException e) {
			return null;
		}
	}

}
