/**
 * 
 */
package hyperocha.protocol.fcp;

import hyperocha.fcp.IdentifierUtil;
import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.PriorityClass;
import hyperocha.fcp.cmd.ClientGet;
import hyperocha.fcp.io.FCPInputStream;
import hyperocha.fcp.job.FCPJob;
import hyperocha.fcp.job.FCPJobRunner;

import java.io.EOFException;
import java.io.IOException;

/**
 * @author   saces
 */
public class FetchJob extends FCPJob {
	
	FreenetKey key;
	String mime;
	int length;
	
	byte[] buff;
	
	ClientGet getCmd;
	
	FCPJobRunner jr;
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public FetchJob(FreenetKey fkey) {
		super(IdentifierUtil.getNewConnectionId("javafs-"));
		key = fkey;
		prepare();
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.dispatcher.job.Job#runFCP2(hyperocha.freenet.dispatcher.Dispatcher, boolean)
	 */
	@Override
	public void runFCP(FCPJobRunner jobRunner, boolean resume) {

		jr = jobRunner;
		getCmd = new ClientGet();
		getCmd.setDataStoreNormal();
		//cmd.setURI("USK@MYLAnId-ZEyXhDGGbYOa1gOtkZZrFNTXjFl1dibLj9E,Xpu27DoAKKc8b0718E-ZteFrGqCYROe7XBBJI57pB4M,AQACAAE/SiteTool/2");
		getCmd.setURI(key.toString());
		System.err.println("fetch key: "+key.toString());
//		if (useFilename) { 
//			cmd.add("URI=" + keyToDownload.getReadFreenetKey());
//		} else {
//			cmd.add("URI=" + keyToDownload.getBaseReadKey());
//		}
	
		getCmd.setIdentifier(getJobID()); 
		getCmd.setVerbosityAll();
		
		getCmd.setRetryNone();       
		getCmd.setPriority(PriorityClass.INTERACTIVE);
		getCmd.setPersistenceConnection();
		getCmd.setGlobal(false);
		getCmd.setReturnDirect();
		
		jobRunner.send(getCmd);
		System.err.println("cmd: "+getCmd);
		
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.dispatcher.job.Job#incomingData(java.lang.String, hyperocha.freenet.fcp.NodeMessage, hyperocha.freenet.fcp.FCPConnection)
	 */
	@Override
	public void incomingData(String id, NodeMessage msg, FCPInputStream fis) {
		System.err.println("recv data msg: "+msg);
		if (msg.isMessageName("AllData")) {
			length = msg.getIntValue("DataLength");
			buff = new byte[length];
			//System.err.println("Hehe1: "+length+" -> "+buff.length);
			int off = 0;
			
			try {
				
				int n = 0;
				while (n < length) {
				    int count = fis.read(buff, off + n, length - n);
				    if (count < 0)
					throw new EOFException();
				    n += count;
				}
				//System.err.println("Hehe2: "+i+" -> "+length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.err.println("Hehe2: "+i+" -> "+length);
		}
		
		setSuccess();
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.dispatcher.job.Job#incomingMessage(java.lang.String, hyperocha.freenet.fcp.NodeMessage)
	 */
	@Override
	public void incomingMessage(String id, NodeMessage msg) {
		System.err.println("recv msg: "+msg);
		if (msg.isMessageName("DataFound")) {
			mime = msg.getStringValue("Metadata.ContentType");
			length = msg.getIntValue("DataLength");
			return;
		}
		if (msg.isMessageName("GetFailed")) {
			int code = msg.getIntValue("Code");
			if ((code == 27) || (code == 11)) {
				getCmd.setURI(msg.getKeyString("RedirectURI"));
				jr.send(getCmd);
				System.err.println("resent: "+getCmd);
				return;
			}
		}
		if (msg.isMessageName("IdentifierCollision")) {
			// FIXME to fast?
			jr.send(getCmd);
			return;
		}
		super.incomingMessage(id, msg);
	}
}
