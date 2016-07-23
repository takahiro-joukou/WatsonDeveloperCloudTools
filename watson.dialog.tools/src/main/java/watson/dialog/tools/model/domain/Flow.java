package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.FlowType;

public class Flow extends BaseModel<FlowType> {

  public static final String NODE_NAME = "flow";

  private List<FolderNode> folderNodes = new ArrayList<FolderNode>();

  public List<FolderNode> getFolderNodes() {
    return folderNodes;
  }

  public void setFolderNodes(List<FolderNode> folderNodes) {
    this.folderNodes = folderNodes;
  }
}
