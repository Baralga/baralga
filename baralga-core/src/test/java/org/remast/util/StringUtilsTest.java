package org.remast.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        assertNull(StringUtils.stripXmlTags(null));
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
