package hyperocha.fcp;

public abstract class FCPPutCommand extends FCPDataCommand {

	protected FCPPutCommand(String commandname) {
		super(commandname);
	}

	public void setCompress(boolean e) {
		putBoolValue("DontCompress", !e);
	}

	public void setCompress() {
		setCompress(true);
	}

	public void setDontCompress() {
		setCompress(false);
	}

	public void setCompressLzma() {
		setCompress(true);
		setCodec("LZMA");
	}

	public void setCodec(String codecs) {
		putStringValue("Codecs", codecs);
	}

	public void setEarlyEncode(boolean ee) {
		putBoolValue("EarlyEncode", ee);
	}

	public void setGenerateCHKOnly() {
		setURI("CHK@");
		putBoolValue("GetCHKOnly", true);
	}

}
