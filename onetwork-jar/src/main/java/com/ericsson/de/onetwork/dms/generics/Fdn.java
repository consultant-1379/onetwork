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

package com.ericsson.de.onetwork.dms.generics;

/**
 * An {@code Fdn} represents the Fully Distinguished Name of the managed object.
 * <p>
 * An FDN, which defines the location of a managed object, is composed of a
 * parent FDN, also an {@code Fdn} object, and the RDN (Relatively
 * Distinguished Name). The RDN is made up of a managed object type, and a
 * managed object name.
 * <p>
 * The parent {@code Fdn} of the given {@code Fdn} holds the location of the
 * {@link Mo} that is the parent of the {@code Mo}, which is designated by
 * the given {@code Fdn}.
 *
 * @author edalrey
 * @since 1Network_15.11
 */
public class Fdn {

    /**
     * This field belongs to an {@link Fdn} instance that represents an
     * {@link Mo} instance. The {@code Mo} instance has a parent {@code Mo}.
     * This
     * parent {@code Mo}'s {@code Fdn} is stored in this field.
     */
    private final Fdn parentFdn;
    /**
     * The type of {@code Mo} that this {@code Fdn} represents. It is defined by
     * the MOM. It is the first part of an RDN.
     */
    private final String moType;
    /**
     * The name of {@code Mo} that this {@code Fdn} represents. It can take any
     * String value. It is the second part of an RDN.
     */
    private final String moName;

    /**
     * Initialises a newly created {@link Fdn} object that represents the Fully
     * Distinguished Name of the managed object.
     *
     * @param parentFdn
     *            the complete {@code Fdn} of parent {@link Mo} to this
     *            {@code Mo}
     * @param moType
     *            the type of managed object
     * @param moName
     *            the assigned name to this {@code Mo}
     */
    public Fdn(final Fdn parentFdn, final String moType, final String moName) {
        this.parentFdn = parentFdn;
        this.moType = moType;
        this.moName = moName;
    }

    /**
     * Compares the {@link toString} value of this {@link Fdn} instance to a
     * second {@code Fdn} instance. That indicates if both {@code Fdn} instances
     * represent the same Fully Distinguished Name.
     *
     * @param fdn
     *            the {@code Fdn} instance to compare this {@code Fdn} instance
     *            with
     * @return boolean value that indicates if both {@code Fdn} instances
     *         represent the same Fully Distinguished Name
     */
    @Override
    public boolean equals(final Object fdn) {
        if (fdn instanceof Fdn) {
            return toString().equals(fdn.toString());
        } else {
            return false;
        }
    }

    /**
     * Returns the numeric value of the level at which this {@link Fdn} sits.
     * The root level is set to be 1.
     *
     * @return the level / depth of the given {@link Mo} in the
     *         {@link NetworkElement} tree structure
     */
    public int getLength() {
        if (null == parentFdn) {
            return 1;
        } else {
            return parentFdn.getLength() + 1;
        }
    }

    /**
     * Returns the parentFdn of the {@link Fdn}.
     *
     * @return the parentFdn of the {@code Fdn}
     */
    public Fdn getParentFdn() {
        return parentFdn;
    }

    /**
     * Returns the data type of the {@link Fdn}.
     *
     * @return the type data of the {@code Fdn}
     */
    public String getType() {
        return moType;
    }

    /**
     * Returns the name of the {@link Fdn}.
     *
     * @return the name of the {@code Fdn}
     */
    public String getName() {
        return moName;
    }

    @Override
    public String toString() {
        if (null == parentFdn) {
            return String.format("%s=%s", moType, moName);
        } else {
            return String.format("%s,%s=%s", parentFdn.toString(), moType, moName);
        }
    }

}
