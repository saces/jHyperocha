/**
 * 
 */
package hyperocha.fcp.job.impl;

import hyperocha.fcp.IdentifierUtil;
import hyperocha.fcp.FreenetKeyPair;
import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.cmd.GenerateSSK;
import hyperocha.fcp.job.FCPJob;
import hyperocha.fcp.job.FCPJobRunner;

/**
 * @author saces
 *
 */
public class GenerateSSKJob extends FCPJob {
	
	private final int _count; // how many keypairs?
	private FreenetKeyPair uri;

	public GenerateSSKJob() {
		this(1);
	}

	public GenerateSSKJob(int num) {
		this(IdentifierUtil.getNewConnectionId(), num);
	}

	/**
	 * 
	 */
	public GenerateSSKJob(String id, int num) {
		super(id);
		_count = num;
	}
	
	@Override
	public void incomingMessage(String id, NodeMessage msg) {
		if (msg.isMessageName("SSKKeypair")) {
			uri = FreenetKeyPair.getKeyPairFromNodeMessage(msg);
			setSuccess();
			return;
		}
		super.incomingMessage(id, msg);
		System.err.println("hallo: " + msg);
	}

	@Override
	public void runFCP(FCPJobRunner jobRunner, boolean resume) {
		GenerateSSK cmd = new GenerateSSK();
		cmd.setIdentifier(getJobID());
		jobRunner.send(cmd);
	}

	public FreenetKeyPair getKeyPair() {
		return uri;
	}

}
