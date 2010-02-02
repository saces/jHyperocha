/**
 *   This file is part of JHyperochaFCPLib.
 *   
 *   Copyright (C) 2006  Hyperocha Project <saces@users.sourceforge.net>
 * 
 * JHyperochaFCPLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * JHyperochaFCPLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JHyperochaFCPLib; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */
package hyperocha.fcp.job.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.cmd.ClientPutComplexDir;
import hyperocha.fcp.job.FCPJobRunner;
import hyperocha.fcp.job.InsertJob;

/**
 * Put a complex/virtual dir
 */
public class SitePutJob extends InsertJob {

	private String indexName;
	protected HashMap<String, SiteItem> items;

	abstract class SiteItem {
		protected String name;
	}

	class FileItem extends SiteItem {
		File file;
	}

	public SitePutJob(String id, FreenetKey uri, String indexname) {
		super(id, uri);
		indexName = indexname;
		items = new HashMap<String, SiteItem>();
	}

	public void addFileItem(String name, File file) {
		FileItem fi = new FileItem();
		fi.name = name;
		fi.file = file;
		items.put(name, fi);
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.job.Job#doPrepare()
	 */
	@Override
	public boolean doPrepare() {
		return true;
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.dispatcher.job.Job#runFCP2(hyperocha.freenet.fcp.dispatcher.Dispatcher)
	 */
	@Override
	public void runFCP(FCPJobRunner jobRunner, boolean resume) {
		ClientPutComplexDir cpcd = new ClientPutComplexDir();
		//cpcd.addFileDisk(index, name, diskfilename, mime)
		cpcd.setIdentifier(getJobID());
		cpcd.setURI(getInsertKey());
		cpcd.setVerbosityAll();
		cpcd.setCompressLzma();
		cpcd.setDefaultName(indexName);

		int index = 0;
		for (Entry<String, SiteItem> entry:items.entrySet()) {
			if (entry.getValue() instanceof FileItem) {
				cpcd.addFileDisk(index, entry.getKey(), ((FileItem)entry.getValue()).file.getAbsolutePath());
				index++;
				continue;
			}
			throw new IllegalArgumentException("Invalid item: "+entry.getValue());
		}

		jobRunner.send(cpcd);
	}

//	/* (non-Javadoc)
//	 * @see hyperocha.freenet.fcp.dispatcher.job.Job#incommingMessage(hyperocha.freenet.fcp.FCPConnection, java.util.Hashtable)
//	 */
//	@Override
//	public void incomingMessage(String id, NodeMessage msg) {
//		if (msg.isMessageName("URIGenerated")) {
//			//trash the uri-generated
//			return;
//		}
//
//		if (id.equals(getJobID())) {
//
//			if (msg.isMessageName("PutFetchable")) {
//				targetBaseKey = msg.getKeyValue("URI");
//				doMetaKey();
//				//System.out.println("CHK ins PF: " + message);
//				// if fast mode setSuccess();
//				return;
//			}
//		
//			if (msg.isMessageName("PutSuccessful")) {
//				//System.out.println("CHK ins PS: " + message);
//				targetBaseKey = msg.getKeyValue("URI");
//				if (node.haveGQ()) {
//					removeMeFromGQ();
//				}
//				setSuccess();
//				return;
//			}
//		
//			if (msg.isMessageName("PutFailed")) {
//				//targetKey = FreenetKey.CHKfromString((String)message.get("URI"));
//				if (node.haveGQ()) {
//					removeMeFromGQ();
//				}
//				setError(msg);
//				return;
//			}
//
//			// TODO Auto-generated method stub
//			System.out.println("CHK ins not handled: " + msg);
//			super.incomingMessage(id, msg);
//		} else if (id.equals(metaID)) { 
//			System.err.println("CHK ins-red not handled: " + msg);
//			
//		} else {
//			throw new Error("debug");
//		}
//		super.incomingMessage(id, msg);
//	}

	@Override
	protected void onSimpleProgress(boolean isFinalized, long totalBlocks,
			long requiredBlocks, long doneBlocks, long failedBlocks,
			long fatallyFailedBlocks) {
		System.out.println("Progress: "+(isFinalized?"  ":"* ")+doneBlocks+'/'+requiredBlocks+'('+totalBlocks+")["+failedBlocks+'/'+fatallyFailedBlocks+']');
	}

	@Override
	protected void onPutFetchable() {
		System.out.println("Fetchable: "+getRequestKey());
	}

	@Override
	protected void onURIGenerated() {
		System.out.println("Generated: "+getRequestKey());
	}

}
