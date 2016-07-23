package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.InputNodeType;

public class InputNode extends CompositeNode<InputNodeType> {

  public static final String NODE_NAME = "input";

  private boolean isAutoLearnCandidate;

  private String ref;

  private List<Grammar> grammars = new ArrayList<Grammar>();

  public boolean getIsAutoLearnCandidate() {
    return isAutoLearnCandidate;
  }

  public void setIsAutoLearnCandidate(boolean isAutoLearnCandidate) {
    if (peerType != null) {
      peerType.setIsAutoLearnCandidate(isAutoLearnCandidate);
    }
    this.isAutoLearnCandidate = isAutoLearnCandidate;
  }

  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    if (peerType != null) {
      peerType.setRef(ref);
    }
    this.ref = ref;
  }

  public List<Grammar> getGrammars() {
    return grammars;
  }

  public void setGrammars(List<Grammar> grammars) {
    this.grammars = grammars;
  }
}
