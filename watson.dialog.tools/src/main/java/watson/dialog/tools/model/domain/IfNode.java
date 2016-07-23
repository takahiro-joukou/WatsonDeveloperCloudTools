package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.IfNodeType;
import watson.dialog.tools.model.dao.xsd.MatchTypeType;
import watson.dialog.tools.model.domain.IType.MatchType;

public class IfNode extends CompositeNode<IfNodeType> {

  public static final String NODE_NAME = "if";

  private MatchType matchType;

  private List<Cond> conds = new ArrayList<Cond>();

  public MatchType getMatchType() {
    return matchType;
  }

  public void setMatchType(MatchType type) {
    if (peerType != null) {
      peerType.setMatchType(MatchTypeType.fromValue(type.value()));
    }
    this.matchType = type;
  }

  public List<Cond> getConds() {
    return conds;
  }

  public void setConds(List<Cond> conds) {
    this.conds = conds;
  }
}
