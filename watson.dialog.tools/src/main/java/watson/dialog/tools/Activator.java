package watson.dialog.tools;

import static watson.dialog.tools.editor.preference.WatsonDialogServicePrefInitializer.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import watson.dialog.tools.model.service.WatsonDialogToolsService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "watson.dialog.tools";

  // The shared instance
  private static Activator plugin;

  /** Watson Dialog Tools Service */
  private WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  /**
   * The constructor
   */
  public Activator() {
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;

    //load plugin preferences
    loadPluginPreferences();
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static Activator getDefault() {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given
   * plug-in relative path
   *
   * @param path the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path) {
    return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  /**
   *
   */
  private void loadPluginPreferences() {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    String username = store.getString(KEY_DIALOG_SERVICE_USER_NAME);
    String password = store.getString(KEY_DIALOG_SERVICE_PASSWORD);

    service.setCredential(username, password);
  }
}
