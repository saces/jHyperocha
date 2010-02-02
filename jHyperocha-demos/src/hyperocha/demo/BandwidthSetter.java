/*
 *   This file is part of JHyperochaFCPLib.
 *
 *   Copyright (C) 2008  Hyperocha Project <saces@users.sourceforge.net>
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
package hyperocha.demo;

import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.cmd.ModifyConfig;
import hyperocha.fcp.io.FCPConnection;
import java.io.IOException;

/**
 * 
 * @author saces
 */
public class BandwidthSetter {

	private BandwidthSetter() {
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		if (args.length != 2) {
			printUsage();
			System.exit(1);
		}

		String node = args[0];

		FCPConnection conn = null;
		try {
			conn = new FCPConnection(node);
		} catch (IOException ex) {
			System.err.println("Could not connect to node '" + node + "'.");
			ex.printStackTrace();
			System.exit(2);
		}

		ModifyConfig cmd = new ModifyConfig();
		cmd.setAutoIdentifier("hello");
		cmd.setBandwith(args[1]);
		try {
			conn.send(cmd);
		} catch (IOException ex) {
			System.err.println("Could not send command: " + cmd);
			ex.printStackTrace();
			System.exit(3);
		}
		try {
			NodeMessage msg = conn.readEndMessage();
			if (msg.isMessageName("ConfigData")) {
				// supi
				conn.close();
				System.exit(0);
			}
			System.err.println("Unecpectet reply from node: " + msg);
			System.exit(5);

		} catch (IOException ex) {
			System.err.println("IO Error while reciving reply");
			ex.printStackTrace();
			System.exit(4);
		}
	}

	private static void printUsage() {
		System.out.println("BandwidthSetter - set nodes line limit");
		System.out.println("Usage: BandwidthSetter server:port limit");
	}

}
