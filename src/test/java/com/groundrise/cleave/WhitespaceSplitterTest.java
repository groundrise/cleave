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

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class WhitespaceSplitterTest {

    class ValidatingReceiver implements Receiver {

        private final String[] words;
        private int position = 0;
        private int nChars = 0;
        private int nWords = 0;

        ValidatingReceiver( final String[] words ) {
            this.words = words;
        }

        @Override
        public void addChar( final String c ) {
            assertTrue( words[nWords].length() >= ( position + c.length() ) );
            assertEquals( words[nWords].substring( position, position + c.length() ), c );
            position += c.length();
            nChars++;
        }

        @Override
        public void endWord() {
            position = 0;
            nWords++;
            if ( nWords > words.length ) {
                fail( "Too many words found." );
            }
        }

        @Override
        public boolean endLine() {
            return true;
        }

        @Override
        public void emptyLine() {}

        int nChars() { return nChars; }
        int nWords() { return nWords; }
    }

    WhitespaceSplitter splitter = null;

    @Before
    public void setUp() {
        splitter = new WhitespaceSplitter();
    }

    @After
    public void tearDown() {
        splitter = null;
    }

    void checkSplit( final String text, final String[] words, final int nChars ) {
        final ValidatingReceiver receiver = new ValidatingReceiver( words );
        splitter.split( text, receiver );
        assertEquals( nChars, receiver.nChars() );
        assertEquals( words.length, receiver.nWords() );
    }

    /**
     * Test splitting nothing
     */
    @Test
    public void testSplittingNothing() {
        checkSplit( "",       new String[] {}, 0 ); // nothing
        checkSplit( "   ",    new String[] {}, 0 ); // spaces
        checkSplit( "\n\r\n", new String[] {}, 0 ); // newlines
        checkSplit( "\t\t\t", new String[] {}, 0 ); // tabs
    }

    /**
     * Test splitting simple words into characters.
     */
    @Test
    public void testSplitSimpleWords() {
        checkSplit( "Steve",    new String[] { "Steve"    }, 5 );
        checkSplit( "Nicholas", new String[] { "Nicholas" }, 8 );
    }

    /**
     * Test splitting simple sentences into words.
     */
    @Test
    public void testSplitSimpleSentences() {
        checkSplit( "Hello, my name is Steve.",    new String[] { "Hello,", "my", "name", "is", "Steve." },      20 );
        checkSplit( "Nick said: \"I will run!\".", new String[] { "Nick", "said:", "\"I", "will", "run!\"." },   21 );
    }

    /**
     * Test splitting word with leading # or @
     */
    @Test
    public void testTwitterSpecialCharacters() {
        checkSplit( "This is a #hashtag", new String[] { "This", "is", "a", "#hashtag" }, 15 );
        checkSplit( "This is a @mention", new String[] { "This", "is", "a", "@mention" }, 15 );
    }
}
