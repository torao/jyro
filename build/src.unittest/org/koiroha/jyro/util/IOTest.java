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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.koiroha.jyro.AbstractJyroTest;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// IOTest:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * TestUnit for Class {@link IO}
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
			shouldDetect(dir, "");

			// unix-like separator
			shouldDetect(dir, "/");

			// sequence of separator
			shouldDetect(dir, "////");

			// envronment-depend separetor
			shouldDetect(dir, "\\");

			// sequence of envronment-depend separetor
			shouldDetect(dir, "\\\\\\\\");

			// no file matches for empty directory
			shouldDetect(dir, "*");

			// --------------------------------------------------------------
			// only subdirectory exists
			File subdir = new File(dir, "temp.dir");
			assertTrue(subdir.mkdir());

			// not matches for empty string
			shouldDetect(dir, "");

			// not matches for directory
			shouldDetect(dir, "temp.dir");

			// wildcard not matches for directory
			shouldDetect(dir, "*.dir");

			// wildcard not matches for directory
			shouldDetect(dir, "*");

			// wildcard not matches for directory
			shouldDetect(dir, "**");

			// wildcard not matches for directory
			shouldDetect(dir, "**/*");

			// --------------------------------------------------------------
			// one file exists under base directory
			File file1 = new File(dir, "temp1.file");
			assertTrue(file1.createNewFile());

			// not matches for empty string
			shouldDetect(dir, "");

			// not matches for directory
			shouldDetect(dir, "temp.dir");

			// not matches for subdirectory files
			shouldDetect(dir, "temp.dir/*");

			// not matches for directory wildcard
			shouldDetect(dir, "**");

			// not matches for directory
			shouldDetect(dir, "**/*", file1);

			// matches directly specified path
			shouldDetect(dir, "temp1.file", file1);

			// matches wildcard specified path
			shouldDetect(dir, "*", file1);

			// matches wildcard specified path
			shouldDetect(dir, "*.file", file1);

			// matches wildcard specified path
			shouldDetect(dir, "temp*.file", file1);

			// matches wildcard specified path
			shouldDetect(dir, "*1*", file1);

			// matches wildcard specified path
			shouldDetect(dir, "t*1*.file", file1);

			// matches wildcard specified path
			shouldDetect(dir, "*temp1.file", file1);

			// matches wildcard specified path
			shouldDetect(dir, "temp1*.file", file1);

			// matches wildcard specified path
			shouldDetect(dir, "temp1.file*", file1);

			// name not matches
			shouldDetect(dir, "temp2.file");

			// name not matches
			shouldDetect(dir, "*.dir");

			// name not matches
			shouldDetect(dir, "temp2.*");

			// name not matches
			shouldDetect(dir, "t*2*.file");

			// --------------------------------------------------------------
			// some files exists under base directory
			File file2 = new File(dir, "temp2.file");
			assertTrue(file2.createNewFile());

			// matches directly specified path
			shouldDetect(dir, "temp2.file", file2);

			// matches wildcard specified path
			shouldDetect(dir, "*", file1, file2);

			// matches wildcard specified path
			shouldDetect(dir, "*.file", file1, file2);

			// matches wildcard specified path
			shouldDetect(dir, "temp*.file", file1, file2);

			// matches wildcard specified path
			shouldDetect(dir, "*2*", file2);

			// name not matches
			shouldDetect(dir, "**/*.file", file1, file2);

			// --------------------------------------------------------------
			// only subdirectory exists
			File file3 = new File(subdir, "temp3.file");
			assertTrue(file3.createNewFile());

			// not matches for empty string
			shouldDetect(dir, "");

			// not matches for directory
			shouldDetect(dir, "temp.dir");

			// wildcard not matches for directory
			shouldDetect(dir, "temp.dir/temp3.file", file3);

			// wildcard not matches for directory
			shouldDetect(dir, "temp.dir\\temp3.file", file3);

			// wildcard not matches for directory
			shouldDetect(dir, "temp.dir/*", file3);

			// wildcard not matches for directory
			shouldDetect(dir, "temp.dir/*.file", file3);

			// wildcard not matches for directory
			shouldDetect(dir, "temp.dir/temp3.*", file3);

			// wildcard not matches for directory
			shouldDetect(dir, "temp.dir/temp*.file", file3);

			// wildcard not matches for directory
			shouldDetect(dir, "*/temp3.file", file3);

			// wildcard not matches for directory
			shouldDetect(dir, "**/temp3.file", file3);

			// wildcard not matches for directory
			shouldDetect(dir, "**/*.file", file1, file2, file3);

			// wildcard not matches for directory
			shouldDetect(dir, "**/*", file1, file2, file3);

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
			IO.close((Closeable)null);
		} catch(Throwable ex){
			fail("exception if null passed: " + ex);
		}
		return;
	}

	/**
	 * Detect specified files from dir and test matches expected files.
	 * @param dir directory
	 * @param path file pattern
	 * @param files files to compare
	 * @throws IOException if filename is invalid
	 */
	private static void shouldDetect(File dir, String path, File... files) throws IOException {
		Iterable<File> it = IO.fileSet(dir, path);

		// build expected filename set
		Set<String> expected = new HashSet<String>();
		for(File f: files){
			expected.add(f.getCanonicalPath());
		}

		// consider that the filename contained specified filenames and remove
		for(File f: it){
			assertTrue("unexpected file detected: " + f, expected.contains(f.getCanonicalPath()));
			expected.remove(f.getCanonicalPath());
		}

		// expected filename should be empty
		assertTrue("file not detected: " + expected, expected.isEmpty());
		return;
	}

}
