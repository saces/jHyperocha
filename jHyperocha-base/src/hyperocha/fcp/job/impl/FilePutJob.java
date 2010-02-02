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

import hyperocha.fcp.FCPCommand;
import hyperocha.fcp.FCPNode;
import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.job.FCPJobRunner;
import hyperocha.fcp.job.FileInsertJob;
import java.io.BufferedInputStream;
import java.io.File;

/**
 * insert a file the best way  includes black magic ritual and making coffe for finding the best way
 * @version   $Id: CHKFileInsertJob.java,v 1.9 2006/11/28 16:30:44 saces Exp $
 */
public class FilePutJob extends FileInsertJob {
	
	private File insertFile;
	private BufferedInputStream fis;
	private FreenetKey targetBaseKey;  // octet stream without any metadata, the real data 
	private FreenetKey targetMetaKey;
	private String metaID;
	//private Dispatcher dispatcher;
	
	private FCPNode node; // run job on this node
	
	public FilePutJob(String id, File source) {
		super(id, id);
		insertFile = source;
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.job.Job#doPrepare()
	 */
	@Override
	public boolean doPrepare() {
		// TODO Check file exists, is file readable? etc pp 
		return insertFile.exists();
	}

	public String getBaseKey() {
		return targetBaseKey.getReadFreenetKey();
	}
	
	public FreenetKey getMetaKey() {
		return targetMetaKey;
	}
	
//	private List composeResume(List cmd) {
//		cmd.add("GetRequestStatus");
//		cmd.add("Identifier=" + this.getJobID());
//		cmd.add("Global=true");
//		cmd.add("OnlyData=false");
//		cmd.add("EndMessage");
//		return cmd;
//	}
	
//	private void removeMeFromGQ() {
//		List cmd = new LinkedList();
//		cmd.add("RemovePersistentRequest");
//		cmd.add("Global=true");
//		cmd.add("Identifier=" + getJobID());
//		cmd.add("EndMessage");
//		//node.getDefaultFCPConnectionRunner().send(cmd);
//	}
	
//	private void doMetaKey() {
//		metaID = IdentifierUtil.getNewConnectionId();
//		//dispatcher.registerJob(metaID, this);
//		List cmd = new LinkedList();
//		
//		cmd.add("ClientPut");
//		cmd.add("URI=CHK@");
//		cmd.add("Identifier=" + metaID);
//		cmd.add("Verbosity=0"); // recive SimpleProgress for unterdruecken timeout       
//		cmd.add("MaxRetries=-1");
//		cmd.add("DontCompress=false"); // force compression
//		cmd.add("TargetFilename=");  // disable gurken-keys
//		cmd.add("EarlyEncode=false");
//		cmd.add("GetCHKOnly=false");
//		cmd.add("Metadata.ContentType=" + DefaultMIMETypes.guessMIMEType(insertFile.getAbsolutePath(), false));
//		cmd.add("PriorityClass=3");
//		cmd.add("Global=false");
//		cmd.add("Persistence=connection");
//		cmd.add("UploadFrom=redirect");
//		cmd.add("TargetURI=" + targetBaseKey.getBaseKey());
//		cmd.add("EndMessage");
//
//		System.err.println("CHK re: " + cmd);
//		//node.getDefaultFCPConnectionRunner().send(cmd);
//	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.dispatcher.job.Job#runFCP2(hyperocha.freenet.fcp.dispatcher.Dispatcher)
	 */
	@Override
	public void runFCP(FCPJobRunner jobRunner, boolean resume) {
		
		//dispatcher = aDispatcher;
		
		//node = dispatcher.getNextNode(getRequiredNetworkType());

//		List cmd = new LinkedList();
//
//		if (resume) {
//			composeResume(cmd);
//		} else {
//			cmd.add("ClientPut");
//			cmd.add("URI=CHK@");
//			cmd.add("Identifier=" + this.getJobID());
//			cmd.add("Verbosity=257"); // recive SimpleProgress for unterdruecken timeout       
//			cmd.add("MaxRetries=-1");
//			cmd.add("DontCompress=false"); // force compression
//			cmd.add("TargetFilename=");  // disable gurken-keys
//			cmd.add("EarlyEncode=false");
//			cmd.add("GetCHKOnly=false");
//			cmd.add("Metadata.ContentType=application/octet-stream");
//			cmd.add("PriorityClass=3");
//			
//			if (node.haveGQ()) {
//				cmd.add("Global=true");
//				cmd.add("Persistence=forever");
//			} else {
//				cmd.add("Persistence=connection");
//			}
//
//		
//			if (node.haveDDA()) {  // direct file acess
//				cmd.add("UploadFrom=disk");
//				cmd.add("Filename=" + insertFile.getAbsolutePath());
//				cmd.add("EndMessage");
//				//node.getDefaultFCPConnectionRunner().send(cmd);
//				//System.err.println("CHK ins: " + cmd);
//				
//			} else {
//				cmd.add("UploadFrom=direct");
//				cmd.add("DataLength=" + Long.toString(insertFile.length()));
//				cmd.add("Data");
//				//System.err.println("CHK ins: " + cmd);
//				//node.getDefaultFCPConnectionRunner().send(cmd, insertFile.length(), fis);
//			}
//			
//			System.err.println("CHK ins: " + cmd);
//
//		}

	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.dispatcher.job.Job#incommingMessage(hyperocha.freenet.fcp.FCPConnection, java.util.Hashtable)
	 */
	@Override
	public void incomingMessage(String id, NodeMessage msg) {
		if (msg.isMessageName("URIGenerated")) {
			//trash the uri-generated
			return;
		}
		
		if (id.equals(getJobID())) {

			if (msg.isMessageName("PutFetchable")) {
				targetBaseKey = msg.getKeyValue("URI");
				//doMetaKey();
				//System.out.println("CHK ins PF: " + message);
				// if fast mode setSuccess();
				return;
			}
		
			if (msg.isMessageName("PutSuccessful")) {
				//System.out.println("CHK ins PS: " + message);
				targetBaseKey = msg.getKeyValue("URI");
				if (node.haveGQ()) {
					//removeMeFromGQ();
				}
				setSuccess();
				return;
			}
		
			if (msg.isMessageName("PutFailed")) {
				//targetKey = FreenetKey.CHKfromString((String)message.get("URI"));
				if (node.haveGQ()) {
					//removeMeFromGQ();
				}
				setError(msg);
				return;
			}
		
			// TODO Auto-generated method stub
			System.out.println("CHK ins not handled: " + msg);
			super.incomingMessage(id, msg);
		} else if (id.equals(metaID)) { 
			System.err.println("CHK ins-red not handled: " + msg);
			
		} else {
			throw new Error("debug");
		}
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.dispatcher.job.Job#setSuccess()
	 */
	@Override
	protected void setSuccess() {
		System.err.println("CHK ins-setsuc");
		if ((targetBaseKey != null) && (targetMetaKey != null)) {
			System.err.println("CHK ins-setsuc ok");
			super.setSuccess();
		} else {
			System.err.println("CHK ins-setsuc noch nicht.");
		}
	}

	@Override
	protected FCPCommand getPutCommand2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onSimpleProgress(boolean isFinalized, long totalBlocks,
			long requiredBlocks, long doneBlocks, long failedBlocks,
			long fatallyFailedBlocks) {
		// TODO Auto-generated method stub
		
	}
	

}
