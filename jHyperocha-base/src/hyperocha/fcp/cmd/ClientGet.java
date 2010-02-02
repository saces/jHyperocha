/**
 * 
 */
package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPDataCommand;


/**
 * @author saces
 *
 */
public class ClientGet extends FCPDataCommand {


	public ClientGet() {
		super("ClientGet");
	}

	private void setDataStore(boolean o, boolean i) {
		putBoolValue("IgnoreDS", i);
		putBoolValue("DSOnly", o);
	}

	public void setDataStoreIgnore() {
		setDataStore(false, true);
	}

	public void setDataStoreOnly() {
		setDataStore(true, false);
	}

	public void setDataStoreNormal() {
		setDataStore(false, false);
	}
	
	public void setReturnFilename(String target) {
		setReturnFilename(target, null);
	}
	
	public void setReturnNone() {
		fields.remove("Filename");
		fields.remove("TempFilename");
		putStringValue("ReturnType", "none");
	}
	
	public void setReturnFilename(String target, String tmptarget) {
		putStringValue("ReturnType", "disk");
		putStringValue("Filename", target);
		if (tmptarget != null) {
			putStringValue("TempFilename", tmptarget);
		}
	}
	
	public void setReturnDirect() {
		fields.remove("Filename");
		fields.remove("TempFilename");
		putStringValue("ReturnType", "direct");
	}

	public void setMaxSize(int i) {
		putIntValue("MaxSize", i);
	}
}
