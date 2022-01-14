package org.remast.baralga.gui.dialogs;

import com.google.common.base.Strings;
import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXHyperlink;
import org.joda.time.DateTime;
import org.remast.baralga.gui.settings.ApplicationSettings;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.Project;
import org.remast.baralga.repository.BaralgaRestRepository;
import org.remast.swing.action.OpenBrowserAction;
import org.remast.text.SmartTimeFormat;
import org.remast.util.TextResourceBundle;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Dialog for login in multiuser mode.
 *
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class LoginDialog extends JDialog {

    /**
     * The bundle for internationalized texts.
     */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(LoginDialog.class);

    // ------------------------------------------------
    // Labels
    // ------------------------------------------------

    private final JLabel serverLabel = new JLabel(textBundle.textFor("LoginDialog.ServerLabel")); //$NON-NLS-1$

    private final JXHyperlink hyperlinkServer = new JXHyperlink(new OpenBrowserAction(ApplicationSettings.instance().getBackendURL())); //$NON-NLS-1$

    private final JXHyperlink hyperlinkSignup = new JXHyperlink(
            new OpenBrowserAction(
                    textBundle.textFor("LoginDialog.SignupLabel"),
                    ApplicationSettings.instance().getBackendURL() + "/signup") //$NON-NLS-1$
    );

    /**
     * Label for username.
     */
    private final JLabel usernameLabel = new JLabel(textBundle.textFor("LoginDialog.UsernameLabel")); //$NON-NLS-1$

    /**
     * Label for password.
     */
    private final JLabel passwordLabel = new JLabel(textBundle.textFor("LoginDialog.PasswordLabel")); //$NON-NLS-1$;


    // ------------------------------------------------
    // Login components
    // ------------------------------------------------

    /**
     * Button to submit the login.
     */
    private JButton submitLoginButton = null;

    /**
     * Username for Login.
     */
    private JTextField usernameField = null;

    /**
     * Password for Login.
     */
    private JPasswordField passwordField = null;


    // ------------------------------------------------
    // Dialog state
    // ------------------------------------------------

    private boolean aborted = true;

    /**
     * Create a new dialog.
     *
     * @param owner
     */
    public LoginDialog(final Frame owner) {
        super(owner);
        setTitle(textBundle.textFor("LoginDialog.Title")); //$NON-NLS-1$

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());

        this.setSize(300, 175);
        this.setModal(true);

        initializeLayout();

        this.getRootPane().setDefaultButton(submitLoginButton);

        readFromModel();
    }

    private void readFromModel() {
        usernameField.setText(UserSettings.instance().getUser());
    }

    private void initializeLayout() {
        final double border = 5;
        final double[][] size = {
                {border, TableLayout.PREFERRED, border, TableLayout.FILL, border}, // Columns
                {border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED,
                        border, TableLayout.PREFERRED, border, TableLayout.FILL, border, TableLayout.PREFERRED, border}}; // Rows

        final TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);

        this.add(serverLabel, "1, 1");
        this.add(hyperlinkServer, "3, 1");

        this.add(usernameLabel, "1, 5");
        this.add(getUsernameField(), "3, 5");

        this.add(passwordLabel, "1, 7");
        this.add(getPasswordField(), "3, 7");

        this.add(hyperlinkSignup, "3, 9, 3, 9");

        this.add(getSubmitLoginButton(), "1, 11, 3, 11");
    }

    /**
     * This method initializes submitLoginButton.
     *
     * @return javax.swing.JButton
     */
    private JButton getSubmitLoginButton() {
        if (submitLoginButton == null) {
            submitLoginButton = new JButton();

            // Confirm with 'Enter' key
            submitLoginButton.setMnemonic(KeyEvent.VK_ENTER);
            submitLoginButton.setText(textBundle.textFor("LoginDialog.SignInLabel")); //$NON-NLS-1$

            submitLoginButton.addActionListener(event -> {
                // Validate
                if (!LoginDialog.this.validateFields()) {
                    return;
                }

                String username = getUsernameField().getText();
                String password = String.valueOf(getPasswordField().getPassword());

                UserSettings.instance().setCredentials(username, password);

                LoginDialog.this.aborted = false;

                LoginDialog.this.dispose();
            });

            submitLoginButton.setDefaultCapable(true);
        }
        return submitLoginButton;
    }

    /**
     * This method initializes usernameField.
     *
     * @return javax.swing.JTextField
     */
    private JTextField getUsernameField() {
        if (usernameField == null) {
            usernameField = new JTextField();
        }
        return usernameField;
    }

    /**
     * This method initializes passwordField.
     *
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getPasswordField() {
        if (passwordField == null) {
            passwordField = new JPasswordField();
        }
        return passwordField;
    }

    public boolean wasAborted() {
        return aborted;
    }

    /**
     * Validates the field to ensure that the entered data is valid.
     *
     * @return <code>true</code> when valid otherwise <code>false</code>
     */
    public boolean validateFields() {
        String username = getUsernameField().getText();
        if (Strings.isNullOrEmpty(getUsernameField().getText())) {
            return false;
        }

        String password = String.valueOf(getPasswordField().getPassword());
        if (Strings.isNullOrEmpty(password)) {
            return false;
        }

        BaralgaRestRepository repository = new BaralgaRestRepository(
                ApplicationSettings.instance().getBackendURL(),
                username,
                password
        );
        repository.initialize();

        boolean loginSuccessful = repository.verifyLogin();
        if (!loginSuccessful) {
            JOptionPane.showMessageDialog(
                    null,
                    textBundle.textFor("LoginDialog.LoginFailed.Message"),  //$NON-NLS-1$
                    textBundle.textFor("LoginDialog.LoginFailed.Title"),  //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return loginSuccessful;
    }

}
