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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link ReceiveIntoStream} class.
 * @author Nicholas Bugajski (nick@groundrise.com)
 */
public class ReceiveIntoBufferTest {

    private ByteArrayOutputStream buffer;
    private ReceiveIntoStream receiver;

    @Before
    public void setUp() {
        this.buffer = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(this.buffer);
        this.receiver = new ReceiveIntoStream(ps);
    }

    @After
    public void tearDown() {
        this.buffer   = null;
        this.receiver = null;
    }

    /**
     * An empty line should produce nothing.
     */
    @Test
    public void emptyLineIsEmpty() {
        this.receiver.endLine();
        final String output = this.buffer.toString();
        assertThat(output, is(""));
    }

    /**
     * Explicitly empty line.
     */
    @Test
    public void emptyLineGetsNewline() {
        this.receiver.emptyLine();
        final String output = this.buffer.toString();
        assertThat(output, is("\n"));
    }

    /**
     * Single character document
     */
    @Test
    public void testOneCharDocument() {
        this.receiver.addChar("a");
        this.receiver.endWord();
        this.receiver.endLine();
        final String output = this.buffer.toString();
        assertThat(output, is("a\n \n\n"));
    }

    /**
     * Multiple character, one word document
     */
    @Test
    public void testTwoCharDocument() {
        this.receiver.addChar("a");
        this.receiver.addChar("b");
        this.receiver.endWord();
        this.receiver.endLine();
        final String output = this.buffer.toString();
        assertThat(output, is("a\nb\n \n\n"));
    }

    /**
     * Verify that a multiple character "char" is treated as one in the char count
     */
    @Test
    public void testLongChar() {
        this.receiver.addChar("abc");
        this.receiver.addChar("de");
        this.receiver.endWord();
        this.receiver.endLine();
        final String output = this.buffer.toString();
        assertThat(output, is("abc\nde\n \n\n"));
    }

    /**
     * Multiple word document
     */
    @Test
    public void testTwoWordDocument() {
        this.receiver.addChar("a");
        this.receiver.addChar("b");
        this.receiver.endWord();
        this.receiver.addChar("c");
        this.receiver.addChar("d");
        this.receiver.endWord();
        this.receiver.endLine();
        final String output = this.buffer.toString();
        assertThat(output, is("a\nb\n \nc\nd\n \n\n"));
    }

    /**
     * Document ending with file separator character
     */
    @Test
    public void testFileSeparator() {
        this.receiver.addChar("One");
        this.receiver.endWord();
        this.receiver.addChar("Two");
        this.receiver.endWord();
        this.receiver.endLine();
        this.receiver.addChar("\u001c");
        this.receiver.endWord();
        this.receiver.endLine();
        final String output = this.buffer.toString();
        assertThat(output, is("One\n \nTwo\n \n\n\u001c\n \n\n"));
    }
}
