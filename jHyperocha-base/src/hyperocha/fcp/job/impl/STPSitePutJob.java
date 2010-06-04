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

import java.util.HashMap;
import java.util.Map.Entry;

import hyperocha.fcp.FreenetKey;
import hyperocha.fcp.IdentifierUtil;
import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.cmd.FCPPluginMessage;
import hyperocha.fcp.job.FCPJobRunner;

/**
 * Put a real complex dir, using sitetoolplugin
 */
public class STPSitePutJob extends SitePutJob {

	private enum Step { NEW, OPEN, EDIT, COMMIT }

	private Step step;

	private volatile boolean isFinalStep = false;

	private final Object _stepWaiter = new Object();

	private final HashMap<String, Object> stepWaiters = new HashMap<String, Object>();


	public STPSitePutJob(String id, FreenetKey uri, String indexname) {
		super(id, uri, indexname);
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.job.Job#doPrepare()
	 */
	@Override
	public boolean doPrepare() {
		return true;
	}

	private void waitStep(String id) {
		//System.out.println("Wait: "+id);
		Object stepWaiter;
		synchronized (stepWaiters) {
			stepWaiter = stepWaiters.get(id);
		}
		synchronized (stepWaiter) {
			try {
				stepWaiter.wait();
			} catch (InterruptedException e) {
			}
		}
		synchronized (stepWaiters) {
			stepWaiters.remove(id);
		}
	}

	private void notifyStep(String id) {
		//System.out.println("notify: "+id);
		Object stepWaiter;
		synchronized (stepWaiters) {
			stepWaiter = stepWaiters.get(id);
		}
		synchronized (stepWaiter) {
			stepWaiter.notifyAll();
		}
	}

	private String makeStepWaiterID(String prefix) {
		Object stepWaiter = new Object();
		String id = IdentifierUtil.getNewConnectionId(prefix);
		synchronized (stepWaiters) {
			stepWaiters.put(id, stepWaiter);
		}
		return id;
	}

//	private void setStep(Step s) {
//		synchronized (stepWaiter) {
//			step = s;
//			try {
//				stepWaiter.wait();
//			} catch (InterruptedException e) {
//			}
//		}
//	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.dispatcher.job.Job#runFCP2(hyperocha.freenet.fcp.dispatcher.Dispatcher)
	 */
	@Override
	public void runFCP(FCPJobRunner jobRunner, boolean resume) {
		FCPPluginMessage cmd = new FCPPluginMessage();
		cmd.setIdentifier(getJobID());
		cmd.setPluginName("plugins.SiteToolPlugin.SiteToolPlugin");
		cmd.addPluginParam("Command", "NewSiteSession");
		String id = makeStepWaiterID("newsession");
		cmd.addPluginParam("Identifier", id);
		cmd.addPluginParam("SessionID", getJobID());
		step = Step.NEW;
		jobRunner.send(cmd);
		waitStep(id);

		for (Entry<String, SiteItem> entry:items.entrySet()) {
			if (isFinished()) return;
			id = makeStepWaiterID("additem");
			if (entry.getValue() instanceof FileItem) {
				FCPPluginMessage acmd = new FCPPluginMessage();
				acmd.setIdentifier(getJobID());
				acmd.setPluginName("plugins.SiteToolPlugin.SiteToolPlugin");
				acmd.addPluginParam("Command", "AddFileItem");
				acmd.addPluginParam("Identifier", id);
				acmd.addPluginParam("SessionID", getJobID());
				acmd.addPluginParam("Name", entry.getKey());
				acmd.addPluginParam("FileName", ((FileItem)entry.getValue()).file.getAbsolutePath());
				jobRunner.send(acmd);
				//System.out.println("Wait: "+id);
				waitStep(id);
				continue;
			}
			throw new IllegalArgumentException("Invalid item: "+entry.getValue());
		}

		if (isFinished()) return;

		id = makeStepWaiterID("seturi");
		
		FCPPluginMessage ucmd = new FCPPluginMessage();
		ucmd.setIdentifier(getJobID());
		ucmd.setPluginName("plugins.SiteToolPlugin.SiteToolPlugin");
		ucmd.addPluginParam("Command", "SetInsertURI");
		ucmd.addPluginParam("Identifier", id);
		ucmd.addPluginParam("SessionID", getJobID());
		String k = getInsertKey();
		if (k.endsWith("/")) {
			k = k.substring(0, k.length()-1);
		}
		ucmd.addPluginParam("InsertURI", k);
		jobRunner.send(ucmd);
		waitStep(id);
		if (isFinished()) return;

		FCPPluginMessage ccmd = new FCPPluginMessage();
		ccmd.setIdentifier(getJobID());
		ccmd.setPluginName("plugins.SiteToolPlugin.SiteToolPlugin");
		ccmd.addPluginParam("Command", "StartSession");
		ccmd.addPluginParam("Identifier", getJobID());
		ccmd.addPluginParam("SessionID", getJobID());
		isFinalStep = true;
		jobRunner.send(ccmd);
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.dispatcher.job.Job#incommingMessage(hyperocha.freenet.fcp.FCPConnection, java.util.Hashtable)
	 */
	@Override
	public void incomingMessage(String id, NodeMessage msg) {
		if (msg.isMessageName("FCPPluginReply")) {
			String status = msg.getStringValue("Replies.Status");
			String identifier = msg.getStringValue("Replies.Identifier");
			if ("Error".equals(status)) {
				setError(msg);
				notifyStep(identifier);
				return;
			}
			if ("FileAdded".equals(status)) {
				notifyStep(identifier);
				return;
			}
			if ("InsertURISet".equals(status)) {
				notifyStep(identifier);
				return;
			}
			if ("Success".equals(status)) {
				notifyStep(identifier);
				return;
			}
			if ("Progress".equals(status)) {
				System.out.println(msg.getStringValue("Replies.Description"));
				return;
			}
			if ("PutSuccesful".equals(status)) {
				setRequestKey(msg.getKey("Replies.URI"));
				setSuccess();
				return;
			}
		}
		System.out.println("Message: " + msg);
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
		super.incomingMessage(id, msg);
	}

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
