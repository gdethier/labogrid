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
package laboGrid.configuration.processingChain;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import laboGrid.lb.LBException;
import laboGrid.procChain.operators.BlockingBorderSender;
import laboGrid.procChain.operators.BorderFiller;
import laboGrid.procChain.operators.InPlaceStream;


import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ProcessingChainDescription implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String id;
	protected LinkedList<ProcessingChainElementDescription> chain;
	protected LinkedList<LoggerDescription> loggers;
	protected LinkedList<OperatorDescription> operators;
	protected String hash;
	
	
	public static ProcessingChainDescription getDefaultProcChainDesc(
			String pressClassName, String[] pressParameters,
			String collClassName, String[] collParameters) {
		LinkedList<ProcessingChainElementDescription> chain = new LinkedList<ProcessingChainElementDescription>();
		LinkedList<OperatorDescription> ops = new LinkedList<OperatorDescription>();

		OperatorDescription send = new OperatorDescription(
				BlockingBorderSender.class.getName(), new String[]{});
		chain.add(send);
		ops.add(send);
		
		OperatorDescription stream = new OperatorDescription(
				InPlaceStream.class.getName(), new String[]{});
		chain.add(stream);
		ops.add(stream);
		
		OperatorDescription recv = new OperatorDescription(
				BorderFiller.class.getName(), new String[]{});
		chain.add(recv);
		ops.add(recv);
		
		OperatorDescription press = new OperatorDescription(
				pressClassName, pressParameters);
		chain.add(press);
		ops.add(press);
		
		OperatorDescription coll = new OperatorDescription(
				collClassName, collParameters);
		chain.add(coll);
		ops.add(coll);
		
		return new ProcessingChainDescription("default", chain, new LinkedList<LoggerDescription>(), ops);
	}

	public ProcessingChainDescription(String id,
			LinkedList<ProcessingChainElementDescription> chain,
			LinkedList<LoggerDescription> loggers,
			LinkedList<OperatorDescription> operators) {
		this.id = id;
		this.chain = chain;
		this.loggers = loggers;
		this.operators = operators;
		Iterator<OperatorDescription> it = operators.iterator();
		hash = "";
		while(it.hasNext()) {
			OperatorDescription opDesc = it.next();
			String className = opDesc.getClassName();
			hash += className;
		}
	}

	public static ProcessingChainDescription newInstance(Element procChains) throws LBException {
		String id = procChains.getAttribute("id");
		LinkedList<ProcessingChainElementDescription> chain = new LinkedList<ProcessingChainElementDescription>();
		LinkedList<OperatorDescription> operators = new LinkedList<OperatorDescription>();
		LinkedList<LoggerDescription> loggers = new LinkedList<LoggerDescription>();
		NodeList n = procChains.getChildNodes();
		for(int i = 0; i < n.getLength(); ++i) {
			Node m = n.item(i);
			if(m instanceof Element) {
				Element e = (Element) m;
				ProcessingChainElementDescription pce = null;
				if(e.getNodeName().equals("Operator")) {
					pce = OperatorDescription.newInstance(e);
					operators.add((OperatorDescription) pce);
				} else if(e.getNodeName().equals("Logger")) {
					pce = LoggerDescription.newInstance(e);
					loggers.add((LoggerDescription) pce);
				} else {
					throw new LBException("Unknown processing chain element "+e.getNodeName());
				}
				chain.add(pce);
			}
		}
		return new ProcessingChainDescription(id, chain, loggers, operators);
	}

	public String getId() {
		return id;
	}

	public List<LoggerDescription> getLoggers() {
		return loggers;
	}
	
	public List<OperatorDescription> getOperators() {
		return operators;
	}

	public String getPerformanceHash() {
		return hash;
	}

	public List<ProcessingChainElementDescription> getChain() {
		return chain;
	}

	public void getAllClassNames(LinkedList<String> names) {
		Iterator<ProcessingChainElementDescription> it = chain.iterator();
		while(it.hasNext()) {
			ProcessingChainElementDescription pced = it.next();
			if(pced instanceof OperatorDescription) {
				OperatorDescription od = (OperatorDescription) pced;
				names.add(od.getClassName());
			} else {
				LoggerDescription ld = (LoggerDescription) pced;
				names.add(ld.getLoggerClass());
				names.add(ld.getClientClass());
			}
		}
	}

}
