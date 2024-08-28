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

import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;

/**
 * A {@code CppMo} is an implementation of {@link Mo} interface. It represents
 * an {@code Mo} instance based on CPP platform managed object.
 *
 * @author edalrey
 * @since 1Network_15.12
 */
public class CppMo extends AbstractMo implements Mo {

    /**
     * Initialises a {@link Mo} that represents a managed object within the CPP
     * model.
     *
     * @param momMoClass
     *            the {@link Class} object on which this {@code Mo} is based
     * @param parentMo
     *            the parent {@code Mo} to this {@code Mo}
     * @param moType
     *            the type of managed object
     * @param name
     *            the user assigned name to this {@code Mo}
     */
    public CppMo(final Class momMoClass, final Mo parentMo, final String moType, final String name) {
        this.momMoClass = momMoClass;
        if (null == parentMo) {
            fdn = new Fdn(null, moType, name);
        } else {
            fdn = new Fdn(parentMo.getFdn(), moType, name);
        }
    }

}
