
package com.ericsson.de.onetwork.ss.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SimServiceStringBuilderTest {

    private static final String NEW_LINE = SimServiceStringBuilder.NEW_LINE;
    private SimServiceStringBuilder sb;
    private final String DATA = "aString";
    private final String PREFIX_2_SPACE = "  ";

    @Test
    public void testAppend() {
        sb = new SimServiceStringBuilder();
        final String expected = DATA + NEW_LINE;
        sb.append(DATA);
        Assert.assertEquals(sb.toString(), expected);
    }

    @Test
    public void testAppend_With_Prefix_NumOfSpace() {
        sb = new SimServiceStringBuilder();
        final String expected = PREFIX_2_SPACE + DATA + NEW_LINE;
        sb.append(2, DATA);
        Assert.assertEquals(sb.toString(), expected);
    }

    @Test
    public void testAppend_Without_NewLine() {
        sb = new SimServiceStringBuilder();
        final String expected = DATA;
        sb.appendWithoutNewLine(DATA);
        Assert.assertEquals(sb.toString(), expected);
    }

    @Test
    public void testAppend_Without_NewLine_With_Prefix_NumOfSpace() {
        sb = new SimServiceStringBuilder();
        final String expected = PREFIX_2_SPACE + DATA + NEW_LINE;
        sb.append(2, DATA);
        Assert.assertEquals(sb.toString(), expected);
    }
}
