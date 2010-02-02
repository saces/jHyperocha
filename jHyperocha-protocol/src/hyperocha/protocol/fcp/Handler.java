package hyperocha.protocol.fcp;

import hyperocha.fcp.FCPSession;
import hyperocha.protocol.fcp.FcpURLConnection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author saces
 *
 */
public class Handler extends URLStreamHandler {
	
	private final FCPSession _session;

	/**
	 * 
	 */
	public Handler() {

		String s = System.getProperty("hyperocha.freenet.fcp.serverport");
		if (s == null) {
			System.err.println("Property \"hyperocha.freenet.fcp.serverport\" not set!");
			throw new IllegalArgumentException();
		}
		
		s = System.getProperty("hyperocha.freenet.fcp.sessionid");
		if (s == null) {
			System.err.println("Property \"hyperocha.freenet.fcp.sessionid\" not set!");
			throw new IllegalArgumentException();
		}

		_session = new FCPSession();
		_session.startSession();
	}

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new FcpURLConnection(url, _session);
	}

}
