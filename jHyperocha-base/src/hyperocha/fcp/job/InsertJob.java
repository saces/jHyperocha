/**
 * 
 */
package hyperocha.fcp.job;

import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.NodeMessage;

/**
 * @author saces
 *
 */
public abstract class InsertJob extends DataJob {

	private FreenetKey insertUri;
	protected boolean compress = true;
	protected boolean earlyencode = false;
	private FreenetKey requestKey = null;

	protected FreenetKey getInsertUri() {
		return insertUri;
	}

	protected InsertJob(String id, FreenetKey uri) {
		super(id);
		insertUri = uri;
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.FCP2Job#incomingMessage(java.lang.String, hyperocha.freenet.fcp.FCP2NodeMessage)
	 */
	@Override
	public void incomingMessage(String id, NodeMessage msg) {
		//System.out.println("FCPLog "+this+' '+msg);
		if (msg.isMessageName("URIGenerated")) {
			requestKey = msg.getKeyValue("URI");
			onURIGenerated();
			return;
		}

		if (msg.isMessageName("PutFetchable")) {
			requestKey = msg.getKeyValue("URI");
			onPutFetchable();
			//System.out.println("CHK ins PF: " + message);
			// if fast mode setSuccess();
			return;
		}

		if (msg.isMessageName("PutSuccessful")) {
			requestKey = msg.getKeyValue("URI");
			setSuccess();
			return;
		}

		if (msg.isMessageName("PutFailed")) {
			//targetKey = FreenetKey.CHKfromString((String)message.get("URI"));
			setError(msg);
			return;
		}

		super.incomingMessage(id, msg);
	}

	/**
     * Overwrite this to get notified on URI generated.
     * 
     */
	protected abstract void onURIGenerated();

	/**
     * Overwrite this to get notified on put is fetchable
     * 
     */
	protected abstract void onPutFetchable();

	protected String getInsertKey() {
		return (insertUri == null ? "CHK@": insertUri.toString());
	}

	public String getRequestKey() {
		return (requestKey == null ? null: requestKey.toString());
	}

	protected void setRequestKey(FreenetKey key) {
		requestKey = key;
	}

}
