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
 * <p>hyperocha's freentkey
 * <p>Keys: [freenet:]sss@jdj,aab,xxx/bla/bla
 * <pre>
 *   sss@ - type
 *   jdj,aab,xxx - the key itselfs
 *   /bla/bla - filename (or intepreted as metadata) 
 * </pre>
 *   
 * all static functions expect an untainted key-string 
 * @author  saces
 * @version $Id: FreenetKey.java,v 1.13 2007/01/02 16:47:22 saces Exp $
 */
public class FreenetKey {
	private FreenetKeyType keyType;
	private String routeKey;  
	private String cryptoKey;  
	private String extra; 
	private String docName;
	private String fileName;
	private int revision = 0;  // negative means update first

	/**
	 * 
	 */
	private FreenetKey() {

	}
	
	public FreenetKey(FreenetKeyType keytype, String pubkey, String cryptokey, String suffix) {
		keyType = keytype;
		routeKey = pubkey;  
		cryptoKey = cryptokey;  
		extra = suffix;  
	}
	
	public boolean isFreenetKeyType(FreenetKeyType keytype) {
		return keyType.equals(keytype);
	}
	
	/**
	 * returns the key without metadata
	 * @return String the key without metadata
	 */
	public String getBaseKey() {
		if (keyType.equals(FreenetKeyType.KSK)) {
			return "KSK@" + fileName;
		}
		String s = keyType.toString() + '@' + routeKey;
		//if (cryptoKey != null) {
			s = s + ',' + cryptoKey;
		//}
		//if (extra != null) {
			s = s + ',' + extra;
		//}
		return s;
	}
	
	public String getReadFreenetKey() {

		String s = getBaseKey();

		if (keyType.equals(FreenetKeyType.KSK)) {
			return s;
		}
		
		if (keyType.equals(FreenetKeyType.CHK)) {
			return s + '/' +  (fileName == null ? "" : fileName);
		}
		//String s = keyType + "@" + pubKey + "," + cryptoKey + "," + extra + "/";

		// docname-revision
		if (false) {
			if (isSetDocName()) {
				s = s + '/' + docName + '-' + revision;
			}
		} else {
	        if (docName != null) {
	        	s = s + '/' + docName;
	        }
	        if (revision != 0) {
	        	s = s + '/' + revision;
	        } else {
	        	if (true) {
	        		s = s + "/-1";
	        	}
	        }
		}
		if (false) {
			s = s + "//";
		} else {
			s = s + "/";
		}
		
		if (fileName != null) {
			s = s + fileName;
		}
		//return "" + keyType + pubKey + "," + cryptoKey + "," + suffiX + "/";
		return s;
	}
	
	private boolean isSetDocName() {
		return ((docName != null) && (docName.trim().length() > 0));
	}

	/** 
	 * returns a string representation of the (hyperocha) Key
	 * 
	 */
	@Override
	public String toString() {
		String s = ""; // = privKey == null ? "" : "[INSERT]freenet:" + getWriteFreenetKey() + "[REQUEST]";
		return s + "freenet:" + getReadFreenetKey();
	}
	
	/**
	 * - trim<br> 
	 * - remove 'freenet:'<br>
	 * - check min length (5)
	 * @param aString the string who needs wellnes
	 * @return null if param is null or length < 5, otherwise the niced string
	 */
	public static String freenetKeyStringNiceness(String aString) {
		if (aString == null) { return null; }
		String s = aString.trim();
		if (s.startsWith("freenet:")) {
			s = s.substring(8);
		}
		if (s.length() < 5) { //at least KSK@x
			return null;
		}
		return s;
	}

	public String getPublicPart() {
		return routeKey;
	}

	public String getCryptoPart() {
		return cryptoKey;
	}

	public String getSuffixPart() {
		return extra;
	}

	/**
	 * return the filename part of the key
	 * @return filename or null if empty
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * return true if the filename part of the key is empty
	 * @return false, if not
	 */
	public boolean isFileNameEmpty() {
		if (fileName == null) { 
			return true; 
		}
		if ((fileName.trim()).length() == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param k
	 * @return key
	 */
	public static FreenetKey KSKfromString(String k) {
		FreenetKey newKey = getKeyFromString(k); 
		if (newKey != null) {
			if (!newKey.isFreenetKeyType(FreenetKeyType.KSK)) {
				return null;
			}
		}
		return newKey;
	}
	
	/**
	 * @param k
	 * @return key
	 */
	public static FreenetKey CHKfromString(String k) {
		FreenetKey newKey = getKeyFromString(k); 
		if (newKey != null) {
			if (!newKey.isFreenetKeyType(FreenetKeyType.CHK)) {
				return null;
			}
		}
		return newKey;
	}
	
	
	
	public static FreenetKey getKeyFromStringNoEx(String aKey) {
		FreenetKey key;
		try {
			key = getKeyFromString(aKey);
		} catch (Exception ex) {
			// TODO what is todo?
			ex.printStackTrace();
			return null;
		}
		return key;
	}
	
	public static FreenetKey getKeyFromString(String aKey) {
		String tmpS = freenetKeyStringNiceness(aKey);
		if ( tmpS == null ) { return null; }
		
		FreenetKey newKey = new FreenetKey();
		
//		 decode keyType
        int atchar = tmpS.indexOf('@');
        if (atchar == -1) { // kein '@' drinne, buh!
        	return null;
        }
		newKey.keyType = FreenetKeyType.getTypeFromString(tmpS.substring(0, atchar).toUpperCase().trim());
		tmpS = tmpS.substring(atchar+1);
        
        if ( newKey.keyType == null ) { // buh! kein gueltiger freenet key type
        	return null;
        }
        
        
        if ( newKey.keyType.equals(FreenetKeyType.KSK) ) {
        	newKey.fileName = tmpS;
        	// KSK kann .5 und .7 sein, mal sehen was der bei 1.0 alles kann ;)
        	return newKey;        	
        }

        // meta string abschnippeln
        int slash2 = tmpS.indexOf('/');
        if (slash2 != -1) { // ein '/' drinne, alles danach kommt nach fileName
        	newKey.fileName = tmpS.substring(slash2 + 1);
        	tmpS = tmpS.substring(0 ,slash2);
        }

		// wenn nicht ksk ist jetzt der nakte zahlenmus uebrig
        int comma = tmpS.indexOf(',');
        if ( comma == 43 || comma == 44 ) { // 0.7 key
        	newKey.routeKey = tmpS.substring(0, comma);
        	newKey.cryptoKey = tmpS.substring(comma+1,comma+44);
        	//if (tmpS.length() > 94) {
        	newKey.extra = tmpS.substring(comma+45,comma+52);
        	//}
        } else {	//        	 .5 krempel
        	//newKey.setNetworkType(Network.FCP1);
//        	newKey.routeKey = tmpS.substring(0,31);
//        	if ( comma == 31) { 
//        		newKey.cryptoKey = tmpS.substring(32,54);
//        	}
        	throw new Error("key invalid: " + aKey);
        }
        
        // der zahlenmus ist jetzt hinfort, alles nach dem
        // ersten slash ist in filename
        if ( newKey.keyType.equals(FreenetKeyType.CHK) ) {
        	// chk behält den filenamen wie er ist
        	return newKey;        	
        }
        
        // Sites sind übrig, doc und revision rausknobeln
       	// FCP version ist nun bekannt

        if ((newKey.isFreenetKeyType(FreenetKeyType.SSK) && ((newKey.fileName == null) || (newKey.fileName.length() == 0)))) {
        	// this is a just generatet key from the node, site and rev are emty
        	return newKey;
        }
        
       	tmpS = newKey.fileName;
       	newKey.fileName = null;
       	
       	// key/docname-revision/filename
        if ((newKey.isFreenetKeyType(FreenetKeyType.SSK)) && (true)) {
        	int schar = tmpS.lastIndexOf('-');
            if (schar == -1) { // kein '-' drinne, buh!
             	return null;
            }
            newKey.docName = tmpS.substring(0, schar);
            tmpS = tmpS.substring(schar+1);
            
            // alles bis zum nächsten slash ist revisionsnummer, 
            // der rest ist filename
            String rev;
            int slash1 = tmpS.indexOf('/');
            if (slash1 == -1) { 
             	rev = tmpS;
            } else {
            	rev = tmpS.substring(0, slash1);
            	newKey.fileName = tmpS.substring(slash1+1);
            }
            newKey.revision = Integer.parseInt(rev);
        	return newKey;
        }
        
       	// key/docname/revision/filename
        if ((newKey.isFreenetKeyType(FreenetKeyType.USK)) && (true)) {
        	int schar = tmpS.indexOf('/');
            if (schar == -1) { // kein '/' drinne, buh!
             	return null;
            }
            newKey.docName = tmpS.substring(0, schar);
            tmpS = tmpS.substring(schar+1);
            
            // alles bis zum nächsten slash ist revisionsnummer, 
            // der rest ist filename
            String rev;
            int slash1 = tmpS.indexOf('/');
            if (slash1 == -1) { 
             	rev = tmpS;
            } else {
            	rev = tmpS.substring(0, slash1);
            	newKey.fileName = tmpS.substring(slash1+1);
            }
            newKey.revision = Integer.parseInt(rev);
        	return newKey;
        }
        
        // key/docname/revision//filename
        // bis zum // docname/revision, danach filename
        if (false) {
        	String docrev;
        	int dslash = tmpS.indexOf("//");
        	if (dslash == -1) { // buh!
        		docrev = tmpS;
        	} else {
        		docrev = tmpS.substring(0, dslash);
            	newKey.fileName = tmpS.substring(dslash+2);
        	}
        	// filename ist weg,
        	// jetzt docrev aufdröseln
        	int slash4 = docrev.indexOf('/');
        	if (slash4 == -1) { // kein /, nur docname
        		newKey.docName = docrev;
        	} else {
        		newKey.docName = docrev.substring(0, slash4);
        		docrev = docrev.substring(slash4+1);
        		newKey.revision = Integer.parseInt(docrev);
        	}
        	return newKey;
        }
        
        // TODO
        System.out.println("hu?");
        new Error().printStackTrace();
		return null;
	}

	/**
	 * @param aKey
	 * @return true if param entspricht irgendein valid freenet key schema
	 */
	public static boolean isValidKey(String aKey) {
		return ( getKeyFromString(aKey) != null );
	}
	
	/**
	 * @param aKey
	 * @return
	 */
	public static String decodeKeyFromNode(String aKey) {
		String r = aKey;
		if( aKey.indexOf("%") > 0 ) {
			try {
				r = java.net.URLDecoder.decode(aKey, "UTF-8");
			} catch (java.io.UnsupportedEncodingException ex) {
				// this is really an error!!!!!!! (no utf-8)
				throw new Error(ex);
			}
		}
		return r;	
	}

	
	/**
	 * debug und test: print detailed key 
	 */
	public void decompose() {
        String r = routeKey == null ? "none" : routeKey;
        //String w = privKey == null ? "none" : privKey;
        String k = cryptoKey == null ? "none" : cryptoKey;
        String e = extra == null ? "none" : extra;
        System.out.println("FreenetKey: " + this.toString());
        
        System.out.println("Key type     : " + keyType);
        System.out.println("Read key     : " + r);
        //System.out.println("Write key    : " + w);
        System.out.println("Crypto key 	 : " + k);
        System.out.println("Extra      	 : " + e);
        System.out.println("Revision   	 : " + revision);
        System.out.println("Doc name   	 : " + (docName == null ? "none" : docName));
        System.out.println("File name    : " + (fileName == null ? "none" : fileName));
    }

	/**
	 * @return the docName
	 */
	public String getDocName() {
		return docName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String filename) {
		fileName = filename;
	}
	
}
