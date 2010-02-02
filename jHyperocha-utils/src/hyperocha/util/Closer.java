package hyperocha.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * 
 * @author saces
 */
public class Closer {

	public static void close(Closeable c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (IOException e) {
		}
	}

}
