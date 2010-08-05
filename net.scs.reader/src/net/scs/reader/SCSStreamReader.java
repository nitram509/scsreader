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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.scs.reader.microcommands.Bell;
import net.scs.reader.microcommands.CarrigeReturn;
import net.scs.reader.microcommands.FormFeed;
import net.scs.reader.microcommands.LineFeed;
import net.scs.reader.microcommands.NewLine;
import net.scs.reader.microcommands.NotSupportedControlCode;
import net.scs.reader.microcommands.NullCmd;
import net.scs.reader.microcommands.PresentationPageMedia;
import net.scs.reader.microcommands.PresentationPosition;
import net.scs.reader.microcommands.PrintCharacter;
import net.scs.reader.microcommands.RequiredNewLine;
import net.scs.reader.microcommands.SetChainImage;
import net.scs.reader.microcommands.SetGraphicErrorAction;
import net.scs.reader.microcommands.SetTranslateTable;

import com.ibm.as400.access.CharConverter;

public class SCSStreamReader {

	private final ReaderConfig rdrcfg;

	private final IScsDataProvider dataProvider;
	private final CharConverter charconv;

	/*
	 * =======================================================================
	 */

	/**
	 * Using default {@link ReaderConfig}.
	 *
	 * @param dataProvider
	 * @throws UnsupportedEncodingException
	 */
	public SCSStreamReader(IScsDataProvider dataProvider) throws UnsupportedEncodingException {
		this(dataProvider, ReaderConfig.getDefault());
	}

	/**
	 * @param dataProvider
	 * @param readerConfig
	 * @throws UnsupportedEncodingException
	 */
	public SCSStreamReader(IScsDataProvider dataProvider, ReaderConfig readerConfig) throws UnsupportedEncodingException {
		super();
		this.dataProvider = dataProvider;
		this.rdrcfg = readerConfig;
		this.charconv = new CharConverter(dataProvider.getCCSID());
	}

	/*
	 * =======================================================================
	 */

	/**
	 * @return
	 */
	public boolean hasNext() {
		return dataProvider.hasMoreBytes();
	}

	/**
	 * @return printer micro comman OR null
	 * @throws IOException
	 */
	public IPrinterMicroCommand next() throws IOException {
		IPrinterMicroCommand cmd = null;
		StreamContext ctx = null;
		do {
			ctx = new StreamContext();
			cmd = readNextCommand(ctx);
			if (cmd != null) {
				dataProvider.skipBytes(cmd.getLength());
			}
		} while (ctx.isnull && rdrcfg.ignoreNulls);
		return cmd;
	}

	/**
	 * Reads the next command byte. Could be called recursivly for better
	 * handling of collecting characters feature.
	 *
	 * @param ctx
	 * @return
	 * @throws IOException
	 * @throws EndOfFileSignal
	 */
	private IPrinterMicroCommand readNextCommand(StreamContext ctx) throws IOException, EndOfFileSignal {
		if (!hasNext()) return ctx.cmd;
		final byte[] bytes = dataProvider.getBytes(ctx.offset);
		if (bytes == null || bytes.length < ctx.offset) {
			return ctx.cmd;
		}
		byte code = bytes[bytes.length-1];
		int intcode = code & 0xFF;
		if (intcode >= 0x40) {
			// handling characters
			if (ctx.cmd != null) { // already collected characters
				((PrintCharacter)ctx.cmd).append(charconv, code);
			} else {
				ctx.cmd = new PrintCharacter(dataProvider, charconv, code);
			}
			if (rdrcfg.collectPrintableChars) {
				ctx.offset++;
				return readNextCommand(ctx);
			}
		} else {
			if (ctx.cmd != null) return ctx.cmd; // collected characters
			// handling control codes
			switch (code) {
			case Bell.OPCODE :
				ctx.cmd = new Bell(dataProvider);
				break;
			case CarrigeReturn.OPCODE :
				ctx.cmd = new CarrigeReturn(dataProvider);
				break;
			case FormFeed.OPCODE :
				ctx.cmd = new FormFeed(dataProvider);
				break;
			case NewLine.OPCODE :
				ctx.cmd = new NewLine(dataProvider);
				break;
			case NullCmd.OPCODE :
				ctx.cmd = new NullCmd(dataProvider);
				ctx.isnull = true;
				break;
			case LineFeed.OPCODE :
				ctx.cmd = new LineFeed(dataProvider);
				break;
			case RequiredNewLine.OPCODE :
				ctx.cmd = new RequiredNewLine(dataProvider);
				break;
			case PresentationPosition.OPCODE:
				ctx.cmd = new PresentationPosition(dataProvider);
				break;
			case (byte)0x2B:
				ctx.cmd = dispatch_2B();
				break;
			default:
				if (!rdrcfg.ignoreUnknownControlCodes) {
					throw new UnsupportedControlCodeException(dataProvider.getPosition(), code);
				} else {
					ctx.cmd = new NotSupportedControlCode(dataProvider);
				}
			}
		}
		return ctx.cmd;
	}

	/**
	 * dispatching for 0x2B control codes ...
	 */
	private IPrinterMicroCommand dispatch_2B() throws IOException {
		if (!hasNext()) return null;

		IPrinterMicroCommand cmd = null;
		byte code = dataProvider.getBytes(2)[1];
		switch (code) {
		case (byte)0xC1:
		case (byte)0xC2:
		case (byte)0xC3:
		case (byte)0xC6:
			if (!rdrcfg.ignoreUnknownControlCodes) {
				throw new UnsupportedControlCodeException(dataProvider.getPosition(), (byte)0x2b, code);
			} else {
				cmd = new NotSupportedControlCode(dataProvider);
			}
			break;
		case (byte)0xD0:

		case (byte)0xD3:
		case (byte)0xD4:
		case (byte)0xD5:
		case (byte)0xD6:
		case (byte)0xD7:
		case (byte)0xD8:
		case (byte)0xD9:
		case (byte)0xDA:
		case (byte)0xDB:
		case (byte)0xDC:
		case (byte)0xDD:
		case (byte)0xDE:
		case (byte)0xDF:
			cmd = new SetChainImage(dataProvider);
			break;
		case (byte)0xC8:
			cmd = new SetGraphicErrorAction(dataProvider);
			break;
		case (byte)0xD1:
			cmd = new SetTranslateTable(dataProvider);
			break;
		case (byte)0xD2:
			// XXX: special cases "Set print density" X'2BD20229' or X'2BD20429xxxx'
			cmd = new PresentationPageMedia(dataProvider, rdrcfg);
			break;
		default:
			// nothing to do here
		}
		return cmd;
	}

	/*
	 * ========================================================================
	 */

	/**
	 * Holds the context of the recursive working stream reader.
	 */
	private static class StreamContext {

		private IPrinterMicroCommand cmd = null;
		private int offset = 1;
		private boolean isnull = false;

	}
}
