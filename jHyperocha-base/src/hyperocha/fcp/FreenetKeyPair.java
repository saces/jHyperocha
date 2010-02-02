/**
 * 
 */
package hyperocha.fcp;

/**
 * @author saces
 *
 */
public class FreenetKeyPair {
	
	private FreenetKey insertKey;
	private FreenetKey requestKey;
	
	private FreenetKeyPair() {
	}

	public FreenetKeyPair(String insertkey, String requestkey) {
		requestKey = FreenetKey.getKeyFromString(requestkey);
		insertKey = FreenetKey.getKeyFromString(insertkey);
	}

	public static FreenetKeyPair getKeyPairFromNodeMessage(NodeMessage msg) {
		FreenetKeyPair kp = new FreenetKeyPair();
		kp.requestKey = FreenetKey.getKeyFromString(msg.getStringValue("RequestURI"));
		kp.insertKey = FreenetKey.getKeyFromString(msg.getStringValue("InsertURI"));
		return kp;
	}
	
	public String getInsertSSK(String sitename, int edition) {
		return insertKey.getBaseKey() + "/" + sitename + "-" + edition;
	}
	
	public String getInsertBaseSSK() {
		return insertKey.getBaseKey();
	}
	
	public String getRequestSSK(String sitename, int edition) {
		return requestKey.getBaseKey() + "/" + sitename + "-" + edition;
	}
	
	public String getRequestBaseSSK() {
		if (requestKey==null) return "";
		return requestKey.getBaseKey();
	}
}
