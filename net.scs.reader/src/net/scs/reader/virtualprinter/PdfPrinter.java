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

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;

public class PdfPrinter extends AbstractPrinter {

	private static final char SP = '\u0020';
	private static final char NBSP = '\u00a0';

	private final Document pdfdoc;
	private final Font font;
	private final Font fontbold;
	private final float leading;

	private int currentPage = 1;

	/**
	 * @param pdfdoc
	 * @param font
	 * @param fontbold
	 * @param leading
	 */
	public PdfPrinter(Document pdfdoc, Font font, Font fontbold, float leading) {
		this(PrinterConfig.getDefault(), pdfdoc, font, fontbold, leading);
	}

	/**
	 * @param printerConfig
	 * @param pdfdoc
	 * @param font
	 * @param fontbold
	 * @param leading
	 */
	public PdfPrinter(PrinterConfig printerConfig, Document pdfdoc, Font font, Font fontbold, float leading) {
		super(printerConfig);
		this.pdfdoc = pdfdoc;
		this.font = font;
		this.fontbold = fontbold;
		this.leading = leading;
	}

	/* (non-Javadoc)
	 * @see com.ibm.scs.virtualprinter.TextPrinter#formFeed()
	 */
	@Override
	public void formFeed() {
		printCurrentPage();

		// do the normal stuff
		super.formFeed();
	}

	private void printCurrentPage() {
		// first create a new page
		pdfdoc.newPage();
		pdfdoc.setPageCount(currentPage++);

		boolean emptypage = true;
		// second write the content
		for (VirtualLine currentLine : getLinesOnCurrentPage()) {
			emptypage = false;
			Paragraph p = new Paragraph();
			p.setSpacingAfter(0.0f);
			p.setSpacingBefore(0.0f);
			p.setExtraParagraphSpace(0.0f);
			p.setLeading(leading);
			currentLine.position(0);
			StringBuilder sb = new StringBuilder();
			boolean isbold = false;
			if (!currentLine.hasNext()) {
				// empty lines need at least one character
				sb.append(printerConfig.NL);
			}
			while (currentLine.hasNext()) {
				final EnhancedCharacter echar = currentLine.next();
				// Workaround, replace 'normal spaces' with 'non-breaking-spaces'
				// <a href="http://sourceforge.net/tracker/?func=detail&aid=2866002&group_id=15255&atid=315255">
				//     Multiline paragraph, leading spaces are ignored problem - ID: 2866002
				// </a>
				char c = (echar.getChar() == SP) ? NBSP : echar.getChar();
				if (isbold == echar.isBold()) {
					sb.append(c);
				} else {
					p.add(new Chunk(sb.toString(), (isbold) ? fontbold : font));
					sb = new StringBuilder();
					sb.append(c);
					isbold = !isbold; // flip it
				}
			}
			p.add(new Chunk(sb.toString(), (isbold) ? fontbold : font));
			try {
				pdfdoc.add(p);
			} catch (DocumentException e) {
				// transform into RuntimeException
				throw new RuntimeException(e);
			}
		}
		if (emptypage) {
			try {
				pdfdoc.add(new Paragraph(Character.toString(NBSP)));
			} catch (DocumentException e) {
				// transform into RuntimeException
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void finish() {
		final VirtualLine[] linesOnCurrentPage = getLinesOnCurrentPage();
		// ignore empty last page
		if ( !(linesOnCurrentPage.length == 1 && linesOnCurrentPage[0].hasNext() == false)) {
			printCurrentPage();
		}
	}

}
