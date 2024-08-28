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

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.ericsson.de.onetwork.bs.features.FeatureManager;
import com.ericsson.de.onetwork.cs.LatestErbsMimService;
import com.ericsson.de.onetwork.cs.NrmService;
import com.ericsson.de.onetwork.cs.OnetworkService;

/**
 * Defines the root path classes for RESTful web services.
 *
 * @author econocl
 * @author eaefhiq
 */
@ApplicationPath("/rest")
public class WebServices extends Application {

    /*
     * (non-Javadoc)
     * @see javax.ws.rs.core.Application#getClasses()
     */
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(NrmService.class);
        set.add(OnetworkService.class);
        set.add(FeatureManager.class);
        set.add(LatestErbsMimService.class);
        return set;
    }
}
