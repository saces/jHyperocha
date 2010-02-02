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

public class PriorityClass {

    public static final PriorityClass MAXIMUM = new PriorityClass("(0) maximum", 0);
    public static final PriorityClass INTERACTIVE = new PriorityClass("(1) interactive", 1);
    public static final PriorityClass SEMI_INTERACTIVE = new PriorityClass("(2) semiinteractive", 2);
    public static final PriorityClass UPDATABLE = new PriorityClass("(3) updatable", 3);
    public static final PriorityClass BULK = new PriorityClass("(4) bulk", 4);
    public static final PriorityClass PREFETCH = new PriorityClass("(5) prefetch", 5);
    public static final PriorityClass MINIMUM = new PriorityClass("(6) minimum", 6);

    private String name;
    private int value;
    
    private static PriorityClass[] a = {
    		MAXIMUM, 			//0
    		INTERACTIVE,		//1
    		SEMI_INTERACTIVE,	//2
    		UPDATABLE,			//3
    		BULK, 				//4
    		PREFETCH,			//5
    		MINIMUM 			//6
    		};

    private PriorityClass(String aName, int aValue) {
            name = aName;
            value = aValue;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
            return name;
    }
       
    static public String getPriorityName(int p) {
    	return a[p].getName();
    }
    
    static public PriorityClass getPriorityClass(int p) {
    	return a[p];
    }
    
    static public PriorityClass getPriorityClassFromName(String name) {
    	return getPriorityClass(getIDfromName(name));
    }

    /**
     * @return Returns the value.
     */
    public int getValue() {
            return value;
    }
    
    /**
     * @return Returns the value.
     */
    public String getValueString() {
            return Integer.toString(value);
    }
    
	@Override
	public String toString() {
    	return name;
    } 
    
    public static int getIDfromName(String name) {
    	if (name.compareToIgnoreCase("(0) maximum") == 0) { return 0; }
    	if (name.compareToIgnoreCase("(1) interactive") == 0) { return 1; }
    	if (name.compareToIgnoreCase("(2) semiinteractive") == 0) { return 2; }
    	if (name.compareToIgnoreCase("(3) updatable") == 0) { return 3; }
    	if (name.compareToIgnoreCase("(4) bulk") == 0) { return 4; }
    	if (name.compareToIgnoreCase("(5) prefetch") == 0) { return 5; }
    	if (name.compareToIgnoreCase("(6) minimum") == 0) { return 6; }
    	return -1;
    }
}
