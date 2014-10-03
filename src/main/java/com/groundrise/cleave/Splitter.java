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
 * Splits strings into words and then those words into characters.
 *
 * @author Nicholas Bugajski (nick@groundrise.com)
 */
interface Splitter {
    /**
     * Current count of characters output.
     * @return Character count
     */
    long characters();

    /**
     * Current count of words output.
     * @return Word count
     */
    long words();

    /**
     * Split the input into words, then the words into characters, outputting
     * each into the given receiver.
     * @param input Text to split
     * @param receiver Consumer of split text
     * @return True if any splitting occurs
     */
    boolean split(final String input, final Receiver receiver);
}
