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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import net.scs.reader.dataprovider.InputStreamDataProvider;
import net.scs.reader.microcommands.FormFeed;
import net.scs.reader.microcommands.LineFeed;
import net.scs.reader.microcommands.NullCmd;
import net.scs.reader.virtualprinter.PdfPrinter;
import net.scs.reader.virtualprinter.PrinterConfig;
import net.scs.reader.virtualprinter.TextPrinter;

import org.junit.Before;
import org.junit.Test;

import com.ibm.as400.access.SCS5256Writer;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

public class Scs5256WriterTests {

	private final static int CCSID = 1141;
	
	private ByteArrayOutputStream baos;

	@Before
	public void setup() throws Exception {
		
		baos = new ByteArrayOutputStream();

		SCS5256Writer scsWtr = new SCS5256Writer(baos, CCSID);

		// Write the contents of the spool file.
		scsWtr.absoluteVerticalPosition(6);
		scsWtr.write("1...5....0....5....0....");
		scsWtr.newLine();
		scsWtr.absoluteHorizontalPosition(13);
		scsWtr.write("13");
		scsWtr.newLine();
		scsWtr.write("          Java Printing");
		scsWtr.newLine();
		scsWtr.newLine();
		scsWtr.write("This document was created using the IBM Toolbox for Java.");
		scsWtr.newLine();
		scsWtr.write("The rest of this document shows some of the things that");
		scsWtr.newLine();
		scsWtr.write("can be done with the SCS5256Writer class.");
		scsWtr.newLine();
		scsWtr.write("Line one");
		scsWtr.newLine();
		scsWtr.write("Line two");
		scsWtr.newLine();
		scsWtr.write("Line three");
		scsWtr.newLine();
		scsWtr.endPage();
		
		scsWtr.absoluteVerticalPosition(6);
		scsWtr.write("new page, line 6");
		scsWtr.endPage();
		
		scsWtr.endPage(); // empty page in the middle
		
		scsWtr.write("Line #1");scsWtr.newLine();
		scsWtr.write("Line #2");scsWtr.newLine();
		scsWtr.write("Line #3");scsWtr.newLine();
		scsWtr.write("Line #4");scsWtr.newLine();
		scsWtr.write("Line #5");scsWtr.newLine();
		scsWtr.write("Line #6");scsWtr.newLine();
		scsWtr.write("Line #7");scsWtr.newLine();
		scsWtr.write("Line #8");scsWtr.newLine();
		scsWtr.write("Line #9");scsWtr.newLine();
		scsWtr.endPage(); 
		
		scsWtr.write("aaaccc");
		scsWtr.carriageReturn();
		scsWtr.write("  bb");
		scsWtr.endPage();

		scsWtr.write("123");
		scsWtr.lineFeed();
		scsWtr.write("456");
		scsWtr.endPage();
				
		scsWtr.write("123");
		scsWtr.write("\0");
		scsWtr.write("\0"); // testing 0x00 ...
		scsWtr.write("456");
		
		scsWtr.close();
		
//		FileOutputStream fos = new FileOutputStream("test.dump");
//		fos.write(baos.toByteArray());
//		fos.close();
	}

	@Test
	public void testReadStreamText() throws Exception{
		final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		final InputStreamDataProvider dp = new InputStreamDataProvider(bais, CCSID);
		final ReaderConfig rcfg = new ReaderConfig.Builder().collectPrintableChars(true).ignoreUnknownControlCodes(false).ignoreNulls(false).getConfig();
		final SCSStreamReader reader = new SCSStreamReader(dp, rcfg);
		
		TextPrinter printer = new TextPrinter();
		
		try {
			while (reader.hasNext()) {
				final IPrinterMicroCommand event = reader.next();
				if (event == null) break;
				if (event instanceof LineFeed) {
//					LineFeed new_name = (LineFeed) event;
				}
				if (event instanceof NullCmd) {
					System.out.println("null ok");
				}
				printer.runMicroCommand(event);
			}
		} catch (EndOfFileSignal e) {
			System.out.println("eof.");
		}
		
		printer.finish();
		
		FileWriter fw = new FileWriter(new File("test.txt"));
		printer.writeText(fw);
		fw.close();
	}

	@Test
	public void testReadStreamPdf() throws Exception{
		final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		final InputStreamDataProvider dp = new InputStreamDataProvider(bais, CCSID);
		final ReaderConfig rcfg = new ReaderConfig.Builder().collectPrintableChars(true).ignoreUnknownControlCodes(false).getConfig();
		final SCSStreamReader reader = new SCSStreamReader(dp, rcfg);
		
		PrinterConfig pcfg = PrinterConfig.getDefault();
		
		float FONT_SIZE = 9.0F;
		float leading = FONT_SIZE * 1.05F;
		
		// calculate font, font size, and margins for the PDF
		final Font monoSpacedFont = FontFactory.getFont(BaseFont.COURIER, FONT_SIZE);
		final Font monoSpacedFontBold = FontFactory.getFont(BaseFont.COURIER_BOLD, Font.BOLD);
//		final float rows = spooledFile.getFloatAttribute(PrintObject.ATTR_PAGELEN);
//		final float cols = spooledFile.getFloatAttribute(PrintObject.ATTR_PAGEWIDTH);
//		float refSize = monoSpacedFont.getBaseFont().getWidthPoint(REFERENZ_STRING, FONT_SIZE);
//		float xSize = ((cols * refSize) / REFERENZ_STRING.length()) + MARGIN_LEFT + MARGIN_RIGHT + 10 /*Reserve*/;
//		float ySize = (rows+1) * LEADING + MARGIN_TOP + MARGIN_BOTTOM;
		Rectangle pageSize = new Rectangle(PageSize.A4);
		final Document pdfdoc = new Document(pageSize);

		final FileOutputStream fos = new FileOutputStream("test.pdf");
		final PdfWriter pdfwriter = PdfWriter.getInstance(pdfdoc, fos);
//		pdfdoc.setMargins(MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, MARGIN_BOTTOM);
		pdfdoc.open();
		
		PdfPrinter pdfprinter = new PdfPrinter(pcfg, pdfdoc, monoSpacedFont, monoSpacedFontBold, leading);
		
		try {
			while (reader.hasNext()) {
				final IPrinterMicroCommand event = reader.next();
				if (event == null) break;
				if (event instanceof FormFeed) {
//					System.out.println("x-form-feed");
				}
				if (event instanceof LineFeed) {
//					LineFeed new_name = (LineFeed) event;
				}
				pdfprinter.runMicroCommand(event);
			}
		} catch (EndOfFileSignal e) {
			System.out.println("eof.");
		}
		
		pdfprinter.finish();
		pdfdoc.close();
		pdfwriter.close();
	}

}
