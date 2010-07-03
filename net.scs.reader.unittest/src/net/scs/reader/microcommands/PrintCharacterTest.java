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
package net.scs.reader.microcommands;

import static org.junit.Assert.*;

import java.io.IOException;

import net.scs.reader.EndOfFileSignal;
import net.scs.reader.IScsDataProvider;

import org.junit.Before;
import org.junit.Test;

import com.ibm.as400.access.CharConverter;

public class PrintCharacterTest {

	private CharConverter charconv;
	private MockDataProvider dataProvider;

	@Before
	public void setUp() throws Exception {
		charconv = new MockConverter();
		dataProvider = new MockDataProvider();
	}

	@Test
	public void testGetLength() {
		final PrintCharacter pc = new PrintCharacter(dataProvider, charconv, "1".getBytes());
		pc.append(charconv, "2".getBytes());
		pc.append(charconv, "3".getBytes());
		pc.append(charconv, "4".getBytes());
		
		assertEquals("should be 4", 4, pc.getLength());
		
	}
	
	/*
	 * ========================================================================
	 */

	private static class MockConverter extends CharConverter {

		private static final long serialVersionUID = 8663109427376806616L;

		/* (non-Javadoc)
		 * @see com.ibm.as400.access.CharConverter#byteArrayToString(byte[])
		 */
		@Override
		public String byteArrayToString(byte[] source) {
			return new String(source);
		}
		
	}
	
	/*
	 * =======================================================================
	 */
	
	private static class MockDataProvider implements IScsDataProvider {

		@Override
		public byte getByte() throws IOException, EndOfFileSignal {
			return 0;
		}

		@Override
		public byte[] getBytes(int howmany) throws IOException,
				IndexOutOfBoundsException {
			return null;
		}

		@Override
		public int getCCSID() {
			return 0;
		}

		@Override
		public int getPosition() {
			return 0;
		}

		@Override
		public boolean hasMoreBytes() {
			return false;
		}

		@Override
		public byte readByte() throws IOException, EndOfFileSignal {
			return 0;
		}

		@Override
		public byte[] readBytes(int howmany) throws IOException,
				IndexOutOfBoundsException, EndOfFileSignal {
			return null;
		}

		@Override
		public void skipBytes(int howmany) throws IOException,
				IndexOutOfBoundsException {
			
		}
		
	}
}
