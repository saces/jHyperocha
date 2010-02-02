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
public class Verbosity {
	public static final Verbosity ALL = new Verbosity(-1);
	public static final Verbosity NONE = new Verbosity(0);
	public static final Verbosity SPLITFILE_PROGRESS = new Verbosity(1);
	public static final Verbosity PUT_FETCHABLE = new Verbosity(256);
	public static final Verbosity COMPRESSION_START_END = new Verbosity(512);

	private int verbosity;

	/**
	 * @param verbosity
	 */
	public Verbosity(int aVerbosity) {
		verbosity = aVerbosity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Verbosity)) return false; 
		return ((Verbosity)obj).verbosity == verbosity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + verbosity;
	}
	
	public int getVerbosity() {
		return verbosity;
	}

}
