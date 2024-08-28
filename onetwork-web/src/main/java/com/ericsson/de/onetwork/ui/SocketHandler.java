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

import java.io.File;
import java.io.IOException;

import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the server side processing of socket communications.
 *
 * @author eephmar
 */
@ServerEndpoint("/sockethandler")
public class SocketHandler {
    private final static Logger logger =
            LoggerFactory.getLogger(SocketHandler.class);
    private static boolean readLogFile = true;

    /**
     * Sets up communications between the server and the client when the socket
     * connection is started. Log file is read per line starting from the most
     * recent line (when the connection is established).
     *
     * @param session
     *            the session in which socket communications will be set up
     * @throws IOException
     *             thrown if sending the session text failed
     * @throws InterruptedException
     */

    @OnOpen
    public void onSocketSessionOpen(final Session session) {
        try {
            logger.debug("Opening socket connection..");
            final Basic basicRemote = session.getBasicRemote();
            final TailerListener listener = new TailerListenerAdapter() {
                @Override
                public void handle(final String line) {
                    try {
                        logger.debug("Line sent by server: " + line);
                        basicRemote.sendText(line);
                    } catch (final IOException e) {
                        logger.error("Failed sending the message " + line + " to client due to: " + e.getMessage());
                    }
                }
            };
            // TODO: Match file with correct path while working on Windows
            // OR totally update code to read it independent of OS env.
            final Tailer tailer = new Tailer(new File("/tmp/1Network_info.log"),
                    listener, 50, true, false);
            final Thread tailerThread = new Thread(tailer);
            tailerThread.start();
            while (readLogFile) {
                Thread.sleep(100);
            }
            logger.debug("Closing log file tailer..");
            tailerThread.interrupt();
            logger.debug("Closing websocket session..");
            readLogFile = true;
            session.close();
        } catch (IOException | InterruptedException ex) {
            logger.debug("ERROR:{}", ex.getMessage());
        }
    }

    /**
     * Ends the reading of the log file when <code>readLogFile</code> value is set to false.
     *
     * @param readLog
     *            stops log file reading when set to false
     */
    public static void setReadLogFile(final boolean readLog) {
        readLogFile = readLog;
    }
}
