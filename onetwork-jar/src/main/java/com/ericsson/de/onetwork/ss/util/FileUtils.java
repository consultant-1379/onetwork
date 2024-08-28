/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.de.onetwork.ss.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common file utilities.
 *
 * @author qfatonu
 */
public class FileUtils {

    private final static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Returns file paths of only mo, mml, sh, pl and txt files at given
     * requested directory.
     *
     * @param dir
     *            the folder where files to be read
     * @return
     * @throws IOException
     *             if fails to read the file
     */
    public static List<Path> getSourceFiles(final Path dir) throws IOException {
        final List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{mo,mml,sh,pl,txt}")) {
            for (final Path entry : stream) {
                result.add(entry);
                logger.debug("entry:{}", entry);
            }
        } catch (final DirectoryIteratorException ex) {
            // I/O error encountered during the iteration, the cause is an
            // IOException
            logger.error(ex.getMessage());
            throw ex.getCause();
        }
        return result;
    }

    /**
     * Return all file paths under given directory.
     *
     * @param dir
     *            directory name where list of paths requested
     * @return list of file paths
     * @throws IOException
     *             if unable to access to files or given directory
     */
    public static List<Path> getFiles(final Path dir) throws IOException {
        final List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (final Path entry : stream) {
                result.add(entry);
                logger.debug("entry:{}", entry);
            }
        } catch (final DirectoryIteratorException ex) {
            // I/O error encountered during the iteration, the cause is an
            // IOException
            logger.error(ex.getMessage());
            throw ex.getCause();
        }
        return result;
    }

    /**
     * Delete folder contents of the given path.
     *
     * @param path
     *            location of the folder
     */
    public static void deleFolderContents(final Path path) {

        if (path.toFile().listFiles().length > 0) {

            try {
                org.apache.commons.io.FileUtils.forceDelete(path.toFile());
                Files.createDirectories(path);

            } catch (final IOException e) {
                logger.error("Delete error:{}", e.getMessage());
            }
        }
    }

    /**
     * Copies files from given folder to destination folder.
     *
     * @param sourceFolderPath
     *            the path where files are to be copied.
     * @param destFolderPath
     *            the path where files to be moved.
     * @throws IOException
     *             if copy fails
     */
    public static void copyFolderFiles(final Path sourceFolderPath, final Path destFolderPath) throws IOException {

        final List<Path> paths = FileUtils.getSourceFiles(sourceFolderPath);

        for (final Path source : paths) {
            Files.copy(source, destFolderPath.resolve(source.getFileName()));
            logger.debug("fileName={} copied to={} successfully!", source.getFileName(), destFolderPath);
        }
    }

    /**
     * Writes the given data into the specified filename at given folder.
     *
     * @param parentFolderName
     *            the name of the folder where file to be written
     * @param fileName
     *            the name of the file to be created
     * @param data
     *            the text which requires to be written into the file
     */
    public static void writeToFile(final String parentFolderName, final String fileName, final String data) {
        final Path parentFolder = Paths.get(parentFolderName);
        final Path filePath = parentFolder.resolve(fileName);

        final Charset charset = Charset.forName("US-ASCII");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, charset, StandardOpenOption.CREATE)) {
            writer.write(data);
        } catch (final IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * Gets platform independent path in order to access to file. For example;
     * if the beginning of the string is a slash, then a character, then a colon
     * and another slash, replace it with the character, the colon, and the
     * slash (leaving the leading slash off)
     *
     * @param path
     *            the plain path created from getResource or simular method.
     * @return platform independent path string
     */
    public static String getPlatformIndependentPathString(final String path) {
        return path.replaceFirst("^/(.:/)", "$1");
    }
}
