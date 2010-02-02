package hyperocha.fcp;

public abstract class FCPDataCommand extends FCPCommand {

	protected FCPDataCommand(String commandname) {
		super(commandname);
	}

	public void setURI(String uri) {
		putStringValue("URI", uri);
	}

	public void setEnabled() {
		setEnabled(true);
	}

	private void setEnabled(boolean e) {
		putBoolValue("Enabled", e);
	}

	public void setGlobal(boolean global) {
		putBoolValue("Global", global);
	}

	public void setGlobal() {
		setGlobal(true);
	}

	private void setPersistence(Persistence p) {
		putStringValue("Persistence", p.getName());
	}

	public void setPersistenceForever() {
		setPersistence(Persistence.FOREVER);
	}

	public void setPersistenceReboot() {
		setPersistence(Persistence.REBOOT);
	}

	public void setPersistenceConnection() {
		setPersistence(Persistence.CONNECTION);
		setGlobal(false);
	}
	public void setPriority(PriorityClass p) {
		putStringValue("PriorityClass", p.getValueString());
	}

	public void setPriority(int p) {
		putIntValue("PriorityClass", p);
	}

	public void setRetries(int r) {
		putStringValue("MaxRetries", Integer.toString(r));
	}

	public void setRetryForever() {
		setRetries(-1);
	}

	public void setRetryNone() {
		setRetries(0);
	}
	private void setVerbosity(Verbosity v) {
		putStringValue("Verbosity", v.toString());
	}

	public void setVerbosityAll() {
		setVerbosity(Verbosity.ALL);
	}

	public void setVerbosityQuite() {
		setVerbosity(Verbosity.NONE);
	}
}
