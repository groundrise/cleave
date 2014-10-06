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

/**
 * Receives words, one character at a time to allow for delimiting.
 *
 * @author Nicholas Bugajski (nick@groundrise.com)
 */
interface Receiver {
    /**
     * Add a character to the output.
     * @param c Character to output
     */
    void addChar(final String c);

    /**
     * Mark the end of a word.
     */
    void endWord();

    /**
     * Mark the end of a line/sentence/document.
     * @return True if line was not empty
     */
    boolean endLine();

    /**
     * Output an empty line.
     */
    void emptyLine();
}
