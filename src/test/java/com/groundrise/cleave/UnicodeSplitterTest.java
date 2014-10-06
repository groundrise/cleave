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

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link UnicodeSplitter} class.
 * @author Nicholas Bugajski (nick@groundrise.com)
 */
public class UnicodeSplitterTest {

    class ValidatingReceiver implements Receiver {

        private final String[] words;
        private int position;
        private int nChars;
        private int nWords;

        ValidatingReceiver(final String[] words) {
            this.words = words;
        }

        @Override
        public void addChar(final String c) {
            assertEquals(this.words[this.nWords].substring(this.position, this.position + c.length()), c);
            this.position += c.length();
            this.nChars++;
        }

        @Override
        public void endWord() {
            this.position = 0;
            this.nWords++;
            if (this.nWords > this.words.length) {
                fail("Too many words found.");
            }
        }

        @Override
        public boolean endLine() {
            return true;
        }

        @Override
        public void emptyLine() {}

        int nChars() { return this.nChars; }
        int nWords() { return this.nWords; }
    }

    UnicodeSplitter splitter;

    @Before
    public void setUp() {
        this.splitter = new UnicodeSplitter();
   }

    @After
    public void tearDown() {
        this.splitter = null;
    }

    void checkSplit(final String text, final String[] words, final int nChars) {
        final ValidatingReceiver receiver = new ValidatingReceiver(words);
        this.splitter.split(text, receiver);
        assertEquals(nChars, receiver.nChars());
        assertEquals(words.length, receiver.nWords());
    }

    /**
     * Test splitting simple sentences into words.
     */

    @Test
    public void testSplitSimpleSentences() {
        this.checkSplit("Hello, my name is Steve.",    new String[] { "Hello", "my", "name", "is", "Steve" }, 18);
        this.checkSplit("Nick said: \"I will run!\".", new String[] { "Nick", "said", "I", "will", "run" },   16);
    }

    /**
     * Test splitting simple words into characters.
     */
    @Test
    public void testSplitSimpleWords() {
        this.checkSplit("Steve",    new String[] { "Steve"    }, 5);
        this.checkSplit("Nicholas", new String[] { "Nicholas" }, 8);
    }
}
