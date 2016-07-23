package watson.dialog.tools.editor.pages.edit;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.model.domain.IType;
import watson.dialog.tools.model.domain.IType.VarType;
import watson.dialog.tools.model.domain.Var;

public class VarDetailsPage extends EditDetailsPage {

  /** Section title */
  private static final String VAR_SECTION_TITLE = "Var";

  /** Var Type */
  private static final String[] VAR_TYPE_ARRAY = new String[]{
    VarType.TEXT.value(),
    VarType.NUMBER.value(),
    VarType.YESNO.value(),
    VarType.DATETIME.value(),
    VarType.TAG.value(),
    VarType.CONST.value()
  };

  /** Var */
  private Var var;

  /** Page Widgets */
  private Label idValueLabel;
  private Text descriptionText;
  private Combo varTypeCombo;
  private Text nameText;
  private Text initValueText;

  /** Listener */
  private DescriptionTextListener descriptionTextListener = new DescriptionTextListener();
  private VarTypeComboListener varTypeComboListener = new VarTypeComboListener();
  private NameTextListener nameTextListener = new NameTextListener();
  private InitValueTextListener initValueTextListener = new InitValueTextListener();

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // Var Section
    createVarSection(parent, toolkit);
  }

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createVarSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(VAR_SECTION_TITLE);

    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    toolkit.createLabel(composite, "Id:");
    idValueLabel = toolkit.createLabel(composite, "");

    toolkit.createLabel(composite, "Description:");
    descriptionText = toolkit.createText(composite, "");
    descriptionText.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    toolkit.createLabel(composite, "Type:");
    varTypeCombo = new Combo(composite, SWT.READ_ONLY);
    for (String type : VAR_TYPE_ARRAY) {
      varTypeCombo.add(type);
    }

    toolkit.createLabel(composite, "Name:");
    nameText = toolkit.createText(composite, "");
    nameText.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    toolkit.createLabel(composite, "InitValue:");
    initValueText = toolkit.createText(composite, "");
    initValueText.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    section.setClient(composite);
  }

  @Override
  public void selectionChanged(IFormPart part, ISelection selection) {
    masterPart = (EditPageMasterSectionPart) part;

    var = (Var) ((IStructuredSelection) selection).getFirstElement();
    removeWidgetsListeners();
    clearPage();

    // id
    String id = var.getId();
    if (id != null) {
      idValueLabel.setText(id);
      idValueLabel.getParent().layout();
    }

    // description
    String description = var.getDescription();
    if (description != null) {
      descriptionText.setText(description);
    }

    // type
    VarType type = var.getType();
    if (type != null) {
      varTypeCombo.select(getComboIndex(type));
    }

    // name
    String name = var.getName();
    if (name != null) {
      nameText.setText(name);
    }

    // initValue
    String initValue = var.getInitValue();
    if (initValue != null) {
      initValueText.setText(initValue);
    }

    addWidgetsListeners();
  }

  private int getComboIndex(VarType type) {
    String value = type.value();
    for (int i = 0; i < VAR_TYPE_ARRAY.length; i++) {
      if (VAR_TYPE_ARRAY[i].equals(value)) {
        return i;
      }
    }
    return 0;
  }

  @Override
  public void clearPage() {
    idValueLabel.setText("");
    descriptionText.setText("");
    varTypeCombo.select(0);
    nameText.setText("");
    initValueText.setText("");
  }

  protected void addWidgetsListeners() {
    descriptionText.addModifyListener(descriptionTextListener);
    varTypeCombo.addSelectionListener(varTypeComboListener);
    nameText.addModifyListener(nameTextListener);
    initValueText.addModifyListener(initValueTextListener);
  }

  protected void removeWidgetsListeners() {
    descriptionText.removeModifyListener(descriptionTextListener);
    varTypeCombo.removeSelectionListener(varTypeComboListener);
    nameText.removeModifyListener(nameTextListener);
    initValueText.removeModifyListener(initValueTextListener);
  }

  private class DescriptionTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      var.setDescription(descriptionText.getText());
      masterPart.markDirty();
    }
  }

  private class VarTypeComboListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      var.setType(IType.VarType.fromValue(varTypeCombo.getText()));
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  private class NameTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      var.setName(nameText.getText());
      masterPart.getDialogTreeViewer().refresh();
      masterPart.markDirty();
    }
  }

  private class InitValueTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      var.setInitValue(initValueText.getText());
      masterPart.markDirty();
    }
  }
}
