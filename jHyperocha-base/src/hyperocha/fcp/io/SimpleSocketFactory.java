/*
 * SocketFactory.java
 *
 * Created on 15. Mai 2007, 20:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hyperocha.fcp.io;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author saces
 */
public class SimpleSocketFactory implements ISocketFactory {
    
    private String _host;
    private int _port;
    private int _to = 60 * 60 * 1000;
    
    /** Creates a new instance of SocketFactory */
    public SimpleSocketFactory(String host, int port) {
        _host = host;
        _port = port;
    }
    
    public SimpleSocketFactory(String host, int port, int timeout) {
    	this(host, port);
    	_to = timeout;
    }
    
    public SimpleSocketFactory(String serverport) {
    	String s[] = serverport.split(":");
        _host = s[0];
        _port = Integer.parseInt(s[1]);
    }

    public Socket getNewSocket() throws IOException {
        Socket s = new Socket(_host, _port);
        s.setSoTimeout(_to);
        return s;
    }

	public boolean isHostInList(String host, int port) {
		return (_host.equals(host) && ((port == -1)?true:(_port==port)));
	}
}
