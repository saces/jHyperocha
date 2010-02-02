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
package hyperocha.fcp;

import java.io.IOException;
import java.io.InputStream;

import hyperocha.fcp.FCPCommand;
import hyperocha.fcp.io.FCPConnection;
import hyperocha.fcp.io.FCPLogger;
import hyperocha.fcp.io.IIncoming;
import hyperocha.fcp.io.ISocketFactory;
import hyperocha.fcp.io.SimpleSocketFactory;

/**
 * @author saces
 * @version $Id: FCPConnectionRunner.java,v 1.8 2006/11/25 14:16:27 saces Exp $
 *
 */
public class FCPConnectionRunner extends Thread {

	private FCPConnection _conn;
	private final ISocketFactory _sf;
	private final IIncoming mCallBack;
	private final FCPLogger _fcpLogger;
	private final String _ident;

	public FCPConnectionRunner(String serverport, String id, IIncoming callback) {
		this(new SimpleSocketFactory(serverport), id, callback, null);
	}

	/**
	 * 
	 */
	public FCPConnectionRunner(ISocketFactory sf, String id, IIncoming callback, FCPLogger fcpLogger) {
		super(id);
		_sf = sf;
		_ident = id;
		mCallBack = callback;
		_fcpLogger = fcpLogger;
		setDaemon(true);
	}

	public synchronized void send(FCPCommand command) {
		try {
			_conn.send(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param b
	 * @deprecated
	 */
	public synchronized void write(byte[] b) {
		try {
			_conn.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param b
	 */
	public synchronized void write(InputStream is, long length) {
		try {
			_conn.write(is, length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see java.lang.Thread#start()
	 * but dosn't return until the connection is up and ready to use (send)
	 */
	@Override
	public final synchronized void start() {
		try {
			_conn = new FCPConnection(_sf, _fcpLogger);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		super.start();
	}

	public synchronized void close() {
		_conn.close();
	}

	@Override
	public final void run() {
		try {
			_conn.startMonitor(mCallBack);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
