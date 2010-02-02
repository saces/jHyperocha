/**
 * 
 */
package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPCommand;


/**
 * @author saces
 *
 */
public class ClientHello extends FCPCommand {
	
	public ClientHello(String clientname) {
		super("ClientHello");
		putStringValue("Name", clientname);
		putStringValue("ExpectedVersion", "2.0");
	}

}
