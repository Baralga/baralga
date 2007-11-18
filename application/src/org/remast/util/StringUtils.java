/**
 * 
 */
package org.remast.util;

/**
 * @author remast
 */
public abstract class StringUtils {

    private static final String XML_TAG_PATTERN = "<[^<>]+>";
    
    /**
     * Strip all xml tags from given String.
     * @param html
     * @return
     */
    public static String stripXmlTags(final String html) {
        if (org.apache.commons.lang.StringUtils.isBlank(html)) {
            return html;
        }
        
        return html.replaceAll(XML_TAG_PATTERN, org.apache.commons.lang.StringUtils.EMPTY);
    }
}
