package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.ConceptNodeType;

public class ConceptNode extends FlowNode<ConceptNodeType> {

  public static final String NODE_NAME = "concept";

  String description;

  String ref;

  private List<Grammar> grammars = new ArrayList<Grammar>();

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    if (peerType != null) {
      peerType.setDescription(description);
    }
    this.description = description;
  }

  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public List<Grammar> getGrammars() {
    return grammars;
  }

  public void setGrammars(List<Grammar> grammars) {
    this.grammars = grammars;
  }
}
