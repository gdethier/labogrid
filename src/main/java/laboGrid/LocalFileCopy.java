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
package laboGrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocalFileCopy {
	
	/**
	 * Copies content of a file into another.
	 * 
	 * @param src Source file.
	 * @param dest Destination file.
	 * 
	 * @throws IOException
	 */
	public static void copyFile(File src, File dest) throws IOException {
		
		// Create output dir
		File path = new File(dest.getParent());
		if(! path.exists() && ! path.mkdirs()) {
			throw new IOException("Could not create output directory");
		}

		// Copy source file to destination file
		FileInputStream in = new FileInputStream(src);
		FileOutputStream out = new FileOutputStream(dest);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0){

			out.write(buf, 0, len);

		}

		in.close();
		out.close();
	
	}

}
