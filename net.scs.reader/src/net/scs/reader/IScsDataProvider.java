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
package net.scs.reader;

import java.io.IOException;

/**
 * Special interface for all data sources, which provide access on SCS streams.
 * <br><br>
 * Known implementations:<br>
 * {@link InputStreamDataProvider}
 */
public interface IScsDataProvider {

	/**
	 * Get bytes from the current position in stream and DON'T increment the position.
	 *
	 * @param howmany
	 * @throws IOException
	 * @throws IndexOutOfBoundsException
	 * @return an array of bytes, mostly length is as requested but can be shorter; could be null, if EOF reached.
	 */
	public abstract byte[] getBytes(int howmany) throws IOException, IndexOutOfBoundsException;

	/**
	 * Get a single byte from the current position in stream and DON'T increment the position.
	 *
	 * @return
	 * @throws IOException
	 * @throws EndOfFileSignal
	 * @see {@link #getBytes(int)}
	 */
	public abstract byte getByte() throws IOException, EndOfFileSignal;

	/**
	 * Get bytes from the current position in stream and increment the position by amount of bytes.
	 * This method is equal to calling {@link #getBytes(int)} and {@link #skipBytes(int)}.
	 *
	 * @param howmany
	 * @return an array of bytes, mostly length is as requested but can be shorter; could be null, if EOF reached.
	 * @throws IOException
	 * @throws IndexOutOfBoundsException
	 * @throws EndOfFileSignal
	 */
	public abstract byte[] readBytes(int howmany) throws IOException, IndexOutOfBoundsException, EndOfFileSignal;

	/**
	 * Get a single byte from the current position in stream and increment the position by one.
	 * This method is equal to calling {@link #getByte()} and {@link #skipBytes(1)}.
	 *
	 * @return
	 * @throws IOException
	 * @throws EndOfFileSignal
	 * @see {@link #readBytes(int)}
	 */
	public abstract byte readByte() throws IOException, EndOfFileSignal;

	/**
	 * Increment the internal position in the stream.
	 *
	 * @param howmany
	 * @throws IOException
	 * @throws IndexOutOfBoundsException
	 */
	public abstract void skipBytes(int howmany) throws IOException, IndexOutOfBoundsException;

	/**
	 * The internal position in the stream.
	 *
	 * @return
	 */
	public abstract int getPosition();

	/**
	 * The associated CCSID.
	 *
	 * @return
	 */
	public abstract int getCCSID();

	/**
	 * True if more bytes available.
	 * <br><br>
	 * <u>Hint:</u>
	 * There have to be at least on try to load more bytes, to detect
	 * the end of a stream. Thus, it's possible to get 0 bytes back,
	 * even if {@link #hasMoreBytes()} returned true.
	 *
	 * @return
	 */
	public abstract boolean hasMoreBytes();

}
