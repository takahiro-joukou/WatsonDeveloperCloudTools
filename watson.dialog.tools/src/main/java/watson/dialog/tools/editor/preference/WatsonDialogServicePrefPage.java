package watson.dialog.tools.editor.preference;

import static watson.dialog.tools.editor.preference.WatsonDialogServicePrefInitializer.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import watson.dialog.tools.Activator;
import watson.dialog.tools.model.service.WatsonDialogToolsService;

public class WatsonDialogServicePrefPage extends PreferencePage implements IWorkbenchPreferencePage {

  /** Watson Dialog Tools Service */
  private WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  public static final String PAGE_ID = "watson.dialog.tools.editor.preference.WatsonDialogPrefPage";

  private Text usernameText;
  private Text passwordText;

  @Override
  public void init(IWorkbench workbench) {
  }

  @Override
  protected Control createContents(Composite parent) {
    setTitle("Watson Dialog Service Editor Preference");

    IPreferenceStore store = Activator.getDefault().getPreferenceStore();

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));

    Group credentialGroup = new Group(composite, SWT.SHADOW_NONE);
    credentialGroup.setLayout(new GridLayout(2, false));
    credentialGroup.setText("Dialog Service Credential");

    GridData layoutData = new GridData();
    layoutData.grabExcessHorizontalSpace = true;
    layoutData.horizontalAlignment = GridData.FILL;
    credentialGroup.setLayoutData(layoutData);

    usernameText = createLabelAndText("username:", credentialGroup);
    usernameText.setText(store.getString(KEY_DIALOG_SERVICE_USER_NAME));

    passwordText = createLabelAndText("password:", credentialGroup);
    passwordText.setText(store.getString(KEY_DIALOG_SERVICE_PASSWORD));

    credentialGroup.pack();

    return composite;
  }

  private Text createLabelAndText(String labelText, Group credentialGroup) {
    Label label = new Label(credentialGroup, SWT.NONE);
    label.setText(labelText);

    Text text = new Text(credentialGroup, SWT.SINGLE | SWT.BORDER);
    GridData layoutData = new GridData();
    layoutData.grabExcessHorizontalSpace = true;
    layoutData.horizontalAlignment = GridData.FILL;
    text.setLayoutData(layoutData);

    return text;
  }

  @Override
  public boolean performOk() {
    String username = usernameText.getText();
    String password = passwordText.getText();

    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setValue(KEY_DIALOG_SERVICE_USER_NAME, username);
    store.setValue(KEY_DIALOG_SERVICE_PASSWORD, password);

    // set credential to service instance object
    service.setCredential(username, password);

    return true;
  }

  @Override
  protected void performDefaults() {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();

    String username = store.getDefaultString(KEY_DIALOG_SERVICE_USER_NAME);
    usernameText.setText(username);

    String password = store.getDefaultString(KEY_DIALOG_SERVICE_PASSWORD);
    passwordText.setText(password);
  }
}
