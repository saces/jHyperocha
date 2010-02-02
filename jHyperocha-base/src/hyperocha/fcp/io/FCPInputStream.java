package hyperocha.fcp.io;

import java.io.InputStream;
import java.io.IOException;

/**
 * read max number of bytes through the stream<br>
 * it is limited to max, or skip until max bytes are read
 */
public class FCPInputStream extends InputStream {

	private int mBytesleft;

    final private InputStream _is;

	@Override
	public int read() throws IOException {
    	if (mBytesleft < 1) return -1;
    	
    	int r = _is.read();
    	if (r > -1) {
    		mBytesleft--;
    	}
    	return r;
    }
    
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
    	
    	if (mBytesleft < 1) return -1;
    	
    	int r = _is.read(b, off, (mBytesleft < len)? mBytesleft : len);

    	if (r > -1) {
    		mBytesleft -= r;
    	}
    	return r;
    }

	@Override
	public long skip(long skipped) throws IOException {
    	
    	if (mBytesleft < 1) return -1;
    	
    	long l = _is.skip((mBytesleft < skipped)? mBytesleft : skipped);
    	if (l > 0) {
    		mBytesleft -= l;
    	}
    	return l;
    }
    
	@Override
	public void close() throws IOException {
    	//throw new Error("Close the underlying stream, not this!");
    }

    public FCPInputStream(InputStream is, int bytestoread) {
    	_is = is;
    	mBytesleft = bytestoread;
    }
}
