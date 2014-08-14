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

import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.Unparsed;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@CommandLineInterface(application = "cleave")
class Options {
    private boolean whitespaceSplit;
    private final List<File> inputFiles = new ArrayList<>();

    @Option(helpRequest = true, description = "display help", shortName = "h")
    void setHelp( boolean help ) {}

    boolean getWhitespaceSplit() {
        return whitespaceSplit;
    }

    @Option(shortName = "w", longName = "whitespace-split", description = "Split into words using whitespace instead of the Unicode standard method.")
    void setWhitespaceSplit(final boolean w) {
        whitespaceSplit = w;
    }

    List<File> getInputFiles() {
        return inputFiles;
    }

    @Unparsed(name = "input files")
    void setInputFiles( List<File> files ) {
        inputFiles.addAll(files);
    }
}
