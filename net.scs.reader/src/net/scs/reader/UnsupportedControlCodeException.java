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

/**
 *  Signals, that the current control code is not supported.
 */
public final class UnsupportedControlCodeException extends UnsupportedOperationException {

	private static final long serialVersionUID = 4440022362384052781L;

	public UnsupportedControlCodeException() {
		super("While parsing the SCS data, there was an unsupported control code!");
	}

	public UnsupportedControlCodeException(int position, byte... codes) {
		super("Error, unsupported SCS control code at: 0x" + Integer.toHexString(position) + " codes: " + codesToString(codes) + ".");
	}

	private static final String codesToString(byte... codes) {
		if (codes == null || codes.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<codes.length; i++) {
			if (sb.length() > 0) {
				sb.append(' ');
			}
			sb.append("0x");
			sb.append(Integer.toHexString(codes[i] & 0xFF));
		}
		return sb.toString();
	}

}
