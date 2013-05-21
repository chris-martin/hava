package edu.gatech.hava.hdt.launch.config;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class HavaMainTab extends AbstractLaunchConfigurationTab {

    @Override
    /** {@inheritDoc} */
    public void activated(final ILaunchConfigurationWorkingCopy workingCopy) { }

    @Override
    public boolean canSave() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void createControl(final Composite parent) {

//        Composite comp = new Composite(parent, SWT.NONE);
//        comp.setLayout(new GridLayout(1, false));

//        Group radios = new Group(parent, SWT.NONE);
//        radios.setLayout(new GridLayout(1, false));
//        Button one = new Button(radios, SWT.RADIO), two = new Button(radios, SWT.RADIO),
//            three = new Button(radios, SWT.RADIO);
//        one.setText("This is button one.");
//        two.setText("This is button two.");
//        three.setText("This is button three.");
//        setControl(radios);

        Composite comp = new Composite(parent, SWT.NONE);
        setControl(comp);
        comp.setLayout(new GridLayout(2, true));
        comp.setFont(parent.getFont());

        Label test = new Label(comp, SWT.NONE);
        test.setText("TEST!");
        test.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    @Override
    /** {@inheritDoc} */
    public void deactivated(final ILaunchConfigurationWorkingCopy workingCopy) { }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public Control getControl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getErrorMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Image getImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** {@inheritDoc} */
    public String getName() {
        return "Main";
    }

    @Override
    public void initializeFrom(final ILaunchConfiguration configuration) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isValid(final ILaunchConfiguration launchConfig) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void launched(final ILaunch launch) {
        // TODO Auto-generated method stub

    }

    @Override
    public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLaunchConfigurationDialog(final ILaunchConfigurationDialog dialog) {
        // TODO Auto-generated method stub

    }

}
