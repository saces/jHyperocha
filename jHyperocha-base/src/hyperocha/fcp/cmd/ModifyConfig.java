/**
 * 
 */
package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPCommand;

/**
 * @author saces
 *
 */
public class ModifyConfig extends FCPCommand {

	public ModifyConfig() {
		super("ModifyConfig");
	}
	
	public void setBandwith(String value) {
		putStringValue("node.outputBandwidthLimit", value);
	}
}
