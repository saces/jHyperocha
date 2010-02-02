/**
 * 
 */
package hyperocha.fcp.job;


import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.FCPCommand;
import hyperocha.fcp.io.FCPInputStream;
import hyperocha.fcp.io.IIncoming;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * the job execution stuff
 * @author saces
 *
 */
public abstract class FCPJobRunner implements IIncoming {
	
	public static final int RUNJOB_UNKNOWN = -1;
	public static final int RUNJOB_NOERROR = 0;
	public static final int RUNJOB_PREPAREFAILED = 1;
	public static final int RUNJOB_OFFLINE = 2;  // run job without fcp conn. nice try ;) 
	public static final int RUNJOB_OVERLOAD = 3; // to many jobs?
	public static final int RUNJOB_BROKEN = 4;  // something "impossible" wrong or panic stop

	
	private Hashtable<String, FCPJob> runningJobs = new Hashtable<String, FCPJob>();
	
	private String defaultReciver;

	public void incomingMessage(String id, NodeMessage message) {
		FCPJob j = getRunningJob(id);
		if (j == null) { 
			onUnknownIDMessage(id, message);
		} else {
			j.incomingMessage(id, message);
		}
	}

	public void incomingData(String id, NodeMessage message, FCPInputStream fis) {
		FCPJob j = getRunningJob(id);
		if (j == null) { 
			onUnknownIDData(id, message, fis);
		} else {
			j.incomingData(id, message, fis);
		}
	}

	protected void onUnknownIDMessage(String id, NodeMessage message) {
		//Logger.error(this, "Recived Message with unhadled ID: »"+id+"« Msg: "+message);
	}

	protected void onUnknownIDData(String id, NodeMessage message, FCPInputStream fis) {
		if (message.isMessageName("AllData")) {
			long size = message.getLongValue("DataLength"); 
			try {
				fis.skip(size);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
	}
	
//	public int runJob(FCPJob job) {
//		
//	}

	/**
	 *  run a job and do not return until the job is done.
	 * @param job
	 */
	public int runJob(FCPJob job) {
		if (job.getStatus()==FCPJob.STATUS_UNPREPARED) {
			job.prepare();
		}
		if (job.getStatus()!=FCPJob.STATUS_PREPARED) {
			job.setError();
			return RUNJOB_PREPAREFAILED;
		}
		return runJob(job, false);
	}

	/**
	 *  start a job and return .
	 * @param job
	 */
	public int startJob(FCPJob job) {
		if (job.getStatus()==FCPJob.STATUS_UNPREPARED) {
			job.prepare();
		}
		if (job.getStatus()!=FCPJob.STATUS_PREPARED) {
			job.setError();
			return RUNJOB_PREPAREFAILED;
		}
		registerJob(job);
		job.run(this, false, false);
//		unregisterJob(job);
		return RUNJOB_NOERROR;

	}

	private int runJob(FCPJob job, boolean resume) {
		registerJob(job);
		job.run(this, true, resume);
		unregisterJob(job);
		return RUNJOB_NOERROR;
	}

	private void registerJob(FCPJob job) {
		runningJobs.put(job.getJobID(), job);
	}

	public void registerJobID(FCPJob job, String id) {
		runningJobs.put(id, job);
	}

	private void unregisterJob(FCPJob job) {
		// FIXME: remove all ids assigned to this job
		runningJobs.remove(job.getJobID());		
	}

	private void unregisterJobID(String id) {
		// FIXME: remove all ids assigned to this job
		runningJobs.remove(id);		
	}

	private FCPJob getRunningJob(String id) {
		return runningJobs.get(id);
	}

	public abstract void send(FCPCommand cmd);

	public abstract void write(InputStream inputStream, long length);
	
	public abstract void write(byte[] b);

}
