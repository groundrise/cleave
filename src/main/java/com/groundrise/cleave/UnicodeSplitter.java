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

class UnicodeSplitter implements Splitter {

    private final BreakIterator wordBounds = BreakIterator.getWordInstance();
    private final BreakIterator charBounds = BreakIterator.getCharacterInstance();
    private long words;
    private long characters;

    @Override
    public long characters() {
        return this.characters;
    }

    @Override
    public long words() {
        return this.words;
    }

    @Override
    public boolean split(final String input, final Receiver receiver) {
        this.wordBounds.setText(input);
        for (int last = this.wordBounds.first(), current = this.wordBounds.next();
                BreakIterator.DONE != current;
                last = current, current = this.wordBounds.next()) {
            boolean isWord = false;
            for (int offset = last; offset < current; ) {
                final int codepoint = input.codePointAt(offset);
                if (Character.isLetterOrDigit(codepoint)) {
                    isWord = true;
                    break;
                }
                offset += Character.charCount(codepoint);
            }
            if (!isWord) {
                continue;
            }
            this.splitWord(input.substring(last, current), receiver);
        }
        return receiver.endLine();
    }

    private void splitWord(final String input, final Receiver receiver) {
        this.words++;
        this.charBounds.setText(input);
        for (int start = this.charBounds.first(), end = this.charBounds.next();
                BreakIterator.DONE != end;
                start = end, end = this.charBounds.next()) {
            this.characters++;
            receiver.addChar(input.substring(start, end));
        }
        receiver.endWord();
    }
}
