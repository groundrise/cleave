/*
 * cleave - a tool to break apart English text
 *
 * Written in 2014 by Nicholas Bugajski <nick@groundrise.com>
 *
 * To the extent possible under law, the author(s) have dedicated all copyright
 * and related and neighboring rights to this software to the public domain
 * worldwide. This software is distributed without any warranty.
 *
 * You should have received a copy of the CC0 Public Domain Dedication along
 * with this software. If not, see
 * <http://creativecommons.org/publicdomain/zero/1.0/>.
 */
package com.groundrise.cleave;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MainTest {

    private Main program;
    private ByteArrayOutputStream baos;
    private PrintStream ps;

    @Before
    public void setUp() {
        program = new Main();
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
    }

    @After
    public void tearDown() {
        program = null;
        baos = null;
        ps = null;
    }

    private InputStream wrap(final String in) {
        return new ByteArrayInputStream(in.getBytes());
    }

    /**
     * Split should not alter an empty input.
     */
    @Test
    public void testSplitEmptyContents() {
        final int result = program.split(wrap(""), ps);
        assertThat(result, is(0));
        assertThat(baos.toString(), is(""));
    }

    /**
     * Split should not change an empty line.
     */
    @Test
    public void testOneEmptyLine() {
        final int result = program.split(wrap("\n"), ps);
        assertThat(result, is(1));
        assertThat(baos.toString(), is("\n"));
    }

    /**
     * Split should not output anything given a line without words.
     */
    @Test
    public void noOutputForWhitespace() {
        final int result = program.split(wrap("   \t     \n"), ps);
        assertThat(result, is(0));
        assertThat(baos.toString(), is(""));
    }

    /**
     * Split should drop a line of whitespace between two normal lines.
     */
    @Test
    public void removeWhitespaceLine() {
        final int result = program.split(wrap("one\n   \t\t\t \ntwo\n"), ps);
        final String output = baos.toString();
        assertThat(result, is(2));
        assertThat(output.length(), is(18));
    }

    /**
     * Test split actually splits content
     */
    @Test
    public void testSplitsContent() {
        final String contents = "A short document.";
        final int result = program.split(wrap(contents), ps);
        final String output = baos.toString();
        assertThat(result, is(1));
        assertThat(output.length(), is(greaterThan(contents.length())));
    }

    /**
     * Test input stream with only one document, no newline.
     */
    @Test
    public void testSplitsSingleDocumentInStream() {
        final String contents = "A short document.";
        final int changed = program.split(wrap(contents), ps);
        final String output = baos.toString();
        assertThat(changed, is(1));
        assertThat(output.length(), is(greaterThan(contents.length())));
    }

    /**
     * Test input stream with multiple documents.
     */
    @Test
    public void testSplitsMultipleDocuments() {
        final String contents = "one\ntwo\n";
        final int changed = program.split(wrap(contents), ps);
        final String output = baos.toString();
        assertThat(changed, is(2));
        assertThat(output.length(), is(greaterThan(contents.length())));
    }
}
