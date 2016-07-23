package watson.dialog.tools.model.service;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import watson.dialog.tools.model.dao.WatsonDialogXsdDao;
import watson.dialog.tools.model.dao.xsd.ActionType;
import watson.dialog.tools.model.dao.xsd.ChatflowNode;
import watson.dialog.tools.model.dao.xsd.ConceptNodeType;
import watson.dialog.tools.model.dao.xsd.CondType;
import watson.dialog.tools.model.dao.xsd.ConstantsType;
import watson.dialog.tools.model.dao.xsd.DefaultNodeType;
import watson.dialog.tools.model.dao.xsd.DialogType;
import watson.dialog.tools.model.dao.xsd.EntitiesType;
import watson.dialog.tools.model.dao.xsd.EntityType;
import watson.dialog.tools.model.dao.xsd.EntityValueType;
import watson.dialog.tools.model.dao.xsd.FolderNodeType;
import watson.dialog.tools.model.dao.xsd.GetUserInputNodeType;
import watson.dialog.tools.model.dao.xsd.GotoNodeType;
import watson.dialog.tools.model.dao.xsd.GrammarType;
import watson.dialog.tools.model.dao.xsd.IfNodeType;
import watson.dialog.tools.model.dao.xsd.InputNodeType;
import watson.dialog.tools.model.dao.xsd.ObjectFactory;
import watson.dialog.tools.model.dao.xsd.OutputNodeType;
import watson.dialog.tools.model.dao.xsd.PromptType;
import watson.dialog.tools.model.dao.xsd.SearchNodeType;
import watson.dialog.tools.model.dao.xsd.SettingType;
import watson.dialog.tools.model.dao.xsd.SettingsType;
import watson.dialog.tools.model.dao.xsd.SpecialSettingType;
import watson.dialog.tools.model.dao.xsd.SpecialSettingsType;
import watson.dialog.tools.model.dao.xsd.VarFolderType;
import watson.dialog.tools.model.dao.xsd.VarType;
import watson.dialog.tools.model.dao.xsd.VariablesType;
import watson.dialog.tools.model.dao.xsd.VariationsType;
import watson.dialog.tools.model.domain.Action;
import watson.dialog.tools.model.domain.BaseModel;
import watson.dialog.tools.model.domain.CompositeNode;
import watson.dialog.tools.model.domain.ConceptNode;
import watson.dialog.tools.model.domain.Cond;
import watson.dialog.tools.model.domain.Constants;
import watson.dialog.tools.model.domain.DefaultNode;
import watson.dialog.tools.model.domain.Entities;
import watson.dialog.tools.model.domain.Entity;
import watson.dialog.tools.model.domain.EntityValue;
import watson.dialog.tools.model.domain.FlowNode;
import watson.dialog.tools.model.domain.FolderNode;
import watson.dialog.tools.model.domain.GetUserInputNode;
import watson.dialog.tools.model.domain.GoToNode;
import watson.dialog.tools.model.domain.Grammar;
import watson.dialog.tools.model.domain.IType;
import watson.dialog.tools.model.domain.IType.MatchType;
import watson.dialog.tools.model.domain.IType.SelectionType;
import watson.dialog.tools.model.domain.IfNode;
import watson.dialog.tools.model.domain.InputNode;
import watson.dialog.tools.model.domain.Item;
import watson.dialog.tools.model.domain.OutputNode;
import watson.dialog.tools.model.domain.Prompt;
import watson.dialog.tools.model.domain.SearchNode;
import watson.dialog.tools.model.domain.Setting;
import watson.dialog.tools.model.domain.Settings;
import watson.dialog.tools.model.domain.SpecialSetting;
import watson.dialog.tools.model.domain.SpecialSettings;
import watson.dialog.tools.model.domain.Var;
import watson.dialog.tools.model.domain.VarFolder;
import watson.dialog.tools.model.domain.Variables;
import watson.dialog.tools.model.domain.Variations;

import com.ibm.watson.developer_cloud.dialog.v1.DialogService;
import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import com.ibm.watson.developer_cloud.dialog.v1.model.ConversationData;
import com.ibm.watson.developer_cloud.dialog.v1.model.Dialog;
import com.ibm.watson.developer_cloud.dialog.v1.model.Message;

public class WatsonDialogToolsService {

  /** Instance */
  private static WatsonDialogToolsService instance = new WatsonDialogToolsService();

  /** Wastson Developer Cloud SDK */
  DialogService sdkService = new DialogService();

  /** Xsd Object Factory */
  private ObjectFactory xsdObjFactory = new ObjectFactory();

  /** Id Map */
  private HashMap<String, BaseModel<?>> idMap = new HashMap<String, BaseModel<?>>();

  /** Id Generate Counter */
  private HashMap<String, Integer> counterMap = new HashMap<String, Integer>();

  /**
   * Default constructor.
   */
  private WatsonDialogToolsService() {
    //nop
  }

  /**
   *
   * @return
   */
  public static WatsonDialogToolsService getInstance() {
    return instance;
  }

  /**
   * @param file
   * @return
   */
  public DialogType readDialogServiceFile(File file) {
    return WatsonDialogXsdDao.read(file);
  }

  /**
   *
   * @param input
   * @return
   */
  public DialogType readDialogServiceFile(InputStream input) {
    return WatsonDialogXsdDao.read(input);
  }

  /**
   *
   * @param dialog
   * @param file
   */
  public void wrieteDialogServiceFile(DialogType dialogType, File file) {
    WatsonDialogXsdDao.write(dialogType, file);
  }

  /**
   *
   * @param id
   * @param model
   */
  public void regsiterModel(String id, BaseModel<?> model) {
    if (id != null) {
      idMap.put(id, model);
    }
  }

  /**
   *
   * @param model
   */
  public void unregsiterModel(BaseModel<?> model) {
    String id = null;
    if (model instanceof CompositeNode) {
      id = ((CompositeNode<?>) model).getId();
    }
    if (model instanceof VarFolder) {
      id = ((VarFolder) model).getId();
    }
    if (model instanceof Var) {
      id = ((Var) model).getId();
    }
    if (model instanceof Action) {
      id = ((Action) model).getId();
    }
    if (model != null) {
      idMap.remove(id);
    }
  }

  /**
   *
   * @param nodeName
   * @return
   */
  private String generateId(String nodeName, BaseModel<?> model) {
    Integer counterInteger = counterMap.get(nodeName);
    if (counterInteger == null) {
      counterInteger = Integer.valueOf(0);
    }
    while (true) {
      counterInteger = counterInteger + 1;
      String id = nodeName + "_" + String.format("%05d", counterInteger);
      if (!idMap.containsKey(id)) {
        counterMap.put(nodeName, counterInteger);
        regsiterModel(id, model);
        return id;
      }
    }
  }

  /**
   *
   * @param parent
   * @return
   */
  public FolderNode createFolderNode(CompositeNode<?> parent) {
    FolderNode folderNode = new FolderNode();
    folderNode.setParent(parent);
    parent.getChildren().add(folderNode);

    FolderNodeType childType = xsdObjFactory.createFolderNodeType();
    if (parent instanceof FolderNode) {
      FolderNodeType parentType = ((FolderNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    folderNode.setPeerType(childType);
    folderNode.setId(generateId(FolderNode.NODE_NAME, folderNode));
    folderNode.setSelectionType(SelectionType.SEQUENTIAL);

    return folderNode;
  }

  /**
   *
   * @param parent
   * @return
   */
  public InputNode createInputNode(CompositeNode<?> parent) {
    InputNode inputNode = new InputNode();
    inputNode.setParent(parent);
    parent.getChildren().add(inputNode);

    InputNodeType childType = xsdObjFactory.createInputNodeType();
    if (parent instanceof FolderNode) {
      FolderNodeType parentType = ((FolderNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof InputNode) {
      InputNodeType parentType = ((InputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof GetUserInputNode) {
      GetUserInputNodeType parentType = ((GetUserInputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof IfNode) {
      IfNodeType parentType = ((IfNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    inputNode.setPeerType(childType);
    inputNode.setId(generateId(InputNode.NODE_NAME, inputNode));

    return inputNode;
  }

  /**
  *
  * @param parent
  * @return
  */
  public OutputNode createOutputNode(CompositeNode<?> parent) {
    OutputNode outputNode = new OutputNode();
    outputNode.setParent(parent);
    parent.getChildren().add(outputNode);

    OutputNodeType childType = xsdObjFactory.createOutputNodeType();
    if (parent instanceof FolderNode) {
      FolderNodeType parentType = ((FolderNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof InputNode) {
      InputNodeType parentType = ((InputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof OutputNode) {
      OutputNodeType parentType = ((OutputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof GetUserInputNode) {
      GetUserInputNodeType parentType = ((GetUserInputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof IfNode) {
      IfNodeType parentType = ((IfNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof DefaultNode) {
      DefaultNodeType parentType = ((DefaultNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    outputNode.setPeerType(childType);
    outputNode.setId(generateId(OutputNode.NODE_NAME, outputNode));

    return outputNode;
  }

  /**
   *
   * @param parent
   * @return
   */
  public GetUserInputNode createGetUserInputNode(CompositeNode<?> parent) {
    GetUserInputNode getUserInputNode = new GetUserInputNode();
    getUserInputNode.setParent(parent);
    parent.getChildren().add(getUserInputNode);

    GetUserInputNodeType childType = xsdObjFactory.createGetUserInputNodeType();
    if (parent instanceof FolderNode) {
      FolderNodeType parentType = ((FolderNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof OutputNode) {
      OutputNodeType parentType = ((OutputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof IfNode) {
      IfNodeType parentType = ((IfNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof DefaultNode) {
      DefaultNodeType parentType = ((DefaultNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    getUserInputNode.setPeerType(childType);
    getUserInputNode.setId(generateId(GetUserInputNode.NODE_NAME, getUserInputNode));

    return getUserInputNode;
  }

  /**
   *
   * @param parent
   * @return
   */
  public SearchNode createSearchNode(CompositeNode<?> parent) {
    SearchNode searchNode = new SearchNode();
    searchNode.setParent(parent);
    parent.getChildren().add(searchNode);

    SearchNodeType childType = xsdObjFactory.createSearchNodeType();
    if (parent instanceof GetUserInputNode) {
      GetUserInputNodeType parentType = ((GetUserInputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    searchNode.setPeerType(childType);
    searchNode.setId(generateId(SearchNode.NODE_NAME, searchNode));

    return searchNode;
  }

  /**
   *
   * @param parent
   * @return
   */
  public DefaultNode createDefaultNode(CompositeNode<?> parent) {
    DefaultNode defaultNode = new DefaultNode();
    defaultNode.setParent(parent);
    parent.getChildren().add(defaultNode);

    DefaultNodeType childType = xsdObjFactory.createDefaultNodeType();
    if (parent instanceof GetUserInputNode) {
      GetUserInputNodeType parentType = ((GetUserInputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    defaultNode.setPeerType(childType);
    defaultNode.setId(generateId(DefaultNode.NODE_NAME, defaultNode));

    return defaultNode;
  }

  /**
   *
   * @param parent
   * @return
   */
  public GoToNode createGoToNode(CompositeNode<?> parent) {
    GoToNode goToNode = new GoToNode();
    goToNode.setParent(parent);
    parent.getChildren().add(goToNode);

    GotoNodeType childType = xsdObjFactory.createGotoNodeType();
    if (parent instanceof FolderNode) {
      FolderNodeType parentType = ((FolderNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof InputNode) {
      InputNodeType parentType = ((InputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof OutputNode) {
      OutputNodeType parentType = ((OutputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof GetUserInputNode) {
      GetUserInputNodeType parentType = ((GetUserInputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof IfNode) {
      IfNodeType parentType = ((IfNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof DefaultNode) {
      DefaultNodeType parentType = ((DefaultNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    goToNode.setPeerType(childType);
    goToNode.setId(generateId(GoToNode.NODE_NAME, goToNode));

    return goToNode;
  }

  /**
   *
   * @param parent
   * @return
   */
  public ConceptNode createConceptNode(CompositeNode<?> parent) {
    ConceptNode conceptNode = new ConceptNode();
    conceptNode.setParent(parent);
    parent.getChildren().add(conceptNode);

    ConceptNodeType childType = xsdObjFactory.createConceptNodeType();
    if (parent instanceof FolderNode) {
      FolderNodeType parentType = ((FolderNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    conceptNode.setPeerType(childType);
    conceptNode.setId(generateId(ConceptNode.NODE_NAME, conceptNode));

    return conceptNode;
  }

  /**
   *
   * @param parent
   * @return
   */
  public IfNode createIfNode(CompositeNode<?> parent) {
    IfNode ifNode = new IfNode();
    ifNode.setParent(parent);
    parent.getChildren().add(ifNode);

    IfNodeType childType = xsdObjFactory.createIfNodeType();
    if (parent instanceof FolderNode) {
      FolderNodeType parentType = ((FolderNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof InputNode) {
      InputNodeType parentType = ((InputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof OutputNode) {
      OutputNodeType parentType = ((OutputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof GetUserInputNode) {
      GetUserInputNodeType parentType = ((GetUserInputNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof IfNode) {
      IfNodeType parentType = ((IfNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    if (parent instanceof DefaultNode) {
      DefaultNodeType parentType = ((DefaultNode) parent).getPeerType();
      parentType.getInputOrOutputOrDefault().add(childType);
    }
    ifNode.setPeerType(childType);
    ifNode.setId(generateId(IfNode.NODE_NAME, ifNode));
    ifNode.setMatchType(MatchType.ALL);

    return ifNode;
  }

  /**
   *
   * @param parent
   * @return
   */
  public Cond createCond(IfNode parent) {
    Cond cond = new Cond();
    cond.setParent(parent);
    parent.getConds().add(cond);

    CondType childType = xsdObjFactory.createCondType();
    IfNodeType parentType = parent.getPeerType();
    parentType.getActionOrScriptOrCond().add(childType);

    cond.setPeerType(childType);
    cond.setId(generateId(Cond.NODE_NAME, cond));
    cond.setVarName(Cond.DEFAULT_VAR_NAME);
    cond.setOperatorType(Cond.DEFAULT_OPERATOR_TYPE);
    cond.setValue(Cond.DEFAULT_VALUE);

    return cond;
  }

  /**
  *
  * @param parent
  * @return
  */
  public Grammar createGrammar(BaseModel<?> parent) {
    Grammar grammar = new Grammar();
    grammar.setParent(parent);

    GrammarType childType = xsdObjFactory.createGrammarType();
    if (parent instanceof ConceptNode) {
      ConceptNode conceptNode = (ConceptNode) parent;
      conceptNode.getGrammars().add(grammar);

      ConceptNodeType parentType = conceptNode.getPeerType();
      parentType.getGrammarOrConceptOrFolder().add(childType);
    }
    if (parent instanceof InputNode) {
      InputNode inputNode = (InputNode) parent;
      inputNode.getGrammars().add(grammar);

      InputNodeType parentType = inputNode.getPeerType();
      parentType.getActionOrScriptOrGrammar().add(childType);
    }
    if (parent instanceof EntityValue) {
      EntityValue entityValue = (EntityValue) parent;
      entityValue.getGrammars().add(grammar);

      EntityValueType parentType = entityValue.getPeerType();
      parentType.getGrammar().add(childType);
    }
    grammar.setPeerType(childType);
    grammar.setType(watson.dialog.tools.model.domain.IType.GrammarType.VARIATIONS);

    return grammar;
  }

  /**
   *
   * @param parent
   * @return
   */
  public Prompt createPrompt(OutputNode parent) {
    Prompt prompt = new Prompt();
    prompt.setParent(parent);
    parent.getPrompts().add(prompt);

    OutputNodeType parentType = parent.getPeerType();
    PromptType childType = xsdObjFactory.createPromptType();
    parentType.getActionOrScriptOrPrompt().add(childType);

    prompt.setPeerType(childType);
    prompt.setSelectionType(SelectionType.SEQUENTIAL);

    return prompt;
  }

  /**
   *
   * @param parent
   * @return
   */
  public Item createItem(BaseModel<?> parent) {
    Item item = new Item();
    item.setParent(parent);

    if (parent instanceof Prompt) {
      Prompt prompt = (Prompt) parent;
      prompt.getItems().add(item);

      PromptType parentType = prompt.getPeerType();
      JAXBElement<String> xsdObj = xsdObjFactory.createPromptTypeItem(item.getValue());
      parentType.getContent().add(xsdObj);

      item.setPeerType(xsdObj);
      item.setValue(Item.DEFAULT_ITEM_VALUE);
    }
    if (parent instanceof Grammar) {
      Grammar grammar = (Grammar) parent;
      grammar.getItems().add(item);

      GrammarType parentType = grammar.getPeerType();
      JAXBElement<String> xsdObj = xsdObjFactory.createGrammarTypeItem(item.getValue());
      parentType.getItemOrSourceOrParam().add(xsdObj);

      item.setPeerType(xsdObj);
      item.setValue(Item.DEFAULT_ITEM_VALUE);
    }
    if (parent instanceof Variations) {
      Variations variations = (Variations) parent;
      variations.getItems().add(item);

      VariationsType parentType = variations.getPeerType();
      parentType.getItem().add(Item.DEFAULT_ITEM_VALUE);

      item.setValue(Item.DEFAULT_ITEM_VALUE);
    }

    return item;
  }

  /**
   *
   * @param parent
   * @return
   */
  public Entities createEntities(watson.dialog.tools.model.domain.Dialog parent) {
    Entities entities = new Entities();
    entities.setParent(parent);
    parent.getEntitiesList().add(entities);

    EntitiesType childType = xsdObjFactory.createEntitiesType();
    parent.getPeerType().getEntities().add(childType);
    entities.setPeerType(childType);

    return entities;
  }

  /**
   *
   * @param parent
   * @return
   */
  public Entity createEntity(Entities parent) {
    Entity entity = new Entity();
    entity.setParent(parent);
    parent.getEntities().add(entity);

    EntityType childType = xsdObjFactory.createEntityType();
    parent.getPeerType().getEntity().add(childType);
    entity.setPeerType(childType);
    entity.setName(Entity.DEFAULT_NAME);

    return entity;
  }

  /**
   *
   * @param parent
   * @return
   */
  public EntityValue createEntityValue(Entity parent) {
    EntityValue entityValue = new EntityValue();
    entityValue.setParent(parent);
    parent.getEntityValues().add(entityValue);

    EntityValueType childType = xsdObjFactory.createEntityValueType();
    parent.getPeerType().getValue().add(childType);
    entityValue.setPeerType(childType);
    entityValue.setName(EntityValue.DEFAULT_NAME);
    entityValue.setValue(EntityValue.DEFAULT_VALUE);

    return entityValue;
  }

  /**
   *
   * @param setttings
   * @return
   */
  public Setting createSetting(Settings parent) {
    Setting setting = new Setting();
    setting.setParent(parent);
    parent.getSettings().add(setting);

    SettingType childType = xsdObjFactory.createSettingType();
    parent.getPeerType().getSetting().add(childType);
    setting.setPeerType(childType);
    setting.setName(Setting.DEFAULT_NAME);
    setting.setValue(Setting.DEFAULT_VALUE);

    return setting;
  }

  /**
   *
   * @param parent
   * @return
   */
  public Variations createVariation(SpecialSetting parent) {
    Variations variations = new Variations();
    variations.setParent(parent);
    parent.getVariations().add(variations);

    SpecialSettingType parentType = parent.getPeerType();
    VariationsType childType = xsdObjFactory.createVariationsType();
    parentType.getVariations().add(childType);

    variations.setPeerType(childType);

    return variations;
  }

  /**
  *
  * @param parent
  * @return
  */
 public SpecialSetting createSpecialSetting(SpecialSettings parent) {
   SpecialSetting specialSetting = new SpecialSetting();
   specialSetting.setParent(parent);
   parent.getSpecialSettings().add(specialSetting);

   SpecialSettingType childType = xsdObjFactory.createSpecialSettingType();
   parent.getPeerType().getSpecialSetting().add(childType);
   specialSetting.setPeerType(childType);
   specialSetting.setId(generateId(SpecialSetting.NODE_NAME, specialSetting));
   specialSetting.setLabel(SpecialSetting.DEFAULT_SPECIAL_SETTING_TYPE_LABEL);

   return specialSetting;
 }

  /**
   *
   * @param parent
   * @param type
   * @return
   */
  public VarFolder createVarFolder(BaseModel<?> parent, IType.VarFolderType type) {
    VarFolder varFolder = new VarFolder();

    varFolder.setParent(parent);
    if (parent instanceof Constants) {
      Constants constants = (Constants) parent;
      constants.getVarFolders().add(varFolder);
    }
    if (parent instanceof Variables) {
      Variables variables = (Variables) parent;
      variables.getVarFolders().add(varFolder);
    }
    if (parent instanceof VarFolder) {
      VarFolder parentVarFolder = (VarFolder) parent;
      parentVarFolder.getVarFolders().add(varFolder);
    }

    VarFolderType childType = xsdObjFactory.createVarFolderType();
    if (parent instanceof Constants) {
      ConstantsType parentType = ((Constants) parent).getPeerType();
      parentType.getVarFolder().add(childType);
    }
    if (parent instanceof Variables) {
      VariablesType parentType = ((Variables) parent).getPeerType();
      parentType.getVarFolder().add(childType);
    }
    if (parent instanceof VarFolder) {
      VarFolderType parentType = ((VarFolder) parent).getPeerType();
      parentType.getVarFolder().add(childType);
    }

    varFolder.setPeerType(childType);
    varFolder.setId(generateId(VarFolder.NODE_NAME, varFolder));
    varFolder.setType(type);

    return varFolder;
  }

  /**
   *
   * @param parent
   * @return
   */
  public Var createVar(VarFolder parent) {
    Var var = new Var();

    var.setParent(parent);
    parent.getVars().add(var);

    VarType childType = xsdObjFactory.createVarType();
    VarFolderType parentType = ((VarFolder) parent).getPeerType();
    parentType.getVar().add(childType);

    var.setPeerType(childType);
    var.setId(generateId(Var.NODE_NAME, var));
    var.setName(Var.DEFAULT_VAR_NAME);
    var.setType(IType.VarType.TEXT);

    return var;
  }

  /**
   *
   * @param parent
   * @return
   */
  public Action createAction(CompositeNode<?> parent) {
    Action action = new Action();
    action.setParent(parent);
    parent.getActions().add(action);

    ActionType childXsdType = xsdObjFactory.createActionType();
    Object parentXsdType = parent.getPeerType();
    if (parentXsdType instanceof FolderNodeType) {
      ((FolderNodeType) parentXsdType).getActionOrScript().add(childXsdType);
    }
    if (parentXsdType instanceof InputNodeType) {
      ((InputNodeType) parentXsdType).getActionOrScriptOrGrammar().add(childXsdType);
    }
    if (parentXsdType instanceof OutputNodeType) {
      ((OutputNodeType) parentXsdType).getActionOrScriptOrPrompt().add(childXsdType);
    }
    if (parentXsdType instanceof GetUserInputNodeType) {
      ((GetUserInputNodeType) parentXsdType).getActionOrScript().add(childXsdType);
    }
    if (parentXsdType instanceof SearchNodeType) {
      ((SearchNodeType) parentXsdType).getActionOrScript().add(childXsdType);
    }
    if (parentXsdType instanceof IfNodeType) {
      ((IfNodeType) parentXsdType).getActionOrScriptOrCond().add(childXsdType);
    }
    if (parentXsdType instanceof GotoNodeType) {
      ((GotoNodeType) parentXsdType).getActionOrScript().add(childXsdType);
    }
    if (parentXsdType instanceof DefaultNodeType) {
      ((DefaultNodeType) parentXsdType).getActionOrScript().add(childXsdType);
    }

    action.setPeerType(childXsdType);
    action.setId(generateId(Action.NODE_NAME, action));
    action.setVarName(Action.DEFAULT_VAR_NAME);
    action.setOperatorType(Action.DEFAULT_OPERATOR_TYPE);
    action.setValue(Action.DEFAULT_VALUE);

    return action;
  }

  /**
   *
   * @param action
   */
  public void deleteAction(Action action) {
    CompositeNode<?> parent = (CompositeNode<?>) action.getParent();
    parent.getActions().remove(action);

    Object childXsdType = action.getPeerType();
    Object parentXsdType = (ChatflowNode) parent.getPeerType();
    if (parentXsdType instanceof FolderNodeType) {
      ((FolderNodeType) parentXsdType).getActionOrScript().remove(childXsdType);
    }
    if (parentXsdType instanceof InputNodeType) {
      ((InputNodeType) parentXsdType).getActionOrScriptOrGrammar().remove(childXsdType);
    }
    if (parentXsdType instanceof OutputNodeType) {
      ((OutputNodeType) parentXsdType).getActionOrScriptOrPrompt().remove(childXsdType);
    }
    if (parentXsdType instanceof GetUserInputNodeType) {
      ((GetUserInputNodeType) parentXsdType).getActionOrScript().remove(childXsdType);
    }
    if (parentXsdType instanceof SearchNodeType) {
      ((SearchNodeType) parentXsdType).getActionOrScript().remove(childXsdType);
    }
    if (parentXsdType instanceof IfNodeType) {
      ((IfNodeType) parentXsdType).getActionOrScriptOrCond().remove(childXsdType);
    }
    if (parentXsdType instanceof GotoNodeType) {
      ((GotoNodeType) parentXsdType).getActionOrScript().remove(childXsdType);
    }
    if (parentXsdType instanceof DefaultNodeType) {
      ((DefaultNodeType) parentXsdType).getActionOrScript().remove(childXsdType);
    }

    unregsiterModel(action);
  }

  /**
  *
  * @param action
  */
 public void deleteCond(Cond cond) {
   IfNode parent = (IfNode) cond.getParent();
   parent.getConds().remove(cond);

   CondType childXsdType = cond.getPeerType();
   IfNodeType parentXsdType = parent.getPeerType();
   parentXsdType.getActionOrScriptOrCond().remove(childXsdType);

   unregsiterModel(cond);
 }

  /**
   *
   * @param model
   */
  public void deleteModel(BaseModel<?> model) {
    //
    // delete model
    //
    if (model.getParent() instanceof watson.dialog.tools.model.domain.Dialog) {
      watson.dialog.tools.model.domain.Dialog parent = (watson.dialog.tools.model.domain.Dialog) model.getParent();
      if (model instanceof Entities) {
        parent.getEntitiesList().remove(model);
      }
    }
    if (model.getParent() instanceof CompositeNode) {
      CompositeNode<?> parent = (CompositeNode<?>) model.getParent();
      parent.getChildren().remove(model);
    }
    if (model.getParent() instanceof Entities) {
      Entities entities = (Entities) model.getParent();
      entities.getEntities().remove(model);
    }
    if (model.getParent() instanceof Entity) {
      Entity entitiy = (Entity) model.getParent();
      entitiy.getEntityValues().remove(model);
    }
    if (model.getParent() instanceof Constants) {
      Constants constants = (Constants) model.getParent();
      constants.getVarFolders().remove(model);
    }
    if (model.getParent() instanceof Variables) {
      Variables variables = (Variables) model.getParent();
      variables.getVarFolders().remove(model);
    }
    if (model.getParent() instanceof VarFolder) {
      VarFolder varFolder = (VarFolder) model.getParent();
      if (model instanceof VarFolder) {
        varFolder.getVarFolders().remove(model);
      }
      if (model instanceof Var) {
        varFolder.getVars().remove(model);
      }
    }
    if (model.getParent() instanceof Grammar) {
      Grammar grammar = (Grammar) model.getParent();
      grammar.getItems().remove(model);
    }
    if (model.getParent() instanceof Prompt) {
      Prompt prompt = (Prompt) model.getParent();
      prompt.getItems().remove(model);
    }
    if (model.getParent() instanceof Settings) {
      Settings settings = (Settings) model.getParent();
      settings.getSettings().remove(model);
    }
    if (model.getParent() instanceof Variations) {
      Variations variations = (Variations) model.getParent();
      int index = variations.getItems().indexOf(model);

      // VariationsType has item list that is defined by List<String>.
      // So, you specified remove object by index.
      variations.getItems().remove(model);
      variations.getPeerType().getItem().remove(index);
    }
    if (model.getParent() instanceof SpecialSettings) {
      SpecialSettings specialSettings = (SpecialSettings) model.getParent();
      specialSettings.getSpecialSettings().remove(model);
    }

    //
    // delete xsd object
    //
    Object childXsdType = model.getPeerType();
    Object parentXsdType = model.getParent().getPeerType();
    if (parentXsdType instanceof DialogType) {
      if (childXsdType instanceof EntitiesType) {
        ((DialogType) parentXsdType).getEntities().remove(childXsdType);
      }
    }
    if (parentXsdType instanceof FolderNodeType) {
      ((FolderNodeType) parentXsdType).getInputOrOutputOrDefault().remove(childXsdType);
    }
    if (parentXsdType instanceof InputNodeType) {
      ((InputNodeType) parentXsdType).getInputOrOutputOrDefault().remove(childXsdType);
    }
    if (parentXsdType instanceof OutputNodeType) {
      ((OutputNodeType) parentXsdType).getInputOrOutputOrDefault().remove(childXsdType);
    }
    if (parentXsdType instanceof GetUserInputNodeType) {
      ((GetUserInputNodeType) parentXsdType).getInputOrOutputOrDefault().remove(childXsdType);
    }
    if (parentXsdType instanceof IfNodeType) {
        ((IfNodeType) parentXsdType).getInputOrOutputOrDefault().remove(childXsdType);
    }
    if (parentXsdType instanceof DefaultNodeType) {
      ((DefaultNodeType) parentXsdType).getInputOrOutputOrDefault().remove(childXsdType);
    }
    if (parentXsdType instanceof EntitiesType) {
      ((EntitiesType) parentXsdType).getEntity().remove(childXsdType);
    }
    if (parentXsdType instanceof EntityType) {
      ((EntityType) parentXsdType).getValue().remove(childXsdType);
    }
    if (parentXsdType instanceof ConstantsType) {
      ((ConstantsType) parentXsdType).getVarFolder().remove(childXsdType);
    }
    if (parentXsdType instanceof VariablesType) {
      ((VariablesType) parentXsdType).getVarFolder().remove(childXsdType);
    }
    if (parentXsdType instanceof VarFolderType) {
      if (childXsdType instanceof VarFolderType) {
        ((VarFolderType) parentXsdType).getVarFolder().remove(childXsdType);
      }
      if (childXsdType instanceof VarType) {
        ((VarFolderType) parentXsdType).getVar().remove(childXsdType);
      }
    }
    if (parentXsdType instanceof GrammarType) {
      ((GrammarType) parentXsdType).getItemOrSourceOrParam().remove(childXsdType);
    }
    if (parentXsdType instanceof PromptType) {
      ((PromptType) parentXsdType).getContent().remove(childXsdType);
    }
    if (parentXsdType instanceof SettingsType) {
      ((SettingsType) parentXsdType).getSetting().remove(childXsdType);
    }
    if (parentXsdType instanceof SpecialSettingsType) {
      ((SpecialSettingsType) parentXsdType).getSpecialSetting().remove(childXsdType);
    }

    unregsiterModel(model);
  }

  /**
   *
   * @param model
   */
  public void moveUpModel(BaseModel<?> model) {
    if (model.getParent() instanceof Grammar) {
      List<Item> items = ((Grammar) model.getParent()).getItems();
      int index = items.indexOf(model);
      Collections.swap(items, index - 1, index);
    }
    if (model.getParent() instanceof Prompt) {
      List<Item> items = ((Prompt) model.getParent()).getItems();
      int index = items.indexOf(model);
      Collections.swap(items, index - 1, index);
    }
    if (model.getParent() instanceof Entity) {
      List<EntityValue> entityValues = ((Entity) model.getParent()).getEntityValues();
      int index = entityValues.indexOf(model);
      Collections.swap(entityValues, index - 1, index);
    }
    if (model.getParent() instanceof Settings) {
      List<Setting> settings = ((Settings) model.getParent()).getSettings();
      int index = settings.indexOf(model);
      Collections.swap(settings, index - 1, index);
    }
    if (model.getParent() instanceof Variations) {
      List<Item> items = ((Variations) model.getParent()).getItems();
      int index = items.indexOf(model);
      Collections.swap(items, index - 1, index);

      // VariationsType has item list that is defined by List<String>.
      // So, you specified remove object by index.
      List<String> xsdItems = ((Variations) model.getParent()).getPeerType().getItem();
      Collections.swap(xsdItems, index - 1, index);
    }

    Object childXsdType = model.getPeerType();
    Object parentXsdType = model.getParent().getPeerType();
    if (parentXsdType instanceof GrammarType) {
      List<JAXBElement<?>> itemTypes = ((GrammarType) parentXsdType).getItemOrSourceOrParam();
      int index = itemTypes.indexOf(childXsdType);
      Collections.swap(itemTypes, index - 1, index);
    }
    if (parentXsdType instanceof PromptType) {
      List<Serializable> itemTypes = ((PromptType) parentXsdType).getContent();
      int index = itemTypes.indexOf(childXsdType);
      Collections.swap(itemTypes, index - 1, index);
    }
    if (parentXsdType instanceof EntityType) {
      List<EntityValueType> entityValueTypes = ((EntityType) parentXsdType).getValue();
      int index = entityValueTypes.indexOf(childXsdType);
      Collections.swap(entityValueTypes, index - 1, index);
    }
    if (parentXsdType instanceof SettingsType) {
      List<SettingType> settingTypes = ((SettingsType) parentXsdType).getSetting();
      int index = settingTypes.indexOf(childXsdType);
      Collections.swap(settingTypes, index - 1, index);
    }
  }

  /**
   *
   * @param model
   */
  public void moveDownModel(BaseModel<?> model) {
    if (model.getParent() instanceof Grammar) {
      List<Item> items = ((Grammar) model.getParent()).getItems();
      int index = items.indexOf(model);
      Collections.swap(items, index, index + 1);
    }
    if (model.getParent() instanceof Prompt) {
      List<Item> items = ((Prompt) model.getParent()).getItems();
      int index = items.indexOf(model);
      Collections.swap(items, index, index + 1);
    }
    if (model.getParent() instanceof Entity) {
      List<EntityValue> entityValues = ((Entity) model.getParent()).getEntityValues();
      int index = entityValues.indexOf(model);
      Collections.swap(entityValues, index, index + 1);
    }
    if (model.getParent() instanceof Settings) {
      List<Setting> settings = ((Settings) model.getParent()).getSettings();
      int index = settings.indexOf(model);
      Collections.swap(settings, index, index + 1);
    }
    if (model.getParent() instanceof Variations) {
      List<Item> items = ((Variations) model.getParent()).getItems();
      int index = items.indexOf(model);
      Collections.swap(items, index, index + 1);

      // VariationsType has item list that is defined by List<String>.
      // So, you specified remove object by index.
      List<String> xsdItems = ((Variations) model.getParent()).getPeerType().getItem();
      Collections.swap(xsdItems, index, index + 1);
    }

    Object childXsdType = model.getPeerType();
    Object parentXsdType = model.getParent().getPeerType();
    if (parentXsdType instanceof GrammarType) {
      List<JAXBElement<?>> itemTypes = ((GrammarType) parentXsdType).getItemOrSourceOrParam();
      int index = itemTypes.indexOf(childXsdType);
      Collections.swap(itemTypes, index, index + 1);
    }
    if (parentXsdType instanceof PromptType) {
      List<Serializable> itemTypes = ((PromptType) parentXsdType).getContent();
      int index = itemTypes.indexOf(childXsdType);
      Collections.swap(itemTypes, index, index + 1);
    }
    if (parentXsdType instanceof EntityType) {
      List<EntityValueType> entityValueTypes = ((EntityType) parentXsdType).getValue();
      int index = entityValueTypes.indexOf(childXsdType);
      Collections.swap(entityValueTypes, index, index + 1);
    }
    if (parentXsdType instanceof SettingsType) {
      List<SettingType> settingTypes = ((SettingsType) parentXsdType).getSetting();
      int index = settingTypes.indexOf(childXsdType);
      Collections.swap(settingTypes, index, index + 1);
    }
  }

  /**
   *
   * @param list
   * @param before
   * @param after
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void reorderDialogNode(List<Object> list, int before, int after) {
    Object dragObj = list.get(before);

    List xsdList = null;
    Object childType = null;
    if (dragObj instanceof FlowNode) {
      FlowNode<?> child = (FlowNode<?>) dragObj;
      childType = child.getPeerType();

      CompositeNode<?> parent = (CompositeNode<?>) child.getParent();
      if (parent instanceof FolderNode) {
        FolderNodeType parentXsdType = ((FolderNode) parent).getPeerType();
        xsdList = parentXsdType.getInputOrOutputOrDefault();
      }
      if (parent instanceof InputNode) {
        InputNodeType parentXsdType = ((InputNode) parent).getPeerType();
        xsdList = parentXsdType.getInputOrOutputOrDefault();
      }
      if (parent instanceof OutputNode) {
        OutputNodeType parentXsdType = ((OutputNode) parent).getPeerType();
        xsdList = parentXsdType.getInputOrOutputOrDefault();
      }
      if (parent instanceof GetUserInputNode) {
        GetUserInputNodeType parentXsdType = ((GetUserInputNode) parent).getPeerType();
        xsdList = parentXsdType.getInputOrOutputOrDefault();
      }
      if (parent instanceof IfNode) {
        IfNodeType parentXsdType = ((IfNode) parent).getPeerType();
        xsdList = parentXsdType.getInputOrOutputOrDefault();
      }
      if (parent instanceof DefaultNode) {
        DefaultNodeType parentXsdType = ((DefaultNode) parent).getPeerType();
        xsdList = parentXsdType.getInputOrOutputOrDefault();
      }
    }
    if (dragObj instanceof Entities) {
      Entities child = (Entities) dragObj;
      childType = child.getPeerType();
      watson.dialog.tools.model.domain.Dialog parent = (watson.dialog.tools.model.domain.Dialog) child.getParent();
      xsdList = parent.getPeerType().getEntities();
    }
    if (dragObj instanceof Entity) {
      Entity child = (Entity) dragObj;
      childType = child.getPeerType();
      Entities parent = (Entities) child.getParent();
      xsdList = parent.getPeerType().getEntity();
    }
    if (dragObj instanceof VarFolder) {
      VarFolder child = (VarFolder) dragObj;
      childType = child.getPeerType();
      if (child.getParent() instanceof Constants) {
        Constants parent = (Constants) child.getParent();
        xsdList = parent.getPeerType().getVarFolder();
      }
      if (child.getParent() instanceof Variables) {
        Variables parent = (Variables) child.getParent();
        xsdList = parent.getPeerType().getVarFolder();
      }
      if (child.getParent() instanceof VarFolder) {
        VarFolder parent = (VarFolder) child.getParent();
        xsdList = parent.getPeerType().getVarFolder();
      }
    }
    if (dragObj instanceof Var) {
      Var child = (Var) dragObj;
      childType = child.getPeerType();
      VarFolder parent = (VarFolder) child.getParent();
      xsdList = parent.getPeerType().getVar();
    }
    if (dragObj instanceof SpecialSetting) {
      SpecialSetting child = (SpecialSetting) dragObj;
      childType = child.getPeerType();
      SpecialSettings parent = (SpecialSettings) child.getParent();
      xsdList = parent.getPeerType().getSpecialSetting();
    }

    if (after < before) {
      list.add(after, dragObj);
      list.remove(before + 1);
      xsdList.add(after, childType);
      xsdList.remove(before + 1);
    } else {
      list.add(after + 1, dragObj);
      list.remove(before);
      xsdList.add(after + 1, childType);
      xsdList.remove(before);
    }
  }

  /**
   *
   * @param username
   * @param password
   */
  public WatsonDialogToolsService setCredential(String username, String password) {
    sdkService = new DialogService();
    sdkService.setUsernameAndPassword(username, password);
    return instance;
  }

  /**
   *
   * @return
   */
  public List<Dialog> listDialogs() {
    return sdkService.getDialogs();
  }

  /**
   *
   * @param name
   * @param dialogFile
   * @return
   */
  public Dialog createDialog(String name, File dialogFile) {
    return sdkService.createDialog(name, dialogFile);
  }

  /**
   *
   * @param dialogId
   * @param dialogFile
   * @return
   */
  public Dialog updateDialog(String dialogId, File dialogFile) {
    return sdkService.updateDialog(dialogId, dialogFile);
  }

  /**
   *
   * @param dialogId
   */
  public void deleteDialog(String dialogId) {
    sdkService.deleteDialog(dialogId);
  }

  /**
   *
   * @param dialogId
   * @param fromDate
   * @param toDate
   * @return
   */
  public List<ConversationData> listConversationHistory(String dialogId, Date fromDate, Date toDate) {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("dialog_id", dialogId);
    params.put("date_from", fromDate);
    params.put("date_to", toDate);
    List<ConversationData> conversationHistory = sdkService.getConversationData(params);
    for (ConversationData conversation : conversationHistory) {
      Collections.sort(conversation.getMessages(), new Comparator<Message>() {
        @Override
        public int compare(Message m1, Message m2) {
          Date d1 = m1.getDateTime();
          Date d2 = m2.getDateTime();
          int ret = d1.compareTo(d2);
          if (ret != 0) {
            return ret;
          }
          if ("true".equals(m1.getFromClient())) {
            return -1;
          } else {
            return 1;
          }
        }
      });
    }
    return conversationHistory;
  }

  /**
   *
   * @param dialogId
   * @return
   */
  public Conversation createConversation(String dialogId) {
    return sdkService.createConversation(dialogId);
  }

  /**
   *
   * @param conversation
   * @param message
   * @return
   */
  public Conversation sendMessage(Conversation conversation, String message) {
    return sdkService.converse(conversation, message);
  }
}
