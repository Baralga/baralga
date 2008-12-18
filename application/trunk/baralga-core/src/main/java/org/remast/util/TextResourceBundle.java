/**
 * 
 */
package org.remast.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author remast
 */
public class TextResourceBundle {

    /** The logger. */
    private static final Log log = LogFactory.getLog(TextResourceBundle.class);

    /** The resource bundle. */
    private ResourceBundle resourceBundle = null;

    private TextResourceBundle(final Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Parameter clazz may not be null.");
        }

        try {
            resourceBundle = ResourceBundle.getBundle(clazz.getPackage().getName() + "."+ "Texts");
        } catch (MissingResourceException e) {
            log.error(e, e);
        }
    }

    public static TextResourceBundle getBundle(Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Parameter clazz may not be null.");
        }

        final TextResourceBundle textBundle = new TextResourceBundle(clazz);
        return textBundle;
    }

    /**
     * Get internationalized string for the given key.
     * @param key the key for the internationalization
     * @return the resolved message
     */
    public String textFor(final String key) {
        try {
            if (resourceBundle == null) {
                return '!' + key + '!';
            }

            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            log.error(e, e);
            return '!' + key + '!';
        }
    }

    /**
     * Get internationalized string for the given key and resolve arguments.
     * @param key the key for the internationalization
     * @param arguments the arguments to be resolved
     * @return the resolved message
     */
    public String textFor(final String key, final Object... arguments) {
        return MessageFormat.format(textFor(key), arguments);
    }
}
