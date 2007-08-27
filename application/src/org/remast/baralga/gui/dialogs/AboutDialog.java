package org.remast.baralga.gui.dialogs;

import javax.swing.JDialog;

import org.jdesktop.swingx.JXImagePanel;
import org.remast.baralga.Messages;

public class AboutDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public AboutDialog() {
        super();
        
        setTitle(Messages.getString("AboutDialog.AboutTitle")); //$NON-NLS-1$
        setModal(true);
        setResizable(false);
        
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        JXImagePanel imagePanel;
        imagePanel = new JXImagePanel(getClass().getResource("/resource/icons/ProTrack-About.png")); //$NON-NLS-1$
        this.add(imagePanel);
        
        this.setSize(327, 376);
    }
    
}
