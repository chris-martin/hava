package edu.gatech.hava.hdt.ui.wizards;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.gatech.hava.hdt.HavaDevToolkit;

public class OpenURLInputDialog extends Dialog {

    private String title, message, input;
    private Combo selection;
    private IInputValidator validator;
    private Text errorText;
    boolean isValid = true;

    private String[] history;

    public String DIALOG_SECTION = "OpenURLInputDialog", HISTORY_SETTINGS_KEY = "URLHistory";

    public OpenURLInputDialog(final Shell parentShell,
                              final String dialogTitle,
                              final String dialogMessage) {
        super(parentShell);

        title = dialogTitle;
        message = dialogMessage;

        validator = new URLValidator();

        //load past dialog use
        IDialogSettings havaSettings = HavaDevToolkit.getDefault().getDialogSettings();
        IDialogSettings openURLSettings = havaSettings.getSection(DIALOG_SECTION);

        if(openURLSettings == null)
            openURLSettings = havaSettings.addNewSection(DIALOG_SECTION);

        setDialogSettings(openURLSettings);
    }

    public void setDialogSettings(IDialogSettings settings) {
        String[] urlHistory = settings.getArray(HISTORY_SETTINGS_KEY);
        if(urlHistory != null)
            history = urlHistory;
        else
            history = new String[]{};
    }

    /**
     * Overridden to set the dialog's title.
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }


    public String getValue() {
        String val = input;
        if (isValid) {
            if (URLValidator.needsProtocol(val)) {
                val = "http://" + val;
            }
            if(! val.endsWith(".hava")) {
                val += ".hava";
            }
            return val;
        }
        return "";
    }

    @Override
    protected void okPressed() {
        //save the URL in the history
        String url = getValue();
        int i;
        for (i = 0; i<history.length; i++)
            if(history[i].equals(url))
                break;
        if(i == history.length)
        {
            IDialogSettings havaSettings = HavaDevToolkit.getDefault().getDialogSettings();
            IDialogSettings openURLSettings = havaSettings.getSection(DIALOG_SECTION);

            if(openURLSettings == null)
                openURLSettings = havaSettings.addNewSection(DIALOG_SECTION);

            String[] newHistory = Arrays.copyOf(history, history.length + 1);
            newHistory[newHistory.length-1] = new String(url);

            openURLSettings.put(HISTORY_SETTINGS_KEY, newHistory);
        }

        super.okPressed();
    }

    @Override
    /**
     * Mostly adapted from InputDialog.createDialogArea, since the only difference in functionality
     * necessary was the use of a combo box instead of a text box.
     */
    public Control createDialogArea(Composite parent) {
        Composite ret = (Composite) super.createDialogArea(parent);

        ret.setLayout(new GridLayout(1, false));

        //the main dialog message
        if (message != null) {
            Label messageLabel = new Label(ret, SWT.WRAP);
            messageLabel.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            messageLabel.setLayoutData(data);
            messageLabel.setFont(parent.getFont());
        }

        //set up the combo box and validation
        selection = new Combo(ret, SWT.SINGLE | SWT.BORDER);
        selection.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        selection.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateInput();
            }
        });
        //fill in the URL history
        if (history != null)
            for (String url : history )
                selection.add(url);

        //set up our error message notification at the bottom
        errorText = new Text(ret, SWT.READ_ONLY | SWT.WRAP);
        errorText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        errorText.setBackground(errorText.getDisplay()
                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        applyDialogFont(ret);
        return ret;
    }

    protected void validateInput() {
        String err = null;

        err = validator.isValid(selection.getText());
        isValid = err == null;

        input = selection.getText();
        setErrorMessage(err);
    }

    public void setErrorMessage(String err) {
        if (errorText != null && !errorText.isDisposed()) {
            errorText.setText(err == null ? " \n " : err);

            boolean hasError = err != null &&
                (StringConverter.removeWhiteSpaces(err)).length() > 0;
            errorText.setEnabled(hasError);
            errorText.setVisible(hasError);
            errorText.getParent().update();

            //get the ok button
            Control button = getButton(IDialogConstants.OK_ID);
            if (button != null) {
                button.setEnabled(err == null);
            }
        }
    }
}

class URLValidator implements IInputValidator {

    /** Regular expression based on allowable URI scheme composition - RFC 3986. */
    private static final String URL_REGEX = "^[a-zA-Z]([a-zA-Z]|\\d|\\+|-|\\.)*:";

    public static boolean needsProtocol(final String url) {

        try {

            @SuppressWarnings("unused")
            URL test = new URL(url);

        } catch (final MalformedURLException e) {

            if (e.getMessage().startsWith("no protocol")){
                return true;
            } else if (!e.getMessage().matches(URL_REGEX)) {
                return true;
            }

        }

        return false;
    }

    @Override
    public String isValid(final String newText) {

        try {
            @SuppressWarnings("unused")
            URL url = new URL(newText);
        } catch (final MalformedURLException e) {
            if (!needsProtocol(newText)) {
                return "Invalid URL.";
            }
        }

        return null;
    }
}
