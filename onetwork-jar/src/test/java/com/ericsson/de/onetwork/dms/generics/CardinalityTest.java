
package com.ericsson.de.onetwork.dms.generics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.bs.BusinessServiceController;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.exceptions.InvalidPlatformRuntimeException;
import com.ericsson.de.onetwork.dms.exceptions.MaximumCardinalityExceededException;
import com.ericsson.de.onetwork.util.FileUtility;

/*
 * Used to verify cardinality values.
 * Created cardinalities_uniq.csv using command:
 * sed -e '/<child/,/<\/child>/!d' Netsim_ERBS_NODE_MODEL_vF_1_101.xml |
 * grep -Ev '(child|cardinality)' | tr '\n' ' ' |
 * sed 's/<hasClass/,<hasClass/g'|
 * sed 's/<*min*>//g' | sed 's/<*max*>//g' | sed 's/<hasClass name=//g' |
 * sed 's/[\t^M"/<>]//g' | sort | uniq | grep -v '^,$' |
 * sed 's/^,//g' > cardinalities_uniq.csv
 */
public class CardinalityTest {

    private final static Logger logger = LoggerFactory.getLogger(BusinessServiceController.class);
    private final static MoFactory MO_FACTORY = new MoFactory(true);
    private final static List<CardinalityTestObject> CARDINALITY_TEST_OBJECTS = new ArrayList<CardinalityTestObject>();
    private final static String MIM = "LTE ERBS F1101";
    private final static String NODE_NAME = "LTE01ERBS00001";
    private final static Map<Mo, Mo> MAX_CARDINALITY_MOS = new HashMap<Mo, Mo>();
    private final static Map<Mo, Mo> MIN_CARDINALITY_MOS = new HashMap<Mo, Mo>();
    private final static int MIN_AND_MAX_CARDINALITY = 3;

    @BeforeClass
    public void beforeClass() {
        final List<String> cardinalities = FileUtility.readFromCSV("/dms/mims/cardinalities_uniq.csv");

        for (final String cardinalityLine : cardinalities) {
            final String[] moCardinalityArray = cardinalityLine.split(" ");
            if (moCardinalityArray.length == MIN_AND_MAX_CARDINALITY) {
                CARDINALITY_TEST_OBJECTS
                        .add(new CardinalityTestObject(moCardinalityArray[0], Integer.parseInt(moCardinalityArray[1]), Integer
                                .parseInt(moCardinalityArray[2])));
            } else {
                CARDINALITY_TEST_OBJECTS.add(new CardinalityTestObject(moCardinalityArray[0], Integer.parseInt(moCardinalityArray[1]), 0));
            }
        }

        final NetworkElement networkElement = new NetworkElement(MIM, NODE_NAME);
        final Mo mo = networkElement.getRootMo();

        recursivelyFindMosForWhichCardinalityIsAvailable(mo);
    }

    @Test
    public void verifyMaxCardinalityExceptionisThrown() throws InvalidChildException, InvalidPlatformRuntimeException {
        boolean exceptionThrown;
        Integer moName = 1000;

        for (final Map.Entry<Mo, Mo> entry : MAX_CARDINALITY_MOS.entrySet()) {
            exceptionThrown = false;
            final Mo parentMo = entry.getValue();
            final Mo childMo = entry.getKey();

            final int numofChildMos = parentMo.getChildrenByType(childMo.getType()).size();
            final int maxChildMos = getMaxMosForThisType(childMo.getType());

            for (int i = numofChildMos; i < maxChildMos + 1; i++) {
                logger.debug("Attempting to create {} {}, current: {}, max {}", childMo.getType(), moName.toString(), numofChildMos, maxChildMos);

                try {
                    MO_FACTORY.getMo(MIM, parentMo, childMo.getType(), moName.toString());
                    moName++;
                    logger.debug("Created {} {}, current: {}, max {}", childMo.getType(), moName.toString(), numofChildMos, maxChildMos);
                    break;
                } catch (final MaximumCardinalityExceededException e) {
                    exceptionThrown = true;
                    logger.debug("Exception thrown for {}", childMo.getType());
                }
            }

            Assert.assertTrue(exceptionThrown);
        }
    }

    @Test
    public void verifyMinCardinalityIsMetForMos() throws InvalidChildException, InvalidPlatformRuntimeException {
        for (final Map.Entry<Mo, Mo> entry : MIN_CARDINALITY_MOS.entrySet()) {
            final Mo parentMo = entry.getValue();
            final Mo childMo = entry.getKey();

            final int actualNumOfChildMos = parentMo.getChildrenByType(childMo.getType()).size();
            final int expectedMinChildMos = getMinMosForThisType(childMo.getType());

            logger.debug("{} MOs, Actual: {}, Expected: {}", childMo.getType(), actualNumOfChildMos, expectedMinChildMos);
            Assert.assertEquals(actualNumOfChildMos, expectedMinChildMos);
        }
    }

    private void recursivelyFindMosForWhichCardinalityIsAvailable(final Mo parentMo) {
        final List<Mo> childMos = parentMo.getChildren();

        if (childMos != null && !childMos.isEmpty()) {
            for (final Mo childMo : childMos) {
                storeMosForWhichMinCardinalityIsAvailable(parentMo, childMo);
                storeMosForWhichMaxCardinalityIsAvailable(parentMo, childMo);
                recursivelyFindMosForWhichCardinalityIsAvailable(childMo);
            }
        }
    }

    private void storeMosForWhichMinCardinalityIsAvailable(final Mo parentMo, final Mo childMo) {
        final Integer maxCardinality = getMinMosForThisType(childMo.getType());
        if (maxCardinality != 0) {
            MIN_CARDINALITY_MOS.put(childMo, parentMo);
        }
    }

    private void storeMosForWhichMaxCardinalityIsAvailable(final Mo parentMo, final Mo childMo) {
        final Integer maxCardinality = getMaxMosForThisType(childMo.getType());
        if (maxCardinality != 0) {
            MAX_CARDINALITY_MOS.put(childMo, parentMo);
        }
    }

    private int getMaxMosForThisType(final String type) {
        for (final CardinalityTestObject cardinalityTestObject : CARDINALITY_TEST_OBJECTS) {
            if (cardinalityTestObject.getMoType().equalsIgnoreCase(type)) {
                return cardinalityTestObject.getMax();
            }
        }

        return 0;
    }

    private int getMinMosForThisType(final String type) {
        for (final CardinalityTestObject cardinalityTestObject : CARDINALITY_TEST_OBJECTS) {
            if (cardinalityTestObject.getMoType().equalsIgnoreCase(type)) {
                return cardinalityTestObject.getMin();
            }
        }

        return 0;
    }

    /**
     * Used to represent cardinality data of an Mo purely for testing purposes.
     */
    public class CardinalityTestObject {
        private String moType;
        private int min = 0;
        private int max = 0;

        public CardinalityTestObject(final String moType, final int min, final int max) {
            this.moType = moType;
            this.min = min;
            this.max = max;
        }

        public String getMoType() {
            return moType;
        }

        public void setMoType(final String moType) {
            this.moType = moType;
        }

        public int getMin() {
            return min;
        }

        public void setMin(final int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(final int max) {
            this.max = max;
        }
    }
}
