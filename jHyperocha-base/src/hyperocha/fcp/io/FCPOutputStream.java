package hyperocha.fcp.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * write max number of bytes through the stream<BR>
 * it is limited to max, or padded to max
 */

public class FCPOutputStream extends OutputStream {
    private int mBytesleft;
    final OutputStream _os;

    /**
     * Close that count stream.
     * @throws IOException 
     */
	@Override
	public void close() throws IOException {
    	throw new AssertionError("Close the underlying stream, not this!");
    	//_os.close();
    }

    /**
     * Flush that count stream.
     * @throws IOException 
     */
	@Override
	public void flush() throws IOException {
    	_os.flush();
    }

    /**
     * Write an array of bytes to that stream.
     * @throws IOException 
     */
	@Override
	public void write(byte b[]) throws IOException {
    	write(b, 0, b.length);
    }

    /**
     * Write part of an array of bytes to that stream.
     * @throws IOException 
     */
	@Override
	public void write(byte b[], int off, int len) throws IOException {
    	if (mBytesleft < 1) return;
    	int l = (mBytesleft < len)? mBytesleft : len;
    	_os.write(b, off, l);
    	mBytesleft -= l;
    }

    /**
     * Write a single byte to that stream.
     * @throws IOException 
     */
	@Override
	public void write(int b) throws IOException {
    	if (mBytesleft < 1) return;
    	_os.write(b);
    	mBytesleft--;
    }

    /**
     * Create a new instance of that class.
     */
    public FCPOutputStream(OutputStream os, int maxcount) {
    	mBytesleft = maxcount;
    	_os = os;
    }
}
