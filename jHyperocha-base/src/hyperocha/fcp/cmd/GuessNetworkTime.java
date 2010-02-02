/**
 * 
 */
package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPCommand;


/**
 * @author saces
 *
 */
public class GuessNetworkTime extends FCPCommand {
	
	public GuessNetworkTime() {
		super("GuessNetworkTime");
	}
	
	public void setIncludeMap() {
		putBoolValue("IncludeMap", true);
	}
}
