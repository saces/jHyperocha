/**
 * 
 */
package hyperocha.fcp.job;

import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.cmd.ClientGet;
import hyperocha.fcp.io.FCPInputStream;

/**
 * @author saces
 *
 */
public abstract class RequestJob extends DataJob {

	private final FreenetKey _fetchKey;

	/**
	 * @param id
	 */
	public RequestJob(String id, FreenetKey fetchkey) {
		super(id);
		_fetchKey = fetchkey;
	}
	
	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.FCP2Job#incomingMessage(java.lang.String, hyperocha.freenet.fcp.FCP2NodeMessage)
	 */
	@Override
	public void incomingMessage(String id, NodeMessage msg) {
		if (msg.isMessageName("DataFound")) {
			if (isPersistent()) {
				// send getRequeststatus
			} else {
				// do nothing, data messege follow
			}
			return;
		}
		if (msg.isMessageName("GetFailed")) {
			setError(msg);
			return;
		}
		super.incomingMessage(id, msg);
	}

	@Override
	public void incomingData(String id, NodeMessage msg, FCPInputStream fis) {
		System.out.println("DataHandler: " + msg);
		if (msg.isMessageName("AllData")) {
			long size = msg.getLongValue("DataLength");
			try {
				onDataFound(fis, size);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			setSuccess();
			return;
		}

		System.out.println("DataHandler (unhandled): " + msg);
	}

	protected abstract void onDataFound(FCPInputStream fis, long size);

	protected ClientGet composeClientGetCommand() {
		ClientGet cmd = new ClientGet();
		cmd.setIdentifier(getJobID());
		cmd.setURI(_fetchKey.getReadFreenetKey());
		cmd.setPersistenceConnection();
		//cmd.setPersistenceForever();
		//cmd.setGlobal();
		cmd.setVerbosityAll();
		cmd.setGlobal(isGlobal());
		return cmd;
	}

	@Override
	public void runFCP(FCPJobRunner jobRunner, boolean resume) {
		ClientGet cmd = composeClientGetCommand();
//		switch (_target.getType()) {
//			case DownloadTo.DIRECT_DATA: { cmd.setReturnDirect();
//										break;
//									  }
//			default: throw new Error("Buh, missing stuff");	
//		}
		System.out.println("testi: "+this+' '+cmd);
		jobRunner.send(cmd);
	}
//	
//	public byte[] getData() {
//		return null; //_target.getTargetDirectData();
//	}

	@Override
	protected void onSimpleProgress(boolean isFinalized, long totalBlocks,
			long requiredBlocks, long doneBlocks, long failedBlocks,
			long fatallyFailedBlocks) {
		// TODO Auto-generated method stub
		
	}

}
