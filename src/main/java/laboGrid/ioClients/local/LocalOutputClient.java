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
package laboGrid.ioClients.local;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import laboGrid.LocalFileCopy;
import laboGrid.ioClients.AbstractOutputClient;
import laboGrid.ioClients.OutputClientCB;
import laboGrid.ioClients.OutputClientCallBack;
import laboGrid.ioClients.OutputClientException;
import laboGrid.ioClients.PutFile;
import laboGrid.ioClients.controller.OutputClientMessage;

import dimawo.agents.UncaughtThrowable;



public class LocalOutputClient extends AbstractOutputClient {

	private static Semaphore dirCreationMutex = new Semaphore(1);

	/** Path where the client can put files. */
	private String path;
	
	
	private static void createOutputDirectory(File dir) throws InterruptedException, OutputClientException {
		
		dirCreationMutex.acquire();
		
		if( ! dir.exists() && ! dir.mkdirs()) {

			throw new OutputClientException("Could not create output folder "+dir);

		}
		
		dirCreationMutex.release();
		
	}
	
	@Override
	public void setParameters(String[] parameters) throws OutputClientException {
		
		if(parameters.length != 1)
			throw new OutputClientException("Wrong arguments number: "+parameters.length);

		this.path = parameters[0];
		
		File pathFile = new File(path);
		try {

			createOutputDirectory(pathFile);

		} catch (InterruptedException e) {

			throw new OutputClientException(e);

		}

	}

	@Override
	protected void handleOutputClientMessage(OutputClientMessage o)
			throws Exception {
		throw new Exception("Unexcpected message");
	}

	@Override
	protected void handlePutFile(PutFile o) throws Exception {
		
		String fileUID = o.getDestinationFileUID();
		File src = o.getSourceFile();
		String dest = o.getDestinationFileUID();
		OutputClientCallBack cb = o.getCallBack();
		
		if( ! src.exists()) {
			
			cb.outputClientPutCB(
					new OutputClientCB(fileUID, new IOException("Source does not exist.")));
			return;
			
		}
		
		File destFile = new File(path+dest);
		try {

			LocalFileCopy.copyFile(src, destFile);

		} catch(IOException e) {

			cb.outputClientPutCB(
					new OutputClientCB(fileUID,
							new OutputClientException(
									"Could not copy content of source " +
									"file into destination file.", e)));
			return;

		}

		cb.outputClientPutCB(new OutputClientCB(fileUID, null));

	}

	@Override
	protected void subClassHandleMessage(Object o) throws Exception {
		throw new Exception("Unexcpected message");
	}

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
	}

}
