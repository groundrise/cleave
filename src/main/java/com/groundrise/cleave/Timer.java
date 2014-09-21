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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Timer {

    private static final Logger log = LoggerFactory.getLogger(Timer.class);

    private final long startCpu;
    private final long startUser;
    private final long startReal;

    Timer() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        if (bean.isCurrentThreadCpuTimeSupported()) {
            startCpu  = bean.getCurrentThreadCpuTime();
            startUser = bean.getCurrentThreadUserTime();
        } else {
            startUser = 0;
            startCpu  = 0;
        }
        startReal = System.nanoTime();
    }

    void report() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        final long endReal = System.nanoTime();
        final long endCpu  = bean.getCurrentThreadCpuTime();
        final long endUser = bean.getCurrentThreadUserTime();
        final long currReal   = endReal - startReal;
        final long currCpu    = endCpu  - startCpu;
        final long currUser   = endUser - startUser;
        final long currSystem = currCpu - currUser;
        if (bean.isCurrentThreadCpuTimeSupported()) {
            log.info("{} ns real, {} ns user + {} ns system = {} ns CPU",
                    new Object[]{currReal, currUser, currSystem, currCpu});
        } else {
            log.info("{}ns real", currReal);
        }
    }
}
