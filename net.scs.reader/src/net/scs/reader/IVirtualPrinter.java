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
 * Things a virtual printer can do.
 */
public interface IVirtualPrinter {

	/**
	 * write a new line
	 */
	public abstract void newLine();

	/**
	 * do a form feed (skip to next page)
	 */
	public abstract void formFeed();

	/**
	 * do a carrige return
	 */
	public abstract void carrigeReturn();

	/**
	 * print a simple space
	 */
	public abstract void space();

	/**
	 * print a single character
	 *
	 * @param character
	 */
	public abstract void printChar(char character);

	/**
	 * print multiple characters at once
	 *
	 * @param characters
	 */
	public abstract void printChars(char[] characters);

	/**
	 * Move head direction horizontal
	 *
	 * @param absolutePosition from left oft the page, starting at position 1
	 */
	public abstract void setHeadDirH(int absolutePosition);

	/**
	 * Move head direction vertical
	 *
	 * @param absolutePosition from top of the page, starting at line 1
	 */
	public abstract void setHeadDirV(int absolutePosition);

	/**
	 * get the current horizontal position of the virtual printer's head
	 * starting at position 1
	 *
	 * @return
	 */
	public abstract int getHeadDirH();

	/**
	 * get the current vertical position of the virtual printer's head
	 * starting at line 1
	 *
	 * @return
	 */
	public abstract int getHeadDirV();

	/**
	 * flushing internal buffers and stuff
	 */
	public abstract void finish();
}
