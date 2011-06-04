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

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Prints on a {@link JTextPane} widget. Is able to display bold printing.
 * I'ts recommended to use a fixed width (monospaced) font.
 */
public class SwingPrinter extends AbstractPrinter {

	public SwingPrinter() {
		super();
	}

	public SwingPrinter(PrinterConfig printerConfig) {
		super(printerConfig);
	}

	/**
	 * Put the text on the widget ...
	 * 
	 * @param textpane
	 * @throws IOException
	 */
	public void writeTextPane(JTextPane textpane) throws IOException {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setBold(attr, true);
		Document doc = textpane.getStyledDocument();
		
		for (VirtualLine line : lines) {
			line.position(0);
			StringBuilder sb = new StringBuilder();
			boolean isbold = false;
			while (line.hasNext()) {
				final EnhancedCharacter echar = line.next();
				if (isbold == echar.isBold()) {
					sb.append(echar.getChar());
				} else {
					attr = new SimpleAttributeSet();
					StyleConstants.setBold(attr, isbold);
					try {
						doc.insertString(doc.getLength(), sb.toString(), attr);
					} catch (BadLocationException e) {
						// transform into RuntimeException
						throw new RuntimeException(e);
					}
					
					sb = new StringBuilder();
					sb.append(echar.getChar());
					isbold = !isbold; // flip it
				}
			}
			attr = new SimpleAttributeSet();
			StyleConstants.setBold(attr, isbold);
			try {
				doc.insertString(doc.getLength(), sb.toString() + printerConfig.NL, attr);
			} catch (BadLocationException e) {
				// transform into RuntimeException
				throw new RuntimeException(e);
			}
		}
		
	}
	
}
