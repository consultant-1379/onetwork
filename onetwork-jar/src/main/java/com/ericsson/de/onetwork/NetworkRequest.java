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

package com.ericsson.de.onetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: Provisional design of class.
 * <p>
 * Network request object contains the network specification defined by the user
 * and it also contains the GNM revision to be used.
 * </p>
 *
 * @author ecasjim
 */
public class NetworkRequest {

    /** GNM revision . */
    private final String gnmRevision;

    /** Network node size. */
    private final int networkNodeSize;

    /** Collection of feature names. */
    private final List<String> featureNames;

    /** MIM version of the requested node */
    private final String mimVersion;

    /**
     * Returns the GNM revision name of this network request.
     *
     * @return Gnm revision name.
     */
    public String getGnmRevision() {
        return gnmRevision;
    }

    /**
     * Returns the network node size.
     *
     * @return network node size as an int.
     */
    public int getNetworkNodeSize() {
        return networkNodeSize;
    }

    /**
     * Returns Collection of feature names required in this
     * <code>NetworkRequest</code>.
     *
     * @return the featureNames
     */
    public List<String> getFeatureNames() {
        return featureNames;
    }

    /**
     * Returns MIM version of the node. For example LTE ERBS F1101.
     *
     * @return the mim version of the node.
     */
    public String getMimVersion() {
        return mimVersion;
    }

    /**
     * Helper Builder Design Pattern class.
     *
     * @author qfatonu
     */
    public static class Builder {
        // Required parameters
        private final int networkNodeSize;
        private final String gnmRevision;

        // Optional parameters
        private List<String> featureNames = new ArrayList<>();
        private String mimVersion = "";

        /**
         * Builder constructor that takes all the required data to build a
         * network as requested by the user.
         *
         * @param gnmRevision
         *            GNM revision name
         * @param networkNodeSize
         *            network node size
         */
        public Builder(final String gnmRevision, final int networkNodeSize) {
            this.gnmRevision = gnmRevision;
            this.networkNodeSize = networkNodeSize;
        }

        /**
         * Sets the features list.
         *
         * @param val
         *            collection of feature names
         * @return this class
         */
        public Builder featureNames(final List<String> val) {
            featureNames = val;
            return this;
        }

        /**
         * Sets MIM version.
         *
         * @param val
         *            MIM version value
         * @return this class
         */
        public Builder mimVersion(final String val) {
            mimVersion = val;
            return this;
        }

        /**
         * Returns instance of this class.
         *
         * @return instance of this class
         */
        public NetworkRequest build() {
            return new NetworkRequest(this);
        }
    }

    private NetworkRequest(final Builder builder) {
        gnmRevision = builder.gnmRevision;
        networkNodeSize = builder.networkNodeSize;
        featureNames = builder.featureNames;
        mimVersion = builder.mimVersion;
    }

    @Override
    public String toString() {
        return "NetworkRequest [gnmRevision=" + gnmRevision + ", networkNodeSize=" + networkNodeSize + ", featureNames=" + featureNames
                + ", mimVersion=" + mimVersion + "]";
    }

}
