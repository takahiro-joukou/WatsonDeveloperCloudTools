package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.EntityType;

public class Entity extends BaseModel<EntityType> {

  public static final String DEFAULT_NAME = "FIXME : name";

  private String gid;

  private String date;

  private String name;

  private List<EntityValue> values = new ArrayList<EntityValue>();

  public String getGid() {
    return gid;
  }

  public void setGid(String gid) {
    this.gid = gid;
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

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<EntityValue> getEntityValues() {
    return values;
  }

  public void setValues(List<EntityValue> values) {
    this.values = values;
  }
}
