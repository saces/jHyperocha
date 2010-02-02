/*
 * FCPConnection.java
 *
 * Created on 15. Mai 2007, 17:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hyperocha.fcp.io;

import hyperocha.fcp.FCPCommand;
import hyperocha.fcp.NodeMessage;
import hyperocha.util.FileUtil;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * 
 * @author saces
 */
public class FCPIOConnection implements Closeable {

	private final ISocketFactory _socketFactory;
	private Socket _fcpSock;
	private InputStream _fcpIn;
	private OutputStream _fcpOut;
	private final FCPLogger _fcpLogger;

	public FCPIOConnection(ISocketFactory socketFactory, FCPLogger fcpLogger) {
		_socketFactory = socketFactory;
		_fcpLogger = fcpLogger;
	}

	public void open() throws IOException {
		_fcpSock = _socketFactory.getNewSocket();
		_fcpOut = _fcpSock.getOutputStream();
		_fcpIn = _fcpSock.getInputStream();
	}

	public void close() {
		// ignore errors while closing
		// egal, die connection ist eh ungueltig danach.
		try {
			_fcpOut.close();
		} catch (Throwable e) {
		}
		try {
			_fcpIn.close();
		} catch (Throwable e) {
		}
		try {
			_fcpSock.close();
		} catch (Throwable e) {
		}
		_fcpOut = null;
		_fcpIn = null;
		_fcpSock = null;
	}

	// DEBUG the IOErrorHandler should have called close (set sock = null)
	// before this
	// here can thrown
	protected boolean isOpen() {
		if (_fcpSock == null)
			return false;
		if (_fcpSock.isClosed())
			throw new Error("Sock closed");
		if (!_fcpSock.isConnected())
			throw new Error("not Connected");
		return true;
	}

	protected void setTimeOut(int to) throws SocketException {
		_fcpSock.setSoTimeout(to);
	}

	public void write(byte[] b) throws IOException {
		_fcpOut.write(b);
	}

	private void write(String s) throws IOException {
		_fcpOut.write(s.getBytes("UTF-8"));
	}

	private void write(int i) throws IOException {
		_fcpOut.write(i);
	}

	public void write(InputStream is, long length) throws IOException {
		FileUtil.copy(is, _fcpOut, length);
	}

	public String readLine() throws IOException {
		int b;
		byte[] bytes = new byte[4096]; // a key and a funny dir/filename in a
										// ssk site, 256 is verry knapp
		int count = 0;
		String result = null;

		while (((b = _fcpIn.read()) != '\n') && (b != -1) && (count < 4096) && (b != '\0')) {
			bytes[count] = (byte) b;
			count++;
		}
		if (count == 0) {
			return null;
		}
		result = new String(bytes, 0, count, "UTF-8");
		if (_fcpLogger != null) {
			_fcpLogger.getOutputStream().write(bytes);
			_fcpLogger.getOutputStream().write('/');
		}
		return result;
	}

	/**
	 * reads the connection to the next EndMessage end return the entire message
	 * 
	 * @return message
	 */
	public NodeMessage readEndMessage() throws IOException {
		NodeMessage result;
		String tmp;

		// the first line is the message name
		tmp = readLine();
		if (tmp == null) {
			throw new SocketException("Remote closed");
		}
		result = new NodeMessage(tmp);
		while (true) {
			tmp = readLine();
			if (((tmp.trim()).length()) == 0) {
				continue; // an empty line, jump over
			} 
			if (tmp.compareTo("Data") == 0) {
				result.setEnd(tmp);
				break;
			}
			if (tmp.compareTo("EndMessage") == 0) {
				result.setEnd(tmp);
				break;
			}
			if (tmp.indexOf("=") > -1) {
				String[] tmp2 = tmp.split("=", 2);
				result.addItem(tmp2[0], tmp2[1]);
			} else {
				System.out.println("this shouldn't happen. FIXME. mpf!");
				result.addItem("Unknown", tmp);
			}
		}
		return result;
	}

	public void send(FCPCommand cmd) throws IOException {
		// System.err.println(cmd);
		cmd.write(_fcpOut);
		if (_fcpLogger != null) {
			cmd.write(_fcpLogger.getOutputStream());
		}
	}

	public void read(byte[] b, int off, int len) throws IOException {
		if (len < 0)
			throw new IndexOutOfBoundsException();
		int n = 0;
		while (n < len) {
			int count = _fcpIn.read(b, off + n, len - n);
			if (count < 0)
				throw new EOFException();
			n += count;
		}
	}

	/**
	 * @param skipped
	 * @return
	 * @throws IOException
	 * @deprecated
	 */
	@Deprecated
	public long skip(long skipped) throws IOException {
		return _fcpIn.skip(skipped);
	}

	public void flush() throws IOException {
		_fcpOut.flush();
	}

	public FCPInputStream getFCPInputStream(int bytestoread) {
		FCPInputStream fis = new FCPInputStream(_fcpIn, bytestoread);
		return fis;
	}
	
}
