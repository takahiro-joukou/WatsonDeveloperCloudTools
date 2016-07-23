package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.SelectionTypeType;
import watson.dialog.tools.model.domain.IType.PromptType;
import watson.dialog.tools.model.domain.IType.SelectionType;

public class Prompt extends BaseModel<watson.dialog.tools.model.dao.xsd.PromptType> {

  private SelectionType selectionType;

  private PromptType promptType;

  private List<Item> items = new ArrayList<Item>();


  public SelectionType getSelectionType() {
    return selectionType;
  }

  public void setSelectionType(SelectionType selectionType) {
    if (peerType != null) {
      peerType.setSelectionType(SelectionTypeType.fromValue(selectionType.value()));
    }
    this.selectionType = selectionType;
  }

  public PromptType getPromptType() {
    return promptType;
  }

  public void setPromptType(PromptType promptType) {
    this.promptType = promptType;
  }

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }
}
