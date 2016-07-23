package watson.dialog.tools.editor.pages.edit;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.model.domain.IType.VarFolderType;
import watson.dialog.tools.model.domain.VarFolder;

public class VarFolderDetailsPage extends EditDetailsPage {

  /** Section title */
  private static final String VAR_FOLDER_SECTION_TITLE = "Var";

  /** VarFolder */
  private VarFolder varFolder;

  /** Page Widgets */
  private Label idValueLabel;
  private Button constTypeRadioButton;
  private Button varTypeRadioButton;
  private Text nameText;

  /** Listener */
  private NameTextListener nameTextListener = new NameTextListener();

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // Entities Section
    createVarFolderSection(parent, toolkit);
  }

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createVarFolderSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(VAR_FOLDER_SECTION_TITLE);

    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    toolkit.createLabel(composite, "Id:");
    idValueLabel = toolkit.createLabel(composite, "");

    toolkit.createLabel(composite, "Type:");
    Composite typeComposite = toolkit.createComposite(composite);
    typeComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
    constTypeRadioButton = toolkit.createButton(typeComposite, VarFolderType.CONST.value(), SWT.RADIO);
    varTypeRadioButton = toolkit.createButton(typeComposite, VarFolderType.VAR.value(), SWT.RADIO);
    constTypeRadioButton.setEnabled(false);
    varTypeRadioButton.setEnabled(false);

    toolkit.createLabel(composite, "Name:");
    nameText = toolkit.createText(composite, "");
    nameText.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    section.setClient(composite);
  }

  @Override
  public void selectionChanged(IFormPart part, ISelection selection) {
    masterPart = (EditPageMasterSectionPart) part;

    varFolder = (VarFolder) ((IStructuredSelection) selection).getFirstElement();
    removeWidgetsListeners();
    clearPage();

    // id
    String id = varFolder.getId();
    if (id != null) {
      idValueLabel.setText(id);
      idValueLabel.getParent().layout();
    }

    // type
    VarFolderType type = varFolder.getType();
    if (type == VarFolderType.CONST) {
      constTypeRadioButton.setSelection(true);
    }
    if (type == VarFolderType.VAR) {
      varTypeRadioButton.setSelection(true);
    }

    // name
    String name = varFolder.getName();
    if (name != null) {
      nameText.setText(name);
    }

    addWidgetsListeners();
  }

  @Override
  protected void clearPage() {
    idValueLabel.setText("");
    constTypeRadioButton.setSelection(false);
    varTypeRadioButton.setSelection(false);
    nameText.setText("");
  }

  protected void addWidgetsListeners() {
    nameText.addModifyListener(nameTextListener);
  }

  protected void removeWidgetsListeners() {
    nameText.removeModifyListener(nameTextListener);
  }

  private class NameTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      varFolder.setName(nameText.getText());
      masterPart.getDialogTreeViewer().refresh();
      masterPart.markDirty();
    }
  }
}
