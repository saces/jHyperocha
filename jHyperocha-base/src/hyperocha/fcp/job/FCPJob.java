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
package hyperocha.fcp.job;

import hyperocha.fcp.IdentifierUtil;
import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.io.FCPInputStream;
import hyperocha.fcp.io.IIncoming;

/**
 * a job
 * @version   $Id: Job.java,v 1.19 2006/11/28 12:18:59 saces Exp $
 */
public abstract class FCPJob implements IIncoming {
    
	private static final int STATUS_ERROR = -1;
	public static final int STATUS_UNPREPARED = 0;
	public static final int STATUS_PREPARED = 1;
	private static final int STATUS_RUNNING = 2;
	private static final int STATUS_DONE = 3;
	//private static final int STATUS_DONE_SUCCESS = 4;
	private static final int STATUS_STOPPING = 4;
	
	private int status = STATUS_UNPREPARED;

	private Throwable lastError = null; // io, runtime
	private NodeMessage lastErrorMessage = null; // fcp protocol 
	
	private String jobID = null;  // = identifer on fcp 2
	private String clientToken = "hyperochaclienttoken";
	
	private final Object waitObject = new Object();
	
	private FCPJobRunner _jobRunner;

	private long jobstarted = 0;
	private long jobfinished = 0;
	
	private long preparestarted = 0;
	private long preparefinished = 0;


	//private PriorityClass priorityClass = null;

	protected FCPJob() {
		this(IdentifierUtil.getNewConnectionId("hyperocha-"));
	}

	protected FCPJob(String id) {
		if (id == null) throw new NullPointerException();
		jobID = id;
		status = STATUS_UNPREPARED;
	}

	private void reset() {
		//cancel(true);
		lastError = null;
		lastErrorMessage = null;
		status = STATUS_UNPREPARED;
	}

	/**
	 * @return  the last occured error
	 */
	public Throwable getLastError() {
		return lastError;
	}

	/**
	 * @return  the lastErrorMessage
	 */
	public NodeMessage getLastErrorMessage() {
		return lastErrorMessage;
	}

	protected void setError() {
		setError(STATUS_ERROR);
	}

	private void setError(int ec) {
		status = ec;
		synchronized(waitObject) {
			waitObject.notifyAll();
		}
	}
	
	protected void setError(Exception e) {
		lastError = e;
		setError();
	}
	
	protected void setError(NodeMessage errmsg) {
		lastErrorMessage = errmsg;
		setError();
	}
	
	protected void setSuccess() {
		//lastError = new Exception(description);
//		status = STATUS_DONE;
		synchronized(waitObject) {
			status = STATUS_DONE;
			waitObject.notifyAll();
		}
	}
	
	public boolean isFatalError() {
		//return lastErrorMessage.getBoolValue("Fatal");
		return false;
	}
	
	/**
	 * @return
	 */
	public int getStatus() {
		return status;
	}
	
	public final void prepare() {
		status = STATUS_UNPREPARED;
		boolean b = false;
		preparestarted = System.currentTimeMillis();
        try {
            b = doPrepare();
        } catch(Exception e) {
        	setError(e);
        	return;
            // TODO: log error?
        }
        preparefinished = System.currentTimeMillis();
		if (b) {
			status = STATUS_PREPARED;
		} else {
			if (status != STATUS_ERROR) {
				setError(new Exception("Prepare Failed!"));
			}
		}
	}
	
	public final void run(FCPJobRunner jobRunner) {
		run(jobRunner, true, false);
	}
	
	protected FCPJobRunner getJobRunner() {
		return _jobRunner;
	}
		
	public final void run(FCPJobRunner jobRunner, boolean block, boolean resume) {
		if (status != STATUS_PREPARED) { throw new Error("FIXME: never run an unprepared job!"); }
		status = STATUS_RUNNING;
        
		jobstarted = System.currentTimeMillis();
//        try {
//            jobStarted(); // notify subclasses that job started
//        } catch(Throwable t) {
//            // TODO: log error?
//        }
	
        // don't die for any reason
        //try {
		_jobRunner = jobRunner;
    		runFCP(jobRunner, resume);
        	if (block) {
        		waitFine();
        	}
//    		if ((status != STATUS_ERROR) && (lastError == null)) {
//    			status = STATUS_DONE;
//    		}
//        } catch(Throwable t) {
//            // TODO: log error?
//            status = STATUS_ERROR;
//            lastError = t;
//        }
//        
		jobfinished = System.currentTimeMillis();
//        try {
//            jobFinished(); // notify subclasses that job finished
//        } catch(Throwable t) {
//            // TODO: log error?
//        }
	}

	public void runFCP(FCPJobRunner jobRunner, boolean resume) {
		throw (new Error("Missing implementation." + this));
	}

//	public void start() {
//		if (status == STATUS_UNPREPARED) { prepare(); }
//		if (status != STATUS_PREPARED) { return; }
//		
//		// now do the real run
//		
//	}

	public void cancel(boolean hard) {
		status = STATUS_STOPPING;
	}

	
	/**
	 * overwrite this
	 * @return bool if succes
	 */
	public boolean doPrepare() {
		return true;
	}

//	public abstract void cancel();
//	public abstract void suspend();
//	public abstract void resume();
//	public abstract void panic();

	public boolean isSuccess() {
		return ((status == STATUS_DONE) && (lastError == null));
	}

	public void waitFine () {
		synchronized(waitObject) {
			while ((status == STATUS_RUNNING) && (lastError == null)) {
				try {
					waitObject.wait();
				} catch (InterruptedException e) {
				}		
			}
		}
	}

	/**
	 * @return  the jobID
	 */
	public String getJobID() {
		return jobID;
	}

	/**
	 * @return
	 */
	protected String getClientToken() {
		return clientToken;
	}

	/** 
	 * The default handler. 
	 * skip the incoming data 
	 */
	public void incomingData(String id, NodeMessage msg, FCPInputStream fis) {
		// the defaulthandler skip the data
		if (msg.isMessageName("AllData")) { // FCP 2
			long size = msg.getLongValue("DataLength"); 
			System.err.println("Skipping Data: " + msg);
			//conn.skip(size);
			return;
		}
	}

	/** 
	 * The default handler. you should always super to this. 
	 */
	public void incomingMessage(String id, NodeMessage msg) {
		if (msg.isMessageName("ProtocolError")) {
			lastErrorMessage = msg;
			boolean goon = false;
			try {
				goon = onProtocolError(msg);
			} catch (Exception e) {
				// TODO silence? log?
			}
			if (!goon) {
				setError(msg);
			}
			return;
		}
		System.err.println("Unhandled Message: "+msg);
	}

	/**
	 * Overwrite this to get notified if a protocoll error occures.
	 * @return true - the problem is resolved by your implementation and the job stays running; false - the job fails.
	 */
	public boolean onProtocolError(NodeMessage msg) {
		return false;
	}

	/**
	 * @return the start timestamp - System.currentTimeMillis();
	 */
	public long getJobStartedMillis() {
		return jobstarted;
	}

	/**
	 * @return the finished timestamp - System.currentTimeMillis();
	 */
	public long getJobFinishedMillis() {
		return jobfinished ;
	}

	/**
	 * @return the execution time in milli sec
	 */
	public long getJobDurationMillis() {
        if( jobfinished <= 0 ) {
            // not yet finished, return current duration
            return System.currentTimeMillis() - jobstarted;
        }
		return (jobfinished - jobstarted);
	}
	
	/**
	 * @return the execution time in milli sec
	 */
	public long getJobPrepareDurationMillis() {
        if( preparefinished <= 0 ) {
            // not yet finished, return current duration
            return System.currentTimeMillis() - preparestarted;
        }
		return (preparefinished - preparestarted);
	}
    
    /**
     * @return  true if job is started
     */
    public boolean isStarted() {
        return jobstarted > 0;
    }

    /**
     * @return  true if job is finished
     */
    public boolean isFinished() {
        return jobfinished > 0;
    }

    /**
     * Overwrite this to get notified if the job was actually started.
     * The default implementation does nothing.
     */
    public void jobStarted() { }

    /**
     * Overwrite this to get notified if the job was finished.
     * The default implementation does nothing.
     */
    public void jobFinished() { }
}
