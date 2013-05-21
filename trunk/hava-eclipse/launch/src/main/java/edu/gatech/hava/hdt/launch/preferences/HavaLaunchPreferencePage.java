package edu.gatech.hava.hdt.launch.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.gatech.hava.hdt.launch.HavaLaunchPlugin;

/**
 * Preference page for running Hava code.
 */
public class HavaLaunchPreferencePage
        extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    public HavaLaunchPreferencePage() {

        super(GRID);

        setPreferenceStore(HavaLaunchPlugin.getDefault().getPreferenceStore());

        setDescription("Hava launch preferences");

    }

    private IntegerFieldEditor createMaxMemoryFieldEditor() {

        final String name = PreferenceConstants.P_MAX_MEMORY;
        final String labelText = "&Maximum percentage memory used";
        final Composite parent = getFieldEditorParent();

        final IntegerFieldEditor fieldEditor =
            new IntegerFieldEditor(name, labelText, parent);

        final int min = 0;
        final int max = 100;

        fieldEditor.setValidRange(min, max);

        return fieldEditor;

    }

    /** {@inheritDoc} */
    @Override
    public void createFieldEditors() {

        addField(createMaxMemoryFieldEditor());

    }

    @Override
    public void init(final IWorkbench workbench) {

    }

}
