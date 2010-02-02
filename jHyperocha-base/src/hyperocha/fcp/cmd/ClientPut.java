/**
 * 
 */
package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPPutCommand;

import java.io.File;

/**
 * @author saces
 *
 */
public class ClientPut extends FCPPutCommand {

	public ClientPut() {
		super("ClientPut");
	}
	
	public void setTargetFilename(String filename) {
		putStringValue("TargetFilename", filename);
	}
	
	public void setNoTargetFilename() {
		setTargetFilename("");
	}
	
	public void setMimeType(String mime) {
		putStringValue("Metadata.ContentType", mime);
	}
	
	public void setMimeOctetStream() {
		setMimeType("application/octet-stream");
	}
	
//	public void setCHKOnly(boolean co) {
//		putBoolValue("GetCHKOnly", co);
//	}
//	
//	public void setCHKOnly() {
//		setURIgenerateCHK();
//		setCHKOnly(true);
//	}
	
	public void setUploadRedirect(String targeturi) {
		putStringValue("UploadFrom", "redirect");
		putStringValue("TargetURI", targeturi);
	}
	
	public void setUploadFrom(File f) {
		putStringValue("UploadFrom", "disk");
		putStringValue("Filename", f.getAbsolutePath());
	}
	
	public void setUploadDirect(long length) {
		//_haveData = true;
		payloadLength = length;
		putStringValue("UploadFrom", "direct");
		putStringValue("DataLength", Long.toString(length));
	}
	
	public void setCompressionCodecs(String codecs) {
		putStringValue("Codecs", codecs);
	}
	
	public void setCompressionCodecLZMA() {
		setCompressionCodecs("LZMA");
	}


}
