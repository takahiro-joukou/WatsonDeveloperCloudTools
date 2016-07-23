package watson.dialog.tools.editor.pages.edit;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

public abstract class EditDetailsPage implements IDetailsPage {

  /** managedForm */
  protected IManagedForm managedForm;

  /** Master Part */
  protected EditPageMasterSectionPart masterPart;

  @Override
  public void initialize(IManagedForm form) {
    managedForm = form;
  }

  @Override
  public void dispose() {
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public void commit(boolean onSave) {
  }

  @Override
  public boolean setFormInput(Object input) {
    return false;
  }

  @Override
  public void setFocus() {
  }

  @Override
  public boolean isStale() {
    return false;
  }

  @Override
  public void refresh() {
  }

  @Override
  public void selectionChanged(IFormPart part, ISelection selection) {
  }

  @Override
  public void createContents(Composite parent) {
  }

  public void markDirty() {
    masterPart.markDirty();
  }

  protected abstract void clearPage();
}
