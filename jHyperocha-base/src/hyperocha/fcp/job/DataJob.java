/**
 * 
 */
package hyperocha.fcp.job;

import hyperocha.fcp.NodeMessage;
import hyperocha.fcp.Persistence;
import hyperocha.fcp.PriorityClass;
import hyperocha.fcp.Verbosity;

/**
 * @author saces
 *
 */
public abstract class DataJob extends FCPJob {

	private boolean global = false;
	private Persistence persitence = Persistence.CONNECTION;
	private Verbosity verbosity = Verbosity.ALL; 
	private PriorityClass startPrio = PriorityClass.BULK;
	private PriorityClass runPrio = startPrio;
	private boolean _shouldUseDDA = false;
	private boolean _usesDDA = false;

	/**
	 * @param id
	 */
	public DataJob(String id) {
		super(id);
	}

	/* (non-Javadoc)
	 * @see hyperocha.freenet.fcp.FCP2Job#incomingMessage(java.lang.String, hyperocha.freenet.fcp.FCP2NodeMessage)
	 */
	@Override
	public void incomingMessage(String id, NodeMessage msg) {
		if (msg.isMessageName("SimpleProgress")) {
			final boolean isFinalized = msg.getBoolValue("FinalizedTotal");
			final long totalBlocks = msg.getLongValue("Total");
			final long requiredBlocks = msg.getLongValue("Required");
			final long doneBlocks = msg.getLongValue("Succeeded");
			final long failedBlocks = msg.getLongValue("Failed");
			final long fatallyFailedBlocks = msg.getLongValue("FatallyFailed");

			try {
				onSimpleProgress(isFinalized, totalBlocks, requiredBlocks, doneBlocks, failedBlocks, fatallyFailedBlocks);
			} catch (Throwable e) {
				// TODO silence? log?
			}
			return;
		}
		super.incomingMessage(id, msg);
	}

	/**
	 * Overwrite this to get notified if the job progress was changed.
	 * The default implementation does nothing.
	 */
	protected abstract void onSimpleProgress(boolean isFinalized, long totalBlocks, long requiredBlocks, long doneBlocks, long failedBlocks, long fatallyFailedBlocks);

	/**
	 * @return the global
	 */
	protected boolean isGlobal() {
		return global;
	}

	/**
	 * @param global the global to set
	 */
	private void setGlobal(boolean global) {
		this.global = global;
	}

	/**
	 * @return the persitence
	 */
	private Persistence getPersitence() {
		return persitence;
	}

	protected boolean isPersistent() {
		return persitence != Persistence.CONNECTION;
	}

	/**
	 * @param persitence the persitence to set
	 */
	private void setPersitence(Persistence persitence) {
		this.persitence = persitence;
	}

	/**
	 * @return the verbosity
	 */
	private Verbosity getVerbosity() {
		return verbosity;
	}

	/**
	 * @param verbosity the verbosity to set
	 */
	private void setVerbosity(Verbosity verbosity) {
		this.verbosity = verbosity;
	}

	/**
	 * @return the startPrio
	 */
	private PriorityClass getPriority() {
		return startPrio;
	}

	/**
	 * @param startPrio the startPrio to set
	 */
	private void setPriority(PriorityClass startPrio) {
		this.startPrio = startPrio;
	}

	private boolean usesDDA() {
		return _usesDDA;
	}

}
