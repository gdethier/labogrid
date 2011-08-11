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
package laboGrid.ioClients;

import java.io.File;
import java.io.IOException;

import laboGrid.ioClients.controller.OutputClientMessage;

import dimawo.middleware.distributedAgent.DistributedAgent;



public interface OutputClient {
	
	/**
	 * Gets a file. If no error occurred, after the returns of this method
	 * the requested file is present on the local file system.
	 * 
	 * @param src The source file name.
	 * @param destFileUID The destination file (identified by a file UID).
	 * 
	 * @throws OutputClientException If an error occurs while the source file is sent.
	 * @throws IOException If the given local source file does not exist.
	 */
	void put(File src, String destFileUID, OutputClientCallBack cb) throws OutputClientException, IOException;
	
	/**
	 * Configures the output client.
	 * 
	 * @param parameters Some parameters.
	 * @throws OutputClientException 
	 */
	void setParameters(String[] parameters) throws OutputClientException;

	void setDistributedAgent(DistributedAgent da);
	void submitOutputClientMessage(OutputClientMessage msg);

	void start() throws Exception;
	void stop() throws Exception;
	void join() throws InterruptedException;

}
