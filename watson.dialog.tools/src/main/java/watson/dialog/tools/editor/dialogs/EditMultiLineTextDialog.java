package watson.dialog.tools.editor.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditMultiLineTextDialog extends Dialog {

  private static final String DIALOG_TITLE = "Edit Value";

  private String value;

  public EditMultiLineTextDialog(Shell shell, String value) {
    super(shell);
    setShellStyle(getShellStyle() | SWT.RESIZE);
    this.value = value;
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

    final Text text = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);

    GridData layoutData = new GridData();
    layoutData.horizontalAlignment = GridData.FILL;
    layoutData.verticalAlignment = GridData.FILL;
    layoutData.grabExcessHorizontalSpace = true;
    layoutData.grabExcessVerticalSpace = true;
    layoutData.heightHint = 80;
    layoutData.widthHint = 350;
    text.setLayoutData(layoutData);
    text.setText(value);
    text.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        value = text.getText();
      }
    });

    return composite;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
