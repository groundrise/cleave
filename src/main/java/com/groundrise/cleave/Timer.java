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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Report the amount of time elapsed to a log.
 *
 * Information is written to the log "INFO" method.  CPU, User and Real time
 * elapsed are all written out.  Start time is time of object construction.
 *
 * @author Nicholas Bugajski (nick@groundrise.com)
 */
class Timer {

    /**
     * For writing logging statements.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Timer.class);

    /**
     * Start time for CPU time (user + system mode).
     */
    private final long startCpu;

    /**
     * Start time for user mode CPU time.
     */
    private final long startUser;

    /**
     * Start time for clock/real time.
     */
    private final long startReal;

    /**
     * Constructs and starts a timer object.
     */
    Timer() {
        final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        if (bean.isCurrentThreadCpuTimeSupported()) {
            this.startCpu  = bean.getCurrentThreadCpuTime();
            this.startUser = bean.getCurrentThreadUserTime();
        } else {
            this.startUser = 0;
            this.startCpu  = 0;
        }
        this.startReal = System.nanoTime();
    }

    /**
     * Calculate current time elapsed and write to LOG.
     */
    void report() {
        final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        final long endReal = System.nanoTime();
        final long endCpu  = bean.getCurrentThreadCpuTime();
        final long endUser = bean.getCurrentThreadUserTime();
        final long currReal   = endReal - this.startReal;
        final long currCpu    = endCpu  - this.startCpu;
        final long currUser   = endUser - this.startUser;
        final long currSystem = currCpu - currUser;
        if (bean.isCurrentThreadCpuTimeSupported()) {
            LOG.info("{} ns real, {} ns user + {} ns system = {} ns CPU",
                    new Object[]{currReal, currUser, currSystem, currCpu});
        } else {
            LOG.info("{}ns real", currReal);
        }
    }
}
