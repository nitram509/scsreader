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

/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import net.scs.reader.EndOfFileSignal;
import net.scs.reader.IPrinterMicroCommand;
import net.scs.reader.IScsDataProvider;
import net.scs.reader.ReaderConfig;
import net.scs.reader.SCSStreamReader;
import net.scs.reader.dataprovider.InputStreamDataProvider;
import net.scs.reader.virtualprinter.PrinterConfig;
import net.scs.reader.virtualprinter.SwtPrinter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ibm.as400.access.SCS5256Writer;

/*
 * Based on Snippet163.java on http://www.eclipse.org/swt/snippets/
 */

/**
 * Simple Example, how to use the SCS Reader within a SWT StyledText editor.
 */
public class SwtStyledTextPrinter {
	
	private final static int CCSID = 1141;
	
	private ByteArrayOutputStream baos;

	private StyledText styledtext;

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		SwtStyledTextPrinter main = new SwtStyledTextPrinter();
		try {
			main.run(shell);
		} catch (Exception e) {
			e.printStackTrace();
		}
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private void run(Shell shell) throws Exception {
		
		initLayout(shell);
		setupPageData();
		
		final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		final IScsDataProvider dp = new InputStreamDataProvider(bais, CCSID);
		
		final ReaderConfig rcfg = new ReaderConfig.Builder()
				.collectPrintableChars(true)
				.ignoreUnknownControlCodes(false)
				.ignoreNulls(false)
				.getConfig();
		final SCSStreamReader reader = new SCSStreamReader(dp, rcfg);

		PrinterConfig pcfg = PrinterConfig.getDefault();
		SwtPrinter printer = new SwtPrinter(pcfg);

		try {
			while (reader.hasNext()) {
				final IPrinterMicroCommand event = reader.next();
				if (event == null) break;
				printer.runMicroCommand(event);
			}
		} catch (EndOfFileSignal e) {
			System.out.println("eof.");
		}

		printer.finish();
		printer.writeStyledText(styledtext);
	}

	private void initLayout(Shell shell) {
		final Display display = shell.getDisplay();
		
		shell.setLayout(new FillLayout());
		styledtext = new StyledText(shell, SWT.BORDER);
		styledtext.setFont(new Font(display, "Courier New", 10, SWT.NORMAL));
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