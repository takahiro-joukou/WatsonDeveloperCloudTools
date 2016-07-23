package watson.dialog.tools.editor.pages.edit;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
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

import watson.dialog.tools.editor.dialogs.SelectFlowNodeDialog;
import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.model.domain.CompositeNode;
import watson.dialog.tools.model.domain.FlowNode;
import watson.dialog.tools.model.domain.SearchNode;

public class SearchNodeDetailsPage extends ActionSectionHolderDetailsPage {

  /** Section title */
  private static final String SEARCH_SECTION_TITLE = "Search";

  /** SearchNode */
  private SearchNode searchNode;

  /** Page Widgets */
  private Label idValueLabel;
  private Text commentText;
  private Text refText;
  private Button isOfflineCheckButton;

  /** Listener */
  private CommentTextListener commentTextListener = new CommentTextListener();
  private RefTextListener refTextListener = new RefTextListener();
  private IsOfflineCheckButtonListener isOfflineCheckButtonListener = new IsOfflineCheckButtonListener();

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // Search Section
    createSearchSection(parent, toolkit);

    // Action Section
    WidgetsFactory.createActionSection(parent, toolkit, this);
  }

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createSearchSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(SEARCH_SECTION_TITLE);

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

    toolkit.createLabel(composite, "Ref:");
    Composite refComposite = toolkit.createComposite(composite);
    refComposite.setLayout(new GridLayout(2, false));
    refComposite.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    refText = toolkit.createText(refComposite, "");
    refText.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());
    Button selectButton = toolkit.createButton(refComposite, "Select", SWT.PUSH);
    selectButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        SelectFlowNodeDialog dialog = new SelectFlowNodeDialog(WidgetsFactory.getShell(), searchNode);
        if (dialog.open() == Window.OK) {
          Object[] selected = dialog.getResult();
          if (selected.length > 0) {
            String id = ((FlowNode<?>) selected[0]).getId();
            if (id != null && !id.isEmpty()) {
              refText.setText(id);
            } else {
              refText.setText("");
            }
          }
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    section.setClient(composite);
  }

  @Override
  public void selectionChanged(IFormPart part, ISelection selection) {
    masterPart = (EditPageMasterSectionPart) part;

    searchNode = (SearchNode) ((IStructuredSelection) selection).getFirstElement();
    selectedAction = null;
    removeWidgetsListeners();
    clearPage();

    // id
    String id = searchNode.getId();
    if (id != null) {
      idValueLabel.setText(id);
      idValueLabel.getParent().layout();
    }

    // comment
    String comment = searchNode.getComment();
    if (comment != null) {
      commentText.setText(comment);
    }

    // isOffline
    isOfflineCheckButton.setSelection(searchNode.getIsOffline());

    // ref
    String ref = searchNode.getRef();
    if (ref != null) {
      refText.setText(ref);
    }

    addWidgetsListeners();
  }

  @Override
  public void clearPage() {
    idValueLabel.setText("");
    commentText.setText("");
    refText.setText("");

    actionTableViewer.setInput(searchNode.getActions());
  }

  protected void addWidgetsListeners() {
    commentText.addModifyListener(commentTextListener);
    refText.addModifyListener(refTextListener);
    isOfflineCheckButton.addSelectionListener(isOfflineCheckButtonListener);
  }

  protected void removeWidgetsListeners() {
    commentText.removeModifyListener(commentTextListener);
    refText.removeModifyListener(refTextListener);
    isOfflineCheckButton.removeSelectionListener(isOfflineCheckButtonListener);
  }

  private class CommentTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      searchNode.setComment(commentText.getText());
      masterPart.markDirty();
    }
  }

  private class RefTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      searchNode.setRef(refText.getText());
      masterPart.getDialogTreeViewer().refresh();
      masterPart.markDirty();
    }
  }

  private class IsOfflineCheckButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      searchNode.setIsOffline(isOfflineCheckButton.getSelection());
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  @Override
  public CompositeNode<?> getActionHolder() {
    return searchNode;
  }
}
