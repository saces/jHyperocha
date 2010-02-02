/**
 * 
 */
package hyperocha.fcp;

import java.io.InputStream;

import hyperocha.fcp.FCPCommand;
import hyperocha.fcp.io.FCPLogger;
import hyperocha.fcp.job.FCPJobRunner;

/**
 * can run many jobs on a single connection including persitence
 * 
 *  @author   saces  the FCPThingy class ;)
 */
public class FCPSession extends FCPJobRunner {

	private final FCPNode _node;
	private FCPConnectionRunner _cr;
	private final FCPLogger _fcpLogger;

	/**
	 * 
	 */
	public FCPSession() {
		String serverport = System.getProperty("hyperocha.freenet.fcp.serverport");
		String sessionid = System.getProperty("hyperocha.freenet.fcp.sessionid");

		if (serverport == null) {
			System.err.println("Property \"hyperocha.freenet.fcp.serverport\" not set!");
			throw new IllegalArgumentException();
		}

		if (sessionid == null) {
			System.err.println("Property \"hyperocha.freenet.fcp.sessionid\" not set!");
			throw new IllegalArgumentException();
		}

		if (sessionid.equals("AUTO")) {
			sessionid = IdentifierUtil.getNewConnectionId();
		}
		_fcpLogger = null;
		_node = new FCPNode(sessionid, serverport, this);
	}

	public FCPSession(String ident, String serverport, FCPLogger fcpLogger) {
		_node = new FCPNode(ident, serverport, this);
		_fcpLogger = fcpLogger;
	}

	public FCPSession(FCPNode node, FCPLogger fcpLogger) {
		_node = node;
		_fcpLogger = fcpLogger;
	}

	public FCPSession(NodeConfig nodeconf, FCPLogger fcpLogger) {
		_node = new FCPNode(nodeconf, this);
		_fcpLogger = fcpLogger;
	}

	public FCPSession(String serverport) {
		this(IdentifierUtil.getNewConnectionId(), serverport, null);
	}

	public String getSessionID() {
		return _node.getID();
	}

//	public void send(String id, String key) {
//		FCPConnectionRunner cr = node.getDefaultFCPConnectionRunner();
//		FCPCommand cmd = new FCPCommand("ClientGet");
//		cmd.setIdentifier(id);
//		cmd.setPriority(PriorityClass.INTERACTIVE);
//		cmd.setRetryNone();
//		cmd.setPersistence(Persistence.CONNECTION);
//		cr.send(cmd);
//	}

	public boolean isOnline() {
		return _node.isOnline();
	}

	public void startSession() {
		_node.goOnline();
		// TODO check
		getCR();
	}

	public void stopSession() {
		_node.goOffline();
		if (_cr != null)
			_cr.close();
	}

	@Override
	public void send(FCPCommand cmd) {
		getCR().send(cmd);		
	}

	private FCPConnectionRunner getCR() {
		if (_cr == null) {
			_cr = _node.getDefaultFCPConnectionRunner(_fcpLogger);
		}
		return _cr;
	}

	@Override
	public void write(InputStream inputStream, long length) {
		getCR().write(inputStream, length);
	}

	@Override
	public void write(byte[] b) {
		getCR().write(b);
	}

}
