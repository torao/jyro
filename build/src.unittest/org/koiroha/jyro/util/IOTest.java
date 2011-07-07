/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.util;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.Test;
import org.koiroha.jyro.AbstractJyroTest;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// IOTest:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @author takami torao
 */
public class IOTest extends AbstractJyroTest{

	/** success flag changed in inner class. */
	private boolean success = false;

	/**
	 * Test IO is declared as utility class.
	 */
	@Test
	public void testClass(){
		testUtilityClass(IO.class);
		return;
	}

	/**
	 * Test {@link org.koiroha.jyro.util.IO#fileSet(java.io.File, java.lang.String)}
	 * @throws IOException if fail
	 */
	@Test
	public void testFileSet() throws IOException {
		File temp = new File(System.getProperty("java.io.tmpdir"));
		File dir = File.createTempFile("iotest", "", temp);
		assertTrue(dir.delete());
		assertTrue(dir.mkdir());
		try {

			// --------------------------------------------------------------
			// empty directory

			// empty stream treat
			Iterator<File> it = IO.fileSet(dir, "").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// unix-like separator
			it = IO.fileSet(dir, "/").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// sequence of separator
			it = IO.fileSet(dir, "////").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// envronment-depend separetor
			it = IO.fileSet(dir, "\\").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// sequence of envronment-depend separetor
			it = IO.fileSet(dir, "\\\\\\\\").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// no file matches for empty directory
			it = IO.fileSet(dir, "*").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// --------------------------------------------------------------
			// only subdirectory exists
			File subdir = new File(dir, "temp.dir");
			assertTrue(subdir.mkdir());

			// not matches for empty string
			it = IO.fileSet(dir, "").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// not matches for directory
			it = IO.fileSet(dir, "temp.dir").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// wildcard not matches for directory
			it = IO.fileSet(dir, "*.dir").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// wildcard not matches for directory
			it = IO.fileSet(dir, "*").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// wildcard not matches for directory
			it = IO.fileSet(dir, "**").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// wildcard not matches for directory
			it = IO.fileSet(dir, "**/*").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// --------------------------------------------------------------
			// some files exists under base directory
			File file1 = new File(dir, "temp1.file");
			assertTrue(file1.createNewFile());

			// not matches for empty string
			it = IO.fileSet(dir, "").iterator();
			assertFalse("some file exists", it.hasNext());

			// not matches for directory
			it = IO.fileSet(dir, "temp.dir").iterator();
			assertFalse("some file exists", it.hasNext());

			// not matches for subdirectory files
			it = IO.fileSet(dir, "temp.dir/*").iterator();
			assertFalse("some file exists", it.hasNext());

			// not matches for directory wildcard
			it = IO.fileSet(dir, "**").iterator();
			assertFalse("some file exists", it.hasNext());

			// not matches for directory
			it = IO.fileSet(dir, "**/*").iterator();
			assertFalse("some file exists", it.hasNext());

			// matches directly specified path
			it = IO.fileSet(dir, "temp1.file").iterator();
			assertTrue("some file exists", it.hasNext());
			assertEquals(file1.getCanonicalPath(), it.next().getCanonicalPath());
			assertFalse(it.hasNext());

			// matches wildcard specified path
			it = IO.fileSet(dir, "*").iterator();
			assertTrue("some file exists", it.hasNext());
			assertEquals(file1.getCanonicalPath(), it.next().getCanonicalPath());
			assertFalse(it.hasNext());

			// matches wildcard specified path
			it = IO.fileSet(dir, "*.file").iterator();
			assertTrue("some file exists", it.hasNext());
			assertEquals(file1.getCanonicalPath(), it.next().getCanonicalPath());
			assertFalse(it.hasNext());

			// matches wildcard specified path
			it = IO.fileSet(dir, "temp*.file").iterator();
			assertTrue("some file exists", it.hasNext());
			assertEquals(file1.getCanonicalPath(), it.next().getCanonicalPath());
			assertFalse(it.hasNext());

			// --------------------------------------------------------------
			// some files exists under base directory
			File file2 = new File(dir, "temp2.file");
			assertTrue(file2.createNewFile());

			// matches directly specified path
			it = IO.fileSet(dir, "temp2.file").iterator();
			assertTrue("some file exists", it.hasNext());
			assertEquals(file2.getCanonicalPath(), it.next().getCanonicalPath());
			assertFalse(it.hasNext());

			// matches wildcard specified path
			Iterable<File> itf = IO.fileSet(dir, "*");
			Set<String> expected = new HashSet<String>();
			expected.add(file1.getCanonicalPath());
			expected.add(file2.getCanonicalPath());
			for(File f: itf){
				assertTrue(expected.contains(f.getCanonicalPath()));
				expected.remove(f.getCanonicalPath());
			}
			assertTrue(expected.isEmpty());

			// matches wildcard specified path
			it = IO.fileSet(dir, "*.file").iterator();
			expected.add(file1.getCanonicalPath());
			expected.add(file2.getCanonicalPath());
			for(File f: itf){
				assertTrue(expected.contains(f.getCanonicalPath()));
				expected.remove(f.getCanonicalPath());
			}
			assertTrue(expected.isEmpty());

			// matches wildcard specified path
			it = IO.fileSet(dir, "temp*.file").iterator();
			expected.add(file1.getCanonicalPath());
			expected.add(file2.getCanonicalPath());
			for(File f: itf){
				assertTrue(expected.contains(f.getCanonicalPath()));
				expected.remove(f.getCanonicalPath());
			}
			assertTrue(expected.isEmpty());

			// --------------------------------------------------------------
			// only subdirectory exists
			File file3 = new File(subdir, "temp.file");
			assertTrue(file3.createNewFile());

			// not matches for empty string
			it = IO.fileSet(dir, "").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// not matches for directory
			it = IO.fileSet(dir, "temp.dir").iterator();
			assertTrue("some file exists", ! it.hasNext());

			// wildcard not matches for directory
			it = IO.fileSet(dir, "temp.dir/temp.file").iterator();
			assertTrue("some file exists", it.hasNext());
			assertEquals(file3.getCanonicalPath(), it.next().getCanonicalPath());

		} finally {
			deleteAll(dir);
		}
		return;
	}

	/**
	 * Test IO.close()
	 */
	@Test
	public void testClose() {

		// ------------------------------------------------------------------
		// normally close called
		success = false;
		IO.close(new Closeable() {
			@Override
			public void close() throws IOException {
				success = true;
			}
		});
		assertTrue("close() not called", success);

		// ------------------------------------------------------------------
		// normally exit when exception occured
		try {
			IO.close(new Closeable() {
				@Override
				public void close() throws IOException {
					throw new IOException("this is test");
				}
			});
		} catch(Throwable ex){
			fail("exception thrown: " + ex);
		}

		// ------------------------------------------------------------------
		// nothing to do if null passed
		try {
			IO.close(null);
		} catch(Throwable ex){
			fail("exception if null passed: " + ex);
		}
		return;
	}

	private static void equals(Iterable<File> it, File... files){

		// build expected
		Set<String> expected = new HashSet<String>();
		expected.add(file1.getCanonicalPath());
		expected.add(file2.getCanonicalPath());
		for(File f: itf){
			assertTrue(expected.contains(f.getCanonicalPath()));
			expected.remove(f.getCanonicalPath());
		}
		assertTrue(expected.isEmpty());
	}

}
