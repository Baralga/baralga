/**
 * 
 */
package org.remast.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides easy access to texts from properties files. This uses the default 
 * Java mechanisms but looks for properties files in the package of a class.
 * This makes it easier to modularize texts in applications as texts are always
 * bundled with the class.
 * @author remast
 */
public class TextResourceBundle {

    /** The name of the properties file. */
    private static final String PROPERTIES_FILE_NAME = "Texts";

    /** The logger. */
    private static final Log log = LogFactory.getLog(TextResourceBundle.class);

    /** The resource bundle. */
    private ResourceBundle resourceBundle = null;

    /** The class to resolve texts for. */
    private Class<?> clazz;

    private TextResourceBundle(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Parameter clazz may not be null.");
        }
        
        this.clazz = clazz;

        try {
            resourceBundle = ResourceBundle.getBundle(clazz.getPackage().getName() + "." + PROPERTIES_FILE_NAME,
                    Locale.getDefault(), clazz.getClassLoader());
        } catch (MissingResourceException e) {
            log.error(e, e);
        }
    }

    /**
     * Get's the bundle to resolve texts.
     * @param clazz the class to resolve bundle for
     * @return the resolved text bundle
     */
    public static TextResourceBundle getBundle(final Class<?> clazz) {
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
            log.error("Could not resolve text for class " + clazz.getName() + " and key \""  + key + "\".", e);
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
