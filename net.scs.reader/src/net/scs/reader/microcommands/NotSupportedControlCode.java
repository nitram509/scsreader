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

import net.scs.reader.EndOfFileSignal;
import net.scs.reader.IScsDataProvider;

/**
 * Represents any code, which is not currently supported by SCS Reader.
 */
public class NotSupportedControlCode extends PrinterMicroCommandAdapter {

	private static final int LENGTH = 1;
	private final byte code;
	
	public NotSupportedControlCode(IScsDataProvider dataProvider) throws EndOfFileSignal, IOException {
		super(dataProvider);
		this.code = dataProvider.getByte();
	}

	@Override
	public int getLength() {
		return LENGTH;
	}

	public byte getCode() {
		return code;
	}

}
