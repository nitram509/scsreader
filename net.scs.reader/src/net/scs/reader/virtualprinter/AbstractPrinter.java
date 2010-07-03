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
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.scs.reader.IPrinterMicroCommand;
import net.scs.reader.IVirtualPrinter;

/**
 * A generic text printer implementation.<br>
 * <br>
 *
 * <u>Hint:</u> the positioning system starts with 1 as the first position.
 * This is expected by the SCS data stream commands. Thus there is an internal
 * conversion to the internally used {@link VirtualLine}, wich is 0-based.
 */
public abstract class AbstractPrinter implements IVirtualPrinter {

	protected final PrinterConfig printerConfig;

	protected List<VirtualLine> lines = new ArrayList<VirtualLine>();

	private int currentLine;
	private int lastPageBreak = 0;

	/**
	 * Using default {@link PrinterConfig}.
	 */
	public AbstractPrinter() {
		this(PrinterConfig.getDefault());
	}

	/**
	 * @param printerConfig
	 */
	public AbstractPrinter(PrinterConfig printerConfig) {
		super();
		this.printerConfig = printerConfig;
		this.currentLine = 0;
		this.lines.add(new VirtualLine(printerConfig));
	}

	/**
	 * @param command
	 */
	public void runMicroCommand(IPrinterMicroCommand command){
		command.print(this);
	}

	/**
	 * Writes all text lines to a {@link Writer}.
	 *
	 * @param writer
	 * @throws IOException
	 */
	public void writeText(Writer writer) throws IOException {
		for (VirtualLine vline : lines) {
			writer.write(vline.toString());
			writer.write(printerConfig.NL);
		}
	}

	@Override
	public void carrigeReturn() {
		lines.get(currentLine).position(0);
	}

	@Override
	public void formFeed() {
		currentLine++; // new page means new line
		while (currentLine >= lines.size()) {
			lines.add(new VirtualLine(printerConfig));
		}
		// and mark this position
		lastPageBreak = currentLine;
	}

	@Override
	public void setHeadDirH(int absolutePosition) {
		lines.get(currentLine).position(absolutePosition - 1);
	}

	@Override
	public void setHeadDirV(int absolutePosition) {
		currentLine = lastPageBreak + absolutePosition - 1;
		while (currentLine >= lines.size()) {
			lines.add(new VirtualLine(printerConfig));
		}
	}

	@Override
	public int getHeadDirH() {
		return lines.get(currentLine).position() + 1;
	}

	@Override
	public int getHeadDirV() {
		return lines.size() - lastPageBreak + 1;
	}

	@Override
	public void newLine() {
		currentLine++;
		while (currentLine >= lines.size()) {
			lines.add(new VirtualLine(printerConfig));
		}
	}

	@Override
	public void space() {
		lines.get(currentLine).put(printerConfig.SPACE.toCharArray());
	}

	@Override
	public void printChar(char character) {
		lines.get(currentLine).put(character);
	}

	@Override
	public void printChars(char[] characters) {
		lines.get(currentLine).put(characters);
	}

	@Override
	public void finish() {
		// nothing to do
	}

	/**
	 * @return the current line on which is printed
	 */
	protected VirtualLine getCurrentLine() {
		return lines.get(currentLine);
	}

	/**
	 * @return all the lines on the current page
	 */
	protected VirtualLine[] getLinesOnCurrentPage() {
		final int nr = lines.size() - lastPageBreak;
		VirtualLine[] result = new VirtualLine[nr];
		for (int i=0; i<nr; i++ ) {
			result[i] = lines.get(lastPageBreak + i);
		}
		return result;
	}

}
