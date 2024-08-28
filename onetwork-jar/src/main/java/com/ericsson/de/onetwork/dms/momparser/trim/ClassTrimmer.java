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

package com.ericsson.de.onetwork.dms.momparser.trim;

import java.util.List;

import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Action;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Attribute;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;

/**
 * Trims unused elements in a {@code Class} object.
 *
 * @author eaefhiq
 */
public class ClassTrimmer implements Trimmer {

    /** The MIM class to be trimmed. */
    private final Class mimClass;

    /**
     * Instantiates a new class trimmer.
     *
     * @param trimmerClass
     *            the class to be trimmed.
     */
    public ClassTrimmer(final Class trimmerClass) {
        mimClass = trimmerClass;
    }

    /**
     * Removes any existing values for these properties. For example, removes
     * any dependencies already set and content within <description> tags.
     */
    @Override
    public void trim() {
        mimClass.setDependencies(null);
        mimClass.setDependenciesScript(null);
        mimClass.setDescription(null);
        mimClass.getPreliminaryOrDeprecatedOrObsolete().clear();

        final List<Object> actionsAndAttributesList = mimClass.getActionOrAttribute();
        for (final Object actionOrAttribute : actionsAndAttributesList) {
            if (actionOrAttribute instanceof Attribute) {
                final Attribute attr = (Attribute) actionOrAttribute;
                attr.setDescription(null);
                attr.setDependencies(null);
                attr.setDependenciesScript(null);
                attr.getPreliminaryOrDeprecatedOrObsolete().clear();
                attr.getDisturbancesOrTakesEffectOrSideEffectsOrPreconditionOrConditionOrCounterTypeOrSamplingRateOrScannerOrCounterResetOrCounterContextOrGetValue()
                        .clear();
            } else if (actionOrAttribute instanceof Action) {
                final Action action = (Action) actionOrAttribute;
                action.setDescription(null);
                action.setDependencies(null);
                action.setDependenciesScript(null);
                action.setDecisionPoint(null);
            }
        }
    }
}
