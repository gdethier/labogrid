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
package laboGrid.procChain.loggers.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import dimawo.middleware.communication.CommunicatorInterface;

import laboGrid.procChain.loggers.Log;



public class LocalFileLogClient extends LogClient {

	private PrintStream ps;

	@Override
	protected void putIdentifiedLog(Log log) {

		log.printLog(ps);

	}

	@Override
	public void setParameters(String[] parameters) throws LogClientException {

		String pathName = parameters[0];
		File path = new File(pathName);
		if( ! path.mkdirs() && ! path.exists()) {

			throw new LogClientException("Could not create log file directory.");

		}
		
		try {
			FileOutputStream fos = new FileOutputStream(path+"/"+getClientId()+".log", true);
			ps = new PrintStream(fos);
		} catch (FileNotFoundException e) {
			throw new LogClientException("Could not create log file", e);
		}

	}

	@Override
	public void close() {
		ps.close();
	}

	@Override
	public void setCommunicator(CommunicatorInterface com) {
		// No need for communication capabilities, ignore.
	}

}
