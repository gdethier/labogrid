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
package laboGrid.procChain;

import java.util.Iterator;
import java.util.LinkedList;

import laboGrid.configuration.processingChain.LoggerDescription;
import laboGrid.configuration.processingChain.OperatorDescription;
import laboGrid.configuration.processingChain.ProcessingChainDescription;
import laboGrid.configuration.processingChain.ProcessingChainElementDescription;
import laboGrid.lb.LBException;
import laboGrid.procChain.loggers.LBLogger;
import laboGrid.procChain.operators.LBOperator;

import dimawo.Reflection;


public class ProcessingChain {

	private LinkedList<ProcessingChainElement> procChain;
	
	
	/////////////////
	// Constructor //
	/////////////////

	/**
	 * Constructor.
	 */
	public ProcessingChain() {

		procChain = new LinkedList<ProcessingChainElement>();

	}
	
	/**
	 * Private constructor only used internally.
	 * 
	 * @param procChain A LinkedList of ProcessingChainElements.
	 */
	private ProcessingChain(LinkedList<ProcessingChainElement> procChain) {

		this.procChain = procChain;

	}

	/**
	 * Instantiates a processing chain given a description.
	 * 
	 * @param pChainDesc The processing chain description.
	 * @param omitLoggers If true, loggers are omitted in the generated processing chain.
	 * 
	 * @return A processing chain.
	 * 
	 * @throws LBException
	 */
	public static ProcessingChain getOperatorsChain(
			ProcessingChainDescription pChainDesc, boolean omitLoggers) throws LBException {

		LinkedList<ProcessingChainElement> procChain = new LinkedList<ProcessingChainElement>();

		Iterator<ProcessingChainElementDescription> it = pChainDesc.getChain().iterator();
		while(it.hasNext()) {

			ProcessingChainElementDescription opDesc = it.next();
			ProcessingChainElement op;
			try {

				op = (ProcessingChainElement) Reflection.newInstance(opDesc.getClassName());

			} catch (Exception e) {

				throw new LBException("Could not instantiate a ProcessingChainElement", e);

			}
			
			if(op instanceof LBLogger && ! omitLoggers) {
				
				LBLogger log = (LBLogger) op;
				LoggerDescription lDesc = (LoggerDescription) opDesc;

				log.setLoggingParameters(lDesc);
				procChain.add(op);
				
			} else if(op instanceof LBOperator) {

				LBOperator lbOp = (LBOperator) op;
				OperatorDescription lDesc = (OperatorDescription) opDesc;

				lbOp.setParameters(lDesc.getParameters());
				procChain.add(op);

			}

		}

		return new ProcessingChain(procChain);

	}
	
	
	/////////////////////
	// Object override //
	/////////////////////
	
	/**
	 * A deep clone is needed.
	 */
	@Override
	public ProcessingChain clone() {
		
		LinkedList<ProcessingChainElement> clone = new LinkedList<ProcessingChainElement>();
		Iterator<ProcessingChainElement> it = procChain.iterator();
		while(it.hasNext()) {

			ProcessingChainElement pce = it.next();
			clone.add(pce.clone());

		}

		return new ProcessingChain(clone);

	}

	
	////////////////////
	// Public methods //
	////////////////////
	
	/**
	 * @return an iterator on the processing chain elements.
	 */
	public Iterator<ProcessingChainElement> iterator() {

		return procChain.iterator();

	}

	public void closeLoggers() {
		
		Iterator<ProcessingChainElement> it = procChain.iterator();
		while(it.hasNext()) {
			ProcessingChainElement pce = it.next();
			
			if(pce instanceof LBLogger) {
				
				LBLogger log = (LBLogger) pce;
				log.close();
				
			}
		}
		
	}

}
