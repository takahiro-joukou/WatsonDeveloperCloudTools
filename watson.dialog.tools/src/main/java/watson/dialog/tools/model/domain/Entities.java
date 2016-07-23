package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.EntitiesType;

public class Entities extends BaseModel<EntitiesType> {

  private List<Entity> entities = new ArrayList<Entity>();

  public List<Entity> getEntities() {
    return entities;
  }

  public void setEntities(List<Entity> entities) {
    this.entities = entities;
  }
}
