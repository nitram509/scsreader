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

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class GenericTextPrinterTest {
	
	private TextPrinter printer;

	@Before
	public void setup() {
		printer = new TextPrinter();
	}
	
	@Test
	public void testMoveHead() {
		printer.setHeadDirH(1);
		assertEquals("horizinontal #1", 1, printer.getHeadDirH());
		
		printer.setHeadDirH(50);
		assertEquals("horizinontal #50", 50, printer.getHeadDirH());
		
		printer.setHeadDirH(1);
		assertEquals("horizinontal back to #1", 1, printer.getHeadDirH());
		
		printer.printChar('a');
		assertEquals("one character", 2, printer.getHeadDirH());
		
		printer.setHeadDirH(1);
		printer.printChars(new char[]{'1','2','3'});
		assertEquals("multiple chars", 4, printer.getHeadDirH());
		
		printer.setHeadDirH(50);
		printer.newLine();
		assertEquals("new line", 1, printer.getHeadDirH());
	}

	@Test
	public void testMoveHeadVertical() {
		printer.setHeadDirV(1); 
		VirtualLine[] lines = printer.getLinesOnCurrentPage();
		assertEquals("single line", 1, lines.length);
		
		printer.setHeadDirV(5); 
		lines = printer.getLinesOnCurrentPage();
		assertEquals("five lines", 5, lines.length);
		
		printer.setHeadDirV(49); 
		printer.newLine();
		lines = printer.getLinesOnCurrentPage();
		assertEquals("new line", 50, lines.length);
		
		printer.setHeadDirV(1); 
		for (int i=0; i<50; i++) printer.newLine();
		lines = printer.getLinesOnCurrentPage();
		assertEquals("multiple lines ", 51, lines.length);
	}
}
