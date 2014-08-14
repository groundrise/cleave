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

public class UnicodeSplitterTest {

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

    UnicodeSplitter splitter = null;

    @Before
    public void setUp() {
        splitter = new UnicodeSplitter();
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
     * Test splitting simple sentences into words.
     */

    @Test
    public void testSplitSimpleSentences() {
        checkSplit( "Hello, my name is Steve.",    new String[] { "Hello", "my", "name", "is", "Steve" }, 18 );
        checkSplit( "Nick said: \"I will run!\".", new String[] { "Nick", "said", "I", "will", "run" },   16 );
    }

    /**
     * Test splitting simple words into characters.
     */
    @Test
    public void testSplitSimpleWords() {
        checkSplit( "Steve",    new String[] { "Steve"    }, 5 );
        checkSplit( "Nicholas", new String[] { "Nicholas" }, 8 );
    }
}
