package watson.dialog.tools.model.domain;

import watson.dialog.tools.model.dao.xsd.GotoNodeType;

public class GoToNode extends CompositeNode<GotoNodeType> {

  public static final String NODE_NAME = "goto";

  private static final String DEFAULT_REF_VALUE = "FIXME : Auto-generated ref value";

  private String ref = DEFAULT_REF_VALUE;

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
