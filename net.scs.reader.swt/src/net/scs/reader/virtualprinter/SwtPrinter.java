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

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

/**
 * Prints on a {@link StyledText} widget. Is able to display bold printing.
 * I'ts recommended to use a fixed width (monospaced) font.
 */
public class SwtPrinter extends AbstractPrinter {

	public SwtPrinter() {
		super();
	}

	public SwtPrinter(PrinterConfig printerConfig) {
		super(printerConfig);
	}

	/**
	 * Put the text on the widget ...
	 * 
	 * @param styledText
	 * @throws IOException
	 */
	public void writeStyledText(StyledText styledText) throws IOException {
		final StringWriter sw = new StringWriter();
		final List<StyleRange> ranges = new ArrayList<StyleRange>();
		int globalpos = 0;
		for (VirtualLine vline : lines) {
			// print first
			sw.write(vline.toString());
			sw.write(printerConfig.NL);
			// calculate style ranges ...
			vline.position(0);
			boolean isbold = false;
			int linepos = 0;
			while (vline.hasNext()) {
				final EnhancedCharacter echar = vline.next();
				if (isbold == echar.isBold()) {
					linepos += 1; 
				} else {
					if (isbold) {
						ranges.add(new StyleRange(globalpos, linepos, null, null, SWT.BOLD));
					}
					globalpos += linepos;
					linepos = 1;
					isbold = !isbold; // flip it
				}
			}
			if (isbold) {
				ranges.add(new StyleRange(globalpos, linepos, null, null, SWT.BOLD));
			}
			globalpos += linepos + printerConfig.NL.length();
		}
		// set information on the widget
		styledText.setText(sw.toString());
		for (StyleRange sr : ranges) {
			styledText.setStyleRange(sr);
		}
	}
	
}
