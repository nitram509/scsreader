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
package net.scs.reader.dataprovider;

import java.io.IOException;

import net.scs.reader.IScsDataProvider;

import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.PrintObject;
import com.ibm.as400.access.PrintObjectInputStream;
import com.ibm.as400.access.RequestNotSupportedException;
import com.ibm.as400.access.SpooledFile;

/**
 * Provides some factory methods for creating {@link IScsDataProvider}s.
 * Using this methods will ensure to use correct CCSID settings.
 */
public class As400ScsDataProviderFactory {

	/**
	 * A {@link IScsDataProvider} which uses a {@link PrintObjectInputStream} internally.
	 * Additionally, it tries to guess automatically the correct CCSID of the SCS stream.
	 *
	 * @param spooledFile
	 * @return
	 * @throws AS400Exception
	 * @throws AS400SecurityException
	 * @throws ErrorCompletingRequestException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws RequestNotSupportedException
	 * @see {@link SpooledFile#getInputStream()}
	 */
	public static IScsDataProvider getPrintObjectDataProvider(SpooledFile spooledFile) throws AS400Exception, AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, RequestNotSupportedException {
		int ccsid = guessCCSID(spooledFile);
		final PrintObjectInputStream inStream = spooledFile.getInputStream();
		final InputStreamDataProvider isdp = new InputStreamDataProvider(inStream, ccsid);
		return isdp;
	}

	/**
	 * @param spooledFile
	 * @return
	 * @throws AS400Exception
	 * @throws AS400SecurityException
	 * @throws ErrorCompletingRequestException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws RequestNotSupportedException
	 */
	public static int guessCCSID(SpooledFile spooledFile) throws AS400Exception, AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, RequestNotSupportedException {
		int ccsid = spooledFile.getSystem().getCcsid();
		final String charid = spooledFile.getStringAttribute(PrintObject.ATTR_CHAR_ID);
		if (!"*SYSVAL".equals(charid)) {
			try {
				final Integer ccsidattr = spooledFile.getSingleIntegerAttribute(PrintObject.ATTR_JOBCCSID);
				if (ccsidattr != null) {
					ccsid = ccsidattr.intValue();
				}
			} catch (RequestNotSupportedException e) {
				// FIXME: report a warning, that "ATTR_JOBCCSID" is not supported by V5R3 and before
				e.printStackTrace();
			}
		}
		if (ccsid < 1 || ccsid >= 65534) {
			// FIXME: report an error
			ccsid = 37;
		}
		return ccsid;
	}

}
