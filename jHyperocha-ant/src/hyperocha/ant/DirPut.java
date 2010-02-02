/**
 * 
 */
package hyperocha.ant;

import java.io.File;

import hyperocha.fcp.FCPSession;
import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.job.impl.DirPutJob;

/**
 * Put a dir
 * @author saces
 *
 */
public class DirPut extends DataTask {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DirPut dp = new DirPut();
		dp.setFCPPort(9482);
		dp.setUri("CHK@");
		dp.setDir("apidocs");
		dp.execute();
	}

	private FreenetKey insertURI;
	private String sourcedir;
	private String indexName;

	public void setUri(String uri) {
		if ((uri.trim().length() < 5) && (uri.toUpperCase().startsWith("CHK"))) {
			insertURI = null; // generate
			return;
		}
		insertURI = FreenetKey.getKeyFromString(uri);
	}

	public void setDir(String dir) {
		sourcedir = dir;
	}

	public void setIndexName(String indexname) {
		indexName = indexname;
	}

	@Override
	protected void execute(FCPSession session) {
		DirPutJob dpj = new DirPutJob(getIdentifier(), insertURI, new File(sourcedir), indexName);
		session.runJob(dpj);
	}

}
