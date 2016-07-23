package watson.dialog.tools.model.domain;

import watson.dialog.tools.model.dao.xsd.CondType;
import watson.dialog.tools.model.domain.IType.ConditionOperatorType;

public class Cond extends BaseModel<CondType> {

  public static final String DEFAULT_VAR_NAME = "FIXME : varName";

  public static final String DEFAULT_VALUE = "FIXME : value";

  public static final ConditionOperatorType DEFAULT_OPERATOR_TYPE = ConditionOperatorType.EQUALS;

  public static final String NODE_NAME = "cond";

  private String gid;

  private String id;

  private String varName;

  private ConditionOperatorType operatorType;

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

  public ConditionOperatorType getOperatorType() {
    return operatorType;
  }

  public void setOperatorType(ConditionOperatorType type) {
    if (peerType != null) {
      peerType.setOperator(watson.dialog.tools.model.dao.xsd.ConditionOperatorType.fromValue(type.value()));
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
