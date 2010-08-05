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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import net.scs.reader.dataprovider.InputStreamDataProvider;

import org.junit.Before;
import org.junit.Test;

public class SCSStreamReaderTest {

	private ByteArrayInputStream bais;

	@Before
	public void setUp() throws Exception {
		byte[] bytes = new byte[] { 
				(byte)0xF1, (byte)0xF2, (byte)0xF3, 0x23, (byte)0xF4, (byte)0xF5, (byte)0xF6
				};
		bais = new ByteArrayInputStream(bytes);
	}

	@Test
	public void testUnsupportedControlCodes() throws IOException {
		final IScsDataProvider dp = new InputStreamDataProvider(bais, 273); 
		final ReaderConfig rcfg = new ReaderConfig.Builder()
				.ignoreUnknownControlCodes(true)
				.getConfig();
		final SCSStreamReader reader = new SCSStreamReader(dp, rcfg);
		try {
			while (reader.hasNext()) {
				final IPrinterMicroCommand event = reader.next();
				System.out.println(event);
			}
		} catch (EndOfFileSignal e) {
			System.out.println("eof.");
		}

	}

}
