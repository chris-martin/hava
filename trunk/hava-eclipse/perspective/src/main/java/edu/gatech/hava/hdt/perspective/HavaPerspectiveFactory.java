package edu.gatech.hava.hdt.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import edu.gatech.hava.hdt.views.IHavaDebugView;
import edu.gatech.hava.hdt.views.IHavaSolutionView;

/**
 * A perspective factory, which generates the initial page layout
 * and visible action set for the Hava perspective.
 */
public class HavaPerspectiveFactory implements IPerspectiveFactory {

    private static final float NAV_WIDTH = .25f;
    private static final float VIEW_WIDTH = .25f;

    private static final String[] ACTION_SETS = {
        "org.eclipse.debug.ui.launchActionSet"
    };

    private static final String[] PERSPECTIVES = {
        "edu.gatech.hava.hdt.perspective"
    };

    private static final String[] NEW_WIZARDS = {
        "edu.gatech.hava.hdt.newProjectWizard",
        "edu.gatech.hava.hdt.newHavaFileWizard"
    };

    private static final String PROJECT_EXPLORER_VIEW =
        "org.eclipse.ui.navigator.ProjectExplorer";

    private static final String[] VIEWS = {
        IHavaSolutionView.VIEW_ID,
        IHavaDebugView.VIEW_ID,
        PROJECT_EXPLORER_VIEW,
        "org.eclipse.ui.views.ProblemView"
    };

    /** {@inheritDoc} */
    @Override
    public void createInitialLayout(final IPageLayout layout) {

        layout.setFixed(false);

        final String editorArea = layout.getEditorArea();

        // right region
        final IFolderLayout viewRegion =
            layout.createFolder(
                "views",
                IPageLayout.RIGHT,
                1 - VIEW_WIDTH,
                editorArea);

        // left region
        final IFolderLayout navRegion =
            layout.createFolder(
                    "nav",
                    IPageLayout.LEFT,
                    NAV_WIDTH,
                    editorArea);

        // add the navigation view
        navRegion.addView(PROJECT_EXPLORER_VIEW);

        // add our solution and debug views
        viewRegion.addView(IHavaSolutionView.VIEW_ID);
        viewRegion.addView(IHavaDebugView.VIEW_ID);

        // add action sets
        for (final String id : ACTION_SETS) {
            layout.addActionSet(id);
        }

        // add perspective shortcuts
        for (final String id : PERSPECTIVES) {
            layout.addPerspectiveShortcut(id);
        }

        // add new file and project shortcuts
        for (final String id : NEW_WIZARDS) {
            layout.addNewWizardShortcut(id);
        }

        // add view shortcuts
        for (final String id : VIEWS) {
            layout.addShowViewShortcut(id);
        }

    }

}
