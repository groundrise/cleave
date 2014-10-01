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

import java.io.PrintStream;

class ReceiveIntoStream implements Receiver {

    private enum Input { START, CHAR, WORD }

    private final PrintStream out;
    private Input last = Input.START;

    ReceiveIntoStream(final PrintStream out) {
        this.out = out;
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
