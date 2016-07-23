package watson.dialog.tools.model.domain;

import watson.dialog.tools.model.dao.xsd.ActionType;
import watson.dialog.tools.model.domain.IType.ActionOperatorType;

public class Action extends BaseModel<ActionType> {

  public static final String DEFAULT_VAR_NAME = "FIXME : varName";

  public static final String DEFAULT_VALUE = "FIXME : value";

  public static final ActionOperatorType DEFAULT_OPERATOR_TYPE = ActionOperatorType.DO_NOTHING_STR;

  public static final String NODE_NAME = "action";

  private String gid;

  private String id;

  private String varName;

  private ActionOperatorType operatorType;

  private String value;

  public String getGid() {
    return gid;
  }

  public void setGid(String gid) {
    this.gid = gid;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    if (peerType != null) {
      peerType.setId(id);
    }
    this.id = id;
  }

  public String getVarName() {
    return varName;
  }

  public void setVarName(String varName) {
    if (peerType != null) {
      peerType.setVarName(varName);
    }
    this.varName = varName;
  }

  public ActionOperatorType getOperatorType() {
    return operatorType;
  }

  public void setOperatorType(ActionOperatorType type) {
    if (peerType != null) {
      peerType.setOperator(watson.dialog.tools.model.dao.xsd.ActionOperatorType.fromValue(type.value()));
    }
    this.operatorType = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    if (peerType != null) {
      peerType.setValue(value);
    }
    this.value = value;
  }
}
