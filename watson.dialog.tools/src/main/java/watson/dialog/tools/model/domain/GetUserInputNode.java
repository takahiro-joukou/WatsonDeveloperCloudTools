package watson.dialog.tools.model.domain;

import watson.dialog.tools.model.dao.xsd.GetUserInputNodeType;

public class GetUserInputNode extends CompositeNode<GetUserInputNodeType> {

  public static final String NODE_NAME = "getUserInput";

  private boolean isDNRCandidate;

  public boolean getIsDNRCandidate() {
    return isDNRCandidate;
  }

  public void setIsDNRCandidate(boolean isDNRCandidate) {
    if (peerType != null) {
      peerType.setIsDNRCandidate(Boolean.toString(isDNRCandidate));
    }
    this.isDNRCandidate = isDNRCandidate;
  }
}
