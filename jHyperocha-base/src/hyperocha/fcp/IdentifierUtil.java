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

/**
 * @author saces
 *
 */
public class IdentifierUtil {
	
	private static long counter = 0;
	
	private static long lasttimestamp;
	
	static synchronized long getUniqueTimeStamp() {
		long now;
		while ((now = System.currentTimeMillis()) <= lasttimestamp) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		lasttimestamp = now;
		return lasttimestamp;
	}
	
	static synchronized long getNextCounter() {
		return counter++;
	}

	/**
	 * 
	 */
	public static String getNewConnectionId() {
		return getNewConnectionId("", "");
	}
	
	public static String getNewConnectionId(String prefix) {
		return getNewConnectionId(prefix, "");
	}
	
	public static String getNewConnectionId(String prefix, String suffix) {
		return (prefix + getUniqueTimeStamp() + suffix);
	}
	
	public static long getNextConnectionId() {
		return getNextCounter();
	}
	
	public static String getNewConnectionId2() {
		return getNewConnectionId2("", "");
	}
	
	public static String getNewConnectionId2(String prefix) {
		return getNewConnectionId2(prefix, "");
	}
	
	public static String getNewConnectionId2(String prefix, String suffix) {
		return (prefix + getNextCounter() + suffix);
	}

}
