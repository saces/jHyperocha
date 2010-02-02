/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */
package hyperocha.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

final public class FileUtil {

	private static final int BUFFER_SIZE = 4096;

	/** Round up a value to the next multiple of a power of 2 */
	private static final long roundup_2n (long val, int blocksize) {
		int mask=blocksize-1;
		return (val+mask)&~mask;
	}

	/**
	 * Guesstimate real disk usage for a file with a given filename, of a given length.
	 */
	public static long estimateUsage(File file, long flen) {
		/**
		 * It's possible that none of these assumptions are accurate for any filesystem;
		 * this is intended to be a plausible worst case.
		 */
		// Assume 4kB clusters for calculating block usage (NTFS)
		long blockUsage = roundup_2n(flen, 4096);
		// Assume 512 byte filename entries, with 100 bytes overhead, for filename overhead (NTFS)
		String filename = file.getName();
		int nameLength = filename.getBytes().length + 100;
		long filenameUsage = roundup_2n(nameLength, 512);
		// Assume 50 bytes per block tree overhead with 1kB blocks (reiser3 worst case)
		long extra = (roundup_2n(flen, 1024) / 1024) * 50;
		return blockUsage + filenameUsage + extra;
	}

    public boolean isParentOf(File poss, File descendant) {
        if (poss.equals(descendant)) {
            return false;
        }
        while ((descendant != null) && !poss.equals(descendant)) {
            descendant = descendant.getParentFile();
        }
        return descendant != null;
    }

	/**
	 *  Is possParent a parent of filename?
	 * Why doesn't java provide this? :(
	 * */
	public static boolean isParent(File poss, File filename) {
		File canon = FileUtil.getCanonicalFile(poss);
		File canonFile = FileUtil.getCanonicalFile(filename);

		if(isParentInner(poss, filename)) return true;
		if(isParentInner(poss, canonFile)) return true;
		if(isParentInner(canon, filename)) return true;
		if(isParentInner(canon, canonFile)) return true;
		return false;
	}

	private static boolean isParentInner(File possParent, File filename) {
		while(true) {
			if(filename.equals(possParent)) return true;
			filename = filename.getParentFile();
			if(filename == null) return false;
		}
	}
	
	public static File getCanonicalFile(File file) {
		File result;
		try {
			result = file.getCanonicalFile();
		} catch (IOException e) {
			result = file.getAbsoluteFile();
		}
		return result;
	}
	
    public static String readUTF(File file) throws FileNotFoundException, IOException {
        return readUTF(file, 0);
    }
        
	public static String readUTF(File file, long offset) throws FileNotFoundException, IOException {
		StringBuffer result = new StringBuffer();
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		InputStreamReader isr = null;
		
		try {
			fis = new FileInputStream(file);
			skipFully(fis, offset);
			bis = new BufferedInputStream(fis);
			isr = new InputStreamReader(bis, "UTF-8");

			char[] buf = new char[4096];
			int length = 0;

			while((length = isr.read(buf)) > 0) {
				result.append(buf, 0, length);
			}

		} finally {
			try {
				if(isr != null) isr.close();
				if(bis != null) bis.close();
				if(fis != null) fis.close();
			} catch (IOException e) {}
		}
		return result.toString();
	}

	/**
	 * Reliably skip a number of bytes or throw.
	 */
	public static void skipFully(InputStream is, long skip) throws IOException {
		long skipped = 0;
		while(skipped < skip) {
			long x = is.skip(skip - skipped);
			if(x <= 0) throw new IOException("Unable to skip "+(skip - skipped)+" bytes");
			skipped += x;
		}
	}

	public static boolean writeTo(InputStream input, File target) throws FileNotFoundException, IOException {
		DataInputStream dis = null;
		FileOutputStream fos = null;
		File file = File.createTempFile("temp", ".tmp", target.getParentFile());
//		if(Logger.shouldLog(Logger.MINOR, FileUtil.class))
//			Logger.minor(FileUtil.class, "Writing to "+file+" to be renamed to "+target);
		
		try {
			dis = new DataInputStream(input);
			fos = new FileOutputStream(file);

			int len = 0;
			byte[] buffer = new byte[4096];
			while ((len = dis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if(dis != null) dis.close();
			if(fos != null) fos.close();
		}
		
		if(FileUtil.renameTo(file, target))
			return true;
		else {
			file.delete();
			return false;
		}
	}
        
        public static boolean renameTo(File orig, File dest) {
        	//System.out.println("FileUtil reanme " + orig.getAbsolutePath() + " to " + dest.getAbsolutePath());
            // Try an atomic rename
            // Shall we prevent symlink-race-conditions here ?
            if(orig.equals(dest))
                throw new IllegalArgumentException("Huh? the two file descriptors are the same!");
            if(!orig.exists()) {
            	throw new IllegalArgumentException("Original doesn't exist!");
            }
            if (!orig.renameTo(dest)) {
                // Not supported on some systems (Windows)
                if (!dest.delete()) {
                    if (dest.exists()) {
                    	System.err.println("FileUtil: Could not delete " + dest + " - check permissions");
      //                  Logger.error("FileUtil", "Could not delete " + dest + " - check permissions");
                    }
                }
                if (!orig.renameTo(dest)) {
//                    Logger.error("FileUtil", "Could not rename " + orig + " to " + dest +
//                            (dest.exists() ? " (target exists)" : "") +
//                            (orig.exists() ? " (source exists)" : "") +
//                            " - check permissions");
                    return false;
                }
            }
            return true;
        }

	public static String sanitize(String s) {
		StringBuffer sb = new StringBuffer(s.length());
		for(int i=0;i<s.length();i++) {
			char c = s.charAt(i);
			if((c == '/') || (c == '\\') || (c == '%') || (c == '>') || (c == '<') || (c == ':') || (c == '\'') || (c == '\"'))
				continue;
			if(Character.isDigit(c))
				sb.append(c);
			else if(Character.isLetter(c))
				sb.append(c);
			else if(Character.isWhitespace(c))
				sb.append(' ');
			else if((c == '-') || (c == '_') || (c == '.'))
				sb.append(c);
		}
		return sb.toString();
	}

	public static String sanitize(String filename, String mimeType) {
		filename = sanitize(filename);
		if(mimeType == null) return filename;
		if(filename.indexOf('.') >= 0) {
			String oldExt = filename.substring(filename.lastIndexOf('.'));
			if(DefaultMIMETypes.isValidExt(mimeType, oldExt)) return filename;
		}
		String defaultExt = DefaultMIMETypes.getExtension(filename);
		if(defaultExt == null) return filename;
		else return filename + '.' + defaultExt;
	}

	/**
	 * Find the length of an input stream. This method will consume the complete
	 * input stream until its {@link InputStream#read(byte[])} method returns
	 * <code>-1</code>, thus signalling the end of the stream.
	 * 
	 * @param source
	 *            The input stream to find the length of
	 * @return The numbe of bytes that can be read from the stream
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static long findLength(InputStream source) throws IOException {
		long length = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int read = 0;
		while (read > -1) {
			read = source.read(buffer);
			if (read != -1) {
				length += read;
			}
		}
		return length;
	}

	/**
	 * Copies <code>length</code> bytes from the source input stream to the
	 * destination output stream. If <code>length</code> is <code>-1</code>
	 * as much bytes as possible will be copied (i.e. until
	 * {@link InputStream#read()} returns <code>-1</code> to signal the end of
	 * the stream).
	 * 
	 * @param source
	 *            The input stream to read from
	 * @param destination
	 *            The output stream to write to
	 * @param length
	 *            The number of bytes to copy
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void copy(InputStream source, OutputStream destination, long length) throws IOException {
		long remaining = length;
		byte[] buffer = new byte[BUFFER_SIZE];
		int read = 0;
		while ((remaining == -1) || (remaining > 0)) {
			read = source.read(buffer, 0, ((remaining > BUFFER_SIZE) || (remaining == -1)) ? BUFFER_SIZE : (int) remaining);
			if (read == -1) {
				if (length == -1) {
					return;
				}
				throw new EOFException("stream reached eof");
			}
			destination.write(buffer, 0, read);
			if (remaining > 0)
				remaining -= read;
		}
	}

	/** Delete everything in a directory. Only use this when we are *very sure* there is no
	 * important data below it! */
	public static boolean removeAll(File wd) {
		if(!wd.isDirectory()) {
			System.err.println("DELETING FILE "+wd);
			if(!wd.delete() && wd.exists()) {
				//Logger.error(FileUtil.class, "Could not delete file: "+wd);
				return false;
			}
		} else {
			File[] subfiles = wd.listFiles();
			for(int i=0;i<subfiles.length;i++) {
				if(!removeAll(subfiles[i])) return false;
			}
			if(!wd.delete()) {
				//Logger.error(FileUtil.class, "Could not delete directory: "+wd);
			}
		}
		return true;
	}

}
