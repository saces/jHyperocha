/* This code is part of Hyperocha. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */
package hyperocha.fcp;

import hyperocha.fcp.IdentifierUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author  saces
 */
public abstract class FCPCommand {

	public static final String ENDMARKER = "EndMessage";
	public static final String DATA_ENDMARKER = "Data";

	protected final HashMap<String, Object> fields = new HashMap<String, Object>();

	protected long payloadLength = 0;

	private final String commandName;

	protected FCPCommand(String commandname) {
		commandName = commandname;
	}

	protected String getEndmarker() {
		return hasPayload() ?  DATA_ENDMARKER : ENDMARKER;
	}

	final protected String getCommandName() {
		return commandName;
	}

	@Override
	public String toString() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String s = null;
		try {
			write(bos);
			s = bos.toString("UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return s;
	}

	private boolean haveFields() {
		return fields.isEmpty() ? false : true;
	}

	protected void putStringValue(String key, String value) {
		fields.put(key, value);		
	}

	protected void putIntValue(String key, int value) {
		fields.put(key, Integer.toString(value));		
	}

	protected void putLongValue(String key, long value) {
		fields.put(key, Long.toString(value));
	}

	protected void putBoolValue(String key, boolean value) {
		fields.put(key, (value ? "true" : "false"));		
	}

	public void setIdentifier(String identifier) {
		putStringValue("Identifier", identifier);
	}

	public void setAutoIdentifier() {
		setAutoIdentifier("jHyperocha");
	}

	public void setAutoIdentifier(String prefix) {
		setIdentifier(IdentifierUtil.getNewConnectionId(prefix));
	}

	public void setAutoIdentifier(String prefix, String postfix) {
		setIdentifier(IdentifierUtil.getNewConnectionId(prefix, postfix));
	}

	public void setClientToken(String clienttoken) {
		putStringValue("ClientToken", clienttoken);
	}

	protected boolean hasPayload() {
		return payloadLength > 0;
	}

	protected long getPayloadLength() {
		return payloadLength;
	}

	public synchronized void write(OutputStream os) throws IOException {
		os.write(getCommandName().getBytes());
		os.write('\n');
		if (haveFields()) {
			for(Iterator<Entry<String, Object>> i = fields.entrySet().iterator();i.hasNext();) {
				Map.Entry<String, Object> entry = i.next();
				String key = entry.getKey();
				String value = (String) entry.getValue();
				os.write(key.getBytes());
				os.write('=');
				os.write(value.getBytes());
				os.write('\n');
				//System.err.println("Written a Line with length: " + (key.length() + value.length() +2) );
			}
		}
		os.write(getEndmarker().getBytes());
		os.write('\n');
		os.flush();
	} 
}

