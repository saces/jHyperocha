/**
 * 
 */
package hyperocha.fcp.cmd;

import hyperocha.fcp.FCPCommand;


/**
 * @author saces
 *
 */
public class GetConfig extends FCPCommand {
	
	GetConfig() {
	super("GetConfig");
	}

	public void withAll() {
		withCurrent();
		withDefault();  
		withSortOrder(); 
		withExpertFlag();
		withForceWriteFlag(); 
		withShortDescription(); 
		withLongDescription();
	}

	public void withLongDescription() {
		putBoolValue("WithLongDescription", true);
	}

	public void withShortDescription() {
		putBoolValue("WithShortDescription", true);
	}

	public void withForceWriteFlag() {
		putBoolValue("WithForceWriteFlag", true);
	}

	public void withExpertFlag() {
		putBoolValue("WithExpertFlag", true);
	}

	public void withSortOrder() {
		putBoolValue("WithSortOrder", true);
	}

	public void withDefault() {
		putBoolValue("WithDefault", true);
	}

	public void withCurrent() {
		putBoolValue("WithCurrent", true);
	}

}
