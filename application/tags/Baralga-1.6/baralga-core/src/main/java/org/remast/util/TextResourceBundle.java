/**
 * 
 */
package org.remast.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides easy access to texts from properties files. This uses the default 
 * Java mechanisms but looks for properties files in the package of a class.
 * This makes it easier to modularize texts in applications as texts are always
 * bundled with the class.
 * @author remast
 */
public final class TextResourceBundle {
    
    /** The logger. */
    private static final Log log = LogFactory.getLog(TextResourceBundle.class);

    /** The resource bundle. */
    private ResourceBundle resourceBundle = null;

    /** The class to resolve texts for. */
    private Class<?> clazz;

    /**
     * Creates a new bundle with text ressources for the given class.
     * @param clazz the class to create bundle for - may not be null
     * @throws IllegalArgumentException if class argument is null
     */
    private TextResourceBundle(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Parameter clazz may not be null.");
        }

        this.clazz = clazz;

        try {
            final String [] superPackageNames = StringUtils.split(clazz.getPackage().getName(), '.');

            // Get name of the current package
            String currentPackageName = "";
            if (superPackageNames.length > 0) {
                currentPackageName = superPackageNames[superPackageNames.length - 1];
            }

            resourceBundle = ResourceBundle.getBundle(
                    clazz.getPackage().getName() + "." + StringUtils.capitalize(currentPackageName) + "Texts",
                    Locale.getDefault(),
                    clazz.getClassLoader()
            );
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
