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

import laboGrid.LocalFileCopy;
import laboGrid.ioClients.AbstractInputClient;
import laboGrid.ioClients.GetFile;
import laboGrid.ioClients.InputClientCallBack;
import laboGrid.ioClients.InputClientException;
import laboGrid.ioClients.InputClientMessage;

import dimawo.agents.UncaughtThrowable;
import dimawo.agents.UnknownAgentMessage;



public class LocalInputClient extends AbstractInputClient {

	/** Path to local files the client can get. */
	private String path;

	@Override
	public void setParameters(String[] parameters) throws InputClientException {
		if(parameters.length != 1)
			throw new InputClientException("Wrong arguments number: "+parameters.length);
		this.path = parameters[0];
	}

	@Override
	protected void handleGetFile(GetFile gf)
	throws InputClientException, IOException {
		
		String fileUID = gf.getFileUID();
		File dest = gf.getFile();
		InputClientCallBack cb = gf.getCallBack();

		if(dest.exists()) {
			
			cb.inputClientGetCB(fileUID, dest, new IOException("File already exists"));
			return;
			
		}
		
		File srcFile = new File(path+fileUID);
		if( ! srcFile.exists()) {
			
			cb.inputClientGetCB(fileUID, null, new InputClientException("Source file "+srcFile+" does not exist."));
			return;
			
		}

		try {
			LocalFileCopy.copyFile(srcFile, dest);
		} catch(IOException e) {
			dest.delete();
			cb.inputClientGetCB(fileUID, null, new InputClientException("Could not copy content of source " +
					"file into destination file.", e));
			return;
		}
		
		cb.inputClientGetCB(fileUID, dest, null);

	}

	@Override
	protected void subClassHandleMessage(Object o) throws Exception {
		throw new UnknownAgentMessage(o);
	}

	@Override
	protected void logAgentExit() {
		agentPrintMessage("exit");
	}

	@Override
	protected void init() throws Throwable {
		agentPrintMessage("init");
	}

	@Override
	protected void handleInputClientMessage(InputClientMessage o)
			throws Exception {
		throw new Exception("Unexpected message: "+o.getClass().getName());
	}

	@Override
	public String getPath() {
		return path;
	}

}
