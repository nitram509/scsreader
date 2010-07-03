package net.scs.reader.dataprovider;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.scs.reader.EndOfFileSignal;

public class FileDataProviderTest {
	
	private final static int BUFFERSIZE = InputStreamDataProvider.BUFFERSIZE;
	
	private final static String TESTFILE = "test-data/file_data_provider_test.bin";

	private InputStreamDataProvider clazz;
	
	/**
	 * Write EBCDIC 0000...1111....2222....333 in sizes equal to the internal buffer size
	 * Thus we have test date without any control codes. 
	 * 
	 * @throws IOException
	 */
	@BeforeClass
	public static void setupTestData() throws IOException {

		final FileOutputStream fos = new FileOutputStream(TESTFILE);
		byte[] testsequence = new byte[BUFFERSIZE];
		for (int i=0xF0; i<0xFA; i++) {
			Arrays.fill(testsequence, (byte)i);
			fos.write(testsequence);
		}
		fos.close();
	}
	
	@Before
	public void setupReader() throws FileNotFoundException {
		final FileInputStream fis = new FileInputStream(TESTFILE);
		clazz = new InputStreamDataProvider(fis, 273);
	}
	
	/*
	 * ========================================================================
	 */
	
	
	@Test
	public void testSkipBytes() throws IOException {
		byte expected = (byte) 0xF0;
		byte actual = clazz.getByte();
		assertEquals("Testing skip bytes #0", expected, actual);
		
		clazz.skipBytes(BUFFERSIZE);
		
		expected = (byte) 0xF1;
		actual = clazz.getByte();
		assertEquals("Testing skip bytes #1", expected, actual);
		
		for (int i=0; i<BUFFERSIZE; i++) {
			clazz.skipBytes(1);
		}
		
		expected = (byte) 0xF2;
		actual = clazz.getByte();
		assertEquals("Testing skip bytes #2", expected, actual);
	}
	
	@Test
	public void testGetByte() throws IOException {
		byte expected = (byte) 0xF0;
		byte actual = clazz.getByte();
		assertEquals("Testing get byte #0", expected, actual);
	}
	
	@Test
	public void testReadByte() throws IOException {
		for (int i=0; i<BUFFERSIZE; i++) {
			byte expected = (byte) 0xF0;
			byte actual = clazz.readByte();
			assertEquals("Testing read byte, round " + i, expected, actual);
		}
		byte expected = (byte) 0xF1;
		byte actual = clazz.readByte();
		assertEquals("Testing read byte, next segment", expected, actual);
	}
	
	@Test
	public void testGetBytes() throws IOException {
		
		// HALF SIZE
		
		byte[] expected = new byte[BUFFERSIZE/2];
		Arrays.fill(expected, (byte)0xF0);
		byte[] actual = clazz.getBytes(BUFFERSIZE/2);
		assertArrayEquals("Testing get bytes half size", expected, actual);
		
		// FULL SIZE
		
		expected = new byte[BUFFERSIZE];
		Arrays.fill(expected, (byte)0xF0);
		actual = clazz.getBytes(BUFFERSIZE);
		assertArrayEquals("Testing get bytes full size", expected, actual);
		
		// FULL SIZE with shift
		clazz.skipBytes(1);
		
		expected = new byte[BUFFERSIZE];
		Arrays.fill(expected, (byte)0xF0);
		expected[BUFFERSIZE-1] = (byte)0xF1;
		actual = clazz.getBytes(BUFFERSIZE);
		assertArrayEquals("Testing get bytes full size with shift", expected, actual);
	}

	@Test
	public void testReadBytes() throws IOException {
		
		// FULL SIZE #0
		
		byte[] expected = new byte[BUFFERSIZE];
		Arrays.fill(expected, (byte)0xF0);
		byte[] actual = clazz.readBytes(BUFFERSIZE);
		assertArrayEquals("Testing read bytes full size #0", expected, actual);
		
		// FULL SIZE #1
		
		expected = new byte[BUFFERSIZE];
		Arrays.fill(expected, (byte)0xF1);
		actual = clazz.readBytes(BUFFERSIZE);
		assertArrayEquals("Testing read bytes full size #1", expected, actual);
		
		// FULL SIZE with shift
		clazz.skipBytes(1);
		
		expected = new byte[BUFFERSIZE];
		Arrays.fill(expected, (byte)0xF2);
		expected[BUFFERSIZE-1] = (byte)0xF3;
		actual = clazz.readBytes(BUFFERSIZE);
		assertArrayEquals("Testing read bytes full size with shift", expected, actual);
	}

	@Test
	public void testGetPosition() throws IOException {

		int expected = 4;
		clazz.skipBytes(4);
		assertEquals("test position #4", expected, clazz.getPosition());
		
		expected = 8;
		clazz.skipBytes(4);
		assertEquals("test position #8", expected, clazz.getPosition());
		
		expected = 12;
		clazz.skipBytes(4);
		assertEquals("test position #12", expected, clazz.getPosition());
		
		expected = 16;
		clazz.skipBytes(4);
		assertEquals("test position #16", expected, clazz.getPosition());
		
		expected = 20;
		clazz.skipBytes(4);
		assertEquals("test position #20", expected, clazz.getPosition());
		
		expected = 25;
		clazz.skipBytes(5);
		assertEquals("test position #25", expected, clazz.getPosition());
		
		expected = BUFFERSIZE * 6;
		final int bytesToSkip = expected - clazz.getPosition();
		clazz.skipBytes(bytesToSkip);
		assertEquals("test position BufferSize*6", expected, clazz.getPosition());
	}

	@Test
	public void testMoreSkipping() throws IOException {
		clazz.skipBytes(BUFFERSIZE * 10 - 5);
		assertEquals("last five bytes", (byte)0xF9, clazz.readByte());
		assertEquals("last five bytes", (byte)0xF9, clazz.readByte());
		assertEquals("last five bytes", (byte)0xF9, clazz.readByte());
		assertEquals("last five bytes", (byte)0xF9, clazz.readByte());
		assertEquals("last five bytes", (byte)0xF9, clazz.readByte());
	}
	
	@Test()
	public void testEnfOfFile() throws IOException {
		clazz.skipBytes(BUFFERSIZE * 10 - 5);
		assertTrue("not eof", clazz.hasMoreBytes());
		assertEquals("last five bytes", (byte)0xF9, clazz.readByte());
		assertEquals("last five bytes", (byte)0xF9, clazz.readByte());
		assertEquals("last five bytes", (byte)0xF9, clazz.readByte());
		assertEquals("last five bytes", (byte)0xF9, clazz.readByte());
		assertEquals("last five bytes", (byte)0xF9, clazz.readByte());
		
		try {
			assertEquals("last byte", (byte)0x00, clazz.readByte());
		} catch (EndOfFileSignal e) {
			// this is expected
		}
		
		assertFalse("EOF", clazz.hasMoreBytes());
		
		assertNull("last bytes", clazz.getBytes(1));
		assertNull("last bytes", clazz.readBytes(1));
		
		try {
			assertEquals("last byte", (byte)0x00, clazz.getByte());
		} catch (EndOfFileSignal e) {
			// this is expected
		}
	}

	@Test()
	public void testMoreEnfOfFile() throws IOException {
		
		
		clazz.skipBytes(BUFFERSIZE * 10 - 5);
		assertTrue("not eof", clazz.hasMoreBytes());
		
		byte[] expected = new byte[5];
		Arrays.fill(expected, (byte)0xF9);
		assertArrayEquals("last five bytes", expected, clazz.readBytes(5));
		
	}

	@Test()
	public void testEnfOfFileOverLoaded() throws IOException {
		
		clazz.skipBytes(BUFFERSIZE * 10 - 5);
		assertTrue("not eof", clazz.hasMoreBytes());
		
		byte[] expected = new byte[5];
		Arrays.fill(expected, (byte)0xF9);
		assertArrayEquals("last five bytes", expected, clazz.readBytes( 5 + 5 ));
		
	}
	
	@Test()
	public void testEnfOfFileReached() throws IOException {
		
		clazz.skipBytes(BUFFERSIZE * 10);
		assertTrue("not eof", clazz.hasMoreBytes());
		assertNull("end of file reached", clazz.readBytes( 5 ));
		assertFalse("eof", clazz.hasMoreBytes());
		
	}
}
