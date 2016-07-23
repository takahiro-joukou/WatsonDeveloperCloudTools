package watson.dialog.tools.editor.dialogs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import watson.dialog.tools.Activator;
import watson.dialog.tools.editor.util.ImageFactory;
import watson.dialog.tools.editor.util.TableLabelProvider;
import watson.dialog.tools.editor.util.WidgetsFactory;
import watson.dialog.tools.model.service.WatsonDialogToolsService;

import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import com.ibm.watson.developer_cloud.dialog.v1.model.ConversationData;
import com.ibm.watson.developer_cloud.dialog.v1.model.Message;

public class RunConversationDilaog extends Dialog {

  /** Dialog Title */
  private static final String DIALOG_TITLE = "Run Dialog Service";

  /** Watson Dialog Tools Service */
  private WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  /** Watson Dialog */
  private com.ibm.watson.developer_cloud.dialog.v1.model.Dialog targetDialog;

  /** Watson Conversation */
  private Conversation targetConversation;

  /** Conversation Table Viewer */
  TableViewer conversationTableViewer;

  /** Conversation List */
  private List<TableData> conversationList = new ArrayList<TableData>();

  /** Date Format */
  private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");

  /** Send Message Text Form */
  private Text sendMessageText;

  public RunConversationDilaog(Shell shell, com.ibm.watson.developer_cloud.dialog.v1.model.Dialog targetDialog) {
    super(shell);
    setShellStyle(getShellStyle() | SWT.RESIZE);
    this.targetDialog = targetDialog;

    try {
      this.targetConversation = service.createConversation(targetDialog.getId());
    } catch (Exception e) {
      IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e);
      String title = "Run Dialog Instance";
      String message = "Error occurred when converse with dialog instance.";
      ErrorDialog.openError(WidgetsFactory.getShell(), title, message, status);
    }

    String dateTimeStr = dateFormat.format(Calendar.getInstance().getTime());
    for (String response : targetConversation.getResponse()) {
      if (!response.isEmpty()) {
        TableData data = new TableData();
        data.dateTime = dateTimeStr;
        data.message = response;
        data.type = Type.RESPONSE;
        conversationList.add(data);
      }
    }
  }

  public RunConversationDilaog(Shell shell, com.ibm.watson.developer_cloud.dialog.v1.model.Dialog targetDialog,
      ConversationData selectedConversation) {
    super(shell);
    setShellStyle(getShellStyle() | SWT.RESIZE);

    // setup dialog and conversation instance
    this.targetDialog = targetDialog;
    this.targetConversation = new Conversation();
    targetConversation.setId(selectedConversation.getConversationId());
    targetConversation.setDialogId(targetDialog.getId());
    targetConversation.setClientId(selectedConversation.getClientId());

    // set table data
    for (Message message : selectedConversation.getMessages()) {
      TableData data = new TableData();
      data.dateTime = dateFormat.format(message.getDateTime());
      data.message = message.getText();
      if ("true".equals(message.getFromClient())) {
        data.type = Type.SEND;
      } else {
        data.type = Type.RESPONSE;
      }
      conversationList.add(data);
    }
  }

  @Override
  public void create() {
    super.create();
    getShell().setText(DIALOG_TITLE);
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(new GridLayout(1, false));

    createLabels(composite);
    createConversationTable(composite);

    return composite;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    // create OK button
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
  }

  private void createLabels(Composite composite) {
    Composite labelComposite = new Composite(composite, SWT.NONE);
    labelComposite.setLayout(new GridLayout(2, false));

    // Dialog
    Label label = new Label(labelComposite, SWT.LEFT);
    label.setText("Dialog Id:");

    label = new Label(labelComposite, SWT.LEFT);
    label.setText(targetDialog.getId());

    // Conversation
    label = new Label(labelComposite, SWT.LEFT);
    label.setText("Conversation Id:");

    label = new Label(labelComposite, SWT.LEFT);
    label.setText(Integer.toString(targetConversation.getId()));
  }

  private void createConversationTable(Composite composite) {
    Composite messagesComposite = new Composite(composite, SWT.NONE);
    messagesComposite.setLayout(new GridLayout(2, false));

    sendMessageText = new Text(messagesComposite, SWT.SINGLE | SWT.BORDER);
    sendMessageText.setLayoutData(WidgetsFactory.createGridDataWithFullHorizontal());
    sendMessageText.setFocus();
    sendMessageText.addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.character == SWT.CR) {
          clickSendMessageButton();
        }
      }
      @Override
      public void keyPressed(KeyEvent e) {
      }
    });

    Button sendMessageButton = new Button(messagesComposite, SWT.PUSH);
    sendMessageButton.setText("Send Message");
    sendMessageButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clickSendMessageButton();
      }
    });

    Table conversationTable = new Table(messagesComposite, SWT.BORDER);
    conversationTable.setHeaderVisible(true);
    conversationTable.setLinesVisible(true);
    GridData layoutData = WidgetsFactory.createGridDataWithFullHorizontal();
    layoutData.heightHint = 500;
    layoutData.horizontalSpan = 2;
    conversationTable.setLayoutData(layoutData);

    //
    // work around following bug :
    //  - https://bugs.eclipse.org/bugs/show_bug.cgi?id=43910
    //
    TableColumn column1 = new TableColumn(conversationTable, SWT.NULL);
    column1.setText("dummyColumn");
    column1.setWidth(0);
    column1.setResizable(false);

    TableColumn column2 = new TableColumn(conversationTable, SWT.NULL);
    column2.setText("Date Time");
    column2.setWidth(120);

    TableColumn column3 = new TableColumn(conversationTable, SWT.NULL);
    column3.setText("Message Text");
    column3.setWidth(630);

    conversationTableViewer = new TableViewer(conversationTable);
    conversationTableViewer.setContentProvider(new ArrayContentProvider());
    conversationTableViewer.setLabelProvider(new TableLabelProvider() {
      @Override
      public String getColumnText(Object element, int columnIndex) {
        TableData tableData = (TableData) element;
        switch (columnIndex) {
        case 1:
          return tableData.dateTime;
        case 2:
          return tableData.message;
        default:
          return "";
        }
      }

      @Override
      public Image getColumnImage(Object element, int columnIndex) {
        TableData tableData = (TableData) element;
        switch (columnIndex) {
        case 2:
          if (tableData.type == Type.SEND) {
            return ImageFactory.getImage(ImageFactory.SEND_MSG_ICON);
          }
          if (tableData.type == Type.RESPONSE) {
            return ImageFactory.getImage(ImageFactory.RESPONSE_MSG_ICON);
          }
        default:
          return null;
        }
      }
    });
    conversationTableViewer.setInput(conversationList);
  }

  private void clickSendMessageButton() {
    String dateTimeStr = dateFormat.format(Calendar.getInstance().getTime());
    String message = sendMessageText.getText();
    sendMessageText.setText("");
    sendMessageText.setFocus();

    // add send message to table
    TableData data = new TableData();
    data.dateTime = dateTimeStr;
    data.message = message;
    data.type = Type.SEND;
    conversationList.add(data);

    try {
      // send and receive response
      Conversation conversation = service.sendMessage(targetConversation, message);

      // add response message to table
      dateTimeStr = dateFormat.format(Calendar.getInstance().getTime());
      for (String response : conversation.getResponse()) {
        if (!response.isEmpty()) {
          data = new TableData();
          data.dateTime = dateTimeStr;
          data.message = response;
          data.type = Type.RESPONSE;
          conversationList.add(data);
        }
      }
      conversationTableViewer.refresh();
    } catch (Exception e) {
      IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e);
      String title = "Run Dialog Instance";
      message = "Error occurred when converse with dialog instance.";
      ErrorDialog.openError(WidgetsFactory.getShell(), title, message, status);
    }
  }

  private class TableData {
    String dateTime;
    String message;
    Type type;
  }

  private enum Type {
    SEND,
    RESPONSE
  }
}
