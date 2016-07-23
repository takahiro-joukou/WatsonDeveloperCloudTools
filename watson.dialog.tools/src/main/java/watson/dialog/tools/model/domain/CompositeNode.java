package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

public class CompositeNode<T> extends FlowNode<T> {

  private List<FlowNode<?>> children = new ArrayList<FlowNode<?>>();

  private List<Action> actions = new ArrayList<Action>();

  public List<FlowNode<?>> getChildren() {
    return children;
  }

  public void setChildren(List<FlowNode<?>> children) {
    this.children = children;
  }

  public List<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }
}
