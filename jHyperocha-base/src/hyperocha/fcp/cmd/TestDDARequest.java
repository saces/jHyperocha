/**
 * 
 */
package hyperocha.fcp.cmd;


import hyperocha.fcp.FCPCommand;

import java.io.File;

/**
 * @author saces
 *
 */
public class TestDDARequest extends FCPCommand {
	
	public TestDDARequest(String dirname, boolean wantread, boolean wantwrite) {
		this(new File(dirname), wantread, wantwrite);
	}

	public TestDDARequest(File dir, boolean wantread, boolean wantwrite) {
		super("TestDDARequest");
		putStringValue("Directory", dir.getAbsolutePath());
		putBoolValue("WantReadDirectory", wantread);
		putBoolValue("WantWriteDirectory", wantwrite);
	}

}
