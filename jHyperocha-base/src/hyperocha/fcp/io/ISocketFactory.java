/**
 * 
 */
package hyperocha.fcp.io;

import java.io.IOException;
import java.net.Socket;

/**
 * @author saces
 *
 */
public interface ISocketFactory {
	Socket getNewSocket() throws IOException;
}
