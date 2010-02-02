package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPCommand;


public class FCPPluginMessage extends FCPCommand {
	
	public FCPPluginMessage() {
		super("FCPPluginMessage");
	}
	
	public void setPluginName(String pluginName) {
		putStringValue("PluginName", pluginName);
	}
	
	public void addPluginParam(String item, boolean value) {
		putBoolValue("Param."+item, value);
	}
	
	public void addPluginParam(String item, String value) {
		putStringValue("Param."+item, value);
	}
	
	public void addPluginParam(String item, int value) {
		putIntValue("Param."+item, value);
	}
	
	public void addPluginParam(String item, long value) {
		putLongValue("Param."+item, value);
	}

	public void setDataLength(int length) {
		payloadLength = length;
		putIntValue("DataLength", length);
	}

}
