package edu.gatech.hava.hdt.views.debug;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import edu.gatech.hava.debug.HDebugReference;
import edu.gatech.hava.debug.IDebugNodeProvider;
import edu.gatech.hava.hdt.views.IHavaDebugView;
import edu.gatech.hava.hdt.views.internal.AbstractTreeView;
import edu.gatech.hava.hdt.views.jump.JumpAction;

/**
 * Implementation of {@link IHavaDebugView} using a {@link TreeViewer}.
 */
public class DebugView extends AbstractTreeView
        implements IHavaDebugView {

    private DebugViewerFilter treeFilter;
    private Label message;
    private Combo search;
    private final Set<String> filterHistory = new HashSet<String>();
    private DebugTreeContentProvider contentProvider;
    private DebugTreeLabelProvider labelProvider;
    private Action sortAction;
    private final Action copyAction = new CopyAction();
    private JumpAction openReferenceAction;

    /** {@inheritDoc} */
    @Override
    public void setContent(final IDebugNodeProvider debugListener,
                           final Exception exception) {

        if (exception == null) {

            viewer.setInput(debugListener);

            viewer.refresh();

            if (debugListener.hasError()) {
                final HDebugReference errorVariable =
                    debugListener.getErrorVariable();
                if (errorVariable != null) {
                    expandError(errorVariable);
                }
            }

        } else {

            viewer.setInput(exception);
            viewer.refresh();

        }

        clearHistory();
    }

    private void expandError(final HDebugReference errorVariable) {

        final DebugTreeNode node = findErrorNode(errorVariable);

        if (node != null) {
            viewer.expandToLevel(node, 1);
            viewer.setSelection(new StructuredSelection(node));
        }

    }

    private DebugTreeNode findErrorNode(final HDebugReference errorVariable) {

        final Object[] elements =
            contentProvider.getElements(viewer.getInput());

        DebugTreeNode node = null;

        for (final Object element : elements) {

            node = (DebugTreeNode) element;

            if (node.getValue() == errorVariable) {
                while (node != null && !node.hasException()) {
                    contentProvider.getChildren(node);
                    node = node.getLastChild();
                }
                return node;
            }

        }

        return null;

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

    private static Label createMessageLabel(final Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 2;
        label.setLayoutData(data);
        return label;
    }

    /**
     * Creates the SWT controls for this workbench part.
     *
     * @param parent the parent control
     */
    @Override
    public void createPartControl(final Composite parent) {

        parent.setLayout(new GridLayout(2, false));

        message = createMessageLabel(parent);

        createFilterLabel(parent);
        search = createFilterField(parent);

        contentProvider = new DebugTreeContentProvider();
        labelProvider = new DebugTreeLabelProvider();

        final Tree tree = new Tree(parent, SWT.MULTI);
        final GridData treeData = new GridData(GridData.FILL_BOTH);
        treeData.horizontalSpan = 2;
        tree.setLayoutData(treeData);

        viewer = new TreeViewer(tree);

        treeFilter = new DebugViewerFilter();
        viewer.addFilter(treeFilter);
        search.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                treeFilter.setSearch(search.getText());
                viewer.refresh();
            }
        });

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

        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(labelProvider);

        initializeCopyPaste();

        // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(
                viewer.getControl(), "hava_dev_toolkit.viewer");

        super.createPartControl(parent);

    }

    /** {@inheritDoc} */
    @Override
    public void dispose() {

        super.dispose();

        clipboard.dispose();

    }

    /** {@inheritDoc} */
    @Override
    protected void fillLocalToolBar(final IToolBarManager manager) {

        manager.add(sortAction);

    }

    /** {@inheritDoc} */
    @Override
    protected void makeActions() {

        sortAction = new DebugSortAction(viewer);
        openReferenceAction = new DebugJumpAction(viewer);

        super.makeActions();

    }

    /** {@inheritDoc} */
    @Override
    protected void fillContextMenu(final IMenuManager manager) {

        int selectionSize =
            ((IStructuredSelection) viewer.getSelection()).size();

        if (selectionSize == 1
                && openReferenceAction.getReference() != null) {
            manager.add(openReferenceAction);
        }

        if (selectionSize != 0) {
            manager.add(copyAction);
        }

        super.fillContextMenu(manager);

    }

    /**
     * Runs the open reference action.
     *
     * @param event event object describing the double-click
     */
    @Override
    protected void treeDoubleClick(final DoubleClickEvent event) {

        openReferenceAction.run();

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

    private Clipboard clipboard;

    private void initializeCopyPaste() {

        final Shell shell = getSite().getShell();

        clipboard = new Clipboard(shell.getDisplay());

        IActionBars bars = getViewSite().getActionBars();

        bars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);

    }

    private class CopyAction extends Action {

        /**
         * Constructor - performs UI configuration.
         */
        public CopyAction() {

            setText("Copy");

            final ISharedImages sharedImages =
                PlatformUI.getWorkbench().getSharedImages();
            final ImageDescriptor imageDescriptor =
                sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY);
            setImageDescriptor(imageDescriptor);

        }

        @Override
        public void run() {

            final ITreeSelection selection =
                (ITreeSelection) viewer.getSelection();

            final String textData = getTextData(selection);
            final Transfer textTransfer = TextTransfer.getInstance();

            final Object[] data = new Object[] {
                    textData
            };

            final Transfer[] transfers = new Transfer[] {
                    textTransfer
            };

            clipboard.setContents(data, transfers);

        }

        private String getTextData(final ITreeSelection selection) {

            final StringBuilder builder = new StringBuilder();
            final TreePath[] paths = selection.getPaths();
            final String lineSeparator = System.getProperty("line.separator");

            for (int i = 0; i < paths.length; i++) {

                if (i != 0) {
                    builder.append(lineSeparator);
                }

                final TreePath path = paths[i];

                for (int x = path.getSegmentCount() - 1; x > 0; x--) {
                    builder.append('\t');
                }

                final Object element = path.getLastSegment();
                builder.append(labelProvider.getText(element));

            }

            return builder.toString();

        }

    }

    private void setMessage(final String message) {
        if (this.message != null) {
            if (message == null) {
                this.message.setText("");
            } else {
                this.message.setText(message);
            }
        }
    }

    @Override
    public void setContent(String fileId, IDebugNodeProvider debugListener,
                           Exception loadException) {

        setMessage(fileId);
        setContent(debugListener, loadException);

    }

}
