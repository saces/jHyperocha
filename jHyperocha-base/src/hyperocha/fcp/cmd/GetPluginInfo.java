package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPCommand;


public class GetPluginInfo extends FCPCommand {

	public GetPluginInfo() {
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
