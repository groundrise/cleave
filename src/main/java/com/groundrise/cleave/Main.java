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

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.lexicalscope.jewel.cli.InvalidOptionSpecificationException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Break up text into words, then those words into characters.
 *
 * Files are processed line by line, so reasonable size lines are assumed.
 *
 * @author Nicholas Bugajski (nick@groundrise.com)
 */
public class Main {
    /**
     * Logger for errors and status.
     */
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Interval, in milliseconds, at which progress will be logged.
     */
    private static final long LOG_INTERVAL = 60 * 1000; // 1 minute

    /**
     * Command line invocation of program.
     * @param args Arguments for program
     */
    public static void main(final String[] args) {
        try {
            final Options opts = CliFactory.parseArgumentsUsingInstance(new Options(), args);
            final Main program = new Main();
            final List<File> files = opts.getInputFiles();
            if (0 == files.size()) {
                files.add(new File("-"));
            }
            program.split(files, System.out);
        } catch (final HelpRequestedException e) {
            log.info(e.getMessage());
            System.exit(0);
        } catch (final ArgumentValidationException | InvalidOptionSpecificationException e) {
            log.error(e.getMessage());
            System.exit(1);
        } catch (final IOException ex) {
            log.error("IO Failure", ex);
            System.exit(1);
        } catch (final RuntimeException e) {
            log.error("Unexpected failure, probably a bug.", e);
            System.exit(1);
        }
        System.exit(0);
    }

    /**
     * Splitter for text.
     */
    private final Splitter splitter = new UnicodeSplitter();

    /**
     * Wall clock time, in milliseconds, at which the last logging statement
     * was written.
     */
    private long lastlog;

    /**
     * Split files, one file at a time, line by line, into words and then into
     * characters writing results into given output stream.
     *
     * @param inFiles Files to read from
     * @param dest Stream to write to
     * @return Number of files processed
     * @throws IOException If there is a problem opening or reading the files
     */
    public int split(final List<File> inFiles, final PrintStream dest) throws IOException {
        final Timer timer = new Timer();
        int changeCount = 0;
        for (final File file : inFiles) {
            this.logProgress(changeCount);
            final boolean madeChanges = this.split(file, dest);
            if (madeChanges) {
                changeCount++;
            }
        }
        this.logStatus(changeCount);
        timer.report();
        return changeCount;
    }

    /**
     * Split file, one line at a time, into words and then into characters
     * writing results into given output stream.
     *
     * @param src File to read from
     * @param dest Stream to write to
     * @return True if any work is done
     * @throws IOException If there is a problem opening or reading the file
     */
    public boolean split(final File src, final PrintStream dest) throws IOException {
        if ("-".equals(src.getName())) {
            return 0 < this.split(System.in, dest);
        }
        if (!src.exists()) {
            throw new FileNotFoundException(src.getPath() + " does not exist.");
        }
        if (!src.canRead()) {
            throw new ArgumentValidationException("Unable to read from: " + src.getPath());
        }
        boolean result;
        try (final InputStream in = new BufferedInputStream(new FileInputStream(src))) {
            result = 0 < this.split(in, dest);
        }
        return result;
    }

    /**
     * Split input stream, one line at a time, into words and then into
     * characters writing results into given output stream.
     *
     * @param in Stream to read from
     * @param out Stream to write to
     * @return Number of lines processed
     */
    public int split(final InputStream in, final PrintStream out) {
        final Scanner chunker = new Scanner(in);
        final Receiver receiver = new ReceiveIntoStream(out);
        int changeCount = 0;
        while (chunker.hasNextLine()) {
            this.logProgress(changeCount);
            if (this.split(chunker.nextLine(), receiver)) {
                changeCount++;
            }
        }
        return changeCount;
    }

    /**
     * Split given string of text into words and then into characters putting
     * result into the given receiver.
     *
     * @param line Text to split
     * @param receiver Receiver of split text
     * @return True if any work is done
     */
    private boolean split(final String line, final Receiver receiver) {
        if (null == line) {
            return false;
        }
        if (0 == line.length()) {
            receiver.emptyLine();
            return true;
        }
        return this.splitter.split(line, receiver);
    }

    /**
     * Write progress to log, if at least LOG_INTERVAL has elapsed.
     * @param count Count of documents processed
     */
    private void logProgress(final int count) {
        if (!log.isDebugEnabled()) {
            return;
        }
        // must have just started
        if (0 == this.lastlog) {
            log.debug("Now cleaving documents.");
            this.lastlog = new Date().getTime();
            return;
        }
        final long now = new Date().getTime();
        if (now > (this.lastlog + LOG_INTERVAL)) {
            log.debug("Cleaved {} documents, so far.", count);
            this.lastlog = now;
        }
    }

    /**
     * Write statistics about work done to log.
     * @param count Count of documents processed
     */
    private void logStatus(final int count) {
        // reset progress logging
        this.lastlog = 0;
        if (0 == count) {
            log.info("Cleaved no documents.");
        } else {
            log.info("Cleaved {} documents into {} words and {} characters.", new Object[]{count, this.splitter.words(), this.splitter.characters()});
        }
    }
}
