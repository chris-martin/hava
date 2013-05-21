package edu.gatech.hava.hdt.views.jump;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import edu.gatech.hava.hdt.ui.URLInput;
import edu.gatech.hava.hdt.views.image.IconConfig;
import edu.gatech.hava.hdt.views.internal.HavaViewPlugin;

/**
 * An action which opens code an editor and highlights the code
 * corresponding to some selected element.
 */
public abstract class JumpAction extends Action {

    /**
     * Constructor - performs UI configuration.
     */
    public JumpAction() {

        setText("Open declaration");

        setToolTipText("Find the Hava code which defines this reference.");

        setImageDescriptor("jump");

    }

    /**
     * @return a Hava reference which will be used to determine
     *         which code will be jumped to
     */
    public abstract JumpLocation getReference();

    /**
     * Executes the jump action, bringing a Hava editor component into
     * focus with relevant code highlighted.
     */
    @Override
    public void run() {

        final JumpLocation location = getReference();

        if (location != null) {
            jumpToReference(location);
        }

    }

    private void setImageDescriptor(final String id) {

        final HavaViewPlugin viewPlugin = HavaViewPlugin.getDefault();

        setImageDescriptor(viewPlugin.getImageDescriptor(new IconConfig(id)));

    }

    private void jumpToReference(final JumpLocation location) {

        final String address = location.getFile();
        final IPath path = new Path(address);
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

        try {
            if (address.startsWith("http://")
                    || address.startsWith("file://")) {
                jumpTo(new URLInput(address, HavaViewPlugin.PLUGIN_ID), location.getLine());
            } else {
                jumpTo(file, location.getLine());
            }
        } catch (final CoreException e) {
            ;
        } catch (final MalformedURLException e) {
            ;
        }

    }

    /**
     * @param file the file to jump into
     * @param beginLine the index of a line of the file to highlight
     */
    private void jumpTo(final IFile file, final int beginLine)
            throws CoreException {

        final IWorkbenchPage workbenchPage = getWorkbenchPage();

        // Add a marker to the given file
        final IMarker marker = createMarker(file, beginLine);

        // Open the editor, using the marker
        final AbstractTextEditor editor = (AbstractTextEditor)
            IDE.openEditor(workbenchPage, marker);

        // Select and reveal the given line
        selectAndRevealLine(editor, beginLine);

        // Delete the marker
        marker.delete();

        workbenchPage.activate(editor);

    }

    /**
     * @param file the file to jump into
     * @param beginLine the index of a line of the file to highlight
     */
    private void jumpTo(final IStorageEditorInput input, final int beginLine)
            throws CoreException {

        final IWorkbenchPage page = getWorkbenchPage();

        // Open the editor, using the marker
        final AbstractTextEditor editor = (AbstractTextEditor)
            page.openEditor(input, "edu.gatech.hava.hdt.editors.havaEditor",
                    true, IWorkbenchPage.MATCH_INPUT);

        // Select and reveal the given line
        selectAndRevealLine(editor, beginLine);

        page.activate(editor);

    }

    private IMarker createMarker(final IFile file,
                                 final int beginLine)
            throws CoreException {

        final Map<String, Integer> map = new HashMap<String, Integer>();
        map.put(IMarker.LINE_NUMBER, new Integer(beginLine));

        final IMarker marker = file.createMarker(IMarker.TEXT);
        marker.setAttributes(map);

        return marker;

    }

    private void selectAndRevealLine(final AbstractTextEditor editor,
                                     final int line) {

        final IDocumentProvider documentProvider = editor.getDocumentProvider();
        final IEditorInput editorInput = editor.getEditorInput();
        final IDocument document = documentProvider.getDocument(editorInput);

        try {

            final int start = document.getLineOffset(line - 1);
            final int length = document.getLineLength(line - 1) - 1;

            editor.selectAndReveal(start, length);

        } catch (final BadLocationException e) {
            ;
        }

    }

    private IWorkbenchPage getWorkbenchPage() {

        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
        final IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

        return workbenchPage;

    }

}
