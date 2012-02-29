package org.remast.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

/**
 * Tests the class {@link StringUtils}.
 * @author remast
 * @see StringUtils
 */
public class StringUtilsTest {
	
    /**
     * Test for {@link StringUtils#stripXmlTags(String)}.
     */
    @Test
	public void testStripXmlTags() {
        assertEquals(null, org.remast.util.StringUtils.stripXmlTags(null));
        assertNotSame("", org.remast.util.StringUtils.stripXmlTags(null));
        assertEquals("", org.remast.util.StringUtils.stripXmlTags(""));
        assertEquals("<", org.remast.util.StringUtils.stripXmlTags("<"));
        assertEquals("<", org.remast.util.StringUtils.stripXmlTags("&lt;"));

        String htmlText = "<p>content</p>";
        String text = "content";
        assertEquals(text, org.remast.util.StringUtils.stripXmlTags(htmlText));

        htmlText = 
	        "<html> " 
	        +  "<body>" 
	        +    "<p>content <i>Italic</i> and < b> bold</B>" 
	        +  "</body>" 
	        + 
	        "</html>";
	    text = "content Italic and  bold";
	    assertEquals(text, org.remast.util.StringUtils.stripXmlTags(htmlText));
	}
}
