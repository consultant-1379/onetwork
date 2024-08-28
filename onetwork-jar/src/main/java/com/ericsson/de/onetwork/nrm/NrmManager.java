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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages the retrieval of the Network Reference Model(NRM) list and individual
 * NRM objects.
 *
 * @author ecasjim
 */
public class NrmManager {

    /** Map of available NRMs */
    private final Map<String, Nrm> nrmMap = new HashMap<String, Nrm>();

    /** Constructor populates NRM map */
    public NrmManager() {
        populateNrmMap();
    }

    /**
     * Returns set of available NRM names.
     *
     * @return set of NRM names
     */
    public Set<String> getNrmList() {
        return nrmMap.keySet();
    }

    /**
     * Returns NRM object.
     *
     * @param nrmName
     *            name of required NRM
     * @return NRM object
     */
    public Nrm getNrm(final String nrmName) {
        return nrmMap.get(nrmName);
    }

    /**
     * TODO: In future sprint, map will be populated using DB.
     */
    private void populateNrmMap() {
        final List<String> gnmList1 = new ArrayList<String>();
        gnmList1.add("LTE_R1");
        gnmList1.add("CORE_R1");
        final Nrm r1 = new Nrm("R1", gnmList1);

        final List<String> gnmList2 = new ArrayList<String>();
        gnmList2.add("LTE_R2");
        gnmList2.add("CORE_R2");
        final Nrm r2 = new Nrm("R2", gnmList2);

        nrmMap.put(r1.getName(), r1);
        nrmMap.put(r2.getName(), r2);
    }
}
