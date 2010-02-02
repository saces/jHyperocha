package hyperocha.ant;

public abstract class DataTask extends FCPTask {
	boolean global;
	long verbosity;

	public void setGlobal(boolean glob) {
		global = glob;
	}

	public void setVerbosity(long v) {
		verbosity = v;
	}

}
