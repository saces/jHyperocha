package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPCommand;


public class LoadPlugin extends FCPCommand {

	public LoadPlugin() {
		super("LoadPlugin");
	}

	public void setPluginURL(String url) {
		putStringValue("PluginURL", url);
	}

	public void setURLType(String type) {
		putStringValue("URLType", type);
	}

	public void setURLTypeLocalFile() {
		putStringValue("URLType", "file");
	}

	public void setURLTypeFreenet() {
		putStringValue("URLType", "freenet");
	}

	public void setURLTypeOfficial() {
		putStringValue("URLType", "official");
	}

	public void setURLTypeURL() {
		putStringValue("URLType", "url");
	}

	public void setURLTypeAuto() {
		fields.remove("URLType");
	}
}
