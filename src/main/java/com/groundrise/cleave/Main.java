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

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final long LOG_INTERVAL = 60 * 1000; // 1 minute

    public static void main(final String[] args) {

        try {
            final Options opts = CliFactory.parseArgumentsUsingInstance(new Options(), args);
            final Main program = new Main();
            final List<File> files = opts.getInputFiles();
            if (0 == files.size()) {
                files.add(new File("-"));
            }
            program.split(files, System.out);
        } catch ( HelpRequestedException e ) {
            log.info(e.getMessage());
            System.exit(0);
        } catch ( ArgumentValidationException | InvalidOptionSpecificationException e ) {
            log.error(e.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            log.error( "IO Failure", ex );
            System.exit(1);
        } catch ( RuntimeException e ) {
            log.error( "Unexpected failure, probably a bug.", e );
            System.exit(1);
        }

        System.exit(0);
    }

    private final Splitter splitter = new UnicodeSplitter();
    private long lastlog;

    public int split(final List<File> inFiles, final PrintStream dest) throws IOException {
        Timer timer = new Timer();
        int changeCount = 0;

        for (File file : inFiles) {
            logProgress(changeCount);
            boolean madeChanges = split(file, dest);
            if (madeChanges) {
                changeCount++;
            }
        }

        logStatus(changeCount);
        timer.report();

        return changeCount;
    }

    public boolean split(final File src, final PrintStream dest) throws IOException {
        if ("-".equals(src.getName())) {
            return 0 < split(System.in, dest);
        }

        if ( !src.exists() ) {
            throw new FileNotFoundException( src.getPath() + " does not exist." );
        }
        if ( !src.canRead() ) {
            throw new ArgumentValidationException( "Unable to read from: " + src.getPath() );
        }

        boolean result;
        try (final InputStream in = new BufferedInputStream(new FileInputStream(src))) {
            result = 0 < split(in, dest);
        }

        return result;
    }

    public int split(final InputStream in, final PrintStream out) {
        final Scanner chunker = new Scanner(in);
        final Receiver receiver = new ReceiveIntoStream(out);
        int changeCount = 0;

        while (chunker.hasNextLine()) {
            logProgress(changeCount);
            if (split(chunker.nextLine(), receiver)) {
                changeCount++;
            }
        }
        return changeCount;
    }

    private boolean split(final String line, final Receiver receiver) {
        if (null == line) {
            return false;
        }
        if (0 == line.length()) {
            receiver.emptyLine();
            return true;
        }

        return splitter.split(line, receiver);
    }

    private void logProgress(final int count) {
        if (!log.isDebugEnabled()) {
            return;
        }

        // must have just started
        if (0 == lastlog) {
            log.debug("Now cleaving documents.");
            lastlog = new Date().getTime();
            return;
        }

        final long now = new Date().getTime();
        if (now > (lastlog + LOG_INTERVAL)) {
            log.debug("Cleaved {} documents, so far.", count);
            lastlog = now;
        }
    }

    private void logStatus(final int count) {
        // reset progress logging
        lastlog = 0;

        if (0 == count) {
            log.info("Cleaved no documents.");
        } else {
            log.info("Cleaved {} documents into {} words and {} characters.", new Object[]{count, splitter.words(), splitter.characters()});
        }
    }
}
