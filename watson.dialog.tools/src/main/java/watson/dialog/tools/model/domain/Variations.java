package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.VariationsType;

public class Variations extends BaseModel<VariationsType> {

  private List<Item> items = new ArrayList<Item>();

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }
}
