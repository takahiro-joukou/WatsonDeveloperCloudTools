package watson.dialog.tools.editor.pages.edit;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.editor.dialogs.EditMultiLineTextDialog;
import watson.dialog.tools.editor.dialogs.SelectFlowNodeDialog;
import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.model.domain.CompositeNode;
import watson.dialog.tools.model.domain.FlowNode;
import watson.dialog.tools.model.domain.Grammar;
import watson.dialog.tools.model.domain.IType.GrammarType;
import watson.dialog.tools.model.domain.InputNode;
import watson.dialog.tools.model.domain.Item;
import watson.dialog.tools.model.service.WatsonDialogToolsService;

public class InputNodeDetailsPage extends ActionSectionHolderDetailsPage {

  /** Watson Dialog Tools Service */
  private static WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  /** Section title */
  private static final String INPUT_SECTION_TITLE = "Input";
  private static final String GRAMMAR_SECTION_TITLE = "Grammar";

  /** InputNode */
  private InputNode inputNode;

  /** Item */
  private Item selectedItem;

  /** Page Widgets */
  private Label idValueLabel;
  private Text commentText;
  private Text refText;
  private Button isOfflineCheckButton;
  private Button isAutoLearnCandidateCheckButton;
  private Button variationsTypeRadioButton;
  private Button addItemButton;
  private Button deleteItemButton;
  private Button upItemButton;
  private Button downItemButton;
  private TableViewer itemTableViewer;

  /** Listener */
  private CommentTextListener commentTextListener = new CommentTextListener();
  private RefTextListener refTextListener = new RefTextListener();
  private IsOfflineCheckButtonListener isOfflineCheckButtonListener = new IsOfflineCheckButtonListener();
  private IsAutoLearnCandidateButtonListener isAutoLearnCandidateButtonListener = new IsAutoLearnCandidateButtonListener();
  private GrammarTypeRadioButtonListener grammarTypeRadioButtonListener = new GrammarTypeRadioButtonListener();

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // Input Section
    createInputSection(parent, toolkit);

    // Grammar Section
    createGrammarSection(parent, toolkit);

    // Action Section
    WidgetsFactory.createActionSection(parent, toolkit, this);
  }

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createInputSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(INPUT_SECTION_TITLE);

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
    isAutoLearnCandidateCheckButton = toolkit.createButton(checkComposite, "IsAutoLearnCandidate", SWT.CHECK);

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
        SelectFlowNodeDialog dialog = new SelectFlowNodeDialog(WidgetsFactory.getShell(), inputNode);
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

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createGrammarSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(GRAMMAR_SECTION_TITLE);
    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    toolkit.createLabel(composite, "GrammarType:");
    Composite grammarTypeComposite = toolkit.createComposite(composite);
    grammarTypeComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
    variationsTypeRadioButton = toolkit.createButton(grammarTypeComposite, GrammarType.VARIATIONS.value(), SWT.RADIO);

    Composite tableComposite = toolkit.createComposite(composite);
    tableComposite.setLayout(new GridLayout(2, false));
    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.horizontalSpan = 2;
    tableComposite.setLayoutData(layoutData);

    Table itemTable = toolkit.createTable(tableComposite, SWT.SINGLE | SWT.FULL_SELECTION);
    itemTable.setHeaderVisible(true);
    itemTable.setLinesVisible(true);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 100;
    itemTable.setLayoutData(layoutData);

    itemTableViewer = new TableViewer(itemTable);
    itemTableViewer.setContentProvider(new ArrayContentProvider());
    itemTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        selectedItem = (Item) selection.getFirstElement();
        notifyChangeGrammarSectionStatus();
      }
    });

    TableViewerColumn viewerColumn = new TableViewerColumn(itemTableViewer, SWT.LEFT);
    viewerColumn.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        Item item = (Item) cell.getElement();
        cell.setText(item.getValue());
      }
    });

    TableColumn tableColumn = viewerColumn.getColumn();
    tableColumn.setText("Item");
    tableColumn.setWidth(400);
    viewerColumn.setEditingSupport(new EditingSupport(itemTableViewer) {
      @Override
      protected void setValue(Object element, Object value) {
        Item item = (Item) element;
        item.setValue(value.toString());
        masterPart.getDialogTreeViewer().refresh();
        masterPart.markDirty();
        itemTableViewer.refresh();
      }

      @Override
      protected Object getValue(Object element) {
        Item item = (Item) element;
        return item.getValue();
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        final Item item = (Item) element;
        DialogCellEditor editor = new DialogCellEditor((Table) itemTableViewer.getControl()) {
          @Override
          protected Object openDialogBox(Control cellEditorWindow) {
            EditMultiLineTextDialog dialog = new EditMultiLineTextDialog(cellEditorWindow.getShell(), item.getValue());
            if (dialog.open() == Window.OK) {
              return dialog.getValue();
            }
            return null;
          }
        };
        return editor;
      }

      @Override
      protected boolean canEdit(Object element) {
        return true;
      }
    });

    Composite buttons = toolkit.createComposite(tableComposite);
    buttons.setLayout(new GridLayout());
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.widthHint = 60;
    buttons.setLayoutData(layoutData);

    addItemButton = toolkit.createButton(buttons, "Add", SWT.PUSH);
    buttons.setLayout(new GridLayout());
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    addItemButton.setLayoutData(layoutData);
    addItemButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Grammar grammar = inputNode.getGrammars().get(0);
        service.createItem(grammar);
        itemTableViewer.refresh();
        masterPart.getDialogTreeViewer().refresh();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    deleteItemButton = toolkit.createButton(buttons, "Delete", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    deleteItemButton.setLayoutData(layoutData);
    deleteItemButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.deleteModel(selectedItem);
        selectedItem = null;

        itemTableViewer.refresh();
        masterPart.getDialogTreeViewer().refresh();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    upItemButton = toolkit.createButton(buttons, "Up", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    upItemButton.setLayoutData(layoutData);
    upItemButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.moveUpModel(selectedItem);

        notifyChangeGrammarSectionStatus();
        itemTableViewer.refresh();
        masterPart.getDialogTreeViewer().refresh();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    downItemButton = toolkit.createButton(buttons, "Down", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    downItemButton.setLayoutData(layoutData);
    downItemButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.moveDownModel(selectedItem);

        notifyChangeGrammarSectionStatus();
        itemTableViewer.refresh();
        masterPart.getDialogTreeViewer().refresh();
        masterPart.markDirty();
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

    inputNode = (InputNode) ((IStructuredSelection) selection).getFirstElement();
    selectedAction = null;
    removeWidgetsListeners();
    clearPage();

    // id
    String id = inputNode.getId();
    if (id != null) {
      idValueLabel.setText(id);
      idValueLabel.getParent().layout();
    }

    // comment
    String comment = inputNode.getComment();
    if (comment != null) {
      commentText.setText(comment);
    }

    // isOffline
    isOfflineCheckButton.setSelection(inputNode.getIsOffline());

    // isAutoLearnCandidate
    isAutoLearnCandidateCheckButton.setSelection(inputNode.getIsAutoLearnCandidate());

    // ref
    String ref = inputNode.getRef();
    if (ref != null) {
      refText.setText(ref);
    }

    // GrammarType
    Grammar grammar = inputNode.getGrammars().get(0);
    if (grammar.getType() == GrammarType.VARIATIONS) {
      variationsTypeRadioButton.setSelection(true);
    }

    addWidgetsListeners();
  }

  @Override
  public void clearPage() {
    idValueLabel.setText("");
    commentText.setText("");
    refText.setText("");
    isOfflineCheckButton.setSelection(false);
    isAutoLearnCandidateCheckButton.setSelection(false);
    variationsTypeRadioButton.setSelection(false);

    deleteItemButton.setEnabled(false);
    upItemButton.setEnabled(false);
    downItemButton.setEnabled(false);

    if (inputNode.getGrammars().isEmpty()) {
      service.createGrammar(inputNode);
    }
    Grammar grammar = inputNode.getGrammars().get(0);
    itemTableViewer.setInput(grammar.getItems());
    actionTableViewer.setInput(inputNode.getActions());
  }

  protected void addWidgetsListeners() {
    commentText.addModifyListener(commentTextListener);
    refText.addModifyListener(refTextListener);
    isOfflineCheckButton.addSelectionListener(isOfflineCheckButtonListener);
    isAutoLearnCandidateCheckButton.addSelectionListener(isAutoLearnCandidateButtonListener);
    variationsTypeRadioButton.addSelectionListener(grammarTypeRadioButtonListener);
  }

  protected void removeWidgetsListeners() {
    commentText.removeModifyListener(commentTextListener);
    refText.removeModifyListener(refTextListener);
    isOfflineCheckButton.removeSelectionListener(isOfflineCheckButtonListener);
    isAutoLearnCandidateCheckButton.removeSelectionListener(isAutoLearnCandidateButtonListener);
    variationsTypeRadioButton.removeSelectionListener(grammarTypeRadioButtonListener);
  }

  private void notifyChangeGrammarSectionStatus() {
    deleteItemButton.setEnabled(true);
    upItemButton.setEnabled(true);
    downItemButton.setEnabled(true);

    if (selectedItem == null) {
      deleteItemButton.setEnabled(false);
      upItemButton.setEnabled(false);
      downItemButton.setEnabled(false);
    } else {
      Grammar grammar = inputNode.getGrammars().get(0);
      List<Item> items = grammar.getItems();
      if (items.indexOf(selectedItem) == 0) {
        upItemButton.setEnabled(false);
      }
      if (items.indexOf(selectedItem) + 1 == items.size()) {
        downItemButton.setEnabled(false);
      }
    }
  }

  private class CommentTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      inputNode.setComment(commentText.getText());
      masterPart.markDirty();
    }
  }

  private class RefTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      inputNode.setRef(refText.getText());
      masterPart.markDirty();
    }
  }

  private class IsOfflineCheckButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      inputNode.setIsOffline(isOfflineCheckButton.getSelection());
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  private class IsAutoLearnCandidateButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      inputNode.setIsAutoLearnCandidate(isAutoLearnCandidateCheckButton.getSelection());
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  private class GrammarTypeRadioButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      Grammar grammar = inputNode.getGrammars().get(0);
      grammar.setType(GrammarType.fromValue(variationsTypeRadioButton.getText()));
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  @Override
  public CompositeNode<?> getActionHolder() {
    return inputNode;
  }
}
