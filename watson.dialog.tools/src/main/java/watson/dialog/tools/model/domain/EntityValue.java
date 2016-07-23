package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.EntityValueType;

public class EntityValue extends BaseModel<EntityValueType> {

  public static final String DEFAULT_NAME = "FIXME : name";

  public static final String DEFAULT_VALUE = "FIXME : value";

  private String name;

  private String value;

  private List<Grammar> grammars = new ArrayList<Grammar>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (peerType != null) {
      peerType.setName(name);
    }
    this.name = name;
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

  public List<Grammar> getGrammars() {
    return grammars;
  }

  public void setGrammars(List<Grammar> grammars) {
    this.grammars = grammars;
  }
}
