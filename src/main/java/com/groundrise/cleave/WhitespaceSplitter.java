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

import com.ibm.icu.text.BreakIterator;

class WhitespaceSplitter implements Splitter {

    private final BreakIterator charBounds = BreakIterator.getCharacterInstance();
    private long words = 0;
    private long characters = 0;

    @Override
    public long characters() {
        return characters;
    }

    @Override
    public long words() {
        return words;
    }

    @Override
    public boolean split( final String input, final Receiver receiver) {

        for ( int start = 0, end; start < input.length(); start = end ) {
            while ( ( start < input.length() ) && Character.isWhitespace( input.codePointAt( start ) ) ) {
                start += Character.charCount( input.codePointAt( start ) );
            }

            end = start;
            while ( ( end < input.length() ) && !Character.isWhitespace( input.codePointAt( end ) ) ) {
                end += Character.charCount( input.codePointAt( end ) );
            }

            if ( start < input.length() ) {
                splitWord( input.substring( start, end ), receiver );
            }
        }

        return receiver.endLine();
    }

    private void splitWord( final String input, final Receiver receiver ) {
        words++;
        charBounds.setText( input );
        for ( int start = charBounds.first(), end = charBounds.next();
                BreakIterator.DONE != end;
                start = end, end = charBounds.next() ) {
            characters++;
            receiver.addChar( input.substring( start, end ) );
        }

        receiver.endWord();
    }
}
