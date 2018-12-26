/**
 * 
 */
package org.remast.util;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Utility methods for working with strings.
 */
public class StringUtils {

    /** Regular expression for xml tags. */
    private static final String XML_TAG_PATTERN = "<[^<>]+>";
    
    /**
     * Strip all xml tags from given String and unescape xml characters.
     * @param xml the xml to be stripped from tags
     * @return
     */
    public static String stripXmlTags(final String xml) {
        if (org.apache.commons.lang3.StringUtils.isBlank(xml)) {
            return xml;
        }
        
        // 1. Remove xml tags
        String strippedXml = xml.replaceAll(XML_TAG_PATTERN, org.apache.commons.lang3.StringUtils.EMPTY);
        
        // 2. Unescape xml
        strippedXml = StringEscapeUtils.unescapeXml(strippedXml);
        
        // 3. Trim whitespace
        strippedXml = org.apache.commons.lang3.StringUtils.trim(strippedXml);
        return strippedXml;
    }
}
