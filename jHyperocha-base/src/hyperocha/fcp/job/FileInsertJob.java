/**
 * 
 */
package hyperocha.fcp.job;

import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.cmd.ClientPut;
import hyperocha.fcp.FCPCommand;

/**
 * @author saces
 *
 */
public abstract class FileInsertJob extends DataJob {
	
	private String insertUri;
	protected boolean compress = true;
	protected boolean earlyencode = false;
	private FreenetKey requestKey = null;
	
	protected void setInsertUri(String newUri) {
		insertUri = newUri;
	}
	
	protected String getInsertUri() {
		return insertUri;
	}
	
	protected FileInsertJob(String id, String inserturi) {
		super(id);
		insertUri = inserturi;
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.FCP2Job#incomingMessage(java.lang.String, hyperocha.freenet.fcp.FCP2NodeMessage)
	 */
	@Override
	public void incomingMessage(String id, NodeMessage msg) {
		System.out.println("FCPLog "+this+' '+msg);
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
			//System.out.println("CHK ins PS: " + message);
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
	protected void onURIGenerated() {
		
	}

	/**
     * Overwrite this to get notified on put is fetchable
     * 
     */
	protected void onPutFetchable() {
		
	}
	
	protected abstract FCPCommand getPutCommand2();
	
	protected FCPCommand getPutCommand() {
		ClientPut cmd = new ClientPut();
		return cmd;
	}

	protected FCPCommand composePutCommand() {
		ClientPut cmd = (ClientPut) getPutCommand();
		cmd.setIdentifier(getJobID());
		cmd.setURI(getInsertKey());
		//cmd.setGlobal(getrue);
		cmd.setPersistenceConnection();
		cmd.setCompress(compress);
		cmd.setEarlyEncode(earlyencode);
		cmd.setVerbosityAll();
		return cmd;
	}
	
    protected String getInsertKey() {
    	return (insertUri == null ? "CHK@": insertUri);
    }

//	private void applyDirectData(ClientPut cmd) {
//		cmd.
//		if (data != null) {
//				cmd.
//				
//			}
//			
//		}
//
//		_source.apply(cmd);
//		cmd.s
//		System.out.println("testi: "+cmd);
//		return cmd;
//	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.FCP2Job#runFCP2(hyperocha.freenet.fcp.FCP2JobRunner, boolean)
	 */
	@Override
	public void runFCP(FCPJobRunner jobRunner, boolean resume) {
		//FCPClientPut cmd = composePutCommand();
		FCPCommand cmd = getPutCommand2();
		//System.err.println(cmd);
		jobRunner.send(cmd);
	}
	

}
