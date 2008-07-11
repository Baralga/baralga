package org.remast.baralga;

import org.apache.commons.lang.StringUtils;

import junit.framework.TestCase;

public class HtmlTest extends TestCase {

    private static final String HTML = 
            "<html> " +
                "<head>test</head>" +
                "<body>" + 
                "<p>content <i>Italic</i> and < b> bold </B>" + 
                "</body>" + 
    		"</html>";
    
    public void testStripHtml()  {
        System.out.println(HTML.replaceAll("<[^<>]+>", StringUtils.EMPTY));
    }
}
