package watson.dialog.tools.editor.pages.edit;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.model.domain.Action;
import watson.dialog.tools.model.domain.CompositeNode;

public abstract class ActionSectionHolderDetailsPage extends EditDetailsPage {

  /** Action */
  protected Action selectedAction;

  /** Page Widgets */
  protected Section actionSection;
  protected TableViewer actionTableViewer;
  protected Button addActionButton;
  protected Button deleteActionButton;

  /**
   *
   * @return
   */
  public abstract CompositeNode<?> getActionHolder();

  /**
   *
   * @param actionSection
   */
  public void setActionSection(Section actionSection) {
    this.actionSection = actionSection;
  }

  /**
   *
   * @param actionTableViewer
   */
  public void setActionTableViewer(TableViewer actionTableViewer) {
    this.actionTableViewer = actionTableViewer;
  }

  /**
   *
   * @param action
   */
  public void setSelectedAction(Action action) {
    this.selectedAction = action;
  }

  /**
   *
   * @param button
   */
  public void setAddActionButton(Button button) {
    this.addActionButton = button;
  }

  /**
   *
   * @param button
   */
  public void setDeleteButton(Button button) {
    this.deleteActionButton = button;
  }

  /**
   *
   */
  public void notifyChangeActionSectionStatus() {
    if (selectedAction != null) {
      deleteActionButton.setEnabled(true);
    } else {
      deleteActionButton.setEnabled(false);
    }
  }
}
