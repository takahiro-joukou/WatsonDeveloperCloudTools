package watson.dialog.tools.editor.preference;



import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import watson.dialog.tools.Activator;

public class WatsonDialogServicePrefInitializer extends AbstractPreferenceInitializer {

  public static final String KEY_DIALOG_SERVICE_USER_NAME = "DIALOG_SERVICE_USER_NAME";
  public static final String KEY_DIALOG_SERVICE_PASSWORD = "DIALOG_SERVICE_PASSWORD";

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setDefault(KEY_DIALOG_SERVICE_USER_NAME,   "username");
    store.setDefault(KEY_DIALOG_SERVICE_PASSWORD, "password");
  }
}
