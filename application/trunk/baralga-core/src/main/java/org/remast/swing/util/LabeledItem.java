package org.remast.swing.util;


/**
 * Represents an item to display in the GUI. A {@link LabeledItem} consists 
 * of an item object and a label for that object.
 * @author remast
 * @param <E> the type of the item object
 */
public class LabeledItem<E extends Comparable<E>> implements Comparable<LabeledItem<E>> {

    /** The item. */
    private final E item;

    /** The label. */
    private String label;

    /**
     * Creates a new instance for the given item.
     * @param item the item (may not be null)
     */
    public LabeledItem(final E item) {
        if (item == null) {
            throw new IllegalArgumentException("Item may not be null.");
        }

        this.item = item;
    }

    /**
     * Creates a new instance for the given item and the corresponding label.
     * @param item the item (may not be null)
     * @param label the label for the item (may not be null)
     */
    public LabeledItem(final E item, final String label) {
        if (item == null || label == null) {
            throw new IllegalArgumentException("Neither item nor label may be null.");
        }

        this.item = item;
        this.label = label;
    }

    /**
     * @return the filterObject
     */
    public E getItem() {
        return item;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        if (label != null) {
            return label;
        }
        return String.valueOf(this.item);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof LabeledItem)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        final LabeledItem<E> that = (LabeledItem<E>) obj;

        return this.getItem().equals(that.getItem());
    }

    @Override
    public int hashCode() {
        return this.getItem().hashCode();
    }

    @Override
    public int compareTo(final LabeledItem<E> o) {
        return this.getItem().compareTo(o.getItem());
    }


}
