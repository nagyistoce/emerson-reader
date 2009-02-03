package org.daisy.reader.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class FileUtils {

	/**
	 * Stream using NIO
	 * @param input
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public static File writeInputStreamToFile(InputStream input, File output) throws IOException {
		WritableByteChannel channel = null;
		try {
			// Obtain a channel
			channel = new FileOutputStream(output).getChannel();

			// Create a direct ByteBuffer;
			ByteBuffer buf = ByteBuffer.allocateDirect(10);

			byte[] bytes = new byte[1024];
			int count = 0;
			int index = 0;

			// Continue writing bytes until there are no more
			while (count >= 0) {
				if (index == count) {
					count = input.read(bytes);
					index = 0;
				}
				// Fill ByteBuffer
				while (index < count && buf.hasRemaining()) {
					buf.put(bytes[index++]);
				}

				// Set the limit to the current position and the position to 0
				// making the new bytes visible for write()
				buf.flip();

				// Write the bytes to the channel
				channel.write(buf);

				// Check if all bytes were written
				if (buf.hasRemaining()) {
					// If not all bytes were written, move the unwritten bytes
					// to the beginning and set position just after the last
					// unwritten byte; also set limit to the capacity
					buf.compact();
				} else {
					// Set the position to 0 and the limit to capacity
					buf.clear();
				}
			}
		} finally {
			if (channel != null)
				channel.close();

		}

		return output;

	}

	/**
	 * Stream using IO
	 * @param input
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public static File writeInputStreamToFile(File dest, InputStream in) throws IOException {
		OutputStream out = null;
		try {			
			out = new FileOutputStream(dest);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return dest;
	}
	
	public static String getNameMinusExtension(File f) {
		StringBuilder sb = new StringBuilder();
		String name = f.getName();
		int end = name.lastIndexOf('.');
		if(end > 0) {
			for (int i = 0; i < name.length(); i++) {
				if(i<end){
					sb.append(name.charAt(i));
				}
			}
			return sb.toString();
		}
		return name;
	}

	public static String getExtension(File f) {
		StringBuilder sb = new StringBuilder();
		String name = f.getName();
		int start = name.lastIndexOf('.');
		if(start > 0) {
			for (int i = 0; i < name.length(); i++) {
				if(i>start){
					sb.append(name.charAt(i));
				}
			}
			return sb.toString();
		}
		return null;
	}
	
}
