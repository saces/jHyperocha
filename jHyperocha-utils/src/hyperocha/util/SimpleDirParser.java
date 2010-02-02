/**
 * 
 */
package hyperocha.util;

import java.io.File;

/**
 * @author saces
 * 
 */
public abstract class SimpleDirParser {

	public abstract void onFileItem(String name, File item);

	public static void parse(File dir, SimpleDirParser callback) {
		parseDir(dir, dir.getAbsolutePath().length() + 1, callback);
	}

	private static void parseDir(File dir, int prefixlength,
			SimpleDirParser callback) {
		if (!dir.isDirectory())
			throw new IllegalStateException("Not a dir.");
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				parseDir(file, prefixlength, callback);
			} else {
				callback.onFileItem(file.getAbsolutePath().substring(
						prefixlength), file);
			}
		}
	}
}
