/**
 * 
 */
package hyperocha.ant;

import java.io.File;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import hyperocha.fcp.FCPSession;
import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.job.impl.SitePutJob;
import hyperocha.util.SimpleDirParser;

/**
 * @author saces
 *
 */
public class SitePut extends DataTask {

	protected FreenetKey insertURI;
	protected String indexName;
	protected Vector<BaseItem> items;
	protected String propertyName;

	public void setUri(String uri) {
		if ((uri.trim().length() < 5) && (uri.toUpperCase().startsWith("CHK"))) {
			insertURI = null; // generate
			return;
		}
		insertURI = FreenetKey.getKeyFromString(uri);
	}

	public void setIndexName(String name) {
		indexName = name;
	}
	
	public void setPropertyName(String name) {
		propertyName = name;
	}

	@Override
	protected void execute(FCPSession session) {
		//System.out.print("execute "+this);

		if (items == null)
			throw new BuildException("Add some content first!");

		final SitePutJob spj = new SitePutJob(getIdentifier(), insertURI, indexName);

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

	public abstract class BaseItem {
		String itemName;
		public void setName(String name) {
			itemName = name;
		}
	}

	public final class DiskDir extends BaseItem {
		File dir;
		public void setPath(String path) {
			//dir = new File(path).getAbsoluteFile();
			dir = new File(path);
		}
	}

	public final class FileItem extends BaseItem {
		File file;
		public void setPath(String path) {
			//file = new File(path).getAbsoluteFile();
			file = new File(path);
		}
	}

	public DiskDir createDiskDir() throws BuildException {
		DiskDir dd = new DiskDir();
		addItem(dd);
		return dd;
	}

	public FileItem createFileItem() throws BuildException {
		FileItem fi = new FileItem();
		addItem(fi);
		return fi;
	}

	private void addItem(BaseItem item) {
		if (items == null) {
			items = new Vector<BaseItem>();
		}
		items.add(item);
	}

}
