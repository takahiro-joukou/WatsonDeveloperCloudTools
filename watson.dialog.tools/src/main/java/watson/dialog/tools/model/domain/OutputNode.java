package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.OutputNodeType;

public class OutputNode extends CompositeNode<OutputNodeType> {

  public static final String NODE_NAME = "output";

  private boolean isInsertDNRStatement;

  private String ref;

  private List<Prompt> prompts = new ArrayList<Prompt>();

  public boolean getIsInsertDNRStatement() {
    return isInsertDNRStatement;
  }

  public void setIsInsertDNRStatement(boolean isInsertDNRStatement) {
    if (peerType != null) {
      peerType.setIsInsertDNRStatement(isInsertDNRStatement);
    }
    this.isInsertDNRStatement = isInsertDNRStatement;
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

  public List<Prompt> getPrompts() {
    return prompts;
  }

  public void setPrompts(List<Prompt> prompts) {
    this.prompts = prompts;
  }
}
