package edu.gatech.hava.hdt.launch.config;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class HavaLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

    public HavaLaunchConfigurationTabGroup() {}

    @Override
    public void createTabs(final ILaunchConfigurationDialog dialog, final String mode) {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
                new HavaMainTab(),
                new CommonTab()
        };

        setTabs(tabs);
    }
}