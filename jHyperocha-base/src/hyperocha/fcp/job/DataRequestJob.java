/**
 * 
 */
package hyperocha.fcp.job;

import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.cmd.ClientGet;
import hyperocha.fcp.io.FCPInputStream;

/**
 * @author saces
 *
 */
public abstract class DataRequestJob extends RequestJob {

	/**
	 * @param id
	 */
	public DataRequestJob(String id, FreenetKey key) {
		super(id, key);
	}

	@Override
	protected ClientGet composeClientGetCommand() {
		ClientGet cmd = super.composeClientGetCommand();
		cmd.setReturnDirect();
		return cmd;
	}

	@Override
	protected void onDataFound(FCPInputStream fis, long size) {
		// TODO Auto-generated method stub
		
	}
}
