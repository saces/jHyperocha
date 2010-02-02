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
public class TestDDAResponse extends FCPCommand {
	
	public TestDDAResponse(String dirname, String readContent) {
		this(new File(dirname), readContent);
	}

	public TestDDAResponse(File dir, String readContent) {
		super("TestDDAResponse");
		putStringValue("Directory", dir.getAbsolutePath());
		putStringValue("ReadContent", readContent);
	}

}
