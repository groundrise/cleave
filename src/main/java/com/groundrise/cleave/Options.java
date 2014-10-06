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

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.Unparsed;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Command line argument parser.
 *
 * @author Nicholas Bugajski (nick@groundrise.com)
 */
@CommandLineInterface(application = "cleave")
class Options {
    /**
     * Input file list.
     */
    private final List<File> inputFiles = new ArrayList<>();

    /**
     * Request help message to be displayed.
     * @param help True if want help message
     */
    @Option(helpRequest = true, description = "display help", shortName = "h")
    void setHelp(final boolean help) {
    }

    /**
     * Current input file list.
     * @return Input file list
     */
    List<File> getInputFiles() {
        return this.inputFiles;
    }

    /**
     * Add files to input list.
     * @param files Files to be added
     */
    @Unparsed(name = "input files")
    void setInputFiles(final List<File> files) {
        this.inputFiles.addAll(files);
    }
}
