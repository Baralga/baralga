/**
 * 
 */
package org.remast.util;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Utility methods for working with strings.
 * @author remast
 */
public abstract class StringUtils {

    /** Regular expression for xml tags. */
    private static final String XML_TAG_PATTERN = "<[^<>]+>";
    
    /**
     * Strip all xml tags from given String.
     * @param xml
     * @return
     */
    public static String stripXmlTags(final String xml) {
        if (org.apache.commons.lang.StringUtils.isBlank(xml)) {
            return xml;
        }
        
        // 1. Remove xml tags
        String strippedXml = xml.replaceAll(XML_TAG_PATTERN, org.apache.commons.lang.StringUtils.EMPTY);
        
        // 2. Unescape xml
        strippedXml = StringEscapeUtils.unescapeXml(strippedXml);
        
        // 3. Trim whitespace
        strippedXml = org.apache.commons.lang.StringUtils.trim(strippedXml);
        return strippedXml;
    }
}
