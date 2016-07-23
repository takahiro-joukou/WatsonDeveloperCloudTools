package watson.dialog.tools.editor.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import watson.dialog.tools.Activator;
import watson.dialog.tools.model.domain.BaseModel;
import watson.dialog.tools.model.domain.ConceptNode;
import watson.dialog.tools.model.domain.Constants;
import watson.dialog.tools.model.domain.DefaultNode;
import watson.dialog.tools.model.domain.Entities;
import watson.dialog.tools.model.domain.Entity;
import watson.dialog.tools.model.domain.Flow;
import watson.dialog.tools.model.domain.FolderNode;
import watson.dialog.tools.model.domain.GetUserInputNode;
import watson.dialog.tools.model.domain.GoToNode;
import watson.dialog.tools.model.domain.IfNode;
import watson.dialog.tools.model.domain.InputNode;
import watson.dialog.tools.model.domain.OutputNode;
import watson.dialog.tools.model.domain.SearchNode;
import watson.dialog.tools.model.domain.Settings;
import watson.dialog.tools.model.domain.SpecialSetting;
import watson.dialog.tools.model.domain.SpecialSettings;
import watson.dialog.tools.model.domain.Var;
import watson.dialog.tools.model.domain.VarFolder;
import watson.dialog.tools.model.domain.Variables;

public class ImageFactory {

  /** 16x16 Icon Key */
  public static final String FLOW_ICON = "icons/flow_node.png";

  public static final String FOLDER_ICON = "icons/folder_node.png";

  public static final String INPUT_ICON = "icons/input_node.png";

  public static final String OUTPUT_ICON = "icons/output_node.png";

  public static final String GETUSERINPUT_ICON = "icons/get_user_input_node.png";

  public static final String SEARCH_ICON = "icons/search_node.png";

  public static final String IF_ICON = "icons/if_node.png";

  public static final String GOTO_ICON = "icons/goto_node.png";

  public static final String DEFAULT_ICON = "icons/default.png";

  public static final String CONCEPT_ICON = "icons/concept_node.png";

  public static final String ENTITIES_ICON = "icons/entities.png";

  public static final String ENTITY_ICON = "icons/entity.png";

  public static final String CONSTANTS_ICON = "icons/constants.png";

  public static final String VARIABLES_ICON = "icons/variables.png";

  public static final String VARFOLDER_ICON = "icons/var_folder.png";

  public static final String VAR_ICON = "icons/var.png";

  public static final String SETTINGS_ICON = "icons/settings.png";

  public static final String SPECIAL_SETTINGS_ICON = "icons/special_settings.png";

  public static final String SPECIAL_SETTING_ICON = "icons/special_setting.png";

  /** 32x32 Icon Key */
  public static final String SEND_MSG_ICON = "icons/send_msg.png";

  public static final String RESPONSE_MSG_ICON = "icons/response_msg.png";


  /** ImageRegistry */
  private static ImageRegistry imageRegistry = new ImageRegistry();

  private ImageFactory() {
  }

  /**
   *
   * @param key
   * @return
   */
  public static Image getImage(String key) {
    Image image = imageRegistry.get(key);
    if (image == null) {
      ImageDescriptor descriptor = Activator.getImageDescriptor(key);
      imageRegistry.put(key, descriptor);
      image = descriptor.createImage();
    }
    return image;
  }

  /**
   *
   * @param model
   * @return
   */
  public static Image getImageByObj(BaseModel<?> model) {
    if (model instanceof Flow) {
      return getImage(FLOW_ICON);
    }
    if (model instanceof FolderNode) {
      return getImage(FOLDER_ICON);
    }
    if (model instanceof InputNode) {
      return getImage(INPUT_ICON);
    }
    if (model instanceof OutputNode) {
      return getImage(OUTPUT_ICON);
    }
    if (model instanceof GetUserInputNode) {
      return getImage(GETUSERINPUT_ICON);
    }
    if (model instanceof SearchNode) {
      return getImage(SEARCH_ICON);
    }
    if (model instanceof IfNode) {
      return getImage(IF_ICON);
    }
    if (model instanceof GoToNode) {
      return getImage(GOTO_ICON);
    }
    if (model instanceof DefaultNode) {
      return getImage(DEFAULT_ICON);
    }
    if (model instanceof ConceptNode) {
      return getImage(CONCEPT_ICON);
    }
    if (model instanceof Entities) {
      return getImage(ENTITIES_ICON);
    }
    if (model instanceof Entity) {
      return getImage(ENTITY_ICON);
    }
    if (model instanceof Constants) {
      return getImage(CONSTANTS_ICON);
    }
    if (model instanceof Variables) {
      return getImage(VARIABLES_ICON);
    }
    if (model instanceof VarFolder) {
      return getImage(VARFOLDER_ICON);
    }
    if (model instanceof Var) {
      return getImage(VAR_ICON);
    }
    if (model instanceof Settings) {
      return getImage(SETTINGS_ICON);
    }
    if (model instanceof SpecialSettings) {
      return getImage(SPECIAL_SETTINGS_ICON);
    }
    if (model instanceof SpecialSetting) {
      return getImage(SPECIAL_SETTING_ICON);
    }

    return null;
  }
}
