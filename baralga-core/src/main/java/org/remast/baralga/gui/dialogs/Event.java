package org.remast.baralga.gui.dialogs;

public abstract class Event {

    public abstract int getType();
    public abstract void fireTableDataChanged();
}

// Other event classes can also implement the Event interface
