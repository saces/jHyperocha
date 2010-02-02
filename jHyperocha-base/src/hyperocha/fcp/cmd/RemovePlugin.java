package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPCommand;


public class RemovePlugin extends FCPCommand {

	public RemovePlugin() {
		super("RemovePlugin");
	}

	public void setPluginName(String pluginName) {
		putStringValue("PluginName", pluginName);
	}

	public void setPurge(boolean purge) {
		putBoolValue("Purge", purge);
	}

	public void setPurge() {
		setPurge(true);
	}

}
