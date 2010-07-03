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
