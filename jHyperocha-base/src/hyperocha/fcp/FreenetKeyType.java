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
public final class FreenetKeyType {
    public static final FreenetKeyType KSK = new FreenetKeyType((short)0);
    public static final FreenetKeyType CHK = new FreenetKeyType((short)1);
    public static final FreenetKeyType SSK = new FreenetKeyType((short)2);
    public static final FreenetKeyType USK = new FreenetKeyType((short)3);
    public static final FreenetKeyType TUK = new FreenetKeyType((short)4);
    
    private short _keyType;
    
    // the same order as declerd
    private static final String[] _names = { "KSK",
                                             "CHK",
                                             "SSK",
                                             "USK",
    										 "TUK" };
    
    /**
     *
     */
    private FreenetKeyType(short keyType) {
        _keyType = keyType;
    }
    
    
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
    @Override
	public boolean equals(Object obj) {
        if (!(obj instanceof FreenetKeyType)) return false;
        return ((FreenetKeyType)obj)._keyType == _keyType;
    }
    
    /**
     *
     */
    @Override
	public String toString() {
        return _names[_keyType];
    }

    /**
     * Returns a list of key types
     */
    public static String[] getFreenetKeyTypes() {
        return _names;
    }
    
    /**
     * @param aType ein string, der vermutlich ein keytype ist
     * @return FreenetKeyType oder null, wenn schief
     */
    public static FreenetKeyType getTypeFromString(String type) {
        if ( type == null ) {
            return null;
        }
        for (short i = 0; i < _names.length; i++) {
            if (type.equalsIgnoreCase(_names[i])) {
                return new FreenetKeyType(i);
            }
        }
        return null;
    }
}
