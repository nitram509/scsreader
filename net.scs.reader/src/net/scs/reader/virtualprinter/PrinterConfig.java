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

/**
 * Configuration parameters for the virtual {@link AbstractPrinter};
 * Use {@link PrinterConfig.Builder} to create custom configurations.
 */
public class PrinterConfig {

	public final String CR;

	public final String NL;

	public final String SPACE;

	public final String FF;

	/*
	 * =======================================================================
	 */

	/**
	 * @param builder
	 */
	private PrinterConfig(Builder builder) {
		this.CR = builder.CR;
		this.FF = builder.FF;
		this.NL = builder.NL;
		this.SPACE = builder.SPACE;
	}

	/**
	 * <u>Default:</u>
	 * <ul>
	 * <li>CR = "\r";</li>
	 * <li>NL = System.getProperty("line.separator");</li>
	 * <li>SPACE = "\u00a0";</li>
	 * <li>FF = "\r";</li>
	 * </ul>
	 *
	 * @return
	 */
	public final static PrinterConfig getDefault() {
		return new Builder().getConfig();
	}

	/**
	 * Builder for custom configurations.<br>
	 * <br>
	 * Sample:
	 * <code>
	 * PrinterConfig cfg = new PrinterConfig.Builder()<br>
	 *         .setCR("\r")<br>
	 *         .setLF("\n")<br>
	 *         .getConfig();
	 * </code>
	 */
	public final static class Builder {

		// default values
		public String CR = "\r";
		public String NL = System.getProperty("line.separator");
		public String SPACE = "\u00a0";
		public String FF = "\u000c";

		// getter
		public PrinterConfig getConfig() {
			return new PrinterConfig(this);
		}

		// setters
		public Builder setCR(String cR) {
			CR = cR;
			return this;
		}
		public Builder setNL(String nL) {
			NL = nL;
			return this;
		}
		public Builder setSPACE(String sPACE) {
			SPACE = sPACE;
			return this;
		}
		public Builder setFF(String fF) {
			FF = fF;
			return this;
		}

	}
}
