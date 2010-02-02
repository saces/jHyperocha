/**
 * 
 */
package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPPutCommand;

/**
 * 
 *
 */
public class ClientPutComplexDir extends FCPPutCommand {

	public ClientPutComplexDir() {
		super("ClientPutComplexDir");
	}

	public void setDefaultName(String name) {
		putStringValue("DefaultName", name);
	}

	public void addFileDisk(int index, String name, String diskfilename) {
		addFileDisk(index, name, diskfilename, null);
	}

	public void addFileDisk(int index, String name, String diskfilename, String mime) {
		putStringValue("Files."+index+".Name", name);
		putStringValue("Files."+index+".UploadFrom", "disk");
		if (mime != null) {
			putStringValue("Files."+index+".Metadata.ContentType", mime);
		}
		putStringValue("Files."+index+".Filename", diskfilename);
	}

	public void addFileDirect(int index, String name, long length, String mime) {
		putStringValue("Files."+index+".Name", name);
		putStringValue("Files."+index+".UploadFrom", "direct");
		if (mime != null) {
			putStringValue("Files."+index+".Metadata.ContentType", mime);
		}
		putLongValue("Files."+index+".DataLength", length);
		payloadLength += length;
	}

	public void addFileRedirect(int index, String name, String targeturi, String mime) {
		putStringValue("Files."+index+".Name", name);
		putStringValue("Files."+index+".UploadFrom", "redirect");
		if (mime != null) {
			putStringValue("Files."+index+".Metadata.ContentType", mime);
		}
		putStringValue("Files."+index+".TargetURI", targeturi);
	}
}
