package watson.dialog.tools.editor;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;

import watson.dialog.tools.editor.pages.edit.WatsonDialogServiceEditPage;
import watson.dialog.tools.editor.pages.run.WatsonDialogServiceRunPage;
import watson.dialog.tools.exception.WatsonDialogToolsException;

/**
 *
 */
public class WatsonDialogServiceEditor extends FormEditor implements IResourceChangeListener {

  public static final String WATSON_DIALOG_TOOLS_EDITOR_ID = "watson.dialog.tools.editor.WatsonDialogServiceEditor";

  /**
   * Creates a multi-page editor example.
   */
  public WatsonDialogServiceEditor() {
    super();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
  }

  /**
   * Creates the pages of the multi-page editor.
   */
  protected void addPages() {
    try {
      addPage(new WatsonDialogServiceEditPage(this));
      addPage(new WatsonDialogServiceRunPage(this));
    } catch (PartInitException e) {
      throw new WatsonDialogToolsException(e);
    }
  }

  /**
   * The <code>MultiPageEditorPart</code> implementation of this
   * <code>IWorkbenchPart</code> method disposes all nested editors.
   * Subclasses may extend.
   */
  public void dispose() {
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    super.dispose();
  }

  /**
   * Saves the multi-page editor's document.
   */
  public void doSave(IProgressMonitor monitor) {
    commitPages(true);
    editorDirtyStateChanged();
  }

  public void doSaveAs() {
  }

  public void init(IEditorSite site, IEditorInput editorInput)
      throws PartInitException {
    if (!(editorInput instanceof IFileEditorInput))
      throw new PartInitException("Invalid Input: Must be IFileEditorInput");
    super.init(site, editorInput);
  }

  public boolean isSaveAsAllowed() {
    return false;
  }

  protected void pageChange(int newPageIndex) {
    super.pageChange(newPageIndex);
  }

  /**
   * Closes all project files on project close.
   */
  public void resourceChanged(final IResourceChangeEvent event) {
    if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
      Display.getDefault().asyncExec(new Runnable() {
        public void run() {
          IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
          for (int i = 0; i < pages.length; i++) {
            if (((FileEditorInput) WatsonDialogServiceEditor.this.getEditorInput()).getFile().getProject()
                .equals(event.getResource())) {
              IEditorPart editorPart = pages[i].findEditor(WatsonDialogServiceEditor.this.getEditorInput());
              pages[i].closeEditor(editorPart, true);
            }
          }
        }
      });
    }
  }
}