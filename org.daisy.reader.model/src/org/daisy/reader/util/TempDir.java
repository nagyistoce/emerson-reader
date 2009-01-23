package org.daisy.reader.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

public class TempDir {

	/** 
	 * Create a temporary directory in the platform instance location.
	 */
	public static File create(String name) throws IOException {
		IPath path = Platform.getLocation().addTrailingSeparator();
		//TODO Platform.getInstanceLocation();
		name = StringUtils.toRestrictedSubset(StringUtils.FilenameRestriction.Z3986, name);
		path = path.append(name);
		
		File dir = path.toFile();
		boolean result = dir.mkdirs();
		if(!result) {
			//TODO get user dir
		}
		
		dir.deleteOnExit();
		if(dir==null||!dir.exists()) throw new IOException(path.toString());
		//System.err.println("TempDir#create: " + dir.getAbsolutePath());
		return dir;
	}
}
