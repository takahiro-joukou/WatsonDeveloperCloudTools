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
import watson.dialog.tools.model.domain.GetUserInputNode;

public class GetUserInputNodeDetailsPage extends ActionSectionHolderDetailsPage {

  /** Section title */
  private static final String GET_USER_INPUT_SECTION_TITLE = "GetUserInput";

  /** GetUserInputNode */
  private GetUserInputNode getUserInputNode;

  /** Page Widgets */
  private Label idValueLabel;
  private Text commentText;
  private Button isOfflineCheckButton;
  private Button isDNRCandidateCheckButton;

  /** Listener */
  private CommentTextListener commentTextListener = new CommentTextListener();
  private IsOfflineCheckButtonListener isOfflineCheckButtonListener = new IsOfflineCheckButtonListener();
  private IsDNRCandidateCheckButtonListener isDNRCandidateCheckButtonListener = new IsDNRCandidateCheckButtonListener();

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // GetUserInput Section
    createGetUserInputSection(parent, toolkit);

    // Action Section
    WidgetsFactory.createActionSection(parent, toolkit, this);
  }

  private void createGetUserInputSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(GET_USER_INPUT_SECTION_TITLE);

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
    isDNRCandidateCheckButton = toolkit.createButton(checkComposite, "IsDNRCandidate", SWT.CHECK);

    section.setClient(composite);
  }

  @Override
  public void selectionChanged(IFormPart part, ISelection selection) {
    masterPart = (EditPageMasterSectionPart) part;

    getUserInputNode = (GetUserInputNode) ((IStructuredSelection) selection).getFirstElement();
    selectedAction = null;
    removeWidgetsListeners();
    clearPage();

    // id
    String id = getUserInputNode.getId();
    if (id != null) {
      idValueLabel.setText(id);
      idValueLabel.getParent().layout();
    }

    // comment
    String comment = getUserInputNode.getComment();
    if (comment != null) {
      commentText.setText(comment);
    }

    // isOffline
    isOfflineCheckButton.setSelection(getUserInputNode.getIsOffline());

    // isDNRCandidate
    isDNRCandidateCheckButton.setSelection(getUserInputNode.getIsDNRCandidate());

    addWidgetsListeners();
  }

  @Override
  public void clearPage() {
    idValueLabel.setText("");
    commentText.setText("");
    isOfflineCheckButton.setSelection(false);
    isDNRCandidateCheckButton.setSelection(false);

    actionTableViewer.setInput(getUserInputNode.getActions());
  }

  private class CommentTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      getUserInputNode.setComment(commentText.getText());
      masterPart.markDirty();
    }
  }

  protected void addWidgetsListeners() {
    commentText.addModifyListener(commentTextListener);
    isOfflineCheckButton.addSelectionListener(isOfflineCheckButtonListener);
    isDNRCandidateCheckButton.addSelectionListener(isDNRCandidateCheckButtonListener);
  }

  protected void removeWidgetsListeners() {
    commentText.removeModifyListener(commentTextListener);
    isOfflineCheckButton.removeSelectionListener(isOfflineCheckButtonListener);
    isDNRCandidateCheckButton.removeSelectionListener(isDNRCandidateCheckButtonListener);
  }

  private class IsOfflineCheckButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      getUserInputNode.setIsOffline(isOfflineCheckButton.getSelection());
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  private class IsDNRCandidateCheckButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      getUserInputNode.setIsDNRCandidate(isDNRCandidateCheckButton.getSelection());
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  @Override
  public CompositeNode<?> getActionHolder() {
    return getUserInputNode;
  }
}
