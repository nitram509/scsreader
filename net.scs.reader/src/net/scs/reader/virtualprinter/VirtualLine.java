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

import java.util.BitSet;

/**
 * This is a virtual line of text, which can simulates printing on paper.
 * Thus it supports bold font, by printing a line twice (which is very
 * common on host platforms).<br>
 * <br>
 * <u>Hint:</u> the positioning system is 0-oriented, which means, 0 is the first position.
 */
public final class VirtualLine {

	private static final char SP = '\u0020';
	private static final char NBSP = '\u00a0';

	private final StringBuilder line = new StringBuilder();
	private final BitSet boldChars = new BitSet();
	private final PrinterConfig prncfg;

	private int position = 0;


	/**
	 * @param printerConfig
	 */
	public VirtualLine(PrinterConfig printerConfig) {
		this.prncfg = printerConfig;
	}

	/**
	 * @return if there are characters left, from the current position
	 */
	public boolean hasNext() {
		return position < line.length();
	}

	/**
	 * @return the character at the current position AND increments position by one
	 * @throws IndexOutOfBoundsException
	 */
	public EnhancedCharacter next() {
		final int p = position++;
		return new ExCharacter(line.charAt(p), boldChars.get(p));
	}

	/**
	 * @return the current position
	 */
	public int position() {
		return position;
	}

	/**
	 * Set the the position, by absolute value
	 *
	 * @param newPosition
	 * @return
	 */
	public int position(int newPosition) {
		while (newPosition > line.length()) {
			line.append(prncfg.SPACE);
		}
		int oldpos = position;
		position = newPosition;
		return oldpos;
	}

	/**
	 * Put a new character at the current position and increments position by one.
	 *
	 * @param newchar
	 * @return the character that was there before
	 */
	public char put(char newchar) {
		if (position >= line.length()) {
			line.append(prncfg.SPACE);
		}
		final char oldchar = line.charAt(position);
		if (oldchar == newchar) {
			boldChars.set(position);
		}
		// blanks do not overwrite already printed chars
		if (newchar != SP && newchar != NBSP) {
			line.setCharAt(position++, newchar);
		} else {
			position++;
		}
		return oldchar;
	}

	/**
	 * @param newchars
	 * @return the characters that were there before
	 * @see {@link VirtualLine#put(char)}
	 */
	public char[] put(char[] newchars) {
		char[] oldchars = new char[newchars.length];
		for (int i=0; i<newchars.length; i++) {
			oldchars[i] = put(newchars[i]);
		}
		return oldchars;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return line.toString();
	}

	/*
	 * ========================================================================
	 */

	/**
	 * Simple bean, which implements {@link EnhancedCharacter}
	 */
	private static final class ExCharacter implements EnhancedCharacter {

		private final char character;
		private final boolean bold;

		public ExCharacter(char character, boolean bold) {
			super();
			this.character = character;
			this.bold = bold;
		}
		@Override
		public char getChar() {
			return character;
		}
		@Override
		public boolean isBold() {
			return bold;
		}

	}
}
