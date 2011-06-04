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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import net.scs.reader.EndOfFileSignal;
import net.scs.reader.IPrinterMicroCommand;
import net.scs.reader.IScsDataProvider;
import net.scs.reader.ReaderConfig;
import net.scs.reader.SCSStreamReader;
import net.scs.reader.dataprovider.InputStreamDataProvider;
import net.scs.reader.virtualprinter.PrinterConfig;
import net.scs.reader.virtualprinter.SwingPrinter;

import com.ibm.as400.access.SCS5256Writer;

/**
 * Simple Example, how to use the SCS Reader within a Swing JTextPane editor.
 */
public class SwingTextPanePrinter {

	private final static int CCSID = 1141;

	private ByteArrayOutputStream baos;

	private JTextPane textpane;

	public static void main(String[] args) {
		SwingTextPanePrinter pgm = new SwingTextPanePrinter();
		try {
			pgm.setupPageData();
			JFrame frame = pgm.initLayout();
			pgm.run();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JFrame initLayout() throws Exception {
		JFrame frame = new JFrame("net.scs.reader Swing example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = frame.getContentPane();
		textpane = new JTextPane();
		textpane.setEditable(false);
		Font font = Font.getFont("Courier New");
		textpane.setFont(font);
		JScrollPane scrollPane = new JScrollPane(textpane);
		content.add(scrollPane, BorderLayout.CENTER);
		frame.setSize(400, 300);
		return frame;
	}

	private void run() throws Exception {

		final ByteArrayInputStream bais = new ByteArrayInputStream(
				baos.toByteArray());
		final IScsDataProvider dp = new InputStreamDataProvider(bais, CCSID);

		final ReaderConfig rcfg = new ReaderConfig.Builder()
				.collectPrintableChars(true).ignoreUnknownControlCodes(false)
				.ignoreNulls(false).getConfig();
		final SCSStreamReader reader = new SCSStreamReader(dp, rcfg);

		PrinterConfig pcfg = PrinterConfig.getDefault();
		SwingPrinter printer = new SwingPrinter(pcfg);

		try {
			while (reader.hasNext()) {
				final IPrinterMicroCommand event = reader.next();
				if (event == null)
					break;
				printer.runMicroCommand(event);
			}
		} catch (EndOfFileSignal e) {
			System.out.println("eof.");
		}

		printer.finish();
		printer.writeTextPane(textpane);
	}

	public void setupPageData() throws Exception {

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
		// simulate double printing => results in bold font
		scsWtr.absoluteHorizontalPosition(1);
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

		scsWtr.close();
	}
}