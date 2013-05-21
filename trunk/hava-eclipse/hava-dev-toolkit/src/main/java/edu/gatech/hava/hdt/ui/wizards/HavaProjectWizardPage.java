package edu.gatech.hava.hdt.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class HavaProjectWizardPage extends WizardPage {

    private Text projectNameText;
    private Label nameLabel;


    public HavaProjectWizardPage() {

        super("havaProject");

        setTitle("");
        setDescription("Enter a project name.");
        //ResourceNavigator nav;

    }

    @Override
    public void createControl(final Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(GridData.FILL_BOTH);

        final GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        nameLabel = new Label(container, SWT.NONE);
        nameLabel.setText("&Project name:");
        nameLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END,
                GridData.VERTICAL_ALIGN_CENTER, false, false, 1, 1));

        projectNameText = new Text(container, SWT.SINGLE | SWT.BORDER);
        projectNameText.addModifyListener(new ModifyListener() {
           @Override
            public void modifyText(final ModifyEvent e) {
                updatePageComplete();
            }
        });
        projectNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        /*Group contentsGroup = new Group(container, SWT.NONE);
        contentsGroup.setText("Contents");
        */

        setControl(container);

    }

    private void updatePageComplete() {

        IStatus status = ResourcesPlugin.getWorkspace().validateName(projectNameText.getText(),
                IResource.PROJECT);
        if (status.isOK()) {
            setPageComplete(true);
            setMessage(null);
        } else {
            setPageComplete(false);
            setMessage(status.getMessage());
        }

    }

    public String getValue() {

        if (isPageComplete()) {
            return projectNameText.getText();
        } else {
            return null;
        }

    }
}
