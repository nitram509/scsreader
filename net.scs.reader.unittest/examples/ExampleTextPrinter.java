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
import java.io.File;
import java.io.FileWriter;

import net.scs.reader.EndOfFileSignal;
import net.scs.reader.IPrinterMicroCommand;
import net.scs.reader.IScsDataProvider;
import net.scs.reader.ReaderConfig;
import net.scs.reader.SCSStreamReader;
import net.scs.reader.dataprovider.As400ScsDataProviderFactory;
import net.scs.reader.virtualprinter.PrinterConfig;
import net.scs.reader.virtualprinter.TextPrinter;

import com.ibm.as400.access.SpooledFile;

public class ExampleTextPrinter {

	public static void main(String[] args) throws Exception {
		SpooledFile spooledFile = null; // TODO: provide your spool file!
		final IScsDataProvider dp = As400ScsDataProviderFactory.getPrintObjectDataProvider(spooledFile);
		final ReaderConfig rcfg = new ReaderConfig.Builder()
				.collectPrintableChars(true)
				.ignoreUnknownControlCodes(false)
				.ignoreNulls(false)
				.getConfig();
		final SCSStreamReader reader = new SCSStreamReader(dp, rcfg);

		PrinterConfig pcfg = PrinterConfig.getDefault();
		TextPrinter printer = new TextPrinter(pcfg);

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

		FileWriter fw = new FileWriter(new File("test.txt"));
		printer.writeText(fw);
		fw.close();
	}
}
