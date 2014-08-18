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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ReceiveIntoBufferTest {

    private ByteArrayOutputStream buffer;
    private ReceiveIntoStream receiver;

    @Before
    public void setUp() {
        buffer = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(buffer);
        receiver = new ReceiveIntoStream(ps);
    }

    @After
    public void tearDown() {
        buffer   = null;
        receiver = null;
    }

    /**
     * An empty line should produce nothing.
     */
    @Test
    public void emptyLineIsEmpty() {
        receiver.endLine();
        String output = buffer.toString();
        assertThat(output, is(""));
    }

    /**
     * Explicitly empty line.
     */
    @Test
    public void emptyLineGetsNewline() {
        receiver.emptyLine();
        String output = buffer.toString();
        assertThat(output, is("\n"));
    }

    /**
     * Single character document
     */
    @Test
    public void testOneCharDocument() {
        receiver.addChar("a");
        receiver.endWord();
        receiver.endLine();
        String output = buffer.toString();
        assertThat(output, is("a\n \n\n"));
    }

    /**
     * Multiple character, one word document
     */
    @Test
    public void testTwoCharDocument() {
        receiver.addChar( "a" );
        receiver.addChar( "b" );
        receiver.endWord();
        receiver.endLine();
        String output = buffer.toString();
        assertThat(output, is("a\nb\n \n\n"));
    }

    /**
     * Verify that a multiple character "char" is treated as one in the char count
     */
    @Test
    public void testLongChar() {
        receiver.addChar( "abc" );
        receiver.addChar( "de" );
        receiver.endWord();
        receiver.endLine();
        String output = buffer.toString();
        assertThat(output, is("abc\nde\n \n\n"));
    }

    /**
     * Multiple word document
     */
    @Test
    public void testTwoWordDocument() {
        receiver.addChar( "a" );
        receiver.addChar( "b" );
        receiver.endWord();
        receiver.addChar( "c" );
        receiver.addChar( "d" );
        receiver.endWord();
        receiver.endLine();
        String output = buffer.toString();
        assertThat(output, is("a\nb\n \nc\nd\n \n\n"));
    }

    /**
     * Document ending with file separator character
     */
    @Test
    public void testFileSeparator() {
        receiver.addChar( "One" );
        receiver.endWord();
        receiver.addChar( "Two" );
        receiver.endWord();
        receiver.endLine();
        receiver.addChar( "\u001c" );
        receiver.endWord();
        receiver.endLine();
        String output = buffer.toString();
        assertThat(output, is("One\n \nTwo\n \n\n\u001c\n \n\n"));
    }
}
