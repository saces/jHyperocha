/**
 * 
 */
package hyperocha.fcp.job;

import hyperocha.fcp.FreenetKey;

/**
 * @author saces
 *
 */
public abstract class FileRequestJob extends RequestJob {

	/**
	 * @param id
	 */
	public FileRequestJob(String id, FreenetKey fetchkey) {
		super(id, fetchkey);
	}
	
//	/* (non-Javadoc)
//	 * @see hyperocha.freenet.fcp.FCP2Job#incomingMessage(java.lang.String, hyperocha.freenet.fcp.FCP2NodeMessage)
//	 */
//	@Override
//	public void incomingMessage(String id, NodeMessage msg) {
//		if (msg.isMessageName("DataFound")) {
//			// TODO set mime+size here
//			if (isPersistent()) {
//				// send getRequeststatus
//			} else {
//				// do nothing, data messege follow
//			}
//			return;
//		}
//		
//		System.err.println("DataRequestJob.incomingMessage: "+msg);
//		if (msg.isMessageName("GetFailed")) {
//			setError(msg);
//			return;
//		}
//		super.incomingMessage(id, msg);
//	}
	
//	@Override
//	public void incomingData(String id, NodeMessage msg, FCPInputStream fis) {
//		System.out.println("KSK DataHandler: " + msg);
//		if (msg.isMessageName("AllData")) {
//			int size = msg.getIntValue("DataLength");
//			// TODO FIXME
////			switch (_target.getType()) {
////				case DownloadTo.DIRECT_DATA: {
////					byte[] b = _target.getTargetDirectData();
////					b = new byte[size];
////					fis.read(b, 0, size);
////					_target.setTargetDirectData(b);
////					break;
////				}
////				default: throw new Error("Buh, missing stuff");	
////			}
//		
//			//SystemUploadFrom.out.println("DataHandler: " + message);
//			//conn.copyFrom(size, os);
//			// FIXME: daten sind ins file copiert, feierabend
//			setSuccess();
//			return;
//		}
//		if (msg.isMessageName("DataChunk")) {
//			long size = msg.getLongValue("Length", 16); 
//			//System.out.println("KSK save DataHandler: " + message);
//			//conn.copyFrom(size, os);
//			// FIXME: daten sind ins file copiert, feierabend
//			//setSuccess();
//			return;
//		}
//		System.out.println("KSK DataHandler (unhandled): " + msg);
//		//if (true) { throw new Error(); }
//	}
//
//	
//	private ClientGet composeClientGetCommand() {
//		ClientGet cmd = new ClientGet();
//		cmd.setIdentifier(getJobID());
//		cmd.setURI(_fetchKey.getReadFreenetKey());
//		cmd.setPersistenceConnection();
//		cmd.setVerbosityAll();
//		cmd.setGlobal(isGlobal());
//		return cmd;
//	}
//	
//	@Override
//	public void runFCP(FCPJobRunner jobRunner, boolean resume) {
//		ClientGet cmd = composeClientGetCommand();
////		switch (_target.getType()) {
////			case DownloadTo.DIRECT_DATA: { cmd.setReturnDirect();
////										break;
////									  }
////			default: throw new Error("Buh, missing stuff");	
////		}
//		System.out.println("testi: "+this+' '+cmd);
//		jobRunner.send(cmd);
//	}
//	
//	public byte[] getData() {
//		return null; //_target.getTargetDirectData();
//	}
}
