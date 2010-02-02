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
 * @version $Id: Persistence.java,v 1.4 2006/11/16 20:08:39 saces Exp $
 *
 */
public enum Persistence {
	CONNECTION("connection"),
	REBOOT("reboot"),
	FOREVER("forever");

	private String name;

	private Persistence(String aName) {
		name = aName;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
