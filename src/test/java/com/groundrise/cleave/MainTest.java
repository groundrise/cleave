/**
 * Cleave - A tool to break apart English text.
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

/**
 * Test case for {@link Main} class.
 * @author Nicholas Bugajski (nick@groundrise.com)
 */
public class MainTest {

    private Main program;
    private ByteArrayOutputStream baos;
    private PrintStream ps;

    @Before
    public void setUp() {
        this.program = new Main();
        this.baos = new ByteArrayOutputStream();
        this.ps = new PrintStream(this.baos);
    }

    @After
    public void tearDown() {
        this.program = null;
        this.baos = null;
        this.ps = null;
    }

    private InputStream wrap(final String in) {
        return new ByteArrayInputStream(in.getBytes());
    }

    /**
     * Split should not alter an empty input.
     */
    @Test
    public void testSplitEmptyContents() {
        final int result = this.program.split(this.wrap(""), this.ps);
        assertThat(result, is(0));
        assertThat(this.baos.toString(), is(""));
    }

    /**
     * Split should not change an empty line.
     */
    @Test
    public void testOneEmptyLine() {
        final int result = this.program.split(this.wrap("\n"), this.ps);
        assertThat(result, is(1));
        assertThat(this.baos.toString(), is("\n"));
    }

    /**
     * Split should not output anything given a line without words.
     */
    @Test
    public void noOutputForWhitespace() {
        final int result = this.program.split(this.wrap("   \t     \n"), this.ps);
        assertThat(result, is(0));
        assertThat(this.baos.toString(), is(""));
    }

    /**
     * Split should drop a line of whitespace between two normal lines.
     */
    @Test
    public void removeWhitespaceLine() {
        final int result = this.program.split(this.wrap("one\n   \t\t\t \ntwo\n"), this.ps);
        final String output = this.baos.toString();
        assertThat(result, is(2));
        assertThat(output.length(), is(18));
    }

    /**
     * Test split actually splits content
     */
    @Test
    public void testSplitsContent() {
        final String contents = "A short document.";
        final int result = this.program.split(this.wrap(contents), this.ps);
        final String output = this.baos.toString();
        assertThat(result, is(1));
        assertThat(output.length(), is(greaterThan(contents.length())));
    }

    /**
     * Test input stream with only one document, no newline.
     */
    @Test
    public void testSplitsSingleDocumentInStream() {
        final String contents = "A short document.";
        final int changed = this.program.split(this.wrap(contents), this.ps);
        final String output = this.baos.toString();
        assertThat(changed, is(1));
        assertThat(output.length(), is(greaterThan(contents.length())));
    }

    /**
     * Test input stream with multiple documents.
     */
    @Test
    public void testSplitsMultipleDocuments() {
        final String contents = "one\ntwo\n";
        final int changed = this.program.split(this.wrap(contents), this.ps);
        final String output = this.baos.toString();
        assertThat(changed, is(2));
        assertThat(output.length(), is(greaterThan(contents.length())));
    }
}
