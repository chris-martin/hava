package edu.gatech.hava.hdt.views.solution;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.hdt.views.IHavaSolutionView;
import edu.gatech.hava.hdt.views.filter.StringFilter;
import edu.gatech.hava.hdt.views.jump.JumpAction;

/**
 * The Hava "Solution" view.  This is a tree view which displays
 * the results from the execution of a Hava script.
 */
public class HavaSolutionView extends ViewPart
        implements IHavaSolutionView {

    private final StringFilter<HReference> filter =
        new StringFilter<HReference>();

    private Label message;

    private Combo search;

    private Action sortAction;

    private SolutionTextViewer viewer;

    private JumpAction openReferenceAction;

    private final Set<String> filterHistory = new HashSet<String>();

    /** {@inheritDoc} */
    @Override
    public void setContent(final HEngine engine,
                           final Exception exception,
                           final HReference exRef) {

        HavaSolution solution = new HavaSolution(engine, exception, exRef);

        solution.setFilter(filter);

        viewer.setInput(solution);

        viewer.refresh();

        clearHistory();

    }

    private static Label createFilterLabel(final Composite parent) {

        final Label label = new Label(parent, SWT.NONE);

        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END,
                GridData.VERTICAL_ALIGN_CENTER, false, false, 1, 1));

        label.setText("Filter:");

        label.setToolTipText("Filter report contents by whether or not "
                + "they contain a particular string of characters.");

        return label;

    }

    private static Combo createFilterField(final Composite parent) {

        final Combo field = new Combo(parent, SWT.SINGLE | SWT.BORDER);

        field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return field;

    }

    private static ScrolledComposite createScrollRegion(final Composite parent) {

        final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        sc.setLayout(new FillLayout());

        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 2;
        sc.setLayoutData(gridData);

        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.pack(false);

        return sc;

    }

    private static Label createMessageLabel(final Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 2;
        label.setLayoutData(data);
        return label;
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     *
     * @param parent The view component that owns these controls.
     */
    public void createPartControl(final Composite parent) {

        parent.setLayout(new GridLayout(2, false));

        message = createMessageLabel(parent);

        createFilterLabel(parent);
        search = createFilterField(parent);

        final ScrolledComposite sc = createScrollRegion(parent);

        final Composite child = new Composite(sc, SWT.NONE);
        sc.setContent(child);
        child.setLayout(new FillLayout());

        viewer = new SolutionTextViewer(child);

        viewer.setDocument(new ReportDocument());
        viewer.setEditable(false);
        viewer.getTextWidget().setFont(JFaceResources.getTextFont());
        sc.setMinSize(child.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        //add searches to history on blur, experimental UI concept
        search.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(final FocusEvent e) {
            }

            @Override
            public void focusLost(final FocusEvent e) {
                if (!search.getText().equals("")) {
                    addToHistory(search.getText());
                }
            }

        });

        //filter every time search is changed
        search.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                filter.setSearch(search.getText());
                viewer.refresh();
            }
        });

        // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(
                viewer.getControl(), "hava_dev_toolkit.viewer");

        makeActions();

        hookContextMenu();

        hookDoubleClickAction();

        final IActionBars bars = getViewSite().getActionBars();
        contributeToActionBars(bars);

    }

    private void hookDoubleClickAction() {

/*
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                openReferenceAction.run();
            }
        });
*/

    }

    private void hookContextMenu() {

        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                HavaSolutionView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);

    }

    private void fillContextMenu(final IMenuManager manager) {

        if (openReferenceAction.getReference() != null) {
            manager.add(openReferenceAction);
        }

        // Other plug-ins can contribute their actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

    }

    private void contributeToActionBars(final IActionBars bars) {

        final IToolBarManager toolBarManager = bars.getToolBarManager();
        fillLocalToolBar(toolBarManager);

    }

    private void fillLocalToolBar(final IToolBarManager manager) {

        manager.add(sortAction);

    }
/*
    private JumpAction createJumpAction() {

        return new SolutionJumpAction(viewer);

    }
*/
    private void makeActions() {

        sortAction = new SolutionSortAction(viewer);

    }

    /**
     * Asks this part to take focus within the workbench.
     */
    public void setFocus() {

        viewer.getControl().setFocus();

    }

    private void addToHistory(final String histItem) {

        if (!filterHistory.contains(histItem)) {
            filterHistory.add(histItem);
            search.add(histItem);
        }

    }

    private void clearHistory() {

        filterHistory.clear();
        search.removeAll();

    }

    private void setMessage(final String messageText) {

        if (message != null) {

            final String messageTextToSet;

            if (messageText == null) {
                messageTextToSet = "";
            } else {
                messageTextToSet = messageText;
            }

            message.setText(messageTextToSet);

        }

    }

    @Override
    public void setContent(final String fileID,
                           final HEngine engine,
                           final Exception exception,
                           final HReference reference) {

        setMessage(fileID);
        setContent(engine, exception, reference);

    }

}
