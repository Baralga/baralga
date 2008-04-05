package org.remast.baralga;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Gives access to all text resources for Baralga.
 * TODO: Split up into two classes. One for the gui and one for the model.
 * @author remast
 */
public abstract class Messages {

    /** The logger. */
    private static final Log log = LogFactory.getLog(Messages.class);

    /** The name of the resource bundle. */
    private static final String BUNDLE_NAME = "org.remast.baralga.Text"; //$NON-NLS-1$

    /** The resource bundle. */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String getString(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            log.error(e, e);
            return '!' + key + '!';
        }
    }

    public static String getString(final String key, final Object argument) {
        return StringUtils.replace(getString(key), "{0}", String.valueOf(argument));
    }
}
