package watson.dialog.tools.editor.pages.edit;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import watson.dialog.tools.model.domain.CompositeNode;
import watson.dialog.tools.model.domain.Flow;
import watson.dialog.tools.model.domain.FolderNode;
import watson.dialog.tools.model.domain.IType.SelectionType;

public class FolderNodeDetailsPage extends ActionSectionHolderDetailsPage {

  /** Section title */
  private static final String FOLDER_SECTION_TITLE = "Folder";

  /** FolderNode */
  private FolderNode folderNode;

  /** Page Widgets */
  private Label idValueLabel;
  private Text commentText;
  private Text labelText;
  private Button isOfflineCheckButton;
  private Button seqTypeRadio;
  private Button randomTypeRadio;

  /** Listener */
  private CommentTextListener commentTextListener = new CommentTextListener();
  private LabelTextListener labelTextListener = new LabelTextListener();
  private IsOfflineCheckButtonListener isOfflineCheckButtonListener = new IsOfflineCheckButtonListener();
  private SelectionTypeRadioButtonListener selectionTypeRadioButtonListener = new SelectionTypeRadioButtonListener();

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // Folder Section
    createFolderSection(parent, toolkit);

    // Action Section
    WidgetsFactory.createActionSection(parent, toolkit, this);
  }

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createFolderSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(FOLDER_SECTION_TITLE);

    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    toolkit.createLabel(composite, "Id:");
    idValueLabel = toolkit.createLabel(composite, "");

    toolkit.createLabel(composite, "Comment:");
    commentText = toolkit.createText(composite, "");
    commentText.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    toolkit.createLabel(composite, "Qualifier:");

    Composite checkComposite = toolkit.createComposite(composite);
    checkComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
    isOfflineCheckButton = toolkit.createButton(checkComposite, "IsOffline", SWT.CHECK);

    toolkit.createLabel(composite, "SelectionType:");
    Composite selectionTypeComposite = toolkit.createComposite(composite);
    selectionTypeComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
    seqTypeRadio = toolkit.createButton(selectionTypeComposite, SelectionType.SEQUENTIAL.value(), SWT.RADIO);
    randomTypeRadio = toolkit.createButton(selectionTypeComposite, SelectionType.RANDOM.value(), SWT.RADIO);

    toolkit.createLabel(composite, "Label:");
    labelText = toolkit.createText(composite, "");
    labelText.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    section.setClient(composite);
  }

  @Override
  public void selectionChanged(IFormPart part, ISelection selection) {
    masterPart = (EditPageMasterSectionPart) part;

    folderNode = (FolderNode) ((IStructuredSelection) selection).getFirstElement();
    selectedAction = null;
    removeWidgetsListeners();
    clearPage();

    // id
    String id = folderNode.getId();
    if (id != null) {
      idValueLabel.setText(id);
      idValueLabel.getParent().layout();
    }

    // comment
    String comment = folderNode.getComment();
    if (comment != null) {
      commentText.setText(comment);
    }

    // isOffline
    isOfflineCheckButton.setSelection(folderNode.getIsOffline());

    // selectionType
    SelectionType selectionType = folderNode.getSelectionType();
    if (selectionType != null) {
      if (selectionType == SelectionType.SEQUENTIAL) {
        seqTypeRadio.setSelection(true);
      }
      if (selectionType == SelectionType.RANDOM) {
        randomTypeRadio.setSelection(true);
      }
    }

    // label
    String label = folderNode.getLabel();
    if (label != null) {
      labelText.setText(label);
    }

    addWidgetsListeners();
  }

  @Override
  public void clearPage() {
    idValueLabel.setText("");
    commentText.setText("");
    isOfflineCheckButton.setSelection(false);
    seqTypeRadio.setSelection(false);
    randomTypeRadio.setSelection(false);
    labelText.setText("");
    actionTableViewer.setInput(folderNode.getActions());

    isOfflineCheckButton.setEnabled(true);
    seqTypeRadio.setEnabled(true);
    randomTypeRadio.setEnabled(true);
    labelText.setEnabled(true);
    actionSection.setVisible(true);

    if (folderNode.getParent() instanceof Flow) {
      String label = folderNode.getLabel();
      if (FolderNode.MAIN_FOLDER_LABEL.equals(label) || FolderNode.LIBRARY_FOLDER_LABEL.equals(label)
          || FolderNode.GLOBAL_FOLDER_LABEL.equals(label)) {
        isOfflineCheckButton.setEnabled(false);
        labelText.setEnabled(false);
      }
    }
    if (folderNode.isChildOfConceptsFolder()) {
      isOfflineCheckButton.setEnabled(false);
      seqTypeRadio.setEnabled(false);
      randomTypeRadio.setEnabled(false);
      labelText.setEnabled(false);
      actionSection.setVisible(false);
    }
  }

  protected void addWidgetsListeners() {
    commentText.addModifyListener(commentTextListener);
    isOfflineCheckButton.addSelectionListener(isOfflineCheckButtonListener);
    seqTypeRadio.addSelectionListener(selectionTypeRadioButtonListener);
    randomTypeRadio.addSelectionListener(selectionTypeRadioButtonListener);
    labelText.addModifyListener(labelTextListener);
  }

  protected void removeWidgetsListeners() {
    commentText.removeModifyListener(commentTextListener);
    isOfflineCheckButton.removeSelectionListener(isOfflineCheckButtonListener);
    seqTypeRadio.removeSelectionListener(selectionTypeRadioButtonListener);
    randomTypeRadio.removeSelectionListener(selectionTypeRadioButtonListener);
    labelText.removeModifyListener(labelTextListener);
  }

  private class CommentTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      folderNode.setComment(commentText.getText());
      masterPart.markDirty();
    }
  }

  private class LabelTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      folderNode.setLabel(labelText.getText());
      masterPart.getDialogTreeViewer().refresh();
      masterPart.markDirty();
    }
  }

  private class IsOfflineCheckButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      folderNode.setIsOffline(isOfflineCheckButton.getSelection());
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  private class SelectionTypeRadioButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      String selectionType = ((Button) e.getSource()).getText();
      folderNode.setSelectionType(SelectionType.fromValue(selectionType));
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  @Override
  public CompositeNode<?> getActionHolder() {
    return folderNode;
  }
}
