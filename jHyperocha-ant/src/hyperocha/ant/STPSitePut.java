/**
 * 
 */
package hyperocha.ant;

import hyperocha.fcp.FCPSession;
import hyperocha.fcp.job.impl.STPSitePutJob;
import hyperocha.util.SimpleDirParser;

import java.io.File;

import org.apache.tools.ant.BuildException;

/**
 * @author saces
 *
 */
public class STPSitePut extends SitePut {

	@Override
	protected void execute(FCPSession session) {
		if (items == null)
			throw new BuildException("Add some content first!");

		final STPSitePutJob spj = new STPSitePutJob(getIdentifier(), insertURI, indexName);

		for (final BaseItem item:items) {
			//System.out.println("Process item: "+item.itemName);
			if (item instanceof FileItem) {
				spj.addFileItem(item.itemName, ((FileItem)item).file);
				continue;
			}
			if (item instanceof DiskDir) {
				SimpleDirParser parser = new SimpleDirParser() {
					@Override
					public void onFileItem(String name, File f) {
						//System.out.println("Add: ("+item.itemName+") '"+name+"' "+f.getAbsolutePath());
						spj.addFileItem(item.itemName+name, f);
					}
					
				};
				SimpleDirParser.parse(((DiskDir)item).dir, parser);
				continue;
			}
			throw new IllegalArgumentException("Invalid item: "+item);
		}

		session.runJob(spj);
		if (!spj.isSuccess())
			throw new BuildException();
		System.out.println("Put successful: "+spj.getRequestKey());
		if (propertyName != null) {
			getProject().setProperty(propertyName, spj.getRequestKey());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
