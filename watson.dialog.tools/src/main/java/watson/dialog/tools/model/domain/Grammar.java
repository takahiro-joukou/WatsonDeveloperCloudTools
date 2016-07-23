package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.GrammarTypeType;
import watson.dialog.tools.model.domain.IType.GrammarType;

public class Grammar extends BaseModel<watson.dialog.tools.model.dao.xsd.GrammarType> {

  private GrammarType type;

  private List<Item> items = new ArrayList<Item>();

  public GrammarType getType() {
    return type;
  }

  public void setType(GrammarType type) {
    if (peerType != null) {
      peerType.setType(GrammarTypeType.fromValue(type.value()));
    }
    this.type = type;
  }

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }
}
