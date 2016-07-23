package watson.dialog.tools.editor.pages.edit;

import java.util.Collections;
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
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.editor.dialogs.EditMultiLineTextDialog;
import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.model.domain.Entity;
import watson.dialog.tools.model.domain.EntityValue;
import watson.dialog.tools.model.domain.Grammar;
import watson.dialog.tools.model.domain.IType.GrammarType;
import watson.dialog.tools.model.domain.Item;
import watson.dialog.tools.model.service.WatsonDialogToolsService;

public class EntityDetailsPage extends EditDetailsPage {

  /** Watson Dialog Tools Service */
  private static WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  /** Section title */
  private static final String ENTITY_SECTION_TITLE = "Entity";
  private static final String ENTITY_VALUE_SECTION_TITLE = "Entity Value";
  private static final String GRAMMAR_SECTION_TITLE = "Grammar";

  /** Entity */
  private Entity entity;

  /** Entity Value */
  private EntityValue selectedEntityValue;

  /** Item */
  private Item selectedItem;

  /** Page Widgets */
  private Text nameText;
  private Button addEntityValueButton;
  private Button deleteEntityValueButton;
  private Button upEntityValueButton;
  private Button downEntityValueButton;
  private Button variationsTypeRadioButton;
  private Button addItemButton;
  private Button deleteItemButton;
  private Button upItemButton;
  private Button downItemButton;
  private TableViewer entityValueTableViewer;
  private TableViewer itemTableViewer;

  /** Listener */
  private NameTextListener nameTextListener = new NameTextListener();
  private GrammarTypeRadioButtonListener grammarTypeRadioButtonListener = new GrammarTypeRadioButtonListener();

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // Entity Section
    createEntitySection(parent, toolkit);

    // Entity Value Section
    createEntityValueSection(parent, toolkit);

    // Grammar Section
    createGrammarSection(parent, toolkit);
  }

  private void createEntitySection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(ENTITY_SECTION_TITLE);

    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    toolkit.createLabel(composite, "Name:");
    nameText = toolkit.createText(composite, "");
    nameText.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    section.setClient(composite);
  }

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createEntityValueSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(ENTITY_VALUE_SECTION_TITLE);
    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    Table entityValueTable = toolkit.createTable(composite, SWT.SINGLE | SWT.FULL_SELECTION);
    entityValueTable.setHeaderVisible(true);
    entityValueTable.setLinesVisible(true);
    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 100;
    entityValueTable.setLayoutData(layoutData);

    entityValueTableViewer = new TableViewer(entityValueTable);
    entityValueTableViewer.setContentProvider(new ArrayContentProvider());
    entityValueTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        selectedEntityValue = (EntityValue) selection.getFirstElement();
        notifyChangeEntityValueSectionStatus();
      }
    });

    TableViewerColumn viewerColumn1 = new TableViewerColumn(entityValueTableViewer, SWT.LEFT);
    viewerColumn1.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        EntityValue entityValue = (EntityValue) cell.getElement();
        cell.setText(entityValue.getName());
      }
    });

    TableColumn tableColumn1 = viewerColumn1.getColumn();
    tableColumn1.setText("Name");
    tableColumn1.setWidth(200);
    viewerColumn1.setEditingSupport(new EditingSupport(entityValueTableViewer) {
      @Override
      protected void setValue(Object element, Object value) {
        EntityValue entityValue = (EntityValue) element;
        entityValue.setName(value.toString());
        entityValueTableViewer.refresh();
        masterPart.markDirty();
      }

      @Override
      protected Object getValue(Object element) {
        EntityValue entityValue = (EntityValue) element;
        return entityValue.getName();
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor((Table) entityValueTableViewer.getControl());
      }

      @Override
      protected boolean canEdit(Object element) {
        return true;
      }
    });

    TableViewerColumn viewerColumn2 = new TableViewerColumn(entityValueTableViewer, SWT.LEFT);
    viewerColumn2.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        EntityValue entityValue = (EntityValue) cell.getElement();
        cell.setText(entityValue.getValue());
      }
    });

    TableColumn tableColumn2 = viewerColumn2.getColumn();
    tableColumn2.setText("Value");
    tableColumn2.setWidth(200);
    viewerColumn2.setEditingSupport(new EditingSupport(entityValueTableViewer) {
      @Override
      protected void setValue(Object element, Object value) {
        EntityValue entityValue = (EntityValue) element;
        entityValue.setValue(value.toString());
        entityValueTableViewer.refresh();
        masterPart.markDirty();
      }

      @Override
      protected Object getValue(Object element) {
        EntityValue entityValue = (EntityValue) element;
        return entityValue.getValue();
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor((Table) entityValueTableViewer.getControl());
      }

      @Override
      protected boolean canEdit(Object element) {
        return true;
      }
    });

    Composite buttons = toolkit.createComposite(composite);
    buttons.setLayout(new GridLayout());
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.widthHint = 60;
    buttons.setLayoutData(layoutData);

    addEntityValueButton = toolkit.createButton(buttons, "Add", SWT.PUSH);
    buttons.setLayout(new GridLayout());
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    addEntityValueButton.setLayoutData(layoutData);
    addEntityValueButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.createEntityValue(entity);
        entityValueTableViewer.refresh();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    deleteEntityValueButton = toolkit.createButton(buttons, "Delete", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    deleteEntityValueButton.setLayoutData(layoutData);
    deleteEntityValueButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.deleteModel(selectedEntityValue);
        selectedEntityValue = null;
        selectedItem = null;
        entityValueTableViewer.refresh();
        itemTableViewer.setInput(Collections.EMPTY_LIST);
        notifyChangeItemSectionStatus();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    upEntityValueButton = toolkit.createButton(buttons, "Up", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    upEntityValueButton.setLayoutData(layoutData);
    upEntityValueButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.moveUpModel(selectedEntityValue);
        notifyChangeEntityValueSectionStatus();
        entityValueTableViewer.refresh();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    downEntityValueButton = toolkit.createButton(buttons, "Down", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    downEntityValueButton.setLayoutData(layoutData);
    downEntityValueButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.moveDownModel(selectedEntityValue);
        notifyChangeEntityValueSectionStatus();
        entityValueTableViewer.refresh();
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
        notifyChangeItemSectionStatus();
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
        Grammar grammar = selectedEntityValue.getGrammars().get(0);
        service.createItem(grammar);
        itemTableViewer.refresh();
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
        notifyChangeItemSectionStatus();
        itemTableViewer.refresh();
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
        notifyChangeItemSectionStatus();
        itemTableViewer.refresh();
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

    entity = (Entity) ((IStructuredSelection) selection).getFirstElement();
    selectedEntityValue = null;
    selectedItem = null;
    removeWidgetsListeners();
    clearPage();

    // name
    String name = entity.getName();
    if (name != null) {
      nameText.setText(name);
    }

    addWidgetsListeners();
  }

  @Override
  public void clearPage() {
    nameText.setText("");
    variationsTypeRadioButton.setSelection(false);

    addEntityValueButton.setEnabled(true);
    deleteEntityValueButton.setEnabled(false);
    upEntityValueButton.setEnabled(false);
    downEntityValueButton.setEnabled(false);

    addItemButton.setEnabled(false);
    deleteItemButton.setEnabled(false);
    upItemButton.setEnabled(false);
    downItemButton.setEnabled(false);
    variationsTypeRadioButton.setEnabled(false);

    entityValueTableViewer.setInput(entity.getEntityValues());
    itemTableViewer.setInput(Collections.EMPTY_LIST);
  }

  protected void addWidgetsListeners() {
    nameText.addModifyListener(nameTextListener);
    variationsTypeRadioButton.addSelectionListener(grammarTypeRadioButtonListener);
  }

  protected void removeWidgetsListeners() {
    nameText.removeModifyListener(nameTextListener);
    variationsTypeRadioButton.removeSelectionListener(grammarTypeRadioButtonListener);
  }

  private void notifyChangeEntityValueSectionStatus() {
    deleteEntityValueButton.setEnabled(true);
    upEntityValueButton.setEnabled(true);
    downEntityValueButton.setEnabled(true);
    addItemButton.setEnabled(true);
    variationsTypeRadioButton.setEnabled(false);
    variationsTypeRadioButton.setSelection(false);

    if (selectedEntityValue == null) {
      deleteEntityValueButton.setEnabled(false);
      upEntityValueButton.setEnabled(false);
      downEntityValueButton.setEnabled(false);
      addItemButton.setEnabled(false);
    } else {
      variationsTypeRadioButton.setEnabled(true);

      if (selectedEntityValue.getGrammars().isEmpty()) {
        service.createGrammar(selectedEntityValue);
      }
      Grammar grammar = selectedEntityValue.getGrammars().get(0);
      if (grammar.getType() == GrammarType.VARIATIONS) {
        variationsTypeRadioButton.setSelection(true);
      }
      List<EntityValue> entityValues = entity.getEntityValues();
      if (entityValues.indexOf(selectedEntityValue) == 0) {
        upEntityValueButton.setEnabled(false);
      }
      if (entityValues.indexOf(selectedEntityValue) + 1 == entityValues.size()) {
        downEntityValueButton.setEnabled(false);
      }
      itemTableViewer.setInput(grammar.getItems());
    }
  }

  private void notifyChangeItemSectionStatus() {
    deleteItemButton.setEnabled(true);
    upItemButton.setEnabled(true);
    downItemButton.setEnabled(true);

    if (selectedItem == null) {
      deleteItemButton.setEnabled(false);
      upItemButton.setEnabled(false);
      downItemButton.setEnabled(false);
    } else {
      Grammar grammar = selectedEntityValue.getGrammars().get(0);
      List<Item> items = grammar.getItems();
      if (items.indexOf(selectedItem) == 0) {
        upItemButton.setEnabled(false);
      }
      if (items.indexOf(selectedItem) + 1 == items.size()) {
        downItemButton.setEnabled(false);
      }
    }
  }

  private class NameTextListener implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent e) {
      entity.setName(nameText.getText());
      masterPart.getDialogTreeViewer().refresh();
      masterPart.markDirty();
    }
  }

  private class GrammarTypeRadioButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      Grammar grammar = selectedEntityValue.getGrammars().get(0);
      grammar.setType(GrammarType.fromValue(variationsTypeRadioButton.getText()));
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }
}
