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
package laboGrid.procChain.loggers;

import java.io.PrintStream;
import java.io.Serializable;

public abstract class Log implements Serializable {

	private static final long serialVersionUID = 1L;

	private String clientId;
	private String loggerId;

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientId() {
		return clientId;
	}
	
	public void setLoggerId(String loggerId) {
		this.loggerId = loggerId;
	}

	public String getLoggerId() {
		return loggerId;
	}

	/**
	 * Prints an LBLog to a PrintStream. This method should be used
	 * so a big log is directly written to disk instead of using a big amount
	 * of memory with a "toString" conversion.
	 * 
	 * @param ps A print stream.
	 */
	public abstract void printLog(PrintStream ps);

}
