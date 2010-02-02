/*
 *   This file is part of JHyperochaFCPLib.
 *   
 *   Copyright (C) 2006  Hyperocha Project <saces@users.sourceforge.net>
 * 
 * JHyperochaFCPLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * JHyperochaFCPLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JHyperochaFCPLib; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */
package hyperocha.fcp;

import hyperocha.fcp.io.FCPLogger;
import hyperocha.fcp.io.IIncoming;
import hyperocha.fcp.io.ISocketFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Observable;

/**
 * @author  saces
 * @version $Id: FCPNode.java,v 1.27 2007/01/02 16:47:22 saces Exp $
 */
public class FCPNode extends Observable implements ISocketFactory {
	public static final int STATUS_ERROR = -1;
	public static final int STATUS_OFFLINE = 0;
	public static final int STATUS_REACHABLE = 1;
	public static final int STATUS_ONLINE = 2;

	private class FCPNodeConfig {
		
		private String nodeID = null;
		private InetAddress host = null;
		private int port = -1;
		private String hostName = null; // avoid name lookup recursion in security manager
		private String hostIp = null; // avoid name lookup recursion in security manager
		
		// default timeout 90 minutes
		// its freenet. feel free to be patient ;)
		private int timeOut = 90 * 60 * 1000; 
		
		private boolean canDownload = true; // the factory can use this node for downloads 
		private boolean canUpload = true; // the factory can use this node for uploads
	    private boolean useDDA = true;
	    /* seen an option on earlier versions, must ask toad about this */  
	    private boolean useGQ = true;  
	    
	    /* from fproxy config page:
	     * Whether to enable Persistence=forever for FCP requests. Meaning whether to
	     * support requests which persist over node restarts; they must be written to
	     * disk and this may constitute a security risk for some people.
	     */ 
	    private boolean havePersistence = true;
	} 
	
	private class FCPNodeStatus {
		private int status = STATUS_OFFLINE;
		private long requestCount = -1; // ??
		private long onlineSince = 0;  // unix epoch
		private int averageSpeed = -1; // requests per houeeer???
	}

//	private FCPIOConnectionErrorHandler ioErrorHandler = null;
	private FCPNodeConfig nodeConfig = new FCPNodeConfig();
	private FCPNodeStatus nodeStatus = new FCPNodeStatus();
    private volatile FCPConnectionRunner defaultConnRunner = null;
    
    private IIncoming _callBack = null;
    
    private Exception lastError = null;
    
    public FCPNode(String id, String serverport, IIncoming callback) {
    	nodeConfig.nodeID = id;
		setServerPort(serverport);
		_callBack = callback;
    }

	public FCPNode(NodeConfig nodeconf, IIncoming callback) {
		nodeConfig.nodeID = nodeconf.getNodeName();
		nodeConfig.hostName = nodeconf.getHost();
		nodeConfig.port = nodeconf.getFCPPort();
		_callBack = callback;
		nodeStatus.status = STATUS_OFFLINE;
	}

	private boolean setServerPort(String serverport) {
		boolean retValue = false;
		nodeStatus.status = STATUS_OFFLINE;
		String[] splitServerPort = serverport.split(":");
        try {
        	nodeConfig.hostName = splitServerPort[0];
        	retValue = setPort(splitServerPort[1]);
		} catch (Exception e) {
			lastError = e;
			e.printStackTrace();
			return false;
		}
		return retValue;
	}
    
	/**
	 * 
	 * @return FCPConnectionRunner
	 */
	public synchronized FCPConnectionRunner getDefaultFCPConnectionRunner(FCPLogger fcpLogger) {
		if (defaultConnRunner == null) {
			defaultConnRunner = new FCPConnectionRunner(this, nodeConfig.nodeID, _callBack, fcpLogger);
			defaultConnRunner.start();
		}
		return defaultConnRunner;
	}
	
	public synchronized void closeDefaultConnectionrunner() {
		if (defaultConnRunner != null) {
			defaultConnRunner.close();
			defaultConnRunner = null;
		}
	}
	
	public synchronized FCPConnectionRunner getNewFCPConnectionRunner(String id, FCPLogger fcpLogger) {
		FCPConnectionRunner conn = new FCPConnectionRunner(this, id, _callBack, fcpLogger);
		conn.start();
		return conn;
	}
	
	/**
	 * @return Socket
	 * @throws IOException
	 */
	public Socket createSocket() throws IOException {
		return createSocket(nodeConfig.timeOut);
	}
	
	/**
	 * @param to timeout
	 * @return Socket
	 * @throws IOException
	 */
	public Socket createSocket(int to) throws IOException {
		Socket sock = new Socket(nodeConfig.host, nodeConfig.port);
	    sock.setSoTimeout(to);
	    return sock;
	}
	
//	public FCPConnection getNewFCPConnection() {
//		return new FCPConnection(this, callBack);
//	}
//	
//	public FCPConnection getNewFCPConnection(IIncoming callback, String id) {
//		return new FCPConnection(this, callback, id);
//	}
//	
//	public FCPConnection getNewFCPConnection(IIncoming callback, String id, boolean prefix, int tries) {
//		return new FCPConnection(this, callback, id, prefix, tries);
//	}

	public boolean performNodeTest(FCPNodeConfig config) {
		// check adresse:Port
		// if rechable, check for peers, at least one connected is necesary
		// TODO
		return false;
	}
	
	public boolean performOptionsTest(FCPNodeConfig config) {
		// check for DDA
		// check Global queue?
		// check persitance
		// TODO
		return false;
	}

	public boolean haveDDA() {
		return nodeConfig.useDDA;
	}
	
	public boolean haveGQ() {
		return nodeConfig.useGQ;
	}
	
//	public FCPVersion getFCPVersion() {
//		// TODO
//		FCPVersion result = null;
//		//FCPConnection conn = new FCPConnection("");
//		//result = 
//		return result;		
//	}

	public void goOnline() {
		if (!init()) { return; }
		//FCPConnection conn = getNewFCPConnection();
		//if(!conn.isValid()) { return; } // helo failed
		//conn.close();
		setStatus(STATUS_ONLINE);
	}
	
	public synchronized void goOffline() {
		if (defaultConnRunner != null) {
			defaultConnRunner.close();
			defaultConnRunner = null;
		}
		setStatus(STATUS_OFFLINE);
	}

	public boolean isValid() {
		return (lastError == null);
	}

	public String getID() {
		return nodeConfig.nodeID;
	}

	public boolean isAddress(String host, int port) {
		if( nodeConfig.port == port ) {
			if( host.equals(nodeConfig.hostIp) || host.equals(nodeConfig.hostName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOffline() {
		return (nodeStatus.status < 1);
	}
	
	public boolean isOnline() {
		return (nodeStatus.status > 0);
	}
	
	/**
	 * @param port  the port to set
	 * @return false if port out of range
	 */
	private boolean setPort(int port) {
		if ((port < 1) || (port > 65535)) return false;
		nodeConfig.port = port;
		return true;
	}
	
	private boolean setPort(String port) {
		return setPort(Integer.parseInt(port));
	}	
	
	private boolean init() {
		return init(false);
	}
	
	private void setStatus(int newStatus) {
		//System.err.println("Node Status set: " + newStatus);
		//new Error().printStackTrace();
		if ( nodeStatus.status == newStatus ) { return; }
		setChanged();
		nodeStatus.status = newStatus;
		notifyObservers(Integer.valueOf(newStatus));
	}
	
	private void setError(Exception e) {
		lastError = e;
		//e.printStackTrace();
		setStatus(STATUS_ERROR);
	}
	
	/**
	 * test if server:port is reachable (atablish connection), but 
	 * do not any real transfer
	 * @param force true - reset state before testing 
	 * @return true if 
	 */
	private boolean init(boolean force) {
		if ((!force) && (nodeStatus.status > 0)) { return true; }
		//nodeStatus.status = STATUS_OFFLINE;
		String server = nodeConfig.hostName;
		InetAddress ia = null;
        try {
        	ia = InetAddress.getByName(server);
        	setAddress(ia);
        	Socket testsock = new Socket();
        	testsock.connect(new InetSocketAddress(nodeConfig.host, nodeConfig.port), 100);
        	testsock.close();
        } catch (ConnectException ce) {
        	lastError = ce;
        	setStatus(STATUS_OFFLINE);
        	return false;
		} catch (Exception e) {
			setError(e);
			//e.printStackTrace();
			return false;
		}
		setStatus(STATUS_REACHABLE);
		return true;
	}
	
	private void setAddress(InetAddress ia) {
		nodeConfig.host = ia;
		nodeConfig.hostName = ia.getHostName();
		nodeConfig.hostIp = ia.getHostAddress();
	}
	
	/**
	 * @param haveDDA The haveDDA to set.
	 */
	public void setDDA(boolean haveDDA) {
		nodeConfig.useDDA = haveDDA;
	}
	
	/**
	 * @return Exception the last occured error or null if none occures
	 */
	public Exception getLastError() {
		return lastError;
	}

	public Socket getNewSocket() throws IOException {
		return createSocket();
	}

//	public FCPConnection getNewFCPConnection(String ident) {
//		return new FCPConnection(this, ident);
//	}

}
