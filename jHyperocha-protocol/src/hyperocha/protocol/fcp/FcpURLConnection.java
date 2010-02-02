/**
 * 
 */
package hyperocha.protocol.fcp;

import hyperocha.fcp.FCPSession;
import hyperocha.fcp.FreenetKey;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author saces
 *
 */
public class FcpURLConnection extends URLConnection {
	
	private FetchJob fj = null;

	private FreenetKey key;
	
	private final FCPSession _session;
	
	/**
	 * @param url
	 */
	public FcpURLConnection(URL aUrl, FCPSession session) {
		super(aUrl);
		_session = session;
		//System.out.println("FCPURLConnection <init>: "+aUrl.getFile());
	}

	/* (non-Javadoc)
	 * @see java.net.URLConnection#connect()
	 */
	@Override
	public void connect() throws IOException {
		if (connected) {
			System.out.println(this+"already connected");
			return;
		}
		//System.out.println("connecting... -> "+this.url);
		//System.out.println("file = " + url.getFile().substring(1));
		key = FreenetKey.getKeyFromString(url.getFile().substring(1));
		//System.out.println("url = " + url);
		//System.out.println("key = " + key);
		if (key == null) {
			throw new IOException("Invalid freenet key: "+url.getFile());
		}
		
		
//		_session.startSession();
//		
//		if (!_session.isOnline()) {
//			throw new IOException("Node not online!");
//		}
		
		connected = true;
	}

	/* (non-Javadoc)
	 * @see java.net.URLConnection#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		connect();

		//System.out.println("get inputstream -> "+this);
	    if(!connected) throw new IOException("Not connected");
	    //if(stat==null) throw new IOException("File not found");
	    //if(stat.type==FSPstat.RDTYPE_DIR) throw new IOException("Is a directory");
	    
		fj = new FetchJob(key);
		
		System.out.println("start job");
		_session.runJob(fj);
		System.out.println("ende job");
		
		if (!fj.isSuccess()) {
			throw new IOException("!!debug!!");
		}
		//fetch fcpconn from x
		//send get

	    return new ByteArrayInputStream(fj.buff);
	}

	/* (non-Javadoc)
	 * @see java.net.URLConnection#getContentLength()
	 */
	@Override
	public int getContentLength() {
		return fj.length;
	}

	/* (non-Javadoc)
	 * @see java.net.URLConnection#getContentType()
	 */
	@Override
	public String getContentType() {
		return fj.mime;
	}
}
