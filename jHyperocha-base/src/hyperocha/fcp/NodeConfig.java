/**
 * 
 */
package hyperocha.fcp;

/**
 * @author saces
 *
 */
public class NodeConfig {

	private String _name;
	private String _host;
	private int _fcpport;

	/**
	 * 
	 */
	public NodeConfig() {}

	/**
	 * @return the _fcpport
	 */
	public int getFCPPort() {
		return _fcpport;
	}

	/**
	 * @param _fcpport the _fcpport to set
	 */
	public void setFCPPort(int fcpport) {
		this._fcpport = fcpport;
	}

	/**
	 * @return the _host
	 */
	public String getHost() {
		return _host;
	}

	/**
	 * @param _host the _host to set
	 */
	public void setHost(String host) {
		this._host = host;
	}

	/**
	 * @return the _name
	 */
	public String getNodeName() {
		return _name;
	}

	/**
	 * @param _name the _name to set
	 */
	public void setNodeName(String name) {
		this._name = name;
	}

}
