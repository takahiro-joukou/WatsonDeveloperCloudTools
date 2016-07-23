package watson.dialog.tools.model.domain;

import watson.dialog.tools.model.dao.xsd.FolderNodeType;
import watson.dialog.tools.model.dao.xsd.SelectionTypeType;
import watson.dialog.tools.model.domain.IType.SelectionType;

public class FolderNode extends CompositeNode<FolderNodeType> {

  public static final String MAIN_FOLDER_LABEL = "Main";

  public static final String LIBRARY_FOLDER_LABEL = "Library";

  public static final String GLOBAL_FOLDER_LABEL = "Global";

  public static final String CONCEPTS_FOLDER_LABEL = "Concepts";

  public static final String NODE_NAME = "folder";

  private String label;

  private SelectionType selectionType;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    if (peerType != null) {
      peerType.setLabel(label);
    }
    this.label = label;
  }

  public SelectionType getSelectionType() {
    return selectionType;
  }

  public void setSelectionType(SelectionType selectionType) {
    if (peerType != null) {
      peerType.setSelectionType(SelectionTypeType.valueOf(selectionType.value()));
    }
    this.selectionType = selectionType;
  }

  public boolean isChildOfConceptsFolder() {
    BaseModel<?> model = this;
    while (true) {
      if (model instanceof FolderNode) {
        FolderNode folderNode = (FolderNode) model;
        if ((folderNode.getParent() instanceof Flow) && (CONCEPTS_FOLDER_LABEL.equals(folderNode.getLabel()))) {
          return true;
        }
      }
      if (model instanceof Flow) {
        return false;
      }
      model = model.getParent();
    }
  }
}
