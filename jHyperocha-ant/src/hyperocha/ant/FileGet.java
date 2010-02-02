/**
 * 
 */
package hyperocha.ant;

import java.io.File;

import hyperocha.fcp.FCPSession;
import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.job.impl.FileGetJob;

/**
 * @author saces
 *
 */
public class FileGet extends DataTask {
	private String msg;
	
	private FreenetKey fetchURI;
	private File targetFile;

	// The setter for the "message" attribute
	public void setMessage(String msg) {
		this.msg = msg;
	}

	public void setUri(String uri) {
		fetchURI = FreenetKey.getKeyFromString(uri);
	}

	public void setFile(String file) {
		targetFile = new File(file);
	}

	@Override
	protected void execute(FCPSession session) {
		FileGetJob fgj = new FileGetJob(getIdentifier(), fetchURI, targetFile);
		session.runJob(fgj);
	}
	
	public static void main(String[] args) {
		FileGet fg = new FileGet();
		fg.setFCPPort(9482);
		fg.setUri("KSK@gpl.txt");
		fg.setFile("test.txt");
		fg.execute();
	}

}
