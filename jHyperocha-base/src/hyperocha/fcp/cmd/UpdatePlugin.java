package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPCommand;


public class UpdatePlugin extends FCPCommand {

	public UpdatePlugin() {
		super("GetPluginInfo");
	}
	
	public void setPluginName(String pluginName) {
		putStringValue("PluginName", pluginName);
	}

	public void setDetailed(boolean detailed) {
		putBoolValue("Detailed", detailed);
	}

	public void setDetailed() {
		setDetailed(true);
	}

}
