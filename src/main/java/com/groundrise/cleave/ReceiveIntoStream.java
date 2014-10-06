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

import java.io.PrintStream;

/**
 * Receives words, one character at a time and outputs those words onto the
 * given print stream.
 *
 * On output:
 * - characters are separated by spaces " "
 * - words are delimited by newlines "\n"
 * - lines are delimited by a blank line "\n"
 *
 * @author Nicholas Bugajski (nick@groundrise.com)
 */
class ReceiveIntoStream implements Receiver {

    /**
     * State machine states.
     */
    private enum Input { START, CHAR, WORD }

    /**
     * Stream to write output to.
     */
    private final PrintStream out;

    /**
     * Keep track of state changes.
     */
    private Input last = Input.START;

    /**
     * Construct a receiver stream that will write output to the given
     * PrintStream.
     * @param dest Stream to write to
     */
    ReceiveIntoStream(final PrintStream dest) {
        this.out = dest;
    }

    @Override
    public void addChar(final String c) {
        this.out.append(c);
        this.out.append('\n');
        this.last = Input.CHAR;
    }

    @Override
    public void endWord() {
        if (Input.CHAR == this.last) {
            this.out.append(' ');
            this.out.append('\n');
        }
        this.last = Input.WORD;
    }

    @Override
    public boolean endLine() {
        if (Input.START == this.last) {
            return false;
        }
        this.out.append('\n');
        this.last = Input.START;
        return true;
    }

    @Override
    public void emptyLine() {
        assert Input.START == this.last;
        this.out.append('\n');
        this.last = Input.START;
    }
}
