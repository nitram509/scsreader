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
import java.io.FileOutputStream;

import net.scs.reader.EndOfFileSignal;
import net.scs.reader.IPrinterMicroCommand;
import net.scs.reader.IScsDataProvider;
import net.scs.reader.ReaderConfig;
import net.scs.reader.SCSStreamReader;
import net.scs.reader.dataprovider.As400ScsDataProviderFactory;
import net.scs.reader.virtualprinter.PdfPrinter;
import net.scs.reader.virtualprinter.PrinterConfig;

import com.ibm.as400.access.SpooledFile;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

public class ExamplePdfPrinter {

	public static void main(String[] args) throws Exception {
		SpooledFile spooledFile = null; // TODO: provide your spool file!
		final IScsDataProvider dp = As400ScsDataProviderFactory.getPrintObjectDataProvider(spooledFile);
		final ReaderConfig rcfg = ReaderConfig.getDefault();
		final SCSStreamReader reader = new SCSStreamReader(dp, rcfg);

		PrinterConfig pcfg = PrinterConfig.getDefault();
		float FONT_SIZE = 9.0F;
		float leading = FONT_SIZE * 1.05F;
		
		// calculate font, font size, and margins for the PDF
		final Font monoSpacedFont = FontFactory.getFont(BaseFont.COURIER, FONT_SIZE);
		final Font monoSpacedFontBold = FontFactory.getFont(BaseFont.COURIER_BOLD, Font.BOLD);
		Rectangle pageSize = new Rectangle(PageSize.A4);
		final Document pdfdoc = new Document(pageSize);

		final FileOutputStream fos = new FileOutputStream("test.pdf");
		final PdfWriter pdfwriter = PdfWriter.getInstance(pdfdoc, fos);
		pdfdoc.open();
		
		PdfPrinter pdfprinter = new PdfPrinter(pcfg, pdfdoc, monoSpacedFont, monoSpacedFontBold, leading);
		
		try {
			while (reader.hasNext()) {
				final IPrinterMicroCommand event = reader.next();
				if (event == null) break;
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