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

/**
 * Configuration parameter for {@link SCSStreamReader}.
 * Use {@link ReaderConfig.Builder} to create custom configurations.
 */
public final class ReaderConfig {

	/**
	 * If 'true' the reader tries to collect subsequent printable characters.
	 * Otherwise each single character is reflected by one {@link IPrinterMicroCommand}.
	 */
	public final boolean collectPrintableChars;

	/**
	 * Decides if an {@link UnsupportedControlCodeException} is thrown or the
	 * reader tries to silently ignore the unknown control codes.
	 */
	public final boolean ignoreUnknownControlCodes;

	/**
	 * If 'true' all 0x00 values in the stream are ignored and the
	 * stream reader doesn't report a {@link IPrinterMicroCommand}.
	 * Otherwise every 0x00 results in a {@link net.scs.reader.microcommands.NullCmd}.
	 * @see {@link SCSStreamReader#next()}
	 */
	public final boolean ignoreNulls;

	private ReaderConfig(Builder builder) {
		super();
		this.collectPrintableChars = builder.collectPrintableChars;
		this.ignoreUnknownControlCodes = builder.ignoreUnknownControlCodes;
		this.ignoreNulls = builder.ignoreNulls;
	}

	/**
	 * <u>Default:</u>
	 * <ul>
	 * <li>collectPrintableChars = true;</li>
	 * <li>ignoreUnknownControlCodes = false;</li>
	 * <li>ignoreNulls = true;</li>
	 * </ul>
	 *
	 * @return
	 */
	public final static ReaderConfig getDefault() {
		return new Builder().getConfig();
	}

	/**
	 * Builder for custom configurations.<br>
	 * <br>
	 * Sample:
	 * <code>
	 * ReaderConfig cfg = new ReaderConfig.Builder()<br>
	 *         .collectPrintableChars(false)<br>
	 *         .ignoreUnknownControlCodes(true)<br>
	 *         .getConfig();
	 * </code>
	 */
	public final static class Builder {

		// default values
		private boolean collectPrintableChars = true;
		private boolean ignoreUnknownControlCodes = false;
		private boolean ignoreNulls = true;

		// getter
		public ReaderConfig getConfig() {
			return new ReaderConfig(this);
		}

		// setters
		public Builder collectPrintableChars(boolean val) {
			collectPrintableChars = val;
			return this;
		}
		public Builder ignoreUnknownControlCodes(boolean val) {
			ignoreUnknownControlCodes = val;
			return this;
		}
		public Builder ignoreNulls(boolean val) {
			ignoreNulls = val;
			return this;
		}

	}

}
