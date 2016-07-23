package watson.dialog.tools.editor.pages.edit;

import java.util.List;

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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.model.domain.IType.SettingType;
import watson.dialog.tools.model.domain.Setting;
import watson.dialog.tools.model.domain.Settings;
import watson.dialog.tools.model.service.WatsonDialogToolsService;

public class SettingsDetailsPage extends EditDetailsPage {

  /** Watson Dialog Tools Service */
  private static WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  /** Section title */
  private static final String SETTINGS_SECTION_TITLE = "Settings";

  /** Setting Name List */
  private static final String[] SETTING_NAME_ARRAY = new String[]{
    "AUTOLEARN",
    "LANGUAGE",
    "MAXAUTOLEARNITEMS",
    "TIMEZONEID",
    "INPUTMASKTYPE",
    "PARENT_ACCOUNT",
    "CONCEPTMATCHING",
    "USE_CONCEPTS",
    "AL_NONE_LABEL",
    "DEFAULT_DNR_RETURN_POINT_CANDIDATE",
    "ENTITIES_SCOPE",
    "MULTISENT",
    "REPORT_SCRIPT_ID",
    "DNR_NODE_ID",
    "DEFAULT_POINT_NODE_ID",
    "USER_LOGGING",
    "CLS_SEARCH_MODE",
    "CLS_MODEL",
    "CLS_MODELNAME",
    "CLS_MAXNBEST",
    "CLS_USE_OFFTOPIC",
    "PLATFORM_VERSION",
    "USE_TRANSLATIONS",
    "USE_SPELLING_CORRECTIONS",
    "USE_STOP_WORDS",
    "USE_AUTOMATIC_STOPWORDS_DETECTION"
  };

  /** Setting Type List */
  private static final String[] SETTING_TYPE_ARRAY = new String[] {
    "",
    "USER"
  };

  /** Settings */
  private Settings settings;

  /** Setting */
  private Setting selectedSetting;

  /** Page Widgets */
  private Button addSettingButton;
  private Button deleteSettingButton;
  private Button upSettingButton;
  private Button downSettingButton;
  private TableViewer setttingTableViewer;

  @Override
  public void createContents(Composite parent) {
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    parent.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    // Settings Section
    createSettingsSection(parent, toolkit);
  }

  /**
   *
   * @param parent
   * @param toolkit
   */
  private void createSettingsSection(Composite parent, FormToolkit toolkit) {
    Section section = toolkit.createSection(parent, Section.TITLE_BAR);
    section.setText(SETTINGS_SECTION_TITLE);
    section.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    Table settingTable = toolkit.createTable(composite, SWT.SINGLE | SWT.FULL_SELECTION);
    settingTable.setHeaderVisible(true);
    settingTable.setLinesVisible(true);
    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 300;
    settingTable.setLayoutData(layoutData);

    setttingTableViewer = new TableViewer(settingTable);
    setttingTableViewer.setContentProvider(new ArrayContentProvider());
    setttingTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        selectedSetting = (Setting) selection.getFirstElement();
        notifyChangeSettingsSectionStatus();
      }
    });

    // name
    TableViewerColumn viewerColumn1 = new TableViewerColumn(setttingTableViewer, SWT.LEFT);
    viewerColumn1.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        Setting setting = (Setting) cell.getElement();
        if (setting != null) {
          cell.setText(setting.getName());
        }
      }
    });
    TableColumn tableColumn1 = viewerColumn1.getColumn();
    tableColumn1.setText("Name");
    tableColumn1.setWidth(200);
    viewerColumn1.setEditingSupport(new EditingSupport(setttingTableViewer) {
      @Override
      protected boolean canEdit(Object element) {
        return true;
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new ComboBoxCellEditor((Table) setttingTableViewer.getControl(), SETTING_NAME_ARRAY, SWT.READ_ONLY);
      }

      @Override
      protected Object getValue(Object element) {
        String name =((Setting) element).getName();
        if (name == null) {
          return Integer.valueOf(0);
        }
        return Integer.valueOf(getIndexOfSettingNameList(name));
      }

      @Override
      protected void setValue(Object element, Object value) {
        Integer index = (Integer) value;
        ((Setting) element).setName(SETTING_NAME_ARRAY[index]);
        setttingTableViewer.refresh();
        masterPart.markDirty();
      }
    });

    // value
    TableViewerColumn viewerColumn3 = new TableViewerColumn(setttingTableViewer, SWT.LEFT);
    viewerColumn3.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        Setting setting = (Setting) cell.getElement();
        String value = setting.getValue();
        if (value != null) {
          cell.setText(value);
        }
      }
    });
    TableColumn tableColumn3 = viewerColumn3.getColumn();
    tableColumn3.setText("Value");
    tableColumn3.setWidth(100);
    viewerColumn3.setEditingSupport(new EditingSupport(setttingTableViewer) {
      @Override
      protected boolean canEdit(Object element) {
        return true;
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new TextCellEditor((Table) setttingTableViewer.getControl());
      }

      @Override
      protected Object getValue(Object element) {
        String value =((Setting) element).getValue();
        if (value == null) {
          return "";
        }
        return value;
      }

      @Override
      protected void setValue(Object element, Object value) {
        ((Setting) element).setValue((String) value);
        setttingTableViewer.refresh();
        masterPart.markDirty();
      }
    });

    // type
    TableViewerColumn viewerColumn2 = new TableViewerColumn(setttingTableViewer, SWT.LEFT);
    viewerColumn2.setLabelProvider(new CellLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        Setting setting = (Setting) cell.getElement();
        SettingType type = setting.getSettingType();
        if (type == null) {
          cell.setText("");
        } else {
          cell.setText(type.value());
        }
      }
    });
    TableColumn tableColumn2 = viewerColumn2.getColumn();
    tableColumn2.setText("TYPE");
    tableColumn2.setWidth(100);
    viewerColumn2.setEditingSupport(new EditingSupport(setttingTableViewer) {
      @Override
      protected boolean canEdit(Object element) {
        return true;
      }

      @Override
      protected CellEditor getCellEditor(Object element) {
        return new ComboBoxCellEditor((Table) setttingTableViewer.getControl(), SETTING_TYPE_ARRAY, SWT.READ_ONLY);
      }

      @Override
      protected Object getValue(Object element) {
        SettingType type = ((Setting) element).getSettingType();
        if (type == null || type.equals("")) {
          return Integer.valueOf(0);
        } else {
          return Integer.valueOf(1);
        }
      }

      @Override
      protected void setValue(Object element, Object value) {
        Integer index = (Integer) value;
        if (index == 0) {
          ((Setting) element).setSettingType(null);
        } else {
          ((Setting) element).setSettingType(SettingType.USER);
        }
        setttingTableViewer.refresh();
        masterPart.markDirty();
      }
    });

    Composite buttons = toolkit.createComposite(composite);
    buttons.setLayout(new GridLayout());
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.verticalAlignment = GridData.FILL;
    layoutData.widthHint = 60;
    buttons.setLayoutData(layoutData);

    addSettingButton = toolkit.createButton(buttons, "Add", SWT.PUSH);
    buttons.setLayout(new GridLayout());
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    addSettingButton.setLayoutData(layoutData);
    addSettingButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.createSetting(settings);
        setttingTableViewer.refresh();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    deleteSettingButton = toolkit.createButton(buttons, "Delete", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    deleteSettingButton.setLayoutData(layoutData);
    deleteSettingButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.deleteModel(selectedSetting);
        selectedSetting = null;

        notifyChangeSettingsSectionStatus();
        setttingTableViewer.refresh();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    upSettingButton = toolkit.createButton(buttons, "Up", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    upSettingButton.setLayoutData(layoutData);
    upSettingButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.moveUpModel(selectedSetting);

        notifyChangeSettingsSectionStatus();
        setttingTableViewer.refresh();
        masterPart.markDirty();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });

    downSettingButton = toolkit.createButton(buttons, "Down", SWT.PUSH);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    downSettingButton.setLayoutData(layoutData);
    downSettingButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        service.moveDownModel(selectedSetting);

        notifyChangeSettingsSectionStatus();
        setttingTableViewer.refresh();
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
  private static int getIndexOfSettingNameList(String name) {
    for (int i = 0; i < SETTING_NAME_ARRAY.length; i++) {
      String str = SETTING_NAME_ARRAY[i];
      if (str.equals(name)) {
        return i;
      }
    }
    return 0;
  }

  @Override
  public void selectionChanged(IFormPart part, ISelection selection) {
    masterPart = (EditPageMasterSectionPart) part;

    settings = (Settings) ((IStructuredSelection) selection).getFirstElement();
    selectedSetting = null;
    clearPage();
  }

  @Override
  public void clearPage() {
    deleteSettingButton.setEnabled(false);
    upSettingButton.setEnabled(false);
    downSettingButton.setEnabled(false);

    setttingTableViewer.setInput(settings.getSettings());
  }

  private void notifyChangeSettingsSectionStatus() {
    deleteSettingButton.setEnabled(true);
    upSettingButton.setEnabled(true);
    downSettingButton.setEnabled(true);

    if (selectedSetting == null) {
      deleteSettingButton.setEnabled(false);
      upSettingButton.setEnabled(false);
      downSettingButton.setEnabled(false);
    } else {
      List<Setting> list = settings.getSettings();
      if (list.indexOf(selectedSetting) == 0) {
        upSettingButton.setEnabled(false);
      }
      if (list.indexOf(selectedSetting) + 1 == list.size()) {
        downSettingButton.setEnabled(false);
      }
    }
  }
}