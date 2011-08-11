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
package laboGrid.standalone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.TreeMap;

import laboGrid.procChain.loggers.Log;

public class LBLogsHandler {
	private String path;
	
	private TreeMap<String, File> openFiles;

	public LBLogsHandler(String path) {
		this.path = path;
		openFiles = new TreeMap<String, File>();
	}

	public void handle(Log log) throws FileNotFoundException {
		String clientId = log.getLoggerId();
		File logFile = openFiles.get(clientId);
		if(logFile == null) {
			logFile = openNewLogFile(clientId);
		}
		writeLog(log, logFile);
	}

	private File openNewLogFile(String clientId) {
		File f = new File(path + "/" + clientId + ".log");
		openFiles.put(clientId, f);
		return f;
	}

	private void writeLog(Log l, File logFile) throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(logFile, true);
		PrintStream ps = new PrintStream(fos);
		l.printLog(ps);
		ps.close();
	}
}
