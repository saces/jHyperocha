package hyperocha.ant;

import java.io.OutputStream;

import hyperocha.fcp.FCPSession;
import hyperocha.fcp.IdentifierUtil;
import hyperocha.fcp.NodeConfig;
import hyperocha.fcp.io.FCPLogger;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public abstract class FCPTask extends Task {
	private String fcpHost;
	private int fcpPort;
	private int fcpTimeout;
	private boolean logFCP;
	private String sessionIdentifier;
	private String identifier;

	public void setFCPHost(String host) {
		fcpHost = host;
	}

	public void setFCPPort(int port) {
		fcpPort = port;
	}

	public void setFCPTimeout(int timeout) {
		fcpTimeout = timeout;
	}

	public void setFCPLog(boolean log) {
		logFCP = log;
	}

	public void setSessionIdentifier(String sessionidentifier) {
		sessionIdentifier = sessionidentifier;
	}

	public void setIdentifier(String id) {
		identifier = id;
	}

	static class FCPLog implements FCPLogger {

		public OutputStream getOutputStream() {
			return System.err;
		}
	}

	@Override
	public final void execute() throws BuildException {
		try {
			innerExecute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void innerExecute() {
		FCPSession session = createSession();
		execute(session);
		session.stopSession();
	}

	protected abstract void execute(FCPSession session);

	private FCPSession createSession() {
		NodeConfig conf = new NodeConfig();
		conf.setHost(fcpHost);
		conf.setFCPPort(fcpPort);
		conf.setNodeName(getSessionIdentifier());
		FCPSession sess = new FCPSession(conf, (logFCP?new FCPLog():null));
		sess.startSession();
		return sess;
	}

//	public String getFcpHost() {
//		return fcpHost;
//	}
//
//	public int getFcpPort() {
//		return fcpPort;
//	}
//
//	public int getFcpTimeout() {
//		return fcpTimeout;
//	}
//
//	public boolean isLogFCP() {
//		return logFCP;
//	}
//
//	public String getConnectionIdentifier() {
//		return sessionIdentifier;
//	}

	public String getSessionIdentifier() {
		if (sessionIdentifier == null) {
			sessionIdentifier = IdentifierUtil.getNewConnectionId("hyperocha-ant-");
		}
		return sessionIdentifier;
	}

	public String getIdentifier() {
		if (identifier == null) {
			identifier = IdentifierUtil.getNewConnectionId("hyperocha-ant-");
		}
		return identifier;
	}

}
