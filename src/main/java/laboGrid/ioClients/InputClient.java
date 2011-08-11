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

import dimawo.middleware.distributedAgent.DistributedAgent;


public interface InputClient {

	/**
	 * Gets a file asynchronously.
	 * 
	 * @param src The source file (identified by a file UID).
	 * @param dest The destination local file.
	 * @param cb The call-back to return file.
	 */
	public void get(String src, File dest, InputClientCallBack cb) throws InputClientException, IOException;
	
	public void submitInputClientMessage(InputClientMessage msg);

	/**
	 * Configures the input client.
	 * 
	 * @param parameters Some parameters.
	 */
	public void setParameters(String[] parameters) throws InputClientException;
	
	/**
	 * Stops the input client.
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception;
	
	/**
	 * Stops the input client.
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception;
	
	/**
	 * Sets the distributed agent for this client.
	 * This operation is optional as not all clients need an access to DA.
	 * 
	 * @param da
	 */
	public void setDistributedAgent(DistributedAgent da);

	public void join() throws InterruptedException;

	/**
	 * Returns the path of files the input client can retrieve
	 * @return A path
	 */
	public String getPath();

}
