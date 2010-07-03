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
package net.scs.reader.virtualprinter;

/**
 * A text printer implementation, which produces text files without any <br>
 * <br>
 *
 * <u>Hint:</u> the positioning system starts with 1 as the first position.
 * This is expected by the SCS data stream commands. Thus there is an internal
 * conversion to the internally used {@link VirtualLine}, wich is 0-based.
 */
public class TextPrinter extends AbstractPrinter {

	/* (non-Javadoc)
	 * @see net.scs.reader.virtualprinter.AbstractPrinter#formFeed()
	 */
	@Override
	public void formFeed() {
		newLine();
		newLine();
		super.formFeed();
	}



}
