package watson.dialog.tools.model.domain;

import watson.dialog.tools.model.dao.xsd.SearchNodeType;

public class SearchNode extends CompositeNode<SearchNodeType> {

  public static final String NODE_NAME = "search";

  String ref;

  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    if (peerType != null) {
      peerType.setRef(ref);
    }
    this.ref = ref;
  }
}
