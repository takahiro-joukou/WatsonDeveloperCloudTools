package watson.dialog.tools.editor.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import watson.dialog.tools.editor.util.ImageFactory;
import watson.dialog.tools.model.domain.BaseModel;
import watson.dialog.tools.model.domain.CompositeNode;
import watson.dialog.tools.model.domain.DefaultNode;
import watson.dialog.tools.model.domain.Flow;
import watson.dialog.tools.model.domain.FolderNode;
import watson.dialog.tools.model.domain.GetUserInputNode;
import watson.dialog.tools.model.domain.GoToNode;
import watson.dialog.tools.model.domain.Grammar;
import watson.dialog.tools.model.domain.IfNode;
import watson.dialog.tools.model.domain.InputNode;
import watson.dialog.tools.model.domain.Item;
import watson.dialog.tools.model.domain.OutputNode;
import watson.dialog.tools.model.domain.Prompt;
import watson.dialog.tools.model.domain.SearchNode;

public class SelectFlowNodeDialog extends ElementTreeSelectionDialog {

  /** Dialog Title */
  private static final String DIALOG_TITLE = "Select Flow Node";

  /** Dialog Message */
  private static final String DIALOG_MESSAGE = "Select reference flow node, then set node id to text field.";

  /** Label Provider */
  private static SelectNodeLabelProvider labelProvider = new SelectNodeLabelProvider();

  /** Content Provider */
  private static SelectNodeContentProvider contentProvider = new SelectNodeContentProvider();

  /**
   *
   * @param parent
   * @param flow
   */
  public SelectFlowNodeDialog(Shell parent, BaseModel<?> model) {
    super(parent, labelProvider, contentProvider);
    setTitle(DIALOG_TITLE);
    setMessage(DIALOG_MESSAGE);
    setAllowMultiple(false);

    while (true) {
      if (model instanceof Flow) {
        setInput((Flow) model);
        break;
      }
      model = model.getParent();
    }
  }

  @Override
  public Object[] getResult() {
    return super.getResult();
  }

  /**
   *
   */
  private static class SelectNodeContentProvider implements ITreeContentProvider {
    @Override
    public Object[] getElements(Object inputObj) {
      List<Object> root = new ArrayList<Object>();
      Flow flow = (Flow) inputObj;
      for (FolderNode folderNode : flow.getFolderNodes()) {
        String label = folderNode.getLabel();
        if (!FolderNode.CONCEPTS_FOLDER_LABEL.equals(label)) {
          root.add(folderNode);
        }
      }
      return root.toArray();
    }

    @Override
    public Object getParent(Object obj) {
      BaseModel<?> model = (BaseModel<?>) obj;
      return model.getParent();
    }

    @Override
    public Object[] getChildren(Object parentObj) {
      List<Object> objs = new ArrayList<Object>();
      if (parentObj instanceof CompositeNode) {
        objs.addAll(((CompositeNode<?>) parentObj).getChildren());
      }
      return objs.toArray();
    }

    @Override
    public boolean hasChildren(Object obj) {
      if (obj instanceof CompositeNode) {
        return !((CompositeNode<?>) obj).getChildren().isEmpty();
      }
      return false;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
  }

  /**
   *
   */
  private static class SelectNodeLabelProvider implements ILabelProvider {

    private static final int MAX_TEXT_LENGTH = 80;

    @Override
    public String getText(Object obj) {
      if (obj instanceof FolderNode) {
        FolderNode folder = (FolderNode) obj;
        return getFolderNodeTextLabel(folder);
      }
      if (obj instanceof InputNode) {
        InputNode inputNode = (InputNode) obj;
        return getInputNodeTextLabel(inputNode);
      }
      if (obj instanceof OutputNode) {
        OutputNode outputNode = (OutputNode) obj;
        return getOutputNodeTextLabel(outputNode);
      }
      if (obj instanceof GetUserInputNode) {
        GetUserInputNode getUserInputNode = (GetUserInputNode) obj;
        return getGetUserInputNodeTextLabel(getUserInputNode);
      }
      if (obj instanceof SearchNode) {
        SearchNode searchNode = (SearchNode) obj;
        return getSearchNodeTextLabel(searchNode);
      }
      if (obj instanceof IfNode) {
        IfNode ifNode = (IfNode) obj;
        return getIfNodeTextLabel(ifNode);
      }
      if (obj instanceof GoToNode) {
        GoToNode goToNode = (GoToNode) obj;
        return getGoToNodeTextLabel(goToNode);
      }
      if (obj instanceof DefaultNode) {
        DefaultNode defaultNode = (DefaultNode) obj;
        return getDefaultNodeTextLabel(defaultNode);
      }
      return "[" + obj + "]";
    }

    private String getFolderNodeTextLabel(FolderNode folder) {
      String text = "folder";
      String id = folder.getId();
      String label = folder.getLabel();
      if (id != null && !id.isEmpty()) {
        text = text + "@" + id;
      }
      if (label != null && !label.isEmpty()) {
        text = text + " : [" + label + "]";
      }
      return text;
    }

    private String getInputNodeTextLabel(InputNode inputNode) {
      String text = "input";
      String id = inputNode.getId();
      if (id != null && !id.isEmpty()) {
        text = text + "@" + id + " : \"";
      } else {
        text = text + " : \"";
      }
      for (Grammar grammar : inputNode.getGrammars()) {
        int size = grammar.getItems().size();
        for (int i = 0; i < size; i++) {
          Item item = grammar.getItems().get(i);
          text = text + item.getValue() + "\"";
          if (text.length() > MAX_TEXT_LENGTH) {
            return getEllipsis(text) + "\"";
          }
          if (i < size - 1) {
            text = text + ",";
          }
        }
      }
      return text;
    }

    private String getOutputNodeTextLabel(OutputNode outputNode) {
      String text = "output";
      String id = outputNode.getId();
      if (id != null && !id.isEmpty()) {
        text = text + "@" + id + " : \"";
      } else {
        text = text + " : \"";
      }
      for (Prompt prompt : outputNode.getPrompts()) {
        int size = prompt.getItems().size();
        for (int i = 0; i < size; i++) {
          Item item = prompt.getItems().get(i);
          text = text + item.getValue() + "\"";
          if (text.length() > MAX_TEXT_LENGTH) {
            return getEllipsis(text) + "\"";
          }
          if (i < size - 1) {
            text = text + ",";
          }
        }
      }
      return text;
    }

    private String getGetUserInputNodeTextLabel(GetUserInputNode getUserInputNode) {
      String text = "getUserInput";
      String id = getUserInputNode.getId();
      if (id != null && !id.isEmpty()) {
        text = text + "@" + id;
      }
      return text;
    }

    private String getSearchNodeTextLabel(SearchNode searchNode) {
      String text = "search";
      String id = searchNode.getId();
      String ref = searchNode.getRef();
      if (id != null && !id.isEmpty()) {
        text = text + "@" + id;
      }
      if (ref != null && !ref.isEmpty()) {
        return text + " : [" + ref + "]";
      }
      return text;
    }

    private String getIfNodeTextLabel(IfNode ifNode) {
      String text = "if";
      String id = ifNode.getId();
      if (id != null && !id.isEmpty()) {
        text = text + "@" + id;
      }
      return text;
    }

    private String getGoToNodeTextLabel(GoToNode goToNode) {
      String text = "goto";
      String id = goToNode.getId();
      String ref = goToNode.getRef();
      if (id != null && !id.isEmpty()) {
        text = text + "@" + id;
      }
      if (ref != null && !ref.isEmpty()) {
        return text + " : [" + ref + "]";
      }
      return text;
    }

    private String getDefaultNodeTextLabel(DefaultNode defaultNode) {
      String text = "default";
      String id = defaultNode.getId();
      if (id != null && !id.isEmpty()) {
        text = text + "@" + id;
      }
      return text;
    }

    private String getEllipsis(String str) {
      if (str.length() <= MAX_TEXT_LENGTH) {
        return str;
      }
      return str.substring(0, MAX_TEXT_LENGTH - 3) + "...";
    }

    @Override
    public Image getImage(Object obj) {
      if (obj instanceof BaseModel) {
        return ImageFactory.getImageByObj((BaseModel<?>) obj);
      }
      return null;
    }

    @Override
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isLabelProperty(Object obj, String property) {
      return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }
  }
}
