/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.de.onetwork.ui;

import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

/**
 * Uses the same instance of the {@code Logger} class throughout the
 * application. Also configures the layout for the logger output.
 *
 * @author econocl
 */
public class LoggerWrapper {

    /** Used for writing to a String buffer for session data */
    private static StringWriter consoleWriter = new StringWriter();

    /** String for defining the format of the logger output */
    private static final String pattern = "%d{ISO8601} %c %p - %m%n";

    /** Creates a layout pattern based on the 'pattern' string above */
    private static final PatternLayout layout = new PatternLayout(pattern);

    /** Appends log events to an I/O stream */
    private static WriterAppender appender = new WriterAppender(layout, consoleWriter);

    /**
     * Returns a {@code Logger} with the specified name, with the configuration
     * specified in the {@code LoggerWrapper} class
     *
     * @param loggerName
     *            the name of the logger
     * @return
     *         a logger configured for use with the 1Network service
     */
    public static Logger getLogger(final String loggerName) {
        final Logger logger = Logger.getLogger(loggerName);
        return configureLogger(logger);
    }

    /**
     * Returns a {@code Logger} defined by the specified class, with the
     * configuration specified in the {@code LoggerWrapper} class
     *
     * @param loggerClass
     *            the name of the class defining the logger
     * @return
     *         a logger configured for use with the 1Network service
     */
    public static Logger getLogger(final Class<?> loggerClass) {
        final Logger logger = Logger.getLogger(loggerClass);
        return configureLogger(logger);
    }

    private static Logger configureLogger(final Logger logger) {
        logger.addAppender(appender);
        logger.setLevel(Level.ALL);
        logger.removeAppender("CONSOLE_APPENDER");
        return logger;
    }

    /**
     * Returns a {@code SringWriter} object, used to package strings used for
     * socket communications into a single buffered object.
     *
     * @return
     *         a {@code SringWriter} object
     */
    public static StringWriter getConsoleWriter() {
        return consoleWriter;
    }
}
