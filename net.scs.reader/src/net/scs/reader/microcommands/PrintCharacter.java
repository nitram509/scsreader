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

import net.scs.reader.IScsDataProvider;
import net.scs.reader.IVirtualPrinter;

import com.ibm.as400.access.CharConverter;

/**
 * Special command that is able to print one or more characters.
 */
public class PrintCharacter extends PrinterMicroCommandAdapter {

	private final StringBuilder stringToPrint = new StringBuilder();
	private int length = 0;

	/**
	 * @param dataProvider
	 * @param charconv
	 * @param code
	 */
	public PrintCharacter(IScsDataProvider dataProvider, CharConverter charconv, byte... code) {
		super(dataProvider);
		this.stringToPrint.append(charconv.byteArrayToString(code));
		this.length += code.length;
	}

	@Override
	public void print(IVirtualPrinter printer) {
		printer.printChars(stringToPrint.toString().toCharArray());
	}

	/* (non-Javadoc)
	 * @see com.ibm.scs.microcommands.PrinterMicroCommandAdapter#getLength()
	 */
	@Override
	public int getLength() {
		return length;
	}

	public void append(CharConverter charconv, byte... code) {
		this.stringToPrint.append(charconv.byteArrayToString(code));
		this.length += code.length;
	}

}
