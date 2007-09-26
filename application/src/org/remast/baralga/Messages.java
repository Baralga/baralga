package org.remast.baralga;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class Messages {

    /** The name of the resource bundle. */
    private static final String BUNDLE_NAME = "org.remast.baralga.Text"; //$NON-NLS-1$

    /** The resource bundle. */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
