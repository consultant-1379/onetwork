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

package com.ericsson.de.onetwork.dms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.dms.exceptions.InvalidPlatformRuntimeException;
import com.ericsson.de.onetwork.dms.generics.Platform;

/**
 * Class that takes in a MIM version and returns the {@link Platform} that MIM
 * is based on.
 *
 * @author edalrey
 * @since 1Network_15.13
 */
public class MimToPlatformMapper {

    private final static Logger logger = LoggerFactory.getLogger(MimToPlatformMapper.class);

    private final static String[] CPP_MIM_NAMES = new String[] { "ERBS" };
    private final static String[] COMECIM_MIM_NAMES = new String[] { "SGSN" };

    /**
     * Returns the model {@link Platform} that a MOM XML is based on.
     *
     * @param mimVersion
     *            the version of the MIM (node model) used to build a node
     * @return the model {@link Platform} that the MOM XML is based on
     * @throws InvalidPlatformRuntimeException
     *             thrown at runtime exception when the model cannot be
     *             determined correctly from the MIM version
     */
    public static Platform getPlatformByMimVersion(final String mimVersion) {
        // Creating a lot of noise. I will keep it commented for future use or
        // can be removed totally.
        // logger.debug("Check platform type of MIM: {}", mimVersion);
        for (final String cppMimName : CPP_MIM_NAMES) {
            if (mimVersion.contains(cppMimName)) {
                return Platform.CPP;
            }
        }
        for (final String comEcimMimName : COMECIM_MIM_NAMES) {
            if (mimVersion.contains(comEcimMimName)) {
                return Platform.COMECIM;
            }
        }
        throw new InvalidPlatformRuntimeException("A valid platform has not been found for MIM version: " + mimVersion);
    }

}
