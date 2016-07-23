package watson.dialog.tools.editor.pages.edit;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.model.domain.CompositeNode;
import watson.dialog.tools.model.domain.Cond;
import watson.dialog.tools.model.domain.IType.ConditionOperatorType;
import watson.dialog.tools.model.domain.IType.MatchType;
import watson.dialog.tools.model.domain.IfNode;
import watson.dialog.tools.model.service.WatsonDialogToolsService;

public class IfNodeDetailsPage extends ActionSectionHolderDetailsPage {

  /** Watson Dialog Tools Service */
  private static WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  /** Section title */
  private static final String IF_SECTION_TITLE = "If";
  private static final String COND_SECTION_TITLE = "Cond";

  /** Condition Operator List */
  private static final String[] CONDITION_OPERATOR_TYPE_ARRAY = new String[]{
    ConditionOperatorType.EQUALS.value(),
    ConditionOperatorType.CONTAINS.value(),
    ConditionOperatorType.MATCHES_PATTERN.value(),
    ConditionOperatorType.LESS_THEN.value(),
    ConditionOperatorType.GREATER_THEN.value(),
    ConditionOperatorType.EQUAL_TO_YES.value(),
    ConditionOperatorType.EQUAL_TO_NO.value(),
    ConditionOperatorType.IS_BLANK.value(),
    ConditionOperatorType.HAS_VALUE.value()
  };

  /** IfNode */
  private IfNode ifNode;

  /** Cond */
  private Cond selectedCond;

  /** Page Widgets */
  private Label idValueLabel;
  private Text commentText;
  private Button isOfflineCheckButton;
  private Button allTypeRadioButton;
  private Button anyTypeRadioButton;
  private Button addCondButton;
  private Button deleteCondButton;
  private TableViewer condTableViewer;

  /** Listener */
  private CommentTextListener commentTextListener = new CommentTextListener();
  private IsOfflineCheckButtonListener isOfflineCheckButtonListener = new IsOfflineCheckButtonListener();
  private MatchTypeRadioButtonListener matchTypeRadioButtonListener = new MatchTypeRadioButtonListener();

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // If Section
    createIfSection(parent, toolkit);

    // Cond Section
    createCondSection(parent, toolkit);

    // Action Section
    WidgetsFactory.createActionSection(parent, toolkit, this);
  }

  private void createIfSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(IF_SECTION_TITLE);
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

    toolkit.createLabel(composite, "MatchType:");
    Composite selectionTypeComposite = toolkit.createComposite(composite);
    selectionTypeComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
    allTypeRadioButton = toolkit.createButton(selectionTypeComposite, MatchType.ALL.value(), SWT.RADIO);
    anyTypeRadioButton = toolkit.createButton(selectionTypeComposite, MatchType.ANY.value(), SWT.RADIO);

    section.setClient(composite);
  }

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createCondSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(COND_SECTION_TITLE);
    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    Table condTable = toolkit.createTable(composite, SWT.SINGLE | SWT.FULL_SELECTION);
    condTable.setHeaderVisible(true);
    condTable.setLinesVisible(true);
    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 100;
    condTable.setLayoutData(layoutData);

    condTableViewer = new TableViewer(condTable);
    condTableViewer.setContentProvider(new ArrayContentProvider());
    condTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        selectedCond = (Cond) selection.getFirstElement();
        notifyChangeCondSectionStatus();
      }
    });

    // varName
    TableViewerColumn viewerColumn1 = new TableViewerColumn(condTableViewer, SWT.LEFT);
    viewerColumn1.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        Cond cond = (Cond) cell.getElement();
        String varName = cond.getVarName();
        if (varName != null) {
          cell.setText(varName);
        }
      }
    });
    TableColumn tableColumn1 = viewerColumn1.getColumn();
    tableColumn1.setText("VarName");
    tableColumn1.setWidth(120);
    viewerColumn1.setEditingSupport(new EditingSupport(condTableViewer) {
      @Override
      protected boolean canEdit(Object element) {
        return true;
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor((Table) condTableViewer.getControl());
      }

      @Override
      protected Object getValue(Object element) {
        String varName =((Cond) element).getVarName();
        if (varName == null) {
          return "";
        }
        return varName;
      }

      @Override
      protected void setValue(Object element, Object value) {
        String varName = (String) value;
        ((Cond) element).setVarName(varName);
        condTableViewer.refresh();
        masterPart.markDirty();
      }
    });

    // operator
    TableViewerColumn viewerColumn2 = new TableViewerColumn(condTableViewer, SWT.LEFT);
    viewerColumn2.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        Cond cond = (Cond) cell.getElement();
        ConditionOperatorType type = cond.getOperatorType();
        if (type != null) {
          cell.setText(type.value());
        }
      }
    });
    TableColumn tableColumn2 = viewerColumn2.getColumn();
    tableColumn2.setText("Operator");
    tableColumn2.setWidth(160);
    viewerColumn2.setEditingSupport(new EditingSupport(condTableViewer) {
      @Override
      protected boolean canEdit(Object element) {
        return true;
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new ComboBoxCellEditor((Table) condTableViewer.getControl(), CONDITION_OPERATOR_TYPE_ARRAY, SWT.READ_ONLY);
      }

      @Override
      protected Object getValue(Object element) {
        ConditionOperatorType type = ((Cond) element).getOperatorType();
        if (type == null) {
          return Integer.valueOf(0);
        }
        return Integer.valueOf(getIndexOfConditionOperatorList(type));
      }

      @Override
      protected void setValue(Object element, Object value) {
        Integer index = (Integer) value;
        ((Cond) element).setOperatorType(ConditionOperatorType.fromValue(CONDITION_OPERATOR_TYPE_ARRAY[index]));
        condTableViewer.refresh();
        masterPart.markDirty();
      }
    });

    // value
    TableViewerColumn viewerColumn3 = new TableViewerColumn(condTableViewer, SWT.LEFT);
    viewerColumn3.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        Cond cond = (Cond) cell.getElement();
        String value = cond.getValue();
        if (value != null) {
          cell.setText(value);
        }
      }
    });
    TableColumn tableColumn3 = viewerColumn3.getColumn();
    tableColumn3.setText("Value");
    tableColumn3.setWidth(160);
    viewerColumn3.setEditingSupport(new EditingSupport(condTableViewer) {
      @Override
      protected boolean canEdit(Object element) {
        return true;
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor((Table) condTableViewer.getControl());
      }

      @Override
      protected Object getValue(Object element) {
        String value =((Cond) element).getValue();
        if (value == null) {
          return "";
        }
        return value;
      }

      @Override
      protected void setValue(Object element, Object value) {
        String str = (String) value;
        ((Cond) element).setValue(str);
        condTableViewer.refresh();
        masterPart.markDirty();
      }
    });

    Composite buttons = toolkit.createComposite(composite);
    buttons.setLayout(new GridLayout());
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.verticalAlignment = GridData.FILL;
    layoutData.widthHint = 80;
    buttons.setLayoutData(layoutData);

    addCondButton = toolkit.createButton(buttons, "Add", SWT.PUSH);
    buttons.setLayout(new GridLayout());
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    addCondButton.setLayoutData(layoutData);
    addCondButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.createCond(ifNode);
        condTableViewer.refresh();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    deleteCondButton = toolkit.createButton(buttons, "Delete", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    deleteCondButton.setLayoutData(layoutData);
    deleteCondButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.deleteCond(selectedCond);
        selectedCond = null;
        condTableViewer.refresh();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    section.setClient(composite);
  }

  /**
   *
   * @param type
   * @return
   */
  private static int getIndexOfConditionOperatorList(ConditionOperatorType type) {
    for (int i = 0; i < CONDITION_OPERATOR_TYPE_ARRAY.length; i++) {
      String str = CONDITION_OPERATOR_TYPE_ARRAY[i];
      if (str.equals(type.value())) {
        return i;
      }
    }
    return 0;
  }

  @Override
  public void selectionChanged(IFormPart part, ISelection selection) {
    masterPart = (EditPageMasterSectionPart) part;

    ifNode = (IfNode) ((IStructuredSelection) selection).getFirstElement();
    selectedAction = null;
    removeWidgetsListeners();
    clearPage();

    // id
    String id = ifNode.getId();
    if (id != null) {
      idValueLabel.setText(id);
      idValueLabel.getParent().layout();
    }

    // comment
    String comment = ifNode.getComment();
    if (comment != null) {
      commentText.setText(comment);
    }

    // isOffline
    isOfflineCheckButton.setSelection(ifNode.getIsOffline());

    // matchType
    if (ifNode.getMatchType() == MatchType.ANY) {
      anyTypeRadioButton.setSelection(true);
    }
    if (ifNode.getMatchType() == MatchType.ALL) {
      allTypeRadioButton.setSelection(true);
    }

    addWidgetsListeners();
  }

  @Override
  public void clearPage() {
    idValueLabel.setText("");
    commentText.setText("");
    isOfflineCheckButton.setSelection(false);
    anyTypeRadioButton.setSelection(false);
    allTypeRadioButton.setSelection(false);

    deleteCondButton.setEnabled(false);

    condTableViewer.setInput(ifNode.getConds());
    actionTableViewer.setInput(ifNode.getActions());
  }

  protected void addWidgetsListeners() {
    commentText.addModifyListener(commentTextListener);
    isOfflineCheckButton.addSelectionListener(isOfflineCheckButtonListener);
    anyTypeRadioButton.addSelectionListener(matchTypeRadioButtonListener);
    allTypeRadioButton.addSelectionListener(matchTypeRadioButtonListener);
  }

  protected void removeWidgetsListeners() {
    commentText.removeModifyListener(commentTextListener);
    isOfflineCheckButton.removeSelectionListener(isOfflineCheckButtonListener);
    anyTypeRadioButton.removeSelectionListener(matchTypeRadioButtonListener);
    allTypeRadioButton.removeSelectionListener(matchTypeRadioButtonListener);
  }

  private void notifyChangeCondSectionStatus() {
    deleteCondButton.setEnabled(true);

    if (selectedCond == null) {
      deleteCondButton.setEnabled(false);
    }
  }

  private class CommentTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      ifNode.setComment(commentText.getText());
      masterPart.markDirty();
    }
  }

  private class IsOfflineCheckButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      ifNode.setIsOffline(isOfflineCheckButton.getSelection());
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  private class MatchTypeRadioButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      if (anyTypeRadioButton.getSelection()) {
        ifNode.setMatchType(MatchType.ANY);
      }
      if (allTypeRadioButton.getSelection()) {
        ifNode.setMatchType(MatchType.ALL);
      }
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  @Override
  public CompositeNode<?> getActionHolder() {
    return ifNode;
  }
}
