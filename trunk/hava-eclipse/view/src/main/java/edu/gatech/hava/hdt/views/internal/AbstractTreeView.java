package edu.gatech.hava.hdt.views.internal;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

/**
 * A generic {@link ViewPart} with a {@link TreeViewer}.
 */
public abstract class AbstractTreeView extends ViewPart {

    /**
     * The tree viewer.
     */
    protected TreeViewer viewer;

    /**
     * Scrolls the tree viewer to the bottom.
     */
    protected void scrollToBottom() {

        final Tree tree = viewer.getTree();
        final ScrollBar bar = tree.getVerticalBar();

        bar.setSelection(bar.getMaximum());

    }

    /**
     * Initializes any actions.
     */
    protected void makeActions() { }

    /**
     * Initializes the context menu for the tree viewer.
     */
    protected void hookContextMenu() {

        final MenuManager menuManager = new MenuManager("#PopupMenu");
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                AbstractTreeView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuManager.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuManager, viewer);

    }

    /**
     * Fills the context menu for the tree viewer.
     *
     * @param manager the manager for the context menu
     */
    protected void fillContextMenu(final IMenuManager manager) {

        // Other plug-ins can contribute their actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

    }

    /**
     * Initializes the tree viewer's double-click action.
     */
    protected void hookDoubleClickAction() {

        viewer.addDoubleClickListener(new IDoubleClickListener() {
            /**
             * Notifies of a double click.
             *
             * @param event event object describing the double-click
             */
            @Override
            public void doubleClick(final DoubleClickEvent event) {
                treeDoubleClick(event);
            }
        });

    }

    /**
     * Notifies of a double click.
     *
     * @param event event object describing the double-click
     */
    protected void treeDoubleClick(final DoubleClickEvent event) { }

    /**
     * Initializes action bars, including the local toolbar.
     *
     * @param bars the action bars object
     */
    protected void contributeToActionBars(final IActionBars bars) {

        final IToolBarManager toolBarManager = bars.getToolBarManager();
        fillLocalToolBar(toolBarManager);

    }

    /**
     * Initializes the local toolbar.
     *
     * @param manager the toolbar manager.
     */
    protected void fillLocalToolBar(final IToolBarManager manager) { }

    /**
     * Asks this part to take focus within the workbench.
     */
    public void setFocus() {

        viewer.getControl().setFocus();

    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     *
     * @param parent The view component that owns these controls.
     */
    public void createPartControl(final Composite parent) {

        makeActions();

        hookContextMenu();

        hookDoubleClickAction();

        final IActionBars bars = getViewSite().getActionBars();
        contributeToActionBars(bars);

    }

}
