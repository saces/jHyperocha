/**
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
package hyperocha.fcp.io;

import hyperocha.fcp.IdentifierUtil;
import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.cmd.ClientHello;

import java.io.IOException;

/**
 * @author saces
 * @version $Id: FCPConnection.java,v 1.25 2007/01/02 16:47:22 saces Exp $
 */
public class FCPConnection extends FCPIOConnection {
	public final static String CONNECTION_ID_PREFIX = "hyperocha-fcplib-";

	// private Exception lasterror = null;

	private final String connectionID;

	private NodeMessage heloMsg;

	/**
	 * 'ready to use' c'tors
	 * 
	 */

	public FCPConnection(String serverport, String id) throws IOException {
		this(new SimpleSocketFactory(serverport), id, null);
	}

	public FCPConnection(String serverport) throws IOException {
		this(new SimpleSocketFactory(serverport), null);
	}

	public FCPConnection(String host, int port) throws IOException {
		this(new SimpleSocketFactory(host, port), null);
	}

	public FCPConnection(ISocketFactory sfac) throws IOException {
		this(sfac, null);
	}

	public FCPConnection(ISocketFactory sfac, FCPLogger fcpLogger) throws IOException {
		this(sfac, IdentifierUtil.getNewConnectionId(CONNECTION_ID_PREFIX), fcpLogger);
	}

	/**
	 * 'ready to use' constructor
	 * 
	 * all other things full automagically fcp1: sending the 4 bytes fcp2: helo.
	 * 
	 * @param node
	 * @param id
	 *            the connection id
	 * @param fcplogger 
	 * @param prefix
	 *            if true, a timstamp will be added <id>-<unixepoch>
	 * @param attempts
	 *            how many tries?
	 */
	public FCPConnection(ISocketFactory sfac, String id, FCPLogger fcplogger) throws IOException {
		super(sfac, fcplogger);
		connectionID = id;
		// callBack = null;
		initFCP2(5);
	}

	private void initFCP2(int tries) throws IOException {
		// try {
		open();
		// } catch (IOException e) {
		// handleIOError(e);
		// return;
		// }
		fcp2Hello(connectionID, tries);
	}

	/**
	 * returns the ConnectionID, bestaetigt from node or null, if the connection
	 * is closed (not opened or closed due an io error or call to close() )
	 * 
	 * @return coonection id
	 */
	public String getConnectionID() {
		return connectionID;
	}

	public boolean isValid() {
		// TODO return (connectionID != null);
		return isIOValid();
	}

	public boolean isIOValid() {
		return isOpen();
	}

	/**
	 * reads the connection to the next EndMessage end return the entire message
	 * 
	 * @return message
	 * @throws IOException
	 */
	public void startMonitor(IIncoming callback) throws IOException {

		NodeMessage msg = null;

		while (isOpen()) {
			msg = readEndMessage();
			if (msg == null) {
				break;
			} // this indicates an error, io connection closed

			//Logger.debug(this, "recived msg: "+msg);
			String tmpID = msg.getStringValue("Identifier");
			if (tmpID == null) {
				if (msg.isMessageName("TestDDAReply") || msg.isMessageName("TestDDAComplete")) {
					tmpID = msg.getStringValue("Directory");
				} else {
					tmpID = connectionID;
				}
			}

			if (msg.haveData()) {
				int len = -1;
				if (msg.isMessageName("AllData") || msg.isMessageName("FCPPluginReply")) {
					len = msg.getIntValue("DataLength");
				} else {
					//Logger.error(this, "Real ERROR, protocol changes?", new Error("DEBUG"));
					throw new Error("Real ERROR, protocol changes?");
				}
				FCPInputStream fis = getFCPInputStream(len);
				callback.incomingData(tmpID, msg, fis);
				fis.skip(Integer.MAX_VALUE);
			} else {
				callback.incomingMessage(tmpID, msg);
			}
		}
	}

	private String fcp2Hello(String connectionid, int attempts) throws IOException {
		if (attempts < 1)
			return null;
		heloMsg = helo(IdentifierUtil.getNewConnectionId(connectionid));
		// FCPUtil.getNewConnectionId("hyperocha-")
		// FIXME: repeat loop here and return id
		// System.out.println("fcp2Hello: " + heloMsg);
		return null;
	}

	/**
	 * the real hello
	 * 
	 * @param connectionid
	 * @return
	 * @throws IOException
	 */
	private NodeMessage helo(String clientname) throws IOException {
		ClientHello cmd = new ClientHello(clientname);
		send(cmd);
		return readEndMessage();
	}

	public NodeMessage getFCPInfo() {
		return heloMsg;
	}
}
