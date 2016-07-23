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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.editor.dialogs.EditMultiLineTextDialog;
import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.model.domain.IType.SelectionType;
import watson.dialog.tools.model.domain.Item;
import watson.dialog.tools.model.domain.SpecialSetting;
import watson.dialog.tools.model.domain.Variations;
import watson.dialog.tools.model.service.WatsonDialogToolsService;

public class SpecialSettingDetailsPage extends EditDetailsPage {

  /** Watson Dialog Tools Service */
  private static WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  /** Section title */
  private static final String SPECIAL_SETTINGS_SECTION_TITLE = "SpecialSettings";
  private static final String VARIATIONS_SECTION_TITLE = "Variations";

  /** Variations Type */
  private static final String[] SPECIAL_SETTING_LABEL_ARRAY = new String[]{
    "DNR join statement",
    "AutoLearn statement"
  };

  /** SpecialSetting Node */
  SpecialSetting specialSetting;

  /** Item */
  private Item selectedItem;

  /** Page Widgets */
  private Label idValueLabel;
  private Combo labelCombo;
  private Button randomTypeRadioButton;
  private Button sequentialTypeRadioButton;
  private Button addItemButton;
  private Button deleteItemButton;
  private Button upItemButton;
  private Button downItemButton;
  private TableViewer itemTableViewer;

  /** Listener */
  private LabelComboListener labelComboListener = new LabelComboListener();
  private SpecialSettingTypeRadioButtonListener specialSettingTypeRadioButtonListener = new SpecialSettingTypeRadioButtonListener();

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // Special Setting Section
    createSpecialSettingSection(parent, toolkit);

    // Variations Section
    createVariationsSection(parent, toolkit);
  }

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createSpecialSettingSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(SPECIAL_SETTINGS_SECTION_TITLE);
    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    toolkit.createLabel(composite, "Id:");
    idValueLabel = toolkit.createLabel(composite, "");

    toolkit.createLabel(composite, "SelectionType:");
    Composite selectionTypeComposite = toolkit.createComposite(composite);
    selectionTypeComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
    sequentialTypeRadioButton = toolkit.createButton(selectionTypeComposite, SelectionType.SEQUENTIAL.value(), SWT.RADIO);
    randomTypeRadioButton = toolkit.createButton(selectionTypeComposite, SelectionType.RANDOM.value(), SWT.RADIO);

    toolkit.createLabel(composite, "Label:");
    labelCombo = new Combo(composite, SWT.READ_ONLY);
    for (String type : SPECIAL_SETTING_LABEL_ARRAY) {
      labelCombo.add(type);
    }

    section.setClient(composite);
  }

  private void createVariationsSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(VARIATIONS_SECTION_TITLE);
    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    Table itemTable = toolkit.createTable(composite, SWT.SINGLE | SWT.FULL_SELECTION);
    itemTable.setHeaderVisible(true);
    itemTable.setLinesVisible(true);
    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 100;
    itemTable.setLayoutData(layoutData);

    itemTableViewer = new TableViewer(itemTable);
    itemTableViewer.setContentProvider(new ArrayContentProvider());
    itemTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        selectedItem = (Item) selection.getFirstElement();
        notifyChangeVariationsSectionStatus();
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

        // Item of variations don't have xsd peer type.
        Variations variations  = (Variations) item.getParent();
        int index  = variations.getItems().indexOf(item);
        variations.getPeerType().getItem().set(index, value.toString());

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

    Composite buttons = toolkit.createComposite(composite);
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
        Variations variations = specialSetting.getVariations().get(0);
        service.createItem(variations);
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

        notifyChangeVariationsSectionStatus();
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

        notifyChangeVariationsSectionStatus();
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

    specialSetting = (SpecialSetting) ((IStructuredSelection) selection).getFirstElement();
    selectedItem = null;
    removeWidgetsListeners();
    clearPage();

    // id
    String id = specialSetting.getId();
    if (id != null) {
      idValueLabel.setText(id);
      idValueLabel.getParent().layout();
    }

    // label
    String label = specialSetting.getLabel();
    if (label != null) {
      labelCombo.select(getComboIndex(label));
    }

    // Variations Type
    if (specialSetting.getSelectionType() == SelectionType.SEQUENTIAL) {
      sequentialTypeRadioButton.setSelection(true);
    }
    if (specialSetting.getSelectionType() == SelectionType.RANDOM) {
      randomTypeRadioButton.setSelection(true);
    }

    addWidgetsListeners();
  }

  private int getComboIndex(String label) {
    for (int i = 0; i < SPECIAL_SETTING_LABEL_ARRAY.length; i++) {
      if (SPECIAL_SETTING_LABEL_ARRAY[i].equals(label)) {
        return i;
      }
    }
    return 0;
  }

  @Override
  public void clearPage() {
    idValueLabel.setText("");
    labelCombo.select(0);
    sequentialTypeRadioButton.setSelection(false);
    randomTypeRadioButton.setSelection(false);

    deleteItemButton.setEnabled(false);
    upItemButton.setEnabled(false);
    downItemButton.setEnabled(false);

    if (specialSetting.getVariations().isEmpty()) {
      service.createVariation(specialSetting);
    }
    Variations variation = specialSetting.getVariations().get(0);
    itemTableViewer.setInput(variation.getItems());
  }

  protected void addWidgetsListeners() {
    labelCombo.addSelectionListener(labelComboListener);
    sequentialTypeRadioButton.addSelectionListener(specialSettingTypeRadioButtonListener);
    randomTypeRadioButton.addSelectionListener(specialSettingTypeRadioButtonListener);
  }

  protected void removeWidgetsListeners() {
    labelCombo.removeSelectionListener(labelComboListener);
    sequentialTypeRadioButton.removeSelectionListener(specialSettingTypeRadioButtonListener);
    randomTypeRadioButton.removeSelectionListener(specialSettingTypeRadioButtonListener);
  }

  private void notifyChangeVariationsSectionStatus() {
    deleteItemButton.setEnabled(true);
    upItemButton.setEnabled(true);
    downItemButton.setEnabled(true);

    if (selectedItem == null) {
      deleteItemButton.setEnabled(false);
      upItemButton.setEnabled(false);
      downItemButton.setEnabled(false);
    } else {
      Variations variation = specialSetting.getVariations().get(0);
      List<Item> items = variation.getItems();
      if (items.indexOf(selectedItem) == 0) {
        upItemButton.setEnabled(false);
      }
      if (items.indexOf(selectedItem) + 1 == items.size()) {
        downItemButton.setEnabled(false);
      }
    }
  }

  private class LabelComboListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      specialSetting.setLabel(labelCombo.getText());
      masterPart.getDialogTreeViewer().refresh();
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  private class SpecialSettingTypeRadioButtonListener implements SelectionListener {
    @Override
    public void widgetSelected(SelectionEvent e) {
      if (sequentialTypeRadioButton.getSelection()) {
        specialSetting.setSelectionType(SelectionType.fromValue(sequentialTypeRadioButton.getText()));
      }
      if (randomTypeRadioButton.getSelection()) {
        specialSetting.setSelectionType(SelectionType.fromValue(randomTypeRadioButton.getText()));
      }
      masterPart.markDirty();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
  }
}
