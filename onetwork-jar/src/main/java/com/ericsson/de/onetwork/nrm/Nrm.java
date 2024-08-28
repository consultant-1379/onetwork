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

package com.ericsson.de.onetwork.nrm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Network Reference Model (NRM). It is used to define an entire network from
 * the users perspective and contain a GNM revision for each network type
 * (LTE, WRAN, GRAN, CORE). Users will select revisions of NRMs on the client
 * side when
 * requesting networks.
 *
 * @author ecasjim
 * @author eaefhiq
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "gnmNames"
})
@XmlRootElement(name = "nrm")
public class Nrm {

    /** NRM name. */
    private final String name;

    /** List of GNMs this NRM contains. */
    private final List<String> gnmNames;

    /**
     * Default constructor
     */
    public Nrm() {
        super();
        name = "";
        gnmNames = new ArrayList<String>();
    }

    /**
     * NRM constructor.
     *
     * @param name
     *            Nrm name
     * @param gnmNames
     *            list of GNM names
     */
    public Nrm(final String name, final List<String> gnmNames) {
        this.name = name;
        this.gnmNames = gnmNames;
    }

    /**
     * Returns NRM name.
     *
     * @return NRM name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns list of GNM names this NRM contains.
     *
     * @return list of GNM names
     */
    public List<String> getGnmNames() {
        return gnmNames;
    }

}
