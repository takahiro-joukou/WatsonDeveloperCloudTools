package watson.dialog.tools.editor.pages.run;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import watson.dialog.tools.Activator;
import watson.dialog.tools.editor.dialogs.RunConversationDilaog;
import watson.dialog.tools.editor.preference.WatsonDialogServicePrefPage;
import watson.dialog.tools.editor.util.ImageFactory;
import watson.dialog.tools.editor.util.TableLabelProvider;
import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.exception.WatsonDialogToolsException;
import watson.dialog.tools.model.service.WatsonDialogToolsService;

import com.ibm.watson.developer_cloud.dialog.v1.model.ConversationData;
import com.ibm.watson.developer_cloud.dialog.v1.model.Dialog;
import com.ibm.watson.developer_cloud.dialog.v1.model.HitNode;
import com.ibm.watson.developer_cloud.dialog.v1.model.Message;
import com.ibm.watson.developer_cloud.dialog.v1.model.NameValue;

public class WatsonDialogServiceRunPage extends FormPage {

  /** Page Id */
  private static final String PAGE_ID = "watson.dialog.tools.editors.pages.WatsonDialogServiceRunPage";

  /** Section Titles */
  private static final String DIALOG_LIST_SECTION_TITLE = "Watson Dialog List";
  private static final String CONVERSATION_HISTORY_SECTION_TILE = "Conversation History";
  private static final String CONVERSATION_DETAILS_SECTION_TILE = "Conversation Details";

  /** Watson Dialog Tools Service */
  private WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  //
  // Dialog Section
  //
  /** Selected Dialog */
  private Dialog selectedDialog;

  /** Dialog List Table Viewer */
  private TableViewer dialogTableViewer;

  /** Dialog Section Buttons */
  Button showDialogsButton;
  Button createDialogButton;
  Button updateDialogButton;
  Button deleteDialogButton;
  Button newConversationButton;

  //
  // Conversation History Section
  //
  /** Date Format */
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

  /** From Date and Time */
  private DateTime fromDate;
  private DateTime fromTime;
  private Date fromDateObj;

  /** To Date and Time */
  private DateTime toDate;
  private DateTime toTime;
  private Date toDateObj;

  /** Selected Conversation */
  private ConversationData selectedConversation;

  /** Conversation History Table Viewer */
  private TableViewer historyTableViewer;

  /** Show History Button*/
  Button showHistoryButton;
  Button restartConversationButton;

  //
  // Conversation Details Section
  //
  /** Conversation Message Table Viewer */
  private TableViewer messagesTableViewer;

  /** Hit Nodes Table Viewer */
  private TableViewer hitNodesTableViewer;

  /** Profile Table Viewer */
  private TableViewer profileTableViewer;

  /** Conversation Details Section */
  Section conversationDetailsSection;

  /**
   *
   * @param editor
   */
  public WatsonDialogServiceRunPage(FormEditor editor) {
    super(editor, PAGE_ID, "Run");
  }

  /**
   *
   */
  protected void createFormContent(IManagedForm managedForm) {
    managedForm.getForm().setText("Run Watson Dialog Service");
    managedForm.setInput(getEditorInput());

    ScrolledForm form = managedForm.getForm();
    Composite body = form.getBody();

    TableWrapLayout layout = new TableWrapLayout();
    layout.numColumns = 2;
    layout.makeColumnsEqualWidth = true;
    body.setLayout(layout);

    FormToolkit toolkit = managedForm.getToolkit();

    createDialogListSection(body, toolkit);
    createConversionDetailsSection(body, toolkit);
    createConversionHistorySection(body, toolkit);

    // set widgets enablement
    notifyChangePageStatus();
  }

  /**
   *
   * @param body
   * @param toolkit
   */
  private void createDialogListSection(Composite body, FormToolkit toolkit) {
    Section section = toolkit.createSection(body, Section.TITLE_BAR);
    section.setText(DIALOG_LIST_SECTION_TITLE);
    TableWrapData wrapData = new TableWrapData(TableWrapData.FILL_GRAB);
    wrapData.heightHint = 260;
    section.setLayoutData(wrapData);

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout(2, false));

    FormText formText = toolkit.createFormText(composite, true);
    formText.setText("<form>"
        + "<p>Set your credential data in <a href='preference' nowrap='false'>preference page</a>. "
        + "Create or retrieve and update Watson Dialog instances of your "
        + "<a href='http://bluemix.com' nowrap='false'>Bluemix</a> environment by edited file.</p>"
        + "</form>",
        true, false);

    formText.addHyperlinkListener(new HyperlinkAdapter() {
      @Override
      public void linkActivated(HyperlinkEvent event) {
        String link = (String) event.data;
        if (link.startsWith("http:")) {
          IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
          try {
            browserSupport.getExternalBrowser().openURL(new URL(link));
          } catch (PartInitException | MalformedURLException e) {
            throw new WatsonDialogToolsException(e);
          }
        }
        if ("preference".equals(link)) {
          PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, WatsonDialogServicePrefPage.PAGE_ID,
              null, null);
          dialog.open();
        }
      }
    });

    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.horizontalSpan = 2;
    layoutData.widthHint = composite.getClientArea().width;
    formText.setLayoutData(layoutData);

    Table dialogTable = toolkit.createTable(composite, SWT.SINGLE | SWT.FULL_SELECTION);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 120;
    dialogTable.setLayoutData(layoutData);

    dialogTableViewer = new TableViewer(dialogTable);
    dialogTableViewer.setContentProvider(new ArrayContentProvider());
    dialogTableViewer.setLabelProvider(new TableLabelProvider() {
      @Override
      public String getColumnText(Object element, int columnIndex) {
        Dialog dialog = (Dialog) element;
        return dialog.getName();
      }

      @Override
      public Image getColumnImage(Object element, int columnIndex) {
        return ImageFactory.getImage(ImageFactory.FLOW_ICON);
      }
    });
    dialogTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        StructuredSelection selection = (StructuredSelection) event.getSelection();
        selectedDialog = (Dialog) selection.getFirstElement();

        notifyChangePageStatus();
      }
    });
    dialogTableViewer.setInput(new ArrayList<Dialog>());

    Composite buttons = toolkit.createComposite(composite);
    layoutData = new GridData();
    layoutData.verticalAlignment = GridData.FILL;
    layoutData.verticalSpan = 2;
    buttons.setLayoutData(layoutData);
    buttons.setLayout(new GridLayout());

    showDialogsButton = toolkit.createButton(buttons, "Show Dialogs", SWT.PUSH);
    showDialogsButton.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());
    showDialogsButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clickShowDialogsButton();
      }
    });

    createDialogButton = toolkit.createButton(buttons, "Create", SWT.PUSH);
    createDialogButton.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());
    createDialogButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clickCreateDialogButton();
      }
    });

    updateDialogButton = toolkit.createButton(buttons, "Update", SWT.PUSH);
    updateDialogButton.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());
    updateDialogButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clickUpdateDialogButton();
      }
    });

    deleteDialogButton = toolkit.createButton(buttons, "Delete", SWT.PUSH);
    deleteDialogButton.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());
    deleteDialogButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clickDeleteDialogButton();
      }
    });

    newConversationButton = toolkit.createButton(composite, "New Conversation", SWT.PUSH);
    layoutData = new GridData();
    layoutData.horizontalAlignment = SWT.RIGHT;
    newConversationButton.setLayoutData(layoutData);
    newConversationButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clickNewConversationButton();
      }
    });

    section.setClient(composite);
  }

  /**
   *
   */
  private void clickShowDialogsButton() {
    String title = "Show Dialogs";
    try {
      List<Dialog> dialogs = service.listDialogs();
      if (dialogs.isEmpty()) {
        String message = "No dialog instance in your Bluemix.";
        MessageDialog.openInformation(WidgetsFactory.getShell(), title, message);
      }
      dialogTableViewer.setInput(dialogs);
    } catch (Exception e) {
      IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e);
      String message = "Error occurred when retrieve dialog instances.";
      ErrorDialog.openError(WidgetsFactory.getShell(), title, message, status);
    }
  }

  /**
   *
   */
  private void clickCreateDialogButton() {
    String title = "Create Dialog";

    File file = getFileObj();
    if (file == null) {
      throw new IllegalStateException("File object must not be null.");
    }

    String message = "Input new dialog instance name.";
    String name = "New name";
    InputDialog input = new InputDialog(WidgetsFactory.getShell(), title, message, name, new IInputValidator() {
      @Override
      public String isValid(String str) {
        if (str == null || str.isEmpty()) {
          return "Invalid dialog name.";
        }
          return null;
        }
      });
    int ret = input.open();

    if (ret == Window.OK) {
      try {
        name = input.getValue();
        Dialog dialog = service.createDialog(name, file);
        dialog.setName(name);

        @SuppressWarnings("unchecked")
        List<Dialog> dialogs = (List<Dialog>)dialogTableViewer.getInput();
        dialogs.add(dialog);

        dialogTableViewer.refresh();
      } catch (Exception e) {
        IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e);
        message = "Error occurred when create dialog instance.";
        ErrorDialog.openError(WidgetsFactory.getShell(), title, message, status);
      }
    }
  }

  /**
   *
   */
  private void clickUpdateDialogButton() {
    File file = getFileObj();
    if (file == null) {
      throw new IllegalStateException("File object must not be null.");
    }

    String title = "Update Dialog";
    try {
      service.updateDialog(selectedDialog.getId(), file);

      String message = "Update dialog instance successfully.";
      MessageDialog.openInformation(WidgetsFactory.getShell(), title, message);
    } catch (Exception e) {
      IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e);
      String message = "Error occurred when update dialog instance.";
      ErrorDialog.openError(WidgetsFactory.getShell(), title, message, status);
    }
  }

  /**
   *
   */
  private void clickDeleteDialogButton() {
    String title = "Delete Dialog";
    String message = "Would you like to delete dialog instance?";
    boolean ret = MessageDialog.openConfirm(WidgetsFactory.getShell(), title, message);

    if (ret) {
      try {
        String dialogId = selectedDialog.getId();
        service.deleteDialog(dialogId);

        @SuppressWarnings("unchecked")
        List<Dialog> dialogs = (List<Dialog>) dialogTableViewer.getInput();
        dialogs.remove(selectedDialog);
        dialogTableViewer.refresh();

        // clear page widgets
        selectedDialog = null;
        selectedConversation = null;

        historyTableViewer.setInput(Collections.EMPTY_LIST);
        messagesTableViewer.setInput(Collections.EMPTY_LIST);
        hitNodesTableViewer.setInput(Collections.EMPTY_LIST);
        profileTableViewer.setInput(Collections.EMPTY_LIST);
        conversationDetailsSection.setVisible(false);

        notifyChangePageStatus();
      } catch (Exception e) {
        IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e);
        message = "Error occurred when delete dialog instances.";
        ErrorDialog.openError(WidgetsFactory.getShell(), title, message, status);
      }
    }
  }

  /**
   *
   * @return
   */
  private File getFileObj() {
    IFile file = ((IFileEditorInput) getManagedForm().getInput()).getFile();
    IPath location = file.getLocation();
    if (location != null) {
      return location.toFile();
    }
    return null;
  }

  /**
   *
   */
  private void clickNewConversationButton() {
    RunConversationDilaog dialog = new RunConversationDilaog(WidgetsFactory.getShell(), selectedDialog);
    if (dialog.open() == Window.OK) {
    }
  }

  /**
   *
   * @param body
   * @param toolkit
   */
  private void createConversionDetailsSection(Composite body, FormToolkit toolkit) {
    conversationDetailsSection = toolkit.createSection(body, Section.TITLE_BAR);
    conversationDetailsSection.setText(CONVERSATION_DETAILS_SECTION_TILE);
    TableWrapData wrapData = new TableWrapData(TableWrapData.FILL_GRAB);
    wrapData.rowspan = 2;
    conversationDetailsSection.setLayoutData(wrapData);

    Composite detailsComposite = toolkit.createComposite(conversationDetailsSection);
    detailsComposite.setLayout(new GridLayout());
    detailsComposite.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    CTabFolder detailsTabFolder = new CTabFolder(detailsComposite, SWT.TOP);
    detailsTabFolder.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());
    detailsTabFolder.setSimple(false);
    detailsTabFolder.setSelection(0);
    detailsTabFolder.setFocus();

    createMessagesTab(toolkit, detailsTabFolder);
    createHitNodesTab(toolkit, detailsTabFolder);
    createProfileTab(toolkit, detailsTabFolder);

    conversationDetailsSection.setClient(detailsComposite);
    conversationDetailsSection.setVisible(false);
  }

  /**
   *
   * @param toolkit
   * @param detailsTabFolder
   */
  private void createMessagesTab(FormToolkit toolkit, CTabFolder detailsTabFolder) {
    CTabItem messageTab = new CTabItem(detailsTabFolder, SWT.NONE);
    messageTab.setText("Converstion Messages");

    Composite messagesComposite = toolkit.createComposite(detailsTabFolder);
    messagesComposite.setLayout(new GridLayout());
    messagesComposite.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    toolkit.createLabel(messagesComposite, "Message List");

    Table messagesTable = toolkit.createTable(messagesComposite, SWT.NONE);
    messagesTable.setHeaderVisible(true);
    messagesTable.setLinesVisible(true);
    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 460;
    layoutData.widthHint = 400;
    messagesTable.setLayoutData(layoutData);

    //
    // work around following bug :
    //  - https://bugs.eclipse.org/bugs/show_bug.cgi?id=43910
    //
    TableColumn column1 = new TableColumn(messagesTable, SWT.NULL);
    column1.setText("dummyColumn");
    column1.setWidth(0);
    column1.setResizable(false);

    TableColumn column2 = new TableColumn(messagesTable, SWT.NULL);
    column2.setText("Date Time");
    column2.setWidth(140);

    TableColumn column3 = new TableColumn(messagesTable, SWT.NULL);
    column3.setText("Message Text");
    column3.setWidth(400);

    messagesTableViewer = new TableViewer(messagesTable);
    messagesTableViewer.setContentProvider(new ArrayContentProvider());
    messagesTableViewer.setLabelProvider(new TableLabelProvider() {
      @Override
      public String getColumnText(Object element, int columnIndex) {
        Message message = (Message) element;
        switch (columnIndex) {
        case 1:
          return dateFormat.format(message.getDateTime());
        case 2:
          return message.getText();
        default:
          return "";
        }
      }

      @Override
      public Image getColumnImage(Object element, int columnIndex) {
        Message message = (Message) element;
        switch (columnIndex) {
        case 2:
          if ("true".equals(message.getFromClient())) {
            return ImageFactory.getImage(ImageFactory.SEND_MSG_ICON);
          } else {
            return ImageFactory.getImage(ImageFactory.RESPONSE_MSG_ICON);
          }
        default:
          return null;
        }
      }
    });
    messageTab.setControl(messagesComposite);
  }

  /**
   *
   * @param toolkit
   * @param detailsTabFolder
   */
  private void createHitNodesTab(FormToolkit toolkit, CTabFolder detailsTabFolder) {
    CTabItem hitNodesTab = new CTabItem(detailsTabFolder, SWT.NONE);
    hitNodesTab.setText("Hit Nodes List");

    Composite hitNodesComposite = toolkit.createComposite(detailsTabFolder);
    hitNodesComposite.setLayout(new GridLayout());
    hitNodesComposite.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    toolkit.createLabel(hitNodesComposite, "Hit Nodes List");

    Table hitNodesTable = toolkit.createTable(hitNodesComposite, SWT.NONE);
    hitNodesTable.setHeaderVisible(true);
    hitNodesTable.setLinesVisible(true);
    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 460;
    layoutData.widthHint = 400;
    hitNodesTable.setLayoutData(layoutData);

    TableColumn column1 = new TableColumn(hitNodesTable, SWT.NULL);
    column1.setText("Id");
    column1.setWidth(70);

    TableColumn column2 = new TableColumn(hitNodesTable, SWT.NULL);
    column2.setText("Type");
    column2.setWidth(130);

    TableColumn column4 = new TableColumn(hitNodesTable, SWT.NULL);
    column4.setText("Label");
    column4.setWidth(170);

    TableColumn column3 = new TableColumn(hitNodesTable, SWT.NULL);
    column3.setText("Details");
    column3.setWidth(280);

    hitNodesTableViewer = new TableViewer(hitNodesTable);
    hitNodesTableViewer.setContentProvider(new ArrayContentProvider());
    hitNodesTableViewer.setLabelProvider(new TableLabelProvider() {
      @Override
      public String getColumnText(Object element, int columnIndex) {
        HitNode hitNode = (HitNode) element;
        switch (columnIndex) {
        case 0:
          return hitNode.getNodeId().toString();
        case 1:
          return hitNode.getType();
        case 2:
          return hitNode.getLabel();
        case 3:
          return hitNode.getDetails();
        default:
          return "";
        }
      }
    });

    hitNodesTab.setControl(hitNodesComposite);
  }

  /**
   *
   * @param toolkit
   * @param detailsTabFolder
   */
  private void createProfileTab(FormToolkit toolkit, CTabFolder detailsTabFolder) {
    CTabItem profileTab = new CTabItem(detailsTabFolder, SWT.NONE);
    profileTab.setText("Profile List");

    Composite profileComposite = toolkit.createComposite(detailsTabFolder);
    profileComposite.setLayout(new GridLayout());
    profileComposite.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    toolkit.createLabel(profileComposite, "Profile List");

    Table profileTable = toolkit.createTable(profileComposite, SWT.NONE);
    profileTable.setHeaderVisible(true);
    profileTable.setLinesVisible(true);
    GridData layoutData = new GridData();
    layoutData.heightHint = 180;
    profileTable.setLayoutData(layoutData);

    TableColumn column1 = new TableColumn(profileTable, SWT.NULL);
    column1.setText("Name");
    column1.setWidth(130);

    TableColumn column2 = new TableColumn(profileTable, SWT.NULL);
    column2.setText("Value");
    column2.setWidth(130);

    profileTableViewer = new TableViewer(profileTable);
    profileTableViewer.setContentProvider(new ArrayContentProvider());
    profileTableViewer.setLabelProvider(new TableLabelProvider() {
      @Override
      public String getColumnText(Object element, int columnIndex) {
        NameValue nameValue = (NameValue) element;
        switch (columnIndex) {
        case 0:
          return nameValue.getName();
        case 1:
          return nameValue.getValue();
        default:
          return "";
        }
      }
    });
    profileTab.setControl(profileComposite);
  }

  /**
   *
   * @param body
   * @param toolkit
   */
  private void createConversionHistorySection(Composite body, FormToolkit toolkit) {
    Section section = toolkit.createSection(body, Section.TITLE_BAR);
    section.setText(CONVERSATION_HISTORY_SECTION_TILE);
    TableWrapData wrapData = new TableWrapData(TableWrapData.FILL_GRAB);
    wrapData.heightHint = 300;
    section.setLayoutData(wrapData);

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    FormText formText = toolkit.createFormText(composite, true);
    formText.setText("<form>"
        + "<p>Show conversion history of selected dialog. You can restart selected conversion.</p>"
        + "</form>",
        true, false);

    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.widthHint = composite.getClientArea().width;
    formText.setLayoutData(layoutData);

    Composite dateComposite = toolkit.createComposite(composite);
    dateComposite.setLayout(new GridLayout(7, false));
    dateComposite.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());

    toolkit.createLabel(dateComposite, "From:");
    fromDate = new DateTime(dateComposite, SWT.DATE);
    fromDate.setDay(fromDate.getDay() - 1);
    fromTime = new DateTime(dateComposite, SWT.TIME);
    fromTime.setHours(0);
    fromTime.setMinutes(0);
    fromTime.setSeconds(0);

    toolkit.createLabel(dateComposite, "To:");
    toDate = new DateTime(dateComposite, SWT.DATE);
    toTime = new DateTime(dateComposite, SWT.TIME);
    toTime.setHours(23);
    toTime.setMinutes(59);
    toTime.setSeconds(59);

    showHistoryButton = toolkit.createButton(dateComposite, "Show History", SWT.PUSH);
    showHistoryButton.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());
    showHistoryButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clickShowConversationHistoryButton();
      }
    });

    Table historyTable = toolkit.createTable(composite, SWT.SINGLE | SWT.FULL_SELECTION);
    historyTable.setHeaderVisible(true);
    historyTable.setLinesVisible(true);
    layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 140;
    historyTable.setLayoutData(layoutData);

    TableColumn column1 = new TableColumn(historyTable, SWT.NULL);
    column1.setText("Date");
    column1.setWidth(140);

    TableColumn column2 = new TableColumn(historyTable, SWT.NULL);
    column2.setText("Client Id");
    column2.setWidth(70);

    TableColumn column3 = new TableColumn(historyTable, SWT.NULL);
    column3.setText("Conversation Id");
    column3.setWidth(110);

    TableColumn column4 = new TableColumn(historyTable, SWT.NULL);
    column4.setText("Latest Message");
    column4.setWidth(200);

    historyTableViewer = new TableViewer(historyTable);
    historyTableViewer.setContentProvider(new ArrayContentProvider());
    historyTableViewer.setLabelProvider(new TableLabelProvider() {
      @Override
      public String getColumnText(Object element, int columnIndex) {
        ConversationData conversation = (ConversationData) element;
        List<Message> messages = conversation.getMessages();
        switch (columnIndex) {
        case 0:
          if (!messages.isEmpty()) {
            Date date = messages.get(0).getDateTime();
            return dateFormat.format(date);
          }
          return "";
        case 1:
          int id = conversation.getClientId();
          return Integer.toString(id);
        case 2:
          id = conversation.getConversationId();
          return Integer.toString(id);
        case 3:
          if (!messages.isEmpty()) {
            int size = messages.size();
            return messages.get(size - 1).getText();
          }
          return "";
        default:
          return "";
        }
      }
    });
    historyTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        StructuredSelection selection = (StructuredSelection) event.getSelection();
        selectedConversation = (ConversationData) selection.getFirstElement();

        if (selectedConversation != null) {
          // message tab
          messagesTableViewer.setInput(selectedConversation.getMessages());
          // hit nodes tab
          hitNodesTableViewer.setInput(selectedConversation.getHitNodes());
          // profile tab
          profileTableViewer.setInput(selectedConversation.getProfile());
          conversationDetailsSection.setVisible(true);
        } else {
          messagesTableViewer.setInput(Collections.EMPTY_LIST);
          hitNodesTableViewer.setInput(Collections.EMPTY_LIST);
          profileTableViewer.setInput(Collections.EMPTY_LIST);
          conversationDetailsSection.setVisible(false);
        }

        notifyChangePageStatus();
      }
    });

    restartConversationButton = toolkit.createButton(composite, "Restart Conversation", SWT.PUSH);
    layoutData = new GridData();
    layoutData.horizontalAlignment = SWT.RIGHT;
    restartConversationButton.setLayoutData(layoutData);
    restartConversationButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clickRestartConversationButton();
      }
    });

    section.setClient(composite);
  }

  /**
   *
   */
  private void clickShowConversationHistoryButton() {
    fromDateObj = getDateFromWidgets(fromDate);
    toDateObj = getDateFromWidgets(toDate);

    try {
      String dialogId = selectedDialog.getId();
      List<ConversationData> history = service.listConversationHistory(dialogId, fromDateObj, toDateObj);
      if (history.isEmpty()) {
        String title = "Show Dialog History";
        String message = "No conversation history in this period.";
        MessageDialog.openInformation(WidgetsFactory.getShell(), title, message);
      }
      historyTableViewer.setInput(history);
    } catch (Exception e) {
      IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e);
      String title = "Show Conversation History";
      String message = "Error occurred when retrieve conversation history.";
      ErrorDialog.openError(WidgetsFactory.getShell(), title, message, status);
    }
  }


  /**
   *
   */
  private void clickRestartConversationButton() {
    RunConversationDilaog dialog = new RunConversationDilaog(WidgetsFactory.getShell(), selectedDialog,
        selectedConversation);

    @SuppressWarnings("unchecked")
    List<ConversationData> historyList = (List<ConversationData>)historyTableViewer.getInput();
    int index = historyList.indexOf(selectedConversation);

    // refresh conversation history
    if (dialog.open() == Window.OK) {
      try {
        String dialogId = selectedDialog.getId();
        List<ConversationData> history = service.listConversationHistory(dialogId, fromDateObj, toDateObj);
        historyTableViewer.setInput(history);
        historyTableViewer.setSelection(new StructuredSelection(historyTableViewer.getElementAt(index)),true);
      } catch (Exception e) {
        IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e);
        String title = "Refresh Conversation History";
        String message = "Error occurred when retrieve conversation history.";
        ErrorDialog.openError(WidgetsFactory.getShell(), title, message, status);
      }
    }
  }

  /**
   *
   */
  private Date getDateFromWidgets(DateTime dateTime) {
    String year = Integer.toString(dateTime.getYear());
    String month = Integer.toString(dateTime.getMonth() + 1);
    String day = Integer.toString(dateTime.getDay());
    String hours = Integer.toString(dateTime.getHours());
    String minutes = Integer.toString(dateTime.getMinutes());
    String seconds = Integer.toString(dateTime.getSeconds());
    String dateStr = year + "/" + month + "/" + day + " " + hours + ":" + minutes + ":" + seconds;

    try {
      return dateFormat.parse(dateStr);
    } catch (ParseException e) {
      throw new WatsonDialogToolsException(e);
    }
  }

  /**
   *
   */
  private void notifyChangePageStatus() {
    if (selectedDialog == null) {
      updateDialogButton.setEnabled(false);
      deleteDialogButton.setEnabled(false);
      newConversationButton.setEnabled(false);
      showHistoryButton.setEnabled(false);
    } else {
      updateDialogButton.setEnabled(true);
      deleteDialogButton.setEnabled(true);
      newConversationButton.setEnabled(true);
      showHistoryButton.setEnabled(true);
    }

    if (selectedConversation == null) {
      restartConversationButton.setEnabled(false);
    } else {
      restartConversationButton.setEnabled(true);
    }
  }
}
