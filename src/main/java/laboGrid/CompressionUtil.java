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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtil {
	
	public static Object readGZipCompressedObject(InputStream in) throws IOException, ClassNotFoundException {
		
		GZIPInputStream gis = new GZIPInputStream(in);
		ObjectInputStream ois = new ObjectInputStream(gis);
		return ois.readObject();
		
	}
	
	public static void writeGZipCompressedObject(Object o, OutputStream out) throws IOException {
		GZIPOutputStream gos = new GZIPOutputStream(out);
		ObjectOutputStream oos = new ObjectOutputStream(gos);
		oos.writeObject(o);
		oos.flush();
		gos.finish();
	}
	
	public static byte[] gzipCompressObject(Object o) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writeGZipCompressedObject(o, baos);
		} catch (IOException e) {
			return null;
		}
		return baos.toByteArray();
		
	}
	
	public static Object gzipDecompressObject(byte[] compressedData) throws IOException, ClassNotFoundException {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
		return readGZipCompressedObject(bais);
		
	}
}
