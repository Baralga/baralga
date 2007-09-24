package org.remast.baralga.model.lists;

import org.apache.commons.lang.builder.EqualsBuilder;

public class FilterItem<E> {
    
    private E item;
    
    private String label;
    
    /**
     * @return the filterObject
     */
    public E getItem() {
        return item;
    }

    /**
     * @param filterObject the filterObject to set
     */
    public void setItem(E filterObject) {
        this.item = filterObject;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public FilterItem(E filterObject) {
        this.item = filterObject;
    }

    public FilterItem(E filterObject, String label) {
        this.item = filterObject;
        this.label = label;
    }
    
    
    @Override
    public String toString() {
        if(label != null)
            return label;
        
        return String.valueOf(this.item);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj != null
                && (obj instanceof FilterItem)) {
            FilterItem<E> that = (FilterItem<E>) obj;
            
            final EqualsBuilder eqBuilder = new EqualsBuilder();
            eqBuilder.append(this.getItem(), that.getItem());
            return eqBuilder.isEquals();
        }
        return false;
    }

}
