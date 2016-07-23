package watson.dialog.tools.editor.util;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.editor.pages.edit.ActionSectionHolderDetailsPage;
import watson.dialog.tools.model.domain.Action;
import watson.dialog.tools.model.domain.IType.ActionOperatorType;
import watson.dialog.tools.model.service.WatsonDialogToolsService;

public class WidgetsFactory {

  /** Watson Dialog Tools Service */
  private static WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  private static final String ACTION_SECTION_TITLE = "Action";

  /** Action Operator List */
  private static final String[] ACTION_OPERATOR_TYPE_LIST = new String[]{
    ActionOperatorType.DO_NOTHING_STR.value(),
    ActionOperatorType.SET_TO.value(),
    ActionOperatorType.SET_TO_USER_INPUT.value(),
    ActionOperatorType.SET_TO_USER_INPUT_CORRECTED.value(),
    ActionOperatorType.INCREMENT_BY.value(),
    ActionOperatorType.DECREMENT_BY.value(),
    ActionOperatorType.SET_TO_YES.value(),
    ActionOperatorType.SET_TO_NO.value(),
    ActionOperatorType.SET_AS_USER_INPUT.value(),
    ActionOperatorType.SET_TO_BLANK.value(),
    ActionOperatorType.APPEND.value()
  };

  private WidgetsFactory() {
  }

  public static GridData createGridDataWithFullHorizontal() {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    return gridData;
  }

  public static Shell getShell() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    return window.getShell();
  }

  public static void createActionSection(Composite parent, FormToolkit toolkit, final ActionSectionHolderDetailsPage page) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE|Section.EXPANDED);
    section.setText(ACTION_SECTION_TITLE);
    section.setExpanded(false);
    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());
    page.setActionSection(section);

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    Table actionTable = toolkit.createTable(composite, SWT.SINGLE | SWT.FULL_SELECTION);
    actionTable.setHeaderVisible(true);
    actionTable.setLinesVisible(true);
    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 100;
    actionTable.setLayoutData(layoutData);

    final TableViewer actionTableViewer = new TableViewer(actionTable);
    page.setActionTableViewer(actionTableViewer);
    actionTableViewer.setContentProvider(new ArrayContentProvider());
    actionTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        page.setSelectedAction((Action) selection.getFirstElement());
        page.notifyChangeActionSectionStatus();
      }
    });

    // varName
    TableViewerColumn viewerColumn1 = new TableViewerColumn(actionTableViewer, SWT.LEFT);
    viewerColumn1.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        Action action = (Action) cell.getElement();
        String varName = action.getVarName();
        if (varName != null) {
          cell.setText(varName);
        }
      }
    });
    TableColumn tableColumn1 = viewerColumn1.getColumn();
    tableColumn1.setText("VarName");
    tableColumn1.setWidth(120);
    viewerColumn1.setEditingSupport(new EditingSupport(actionTableViewer) {
      @Override
      protected boolean canEdit(Object element) {
        return true;
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor((Table) actionTableViewer.getControl());
      }

      @Override
      protected Object getValue(Object element) {
        String varName =((Action) element).getVarName();
        if (varName == null) {
          return "";
        }
        return varName;
      }

      @Override
      protected void setValue(Object element, Object value) {
        String varName = (String) value;
        ((Action) element).setVarName(varName);
        actionTableViewer.refresh();
        page.markDirty();
      }
    });

    // operator
    TableViewerColumn viewerColumn2 = new TableViewerColumn(actionTableViewer, SWT.LEFT);
    viewerColumn2.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        Action action = (Action) cell.getElement();
        ActionOperatorType type = action.getOperatorType();
        if (type != null) {
          cell.setText(type.value());
        }
      }
    });
    TableColumn tableColumn2 = viewerColumn2.getColumn();
    tableColumn2.setText("Operator");
    tableColumn2.setWidth(160);
    viewerColumn2.setEditingSupport(new EditingSupport(actionTableViewer) {
      @Override
      protected boolean canEdit(Object element) {
        return true;
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new ComboBoxCellEditor((Table) actionTableViewer.getControl(), ACTION_OPERATOR_TYPE_LIST, SWT.READ_ONLY);
      }

      @Override
      protected Object getValue(Object element) {
        ActionOperatorType type =((Action) element).getOperatorType();
        if (type == null) {
          return Integer.valueOf(0);
        }
        return Integer.valueOf(getIndexOfActionOperatorList(type));
      }

      @Override
      protected void setValue(Object element, Object value) {
        Integer index = (Integer) value;
        ((Action) element).setOperatorType(ActionOperatorType.fromValue(ACTION_OPERATOR_TYPE_LIST[index]));
        actionTableViewer.refresh();
        page.markDirty();
      }
    });

    // value
    TableViewerColumn viewerColumn3 = new TableViewerColumn(actionTableViewer, SWT.LEFT);
    viewerColumn3.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        Action action = (Action) cell.getElement();
        String value = action.getValue();
        if (value != null) {
          cell.setText(value);
        }
      }
    });
    TableColumn tableColumn3 = viewerColumn3.getColumn();
    tableColumn3.setText("Value");
    tableColumn3.setWidth(160);
    viewerColumn3.setEditingSupport(new EditingSupport(actionTableViewer) {
      @Override
      protected boolean canEdit(Object element) {
        return true;
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor((Table) actionTableViewer.getControl());
      }

      @Override
      protected Object getValue(Object element) {
        String value =((Action) element).getValue();
        if (value == null) {
          return "";
        }
        return value;
      }

      @Override
      protected void setValue(Object element, Object value) {
        String str = (String) value;
        ((Action) element).setValue(str);
        actionTableViewer.refresh();
        page.markDirty();
      }
    });

    Composite buttons = toolkit.createComposite(composite);
    buttons.setLayout(new GridLayout());
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.verticalAlignment = GridData.FILL;
    layoutData.widthHint = 80;
    buttons.setLayoutData(layoutData);

    final Button addActionButton = toolkit.createButton(buttons, "Add", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    addActionButton.setLayoutData(layoutData);
    addActionButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.createAction(page.getActionHolder());
        actionTableViewer.refresh();
        page.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });
    page.setAddActionButton(addActionButton);

    final Button deleteActionButton = toolkit.createButton(buttons, "Delete", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    deleteActionButton.setLayoutData(layoutData);
    deleteActionButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        IStructuredSelection selection = (IStructuredSelection) actionTableViewer.getSelection();
        service.deleteAction((Action) selection.getFirstElement());
        actionTableViewer.refresh();
        page.setSelectedAction(null);
        page.notifyChangeActionSectionStatus();
        page.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });
    page.setDeleteButton(deleteActionButton);

    section.setClient(composite);
  }

  /**
   *
   * @param type
   * @return
   */
  private static int getIndexOfActionOperatorList(ActionOperatorType  type) {
    for (int i = 0; i < ACTION_OPERATOR_TYPE_LIST.length; i++) {
      String str = ACTION_OPERATOR_TYPE_LIST[i];
      if (str.equals(type.value())) {
        return i;
      }
    }
    return 0;
  }
}
