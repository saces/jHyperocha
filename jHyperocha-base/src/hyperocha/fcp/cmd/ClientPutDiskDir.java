/**
 * 
 */
package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPPutCommand;


/**
 * 
 *
 */
public class ClientPutDiskDir extends FCPPutCommand {
	
	public ClientPutDiskDir() {
		super("ClientPutDiskDir");
	}

	public void setDefaultName(String name) {
		putStringValue("DefaultName", name);
	}

	public void setDiskDir(String name) {
		putStringValue("Filename", name);
	}

	public void setAllowUnreadableFiles(boolean b) {
		putBoolValue("AllowUnreadableFiles", b);
	}
}
