package watson.dialog.tools.editor.pages.edit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import watson.dialog.tools.editor.util.ImageFactory;
import watson.dialog.tools.exception.WatsonDialogToolsException;
import watson.dialog.tools.model.dao.WatsonDialogXsdDao;
import watson.dialog.tools.model.dao.xsd.DialogType;
import watson.dialog.tools.model.domain.BaseModel;
import watson.dialog.tools.model.domain.CompositeNode;
import watson.dialog.tools.model.domain.ConceptNode;
import watson.dialog.tools.model.domain.Constants;
import watson.dialog.tools.model.domain.DefaultNode;
import watson.dialog.tools.model.domain.Dialog;
import watson.dialog.tools.model.domain.Entities;
import watson.dialog.tools.model.domain.Entity;
import watson.dialog.tools.model.domain.Flow;
import watson.dialog.tools.model.domain.FlowNode;
import watson.dialog.tools.model.domain.FolderNode;
import watson.dialog.tools.model.domain.GetUserInputNode;
import watson.dialog.tools.model.domain.GoToNode;
import watson.dialog.tools.model.domain.Grammar;
import watson.dialog.tools.model.domain.IType;
import watson.dialog.tools.model.domain.IType.VarFolderType;
import watson.dialog.tools.model.domain.IfNode;
import watson.dialog.tools.model.domain.InputNode;
import watson.dialog.tools.model.domain.Item;
import watson.dialog.tools.model.domain.OutputNode;
import watson.dialog.tools.model.domain.Prompt;
import watson.dialog.tools.model.domain.SearchNode;
import watson.dialog.tools.model.domain.Settings;
import watson.dialog.tools.model.domain.SpecialSetting;
import watson.dialog.tools.model.domain.SpecialSettings;
import watson.dialog.tools.model.domain.Var;
import watson.dialog.tools.model.domain.VarFolder;
import watson.dialog.tools.model.domain.Variables;
import watson.dialog.tools.model.domain.builder.DomainBuilder;
import watson.dialog.tools.model.service.WatsonDialogToolsService;

public class EditPageMasterSectionPart extends SectionPart {

  /** Dialog Tree Viewer */
  private TreeViewer dialogTreeViewer;

  /** Watson Dialog Tools Service */
  private WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  /**
   *
   * @param parent
   * @param toolkit
   * @param style
   */
  public EditPageMasterSectionPart(Composite parent, IManagedForm managedForm) {
    super(parent, managedForm.getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
    managedForm.addPart(this);
    createContent(managedForm.getToolkit());
  }

  /**
   *
   * @return
   */
  public TreeViewer getDialogTreeViewer() {
    return dialogTreeViewer;
  }

  /**
   *
   * @param toolkit
   */
  private void createContent(FormToolkit toolkit) {
    Section section = getSection();
    section.setText("Watson Dialog Service Elements");
    section.setDescription("Select and edit elements of Watson Dialog Service.");

    Composite composite = toolkit.createComposite(section);
    composite.setLayout(new GridLayout());

    this.dialogTreeViewer = createTreeView(toolkit, composite);
    dialogTreeViewer.setInput(createDomaingModel());
    dialogTreeViewer.expandAll();
    section.setClient(composite);
  }

  /**
   *
   * @param toolkit
   * @param composite
   * @return
   */
  private TreeViewer createTreeView(FormToolkit toolkit, Composite composite) {
    final Tree tree = toolkit.createTree(composite, SWT.NULL);

    GridData layoutData = new GridData();
    layoutData.horizontalAlignment = GridData.FILL;
    layoutData.verticalAlignment = GridData.FILL;
    layoutData.grabExcessHorizontalSpace = true;
    layoutData.grabExcessVerticalSpace = true;
    layoutData.widthHint = 350;
    layoutData.heightHint = 450;
    tree.setLayoutData(layoutData);

    TreeViewer dialogTreeViewer = new TreeViewer(tree);
    dialogTreeViewer.setContentProvider(new ContentProvider());
    dialogTreeViewer.setLabelProvider(new TreeLabelProvicer());
    dialogTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        getManagedForm().fireSelectionChanged(EditPageMasterSectionPart.this, event.getSelection());
      }
    });
    Transfer[] transferTypes = { LocalSelectionTransfer.getTransfer() };
    dialogTreeViewer.addDragSupport(DND.DROP_MOVE, transferTypes, new DialogTreeDragListener(dialogTreeViewer));
    dialogTreeViewer.addDropSupport(DND.DROP_MOVE, transferTypes, new DialogTreeDropListener(dialogTreeViewer));

    final Menu menu = new Menu(tree);
    tree.setMenu(menu);
    menu.addMenuListener(new DialogTreeMenuAdapter(menu, dialogTreeViewer));
    return dialogTreeViewer;
  }

  private void setSelection(BaseModel<?> model) {
    List<Object> objs = new ArrayList<Object>();
    while (model.getParent() != null) {
      objs.add(model);
      model = model.getParent();
    }
    objs.add(model);
    Collections.reverse(objs);
    dialogTreeViewer.setSelection(new TreeSelection(new TreePath(objs.toArray())));
  }

  /**
   *
   * @return
   */
  private Dialog createDomaingModel() {
    IFile file = ((IFileEditorInput) getManagedForm().getInput()).getFile();
    try {
      InputStream input = file.getContents();
      DialogType type = service.readDialogServiceFile(input);
      return DomainBuilder.build(type);
    } catch (CoreException e) {
      throw new WatsonDialogToolsException(e);
    }
  }

  @Override
  public void commit(boolean onSave) {
    super.commit(onSave);
    if (!onSave) {
      return;
    }

    Dialog dialog = (Dialog) dialogTreeViewer.getInput();
    IFile file = ((IFileEditorInput) getManagedForm().getInput()).getFile();
    try {
      file.setContents(WatsonDialogXsdDao.getWriteStream(dialog.getPeerType()), true, false, null);
    } catch (CoreException e) {
      throw new WatsonDialogToolsException(e);
    }
  }

  /**
   *
   *
   */
  private class ContentProvider implements ITreeContentProvider {
    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object[] getElements(Object inputObj) {
      List<Object> root = new ArrayList<Object>();

      Dialog dialog = (Dialog) inputObj;
      root.add(dialog.getFlow());
      root.addAll(dialog.getEntitiesList());
      root.addAll(dialog.getConstantsList());
      root.addAll(dialog.getVariablesList());
      root.addAll(dialog.getSettingsList());
      root.addAll(dialog.getSpecialSettingsList());

      return root.toArray();
    }

    @Override
    public Object[] getChildren(Object parentObj) {
      List<Object> objs = new ArrayList<Object>();
      if (parentObj instanceof Flow) {
        Flow flow = (Flow) parentObj;
        objs.addAll(flow.getFolderNodes());
      }
      if (parentObj instanceof CompositeNode) {
        objs.addAll(((CompositeNode<?>) parentObj).getChildren());
      }
      if (parentObj instanceof Entities) {
        Entities entities = (Entities) parentObj;
        objs.addAll(entities.getEntities());
      }
      if (parentObj instanceof Constants) {
        Constants constants = (Constants) parentObj;
        objs.addAll(constants.getVarFolders());
      }
      if (parentObj instanceof Variables) {
        Variables variables = (Variables) parentObj;
        objs.addAll(variables.getVarFolders());
      }
      if (parentObj instanceof VarFolder) {
        VarFolder varFolder = (VarFolder) parentObj;
        objs.addAll(varFolder.getVarFolders());
        objs.addAll(varFolder.getVars());
      }
      if (parentObj instanceof SpecialSettings) {
        SpecialSettings specialSettings = (SpecialSettings) parentObj;
        objs.addAll(specialSettings.getSpecialSettings());
      }
      return objs.toArray();
    }

    @Override
    public Object getParent(Object obj) {
      BaseModel<?> model = (BaseModel<?>) obj;
      return model.getParent();
    }

    @Override
    public boolean hasChildren(Object obj) {
      if (obj instanceof Flow) {
        Flow flow = (Flow) obj;
        return !flow.getFolderNodes().isEmpty();
      }
      if (obj instanceof CompositeNode) {
        return !((CompositeNode<?>) obj).getChildren().isEmpty();
      }
      if (obj instanceof Entities) {
        Entities entities = (Entities) obj;
        return !entities.getEntities().isEmpty();
      }
      if (obj instanceof Constants) {
        Constants constants = (Constants) obj;
        return !constants.getVarFolders().isEmpty();
      }
      if (obj instanceof Variables) {
        Variables variables = (Variables) obj;
        return !variables.getVarFolders().isEmpty();
      }
      if (obj instanceof VarFolder) {
        VarFolder varFolder = (VarFolder) obj;
        if (!varFolder.getVarFolders().isEmpty() || !varFolder.getVars().isEmpty()) {
          return true;
        }
      }
      if (obj instanceof SpecialSettings) {
        SpecialSettings specialSettings = (SpecialSettings) obj;
        return !specialSettings.getSpecialSettings().isEmpty();
      }
      return false;
    }
  }

  /**
   *
   */
  private class TreeLabelProvicer implements ILabelProvider {

    private static final int MAX_TEXT_LENGTH = 80;

    @Override
    public Image getImage(Object obj) {
      if (obj instanceof BaseModel) {
        return ImageFactory.getImageByObj((BaseModel<?>) obj);
      }
      return null;
    }

    @Override
    public String getText(Object obj) {
      if (obj instanceof Flow) {
        return Flow.NODE_NAME;
      }
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
      if (obj instanceof ConceptNode) {
        ConceptNode conceptNode = (ConceptNode) obj;
        return getConceptNodeTextLabel(conceptNode);
      }
      if (obj instanceof Entities) {
        Entities entities = (Entities) obj;
        return getEntitiesTextLabel(entities);
      }
      if (obj instanceof Entity) {
        Entity entity = (Entity) obj;
        return getEntityTextLabel(entity);
      }
      if (obj instanceof Constants) {
        Constants constants = (Constants) obj;
        return getConstantsTextLabel(constants);
      }
      if (obj instanceof Variables) {
        Variables variables = (Variables) obj;
        return getVariablesTextLabel(variables);
      }
      if (obj instanceof VarFolder) {
        VarFolder varFolder = (VarFolder) obj;
        return getVarFolderTextLabel(varFolder);
      }
      if (obj instanceof Var) {
        Var var = (Var) obj;
        return getVarTextLabel(var);
      }
      if (obj instanceof Settings) {
        Settings settings = (Settings) obj;
        return getSettingsTextLabel(settings);
      }
      if (obj instanceof SpecialSettings) {
        SpecialSettings specialSettings = (SpecialSettings) obj;
        return getSpecialSettingsTextLabel(specialSettings);
      }
      if (obj instanceof SpecialSetting) {
        SpecialSetting specialSetting = (SpecialSetting) obj;
        return getSpecialSettingTextLabel(specialSetting);
      }
      return "[" + obj + "]";
    }

    private String getFolderNodeTextLabel(FolderNode folder) {
      String text = FolderNode.NODE_NAME;
      String label = folder.getLabel();
      if (label != null && !label.isEmpty()) {
        return text + " : [" + label + "]";
      }
      return text;
    }

    private String getInputNodeTextLabel(InputNode inputNode) {
      String text = InputNode.NODE_NAME + " : \"";
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
      String text = OutputNode.NODE_NAME + " : \"";
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
      String text = GetUserInputNode.NODE_NAME;
      return text;
    }

    private String getSearchNodeTextLabel(SearchNode searchNode) {
      String text = SearchNode.NODE_NAME;
      String ref = searchNode.getRef();
      if (ref != null && !ref.isEmpty()) {
        return text + " : [" + ref + "]";
      }
      return text;
    }

    private String getIfNodeTextLabel(IfNode ifNode) {
      String text = IfNode.NODE_NAME;
      return text;
    }

    private String getGoToNodeTextLabel(GoToNode goToNode) {
      String text = GoToNode.NODE_NAME;
      String ref = goToNode.getRef();
      if (ref != null && !ref.isEmpty()) {
        return text + " : [" + ref + "]";
      }
      return text;
    }

    private String getDefaultNodeTextLabel(DefaultNode defaultNode) {
      String text = DefaultNode.NODE_NAME;
      return text;
    }

    private String getConceptNodeTextLabel(ConceptNode conceptNode) {
      String text = ConceptNode.NODE_NAME + " : ";
      for (Grammar grammar : conceptNode.getGrammars()) {
        text = text + "[";
        int size = grammar.getItems().size();
        for (int i = 0; i < size; i++) {
          Item item = grammar.getItems().get(i);
          text = text + item.getValue();
          if (text.length() > MAX_TEXT_LENGTH) {
            return getEllipsis(text);
          }
          if (i < size - 1) {
            text = text + ",";
          }
        }
        text = text + "]";
      }
      return text;
    }

    private String getEntitiesTextLabel(Entities entities) {
      String text = "entities";
      return text;
    }

    private String getEntityTextLabel(Entity entity) {
      String text = "entity";
      String name = entity.getName();
      if (name != null && !name.isEmpty()) {
        return text + " : [" + name + "]";
      }
      return text;
    }

    private String getConstantsTextLabel(Constants variables) {
      String text = "constants";
      return text;
    }

    private String getVariablesTextLabel(Variables variables) {
      String text = "variables";
      return text;
    }

    private String getVarFolderTextLabel(VarFolder varFolder) {
      String text = "var_folder";
      String name = varFolder.getName();
      if (name != null && !name.isEmpty()) {
        return text + " : [" + name + "]";
      }
      return text;
    }

    private String getVarTextLabel(Var var) {
      String text = "var";
      String name = var.getName();
      if (name != null && !name.isEmpty()) {
        return text + " : [" + name + "]";
      }
      return text;
    }

    private String getSettingsTextLabel(Settings settings) {
      String text = "settings";
      return text;
    }

    private String getSpecialSettingsTextLabel(SpecialSettings specialSettings) {
      String text = "specialSettings";
      return text;
    }

    private String getSpecialSettingTextLabel(SpecialSetting specialSetting) {
      String text = "specialSetting";
      String label = specialSetting.getLabel();
      if (label != null && !label.isEmpty()) {
        return text + " : [" + label + "]";
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
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
      return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }
  }

  /**
   *
   */
  private class DialogTreeMenuAdapter extends MenuAdapter {

    private Menu menu;

    public DialogTreeMenuAdapter(Menu menu, TreeViewer viewer) {
      this.menu = menu;
    }

    public void menuShown(MenuEvent e) {
      // dispose all context menu
      MenuItem[] items = menu.getItems();
      for (int i = 0; i < items.length; i++) {
        items[i].dispose();
      }

      // recreate context menu
      TreeSelection selection = (TreeSelection) dialogTreeViewer.getSelection();
      final BaseModel<?> node = (BaseModel<?>) selection.getFirstElement();
      if (node instanceof FolderNode) {
        FolderNode folder = (FolderNode) node;
        if (!folder.isChildOfConceptsFolder()) {
          getCreateFolderMenu(folder);
          getCreateInputMenu(folder);
          getCreateOutputMenu(folder);
          getCreateGetUserInputMenu(folder);
          getCreateIfMenu(folder);
          getCreateGoToMenu(folder);
          getSeparatorMenu();
          getDeleteMenu(folder);
        } else {
          getCreateFolderMenu(folder);
          getCreateConceptMenu(folder);
          getSeparatorMenu();
          getDeleteMenu(folder);
        }
      }
      if (node instanceof InputNode) {
        InputNode inputNode = (InputNode) node;
        getCreateInputMenu(inputNode);
        getCreateOutputMenu(inputNode);
        getCreateIfMenu(inputNode);
        getCreateGoToMenu(inputNode);
        getSeparatorMenu();
        getDeleteMenu(inputNode);
      }
      if (node instanceof OutputNode) {
        OutputNode outputNode = (OutputNode) node;
        getCreateOutputMenu(outputNode);
        getCreateGetUserInputMenu(outputNode);
        getCreateIfMenu(outputNode);
        getCreateGoToMenu(outputNode);
        getSeparatorMenu();
        getDeleteMenu(outputNode);
      }
      if (node instanceof GetUserInputNode) {
        GetUserInputNode getUserInputNode = (GetUserInputNode) node;
        getCreateInputMenu(getUserInputNode);
        getCreateOutputMenu(getUserInputNode);
        getCreateSearchMenu(getUserInputNode);
        getCreateIfMenu(getUserInputNode);
        getCreateGoToMenu(getUserInputNode);
        getCreateDefaultMenu(getUserInputNode);
        getSeparatorMenu();
        getDeleteMenu(getUserInputNode);
      }
      if (node instanceof SearchNode) {
        getDeleteMenu(node);
      }
      if (node instanceof IfNode) {
        IfNode ifNode = (IfNode) node;
        getCreateInputMenu(ifNode);
        getCreateOutputMenu(ifNode);
        getCreateGetUserInputMenu(ifNode);
        getCreateIfMenu(ifNode);
        getCreateGoToMenu(ifNode);
        getSeparatorMenu();
        getDeleteMenu(ifNode);
      }
      if (node instanceof GoToNode) {
        getDeleteMenu(node);
      }
      if (node instanceof DefaultNode) {
        DefaultNode defaultNode = (DefaultNode) node;
        getCreateOutputMenu(defaultNode);
        getCreateGetUserInputMenu(defaultNode);
        getCreateIfMenu(defaultNode);
        getCreateGoToMenu(defaultNode);
        getSeparatorMenu();
        getDeleteMenu(defaultNode);
      }
      if (node instanceof ConceptNode) {
        getDeleteMenu(node);
      }
      if (node instanceof Entities) {
        Entities entities = (Entities) node;
        getCreateEntitiesMenu(entities);
        getCreateEntityMenu(entities);
        // Dialog should have more than one entities obj.
        if (((Dialog)entities.getParent()).getEntitiesList().size() > 1) {
          getSeparatorMenu();
          getDeleteMenu(node);
        }
      }
      if (node instanceof Entity) {
        getDeleteMenu(node);
      }
      if (node instanceof Constants) {
        getCreateVarFolderMenu(node);
      }
      if (node instanceof Variables) {
        getCreateVarFolderMenu(node);
      }
      if (node instanceof VarFolder) {
        VarFolder varFolder = (VarFolder) node;
        getCreateVarFolderMenu(varFolder);
        getCreateVarMenu(varFolder);
        getSeparatorMenu();
        getDeleteMenu(varFolder);
      }
      if (node instanceof Var) {
        getDeleteMenu(node);
      }
      if (node instanceof SpecialSettings) {
        SpecialSettings specialSettings = (SpecialSettings) node;
        getCreateSpecialSettingMenu(specialSettings);
      }
      if (node instanceof SpecialSetting) {
        getDeleteMenu(node);
      }
    }

    private void getSeparatorMenu() {
      new MenuItem(menu, SWT.SEPARATOR);
    }

    private void getCreateFolderMenu(final CompositeNode<?> parent) {
      MenuItem createOutputMenu = new MenuItem(menu, SWT.NULL);
      createOutputMenu.setImage(ImageFactory.getImage(ImageFactory.FOLDER_ICON));
      createOutputMenu.setText("Create Folder Node");
      createOutputMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          FolderNode child = service.createFolderNode(parent);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateInputMenu(final CompositeNode<?> parent) {
      MenuItem createInputMenu = new MenuItem(menu, SWT.NULL);
      createInputMenu.setImage(ImageFactory.getImage(ImageFactory.INPUT_ICON));
      createInputMenu.setText("Create Input Node");
      createInputMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          InputNode child = service.createInputNode(parent);
          Grammar grammar = service.createGrammar(child);
          service.createItem(grammar);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateOutputMenu(final CompositeNode<?> parent) {
      MenuItem createOutputMenu = new MenuItem(menu, SWT.NULL);
      createOutputMenu.setImage(ImageFactory.getImage(ImageFactory.OUTPUT_ICON));
      createOutputMenu.setText("Create Output Node");
      createOutputMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          OutputNode child = service.createOutputNode(parent);
          Prompt prompt = service.createPrompt(child);
          service.createItem(prompt);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateGetUserInputMenu(final CompositeNode<?> parent) {
      MenuItem createGetUserInputMenu = new MenuItem(menu, SWT.NULL);
      createGetUserInputMenu.setImage(ImageFactory.getImage(ImageFactory.GETUSERINPUT_ICON));
      createGetUserInputMenu.setText("Create GetUserInput Node");
      createGetUserInputMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          GetUserInputNode child = service.createGetUserInputNode(parent);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateSearchMenu(final CompositeNode<?> parent) {
      MenuItem createSearchMenu = new MenuItem(menu, SWT.NULL);
      createSearchMenu.setImage(ImageFactory.getImage(ImageFactory.SEARCH_ICON));
      createSearchMenu.setText("Create Search Node");
      createSearchMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          SearchNode child = service.createSearchNode(parent);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateIfMenu(final CompositeNode<?> parent) {
      MenuItem createIfMenu = new MenuItem(menu, SWT.NULL);
      createIfMenu.setImage(ImageFactory.getImage(ImageFactory.IF_ICON));
      createIfMenu.setText("Create If Node");
      createIfMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          IfNode child = service.createIfNode(parent);
          service.createCond(child);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateDefaultMenu(final CompositeNode<?> parent) {
      MenuItem createDefaultMenu = new MenuItem(menu, SWT.NULL);
      createDefaultMenu.setImage(ImageFactory.getImage(ImageFactory.DEFAULT_ICON));
      createDefaultMenu.setText("Create Default Node");
      createDefaultMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          DefaultNode child = service.createDefaultNode(parent);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateGoToMenu(final CompositeNode<?> parent) {
      MenuItem createGoToMenu = new MenuItem(menu, SWT.NULL);
      createGoToMenu.setImage(ImageFactory.getImage(ImageFactory.GOTO_ICON));
      createGoToMenu.setText("Create GoTo Node");
      createGoToMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          GoToNode child = service.createGoToNode(parent);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateConceptMenu(final CompositeNode<?> parent) {
      MenuItem createConceptMenu = new MenuItem(menu, SWT.NULL);
      createConceptMenu.setImage(ImageFactory.getImage(ImageFactory.CONCEPT_ICON));
      createConceptMenu.setText("Create Concept Node");
      createConceptMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          ConceptNode child = service.createConceptNode(parent);
          Grammar grammar = service.createGrammar(child);
          service.createItem(grammar);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateEntitiesMenu(final Entities parent) {
      MenuItem createEntityMenu = new MenuItem(menu, SWT.NULL);
      createEntityMenu.setImage(ImageFactory.getImage(ImageFactory.ENTITIES_ICON));
      createEntityMenu.setText("Create Entities Node");
      createEntityMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          Entities child = service.createEntities((Dialog)parent.getParent());
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateEntityMenu(final Entities parent) {
      MenuItem createEntityMenu = new MenuItem(menu, SWT.NULL);
      createEntityMenu.setImage(ImageFactory.getImage(ImageFactory.ENTITY_ICON));
      createEntityMenu.setText("Create Entity Node");
      createEntityMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          Entity child = service.createEntity(parent);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateVarFolderMenu(final BaseModel<?> parent) {
      MenuItem createVarFolderMenu = new MenuItem(menu, SWT.NULL);
      createVarFolderMenu.setImage(ImageFactory.getImage(ImageFactory.VARFOLDER_ICON));
      createVarFolderMenu.setText("Create VarFolder Node");
      createVarFolderMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          VarFolder child = service.createVarFolder(parent, getVarFolderType(parent));
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateVarMenu(final VarFolder parent) {
      MenuItem createVarMenu = new MenuItem(menu, SWT.NULL);
      createVarMenu.setImage(ImageFactory.getImage(ImageFactory.VAR_ICON));
      createVarMenu.setText("Create Var Node");
      createVarMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          Var child = service.createVar(parent);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getCreateSpecialSettingMenu(final SpecialSettings parent) {
      MenuItem createVarMenu = new MenuItem(menu, SWT.NULL);
      createVarMenu.setImage(ImageFactory.getImage(ImageFactory.SPECIAL_SETTING_ICON));
      createVarMenu.setText("Create SpecialSetting Node");
      createVarMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          SpecialSetting child = service.createSpecialSetting(parent);
          markDirtyAndSetSelection(child);
        }
      });
    }

    private void getDeleteMenu(final BaseModel<?> node) {
      MenuItem deleteMenu = new MenuItem(menu, SWT.NULL);
      deleteMenu.setText("Delete Node");
      deleteMenu.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          BaseModel<?> parent = node.getParent();
          service.deleteModel(node);
          markDirtyAndSetSelection(parent);
        }
      });
    }

    private VarFolderType getVarFolderType(BaseModel<?> model) {
      while (true) {
        if (model instanceof Constants) {
          return IType.VarFolderType.CONST;
        }
        if (model instanceof Variables) {
          return IType.VarFolderType.VAR;
        }
        model = model.getParent();
      }
    }

    private void markDirtyAndSetSelection(BaseModel<?> selection) {
      markDirty();
      dialogTreeViewer.refresh();
      setSelection(selection);
    }
  }

  /**
   *
   */
  private class DialogTreeDragListener extends DragSourceAdapter {

    private ISelectionProvider selectionProvicer;

    public DialogTreeDragListener(ISelectionProvider selectionProvider) {
      this.selectionProvicer = selectionProvider;
    }

    @Override
    public void dragSetData(DragSourceEvent event) {
      ISelection selection = (ISelection) selectionProvicer.getSelection();

      LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
      if (transfer.isSupportedType(event.dataType)) {
        transfer.setSelection(selection);
        transfer.setSelectionSetTime(event.time & 0xFFFF);
      }
    }


  }

  /**
   *
   */
  private class DialogTreeDropListener extends ViewerDropAdapter {

    public DialogTreeDropListener(Viewer viewer) {
      super(viewer);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean performDrop(Object data) {
      List list = null;

      Object selectedObj = getSelectedObject();
      Object targetObj = getCurrentTarget();
      if (selectedObj instanceof FlowNode) {
        FlowNode<?> drop = (FlowNode<?>) targetObj;
        CompositeNode<?> parent =  (CompositeNode<?>) drop.getParent();
        list = parent.getChildren();
      }
      if (selectedObj instanceof Entities) {
        Entities entities = (Entities) selectedObj;
        Dialog dialog =  (Dialog) entities.getParent();
        list = dialog.getEntitiesList();
      }
      if (selectedObj instanceof Entity) {
        Entity entity = (Entity) selectedObj;
        Entities entities =  (Entities) entity.getParent();
        list = entities.getEntities();
      }
      if (selectedObj instanceof VarFolder) {
        VarFolder varFolder = (VarFolder) selectedObj;
        if (varFolder.getParent() instanceof Constants) {
          Constants variables = (Constants) varFolder.getParent();
          list = variables.getVarFolders();
        }
        if (varFolder.getParent() instanceof Variables) {
          Variables variables = (Variables) varFolder.getParent();
          list = variables.getVarFolders();
        }
        if (varFolder.getParent() instanceof VarFolder) {
          VarFolder parentVarFolder = (VarFolder) varFolder.getParent();
          list = parentVarFolder.getVarFolders();
        }
      }
      if (selectedObj instanceof Var) {
        Var var = (Var) selectedObj;
        VarFolder varFolder =  (VarFolder) var.getParent();
        list = varFolder.getVars();
      }
      if (selectedObj instanceof SpecialSetting) {
        SpecialSetting specialSetting = (SpecialSetting) selectedObj;
        SpecialSettings specialSettings =  (SpecialSettings) specialSetting.getParent();
        list = specialSettings.getSpecialSettings();
      }

      int before = list.indexOf(selectedObj);
      int after = list.indexOf(targetObj);

      service.reorderDialogNode(list, before, after);

      markDirty();
      getViewer().refresh();
      return true;
    }

    @Override
    public boolean validateDrop(Object target, int operation, TransferData transferType) {

      if (!LocalSelectionTransfer.getTransfer().isSupportedType(transferType)) {
        return false;
      }
      if (operation != DND.DROP_MOVE) {
        return false;
      }

      Object selectedObj = getSelectedObject();
      if (target == selectedObj) {
        return false;
      }

      if (selectedObj instanceof FlowNode) {
        FlowNode<?> dragObj = (FlowNode<?>) selectedObj;
        BaseModel<?> parent = dragObj.getParent();
        // can't move direct child of flow node
        if (parent instanceof Flow) {
          return false;
        }
      }
      if ((selectedObj instanceof Variables) || (selectedObj instanceof Constants) || (selectedObj instanceof Settings)
          || (selectedObj instanceof SpecialSettings)) {
        return false;
      }

      //
      // Support only re-order node. If target nodes have the same parent node, then return true.
      //
      if ((target instanceof FlowNode) && (selectedObj instanceof FlowNode)) {
        FlowNode<?> dragObj = (FlowNode<?>) selectedObj;
        FlowNode<?> dropObj = (FlowNode<?>) target;
        if (dragObj.getParent() == dropObj.getParent()) {
          return true;
        }
      }
      if ((target instanceof Entities) && (selectedObj instanceof Entities)) {
        return true;
      }
      if ((target instanceof Entity) && (selectedObj instanceof Entity)) {
        Entity dragObj = (Entity) selectedObj;
        Entity dropObj = (Entity) target;
        if (dragObj.getParent() == dropObj.getParent()) {
          return true;
        }
      }
      if ((target instanceof VarFolder) && (selectedObj instanceof VarFolder)) {
        VarFolder dragObj = (VarFolder) selectedObj;
        VarFolder dropObj = (VarFolder) target;
        if (dragObj.getParent() == dropObj.getParent()) {
          return true;
        }
      }
      if ((target instanceof Var) && (selectedObj instanceof Var)) {
        Var dragObj = (Var) selectedObj;
        Var dropObj = (Var) target;
        if (dragObj.getParent() == dropObj.getParent()) {
          return true;
        }
      }
      if ((target instanceof SpecialSetting) && (selectedObj instanceof SpecialSetting)) {
        return true;
      }
      return false;
    }
  }
}