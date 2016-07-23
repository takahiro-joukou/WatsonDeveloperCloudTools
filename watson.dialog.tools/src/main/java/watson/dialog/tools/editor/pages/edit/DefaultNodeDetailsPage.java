package watson.dialog.tools.editor.pages.edit;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.model.domain.CompositeNode;
import watson.dialog.tools.model.domain.DefaultNode;

public class DefaultNodeDetailsPage extends ActionSectionHolderDetailsPage {

  /** Section title */
  private static final String DEFAUT_SECTION_TITLE = "Default";

  /** DefaultNode */
  private DefaultNode defaultNode;

  /** Page Widgets */
  private Label idValueLabel;
  private Button isOfflineCheckButton;;

  /** Listener */
  private IsOfflineCheckButtonListener isOfflineCheckButtonListener = new IsOfflineCheckButtonListener();

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // Default Section
    createDefaultSection(parent, toolkit);

    // Action Section
    WidgetsFactory.createActionSection(parent, toolkit, this);
  }

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createDefaultSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(DEFAUT_SECTION_TITLE);

    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    toolkit.createLabel(composite, "Id:");
    idValueLabel = toolkit.createLabel(composite, "");

    toolkit.createLabel(composite, "Qualifier:");

    Composite checkComposite = toolkit.createComposite(composite);
    checkComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
    isOfflineCheckButton = toolkit.createButton(checkComposite, "IsOffline", SWT.CHECK);

    section.setClient(composite);
  }

  @Override
  public void selectionChanged(IFormPart part, ISelection selection) {
    masterPart = (EditPageMasterSectionPart) part;

    defaultNode = (DefaultNode) ((IStructuredSelection) selection).getFirstElement();
    selectedAction = null;
    removeWidgetsListeners();
    clearPage();

    // id
    String id = defaultNode.getId();
    if (id != null) {
      idValueLabel.setText(id);
      idValueLabel.getParent().layout();
    }

    // isOffline
    isOfflineCheckButton.setSelection(defaultNode.getIsOffline());

    addWidgetsListeners();
  }

  @Override
  public void clearPage() {
    idValueLabel.setText("");
    isOfflineCheckButton.setSelection(false);
    actionTableViewer.setInput(defaultNode.getActions());
  }

  protected void addWidgetsListeners() {
    isOfflineCheckButton.addSelectionListener(isOfflineCheckButtonListener);
  }

  protected void removeWidgetsListeners() {
    isOfflineCheckButton.removeSelectionListener(isOfflineCheckButtonListener);
  }

  private class IsOfflineCheckButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      defaultNode.setIsOffline(isOfflineCheckButton.getSelection());
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  @Override
  public CompositeNode<?> getActionHolder() {
    return defaultNode;
  }
}
