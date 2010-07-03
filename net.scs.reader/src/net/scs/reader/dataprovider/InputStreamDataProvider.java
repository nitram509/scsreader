/*
 * SCS Reader
 * Copyright (C) 2010  Martin W. Kirst
 *                     (master_jaf at users dot sourceforge dot net)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.scs.reader.dataprovider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.scs.reader.EndOfFileSignal;
import net.scs.reader.IScsDataProvider;

/**
 * Provides SCS streams directly from physical files.
 * Useful when you saved streams before.
 * <br>
 * <br>
 * <u>Implementation information:</u>
 * This implementation uses a 'forward only' and 'read only' buffer (~64kB).
 * It's useful, when stream parsing is byte oriented and you want to lower the
 * amount of IO calls over the network. Thus, even if you only {@link #readByte()}
 * there will be much more bytes loaded (over the network) when requested
 * the first time. Later calls on {@link #readByte()} will only use the internal
 * buffer. When the internal buffer is empty, the internal buffer will be refilled
 * automatically (over the network).
 *
 * @see {@link IScsDataProvider}
 */
public class InputStreamDataProvider implements IScsDataProvider {

	/*default*/ static final int BUFFERSIZE = 64000;

	private final InputStream is;

	private int streamsegments = 0;
	private int streamoffset = 0;
	private int streamread = 0;
	private boolean eof = false;
	final private byte[] streambuf = new byte[BUFFERSIZE];
	final private byte[] tmp = new byte[BUFFERSIZE];

	private final int ccsid;

	/**
	 * @param file
	 * @param ccsid
	 * @throws FileNotFoundException
	 */
	public InputStreamDataProvider(InputStream is, int ccsid) throws FileNotFoundException {
		this.is = is;
		this.ccsid = ccsid;
	}

	/**
	 * Refills the internal buffer, by moving the already read bytes to
	 * the first position in the buffer and refilling the buffer with as
	 * much bytes as available.
	 *
	 * @throws IOException
	 */
	private void reloadInternalBuffer() throws IOException {
		streamsegments += streamread;
		if (streamoffset > 0) {
			final int maxallowed = streamoffset;
			final int currentlength = streamread - streamoffset;
			System.arraycopy(streambuf, streamoffset, tmp, 0, currentlength);
			System.arraycopy(tmp, 0, streambuf, 0, currentlength);
			final int read = is.read(streambuf, currentlength, maxallowed);
			if (read >= 0) {
				streamread = read + currentlength;
			} else {
				streamread = currentlength; // EOF!
				eof = true;
			}
		} else {
			streamread = is.read(streambuf);
		}
		streamoffset = 0;

		//		 DEBUG dump the stream ...
//		if (streamread > 0) {
//			final FileOutputStream fos = new FileOutputStream("test.dump");
//			fos.write(streambuf, 0, streamread);
//			fos.close();
//		}
	}

	/* (non-Javadoc)
	 * @see com.ibm.scs.ISCSDataProvider#loadBytes(int)
	 */
	@Override
	public byte[] getBytes(int howmany) throws IOException {
		if (howmany < 0) return null;
		if (streamread < 0) return null; // EOF
		if (howmany == 0) return new byte[0];
		if (howmany > BUFFERSIZE) {
			throw new IndexOutOfBoundsException("To many bytes. Max allowed: " + BUFFERSIZE + " current value: " + howmany);
		}
		if (howmany > streamread - streamoffset) {
			reloadInternalBuffer();
			if (!hasMoreBytes()) {
				return null;
			}
		}
		final int amount = Math.min(howmany, streamread);
		final byte[] result = new byte[amount];
		System.arraycopy(streambuf, streamoffset, result, 0, amount);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.ibm.scs.ISCSDataProvider#getByte()
	 */
	@Override
	public byte getByte() throws IOException {
		if (streamoffset >= streamread) {
			reloadInternalBuffer();
			if (streamoffset >= streamread) {
				throw new EndOfFileSignal("End of file!");
			}
		}
		return streambuf[streamoffset];
	}

	/* (non-Javadoc)
	 * @see com.ibm.scs.IScsDataProvider#readBytes(int)
	 */
	@Override
	public byte[] readBytes(int howmany) throws IOException {
		final int oldposition = getPosition();
		final byte[] result = getBytes(howmany);
		if (result != null) {
			streamoffset = (oldposition + result.length) - streamsegments;
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see com.ibm.scs.IScsDataProvider#readByte()
	 */
	@Override
	public byte readByte() throws IOException {
		if (streamoffset >= streamread) {
			reloadInternalBuffer();
			if (streamoffset >= streamread) {
				throw new EndOfFileSignal("End of file!");
			}
		}
		return streambuf[streamoffset++];
	}

	/* (non-Javadoc)
	 * @see com.ibm.scs.ISCSDataProvider#skipBytes()
	 */
	@Override
	public void skipBytes(int howmany) throws IOException {
		boolean eof = false;
		while (howmany > 0 && !eof) {
			int amount = Math.min(howmany, BUFFERSIZE);
			final byte[] buf = readBytes(amount);
			eof = (buf == null) || (buf.length == 0);
			howmany -= amount;
		}
	}

	/* (non-Javadoc)
	 * @see com.ibm.scs.IScsDataProvider#getPosition()
	 */
	@Override
	public int getPosition() {
		return streamsegments + streamoffset;
	}

	/* (non-Javadoc)
	 * @see com.ibm.scs.IScsDataProvider#getCCSID()
	 */
	@Override
	public int getCCSID() {
		return ccsid;
	}

	/* (non-Javadoc)
	 * @see com.ibm.scs.IScsDataProvider#hasMoreBytes()
	 */
	@Override
	public boolean hasMoreBytes() {
		return (streamoffset < streamread) || (eof == false);
	}

}
