package watson.dialog.tools.model.domain;

import watson.dialog.tools.model.dao.xsd.VarTypeType;
import watson.dialog.tools.model.domain.IType.VarType;

public class Var extends BaseModel<watson.dialog.tools.model.dao.xsd.VarType> {

  public static final String DEFAULT_VAR_NAME = "FIXME : name";

  public static final String NODE_NAME = "var";

  private String gid;

  private String id;

  private String name;

  private String description;

  private String initValue;

  private VarType type;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (peerType != null) {
      peerType.setName(name);
    }
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    if (peerType != null) {
      peerType.setDescription(description);
    }
    this.description = description;
  }

  public String getInitValue() {
    return initValue;
  }

  public void setInitValue(String initValue) {
    if (peerType != null) {
      peerType.setInitValue(initValue);
    }
    this.initValue = initValue;
  }

  public VarType getType() {
    return type;
  }

  public void setType(VarType type) {
    if (peerType != null) {
      peerType.setType(VarTypeType.valueOf(type.value()));
    }
    this.type = type;
  }
}
