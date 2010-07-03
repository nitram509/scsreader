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

import java.io.IOException;

import net.scs.reader.IScsDataProvider;

/**
 * Control Code: STT
 * EBCDIC      : X'2BD1xx'
 * Name        : Set translate table
 * Line Support: Ignore
 * PCL Support : Ignore
 * @see <a href="http://publib.boulder.ibm.com/infocenter/zos/v1r10/index.jsp?topic=/com.ibm.zos.r10.aopu000/appscs.htm">z/OS V1R8.0-V1R10.0 Infoprint Server User's Guide S544-5746-09</a>
 */
public class SetTranslateTable extends PrinterMicroCommandAdapter {

	private final byte length;

	public SetTranslateTable(IScsDataProvider dataProvider) throws IOException {
		super(dataProvider);
		length = dataProvider.getBytes(3)[2];
	}

	/* (non-Javadoc)
	 * @see com.ibm.scs.microcommands.PrinterMicroCommandAdapter#getLength()
	 */
	@Override
	public int getLength() {
		return 3 + (length & 0xFF);
	}

}
