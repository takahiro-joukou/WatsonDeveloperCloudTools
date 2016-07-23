package watson.dialog.tools.model.domain.builder;

import javax.xml.bind.JAXBElement;

import watson.dialog.tools.model.dao.xsd.ActionType;
import watson.dialog.tools.model.dao.xsd.ConceptNodeType;
import watson.dialog.tools.model.dao.xsd.CondType;
import watson.dialog.tools.model.dao.xsd.ConditionOperatorType;
import watson.dialog.tools.model.dao.xsd.ConstantsType;
import watson.dialog.tools.model.dao.xsd.DefaultNodeType;
import watson.dialog.tools.model.dao.xsd.EntitiesType;
import watson.dialog.tools.model.dao.xsd.EntityType;
import watson.dialog.tools.model.dao.xsd.EntityValueType;
import watson.dialog.tools.model.dao.xsd.FlowType;
import watson.dialog.tools.model.dao.xsd.FolderNodeType;
import watson.dialog.tools.model.dao.xsd.GetUserInputNodeType;
import watson.dialog.tools.model.dao.xsd.GotoNodeType;
import watson.dialog.tools.model.dao.xsd.GrammarType;
import watson.dialog.tools.model.dao.xsd.GrammarTypeType;
import watson.dialog.tools.model.dao.xsd.IfNodeType;
import watson.dialog.tools.model.dao.xsd.InputNodeType;
import watson.dialog.tools.model.dao.xsd.MatchTypeType;
import watson.dialog.tools.model.dao.xsd.OutputNodeType;
import watson.dialog.tools.model.dao.xsd.PromptType;
import watson.dialog.tools.model.dao.xsd.PromptTypeType;
import watson.dialog.tools.model.dao.xsd.SearchNodeType;
import watson.dialog.tools.model.dao.xsd.SelectionTypeType;
import watson.dialog.tools.model.dao.xsd.SettingType;
import watson.dialog.tools.model.dao.xsd.SettingTypeType;
import watson.dialog.tools.model.dao.xsd.SettingsType;
import watson.dialog.tools.model.dao.xsd.SpecialSettingType;
import watson.dialog.tools.model.dao.xsd.SpecialSettingsType;
import watson.dialog.tools.model.dao.xsd.VarFolderType;
import watson.dialog.tools.model.dao.xsd.VarFolderTypeType;
import watson.dialog.tools.model.dao.xsd.VarType;
import watson.dialog.tools.model.dao.xsd.VarTypeType;
import watson.dialog.tools.model.dao.xsd.VariablesType;
import watson.dialog.tools.model.dao.xsd.VariationsType;
import watson.dialog.tools.model.domain.Action;
import watson.dialog.tools.model.domain.CompositeNode;
import watson.dialog.tools.model.domain.ConceptNode;
import watson.dialog.tools.model.domain.Cond;
import watson.dialog.tools.model.domain.Constants;
import watson.dialog.tools.model.domain.DefaultNode;
import watson.dialog.tools.model.domain.Dialog;
import watson.dialog.tools.model.domain.Entities;
import watson.dialog.tools.model.domain.Entity;
import watson.dialog.tools.model.domain.EntityValue;
import watson.dialog.tools.model.domain.Flow;
import watson.dialog.tools.model.domain.FolderNode;
import watson.dialog.tools.model.domain.GetUserInputNode;
import watson.dialog.tools.model.domain.GoToNode;
import watson.dialog.tools.model.domain.Grammar;
import watson.dialog.tools.model.domain.IType.ActionOperatorType;
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
import watson.dialog.tools.model.service.WatsonDialogToolsService;

class BuilderHelper {

  /** Watson Dialog Tools Service */
  private static WatsonDialogToolsService service = WatsonDialogToolsService.getInstance();

  /**
   *
   */
  private BuilderHelper() {
  }

  /**
   *
   * @param parent
   * @param flowType
   * @return
   */
  static Flow createFlowNodeByXsd(Dialog parent, FlowType flowType) {
    Flow flow = new Flow();
    flow.setPeerType(flowType);

    flow.setParent(parent);
    parent.setFlow(flow);
    return flow;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static FolderNode createFolderNodeByXsd(Flow parent, FolderNodeType xsdType) {
    FolderNode folderNode = doCreateFolderNode(xsdType);

    folderNode.setParent(parent);
    parent.getFolderNodes().add(folderNode);
    service.regsiterModel(folderNode.getId(), folderNode);
    return folderNode;
  }

  /**
  *
  * @param parent
  * @param xsdType
  * @return
  */
  static FolderNode createFolderNodeByXsd(CompositeNode<?> parent, FolderNodeType xsdType) {
    FolderNode folderNode = doCreateFolderNode(xsdType);

    folderNode.setParent(parent);
    parent.getChildren().add(folderNode);
    service.regsiterModel(folderNode.getId(), folderNode);
    return folderNode;
  }

  /**
   *
   * @param xsdType
   * @return
   */
  private static FolderNode doCreateFolderNode(FolderNodeType xsdType) {
    FolderNode folderNode = new FolderNode();
    folderNode.setGid(xsdType.getGid());
    folderNode.setId(xsdType.getId());
    folderNode.setLabel(xsdType.getLabel());
    folderNode.setComment(xsdType.getComment());
    folderNode.setDate(xsdType.getDate());
    folderNode.setSelectionType(convertSelectionType(xsdType.getSelectionType()));
    folderNode.setIsOffline(convertBoolean(xsdType.isIsOffline()));
    folderNode.setPeerType(xsdType);
    return folderNode;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static InputNode createInputNodeByXsd(CompositeNode<?> parent, InputNodeType xsdType) {
    InputNode inputNode = new InputNode();
    inputNode.setGid(xsdType.getGid());
    inputNode.setId(xsdType.getId());
    inputNode.setComment(xsdType.getComment());
    inputNode.setDate(xsdType.getDate());
    inputNode.setIsOffline(convertBoolean(xsdType.isIsOffline()));
    inputNode.setIsAutoLearnCandidate(convertBoolean(xsdType.isIsAutoLearnCandidate()));
    inputNode.setRef(xsdType.getRef());
    inputNode.setPeerType(xsdType);

    inputNode.setParent(parent);
    parent.getChildren().add(inputNode);
    service.regsiterModel(inputNode.getId(), inputNode);
    return inputNode;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static OutputNode createOutputNodeByXsd(CompositeNode<?> parent, OutputNodeType xsdType) {
    OutputNode outputNode = new OutputNode();
    outputNode.setGid(xsdType.getGid());
    outputNode.setId(xsdType.getId());
    outputNode.setComment(xsdType.getComment());
    outputNode.setDate(xsdType.getDate());
    outputNode.setIsOffline(convertBoolean(xsdType.isIsOffline()));
    outputNode.setIsInsertDNRStatement(convertBoolean(xsdType.isIsInsertDNRStatement()));
    outputNode.setRef(xsdType.getRef());
    outputNode.setPeerType(xsdType);

    outputNode.setParent(parent);
    parent.getChildren().add(outputNode);
    service.regsiterModel(outputNode.getId(), outputNode);
    return outputNode;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static GetUserInputNode createGetUserInputNodeByXsd(CompositeNode<?> parent, GetUserInputNodeType xsdType) {
    GetUserInputNode userInputNode = new GetUserInputNode();
    userInputNode.setGid(xsdType.getGid());
    userInputNode.setId(xsdType.getId());
    userInputNode.setComment(xsdType.getComment());
    userInputNode.setDate(xsdType.getDate());
    userInputNode.setIsOffline(convertBoolean(xsdType.isIsOffline()));
    userInputNode.setIsDNRCandidate(Boolean.valueOf(xsdType.getIsDNRCandidate()));
    userInputNode.setPeerType(xsdType);

    userInputNode.setParent(parent);
    parent.getChildren().add(userInputNode);
    service.regsiterModel(userInputNode.getId(), userInputNode);
    return userInputNode;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static SearchNode createSearchNodeByXsd(CompositeNode<?> parent, SearchNodeType xsdType) {
    SearchNode searchNode = new SearchNode();
    searchNode.setGid(xsdType.getGid());
    searchNode.setId(xsdType.getId());
    searchNode.setComment(xsdType.getComment());
    searchNode.setDate(xsdType.getDate());
    searchNode.setIsOffline(convertBoolean(xsdType.isIsOffline()));
    searchNode.setRef(xsdType.getRef());
    searchNode.setPeerType(xsdType);

    searchNode.setParent(parent);
    parent.getChildren().add(searchNode);
    service.regsiterModel(searchNode.getId(), searchNode);
    return searchNode;
  }

  /**
   *
   * @param getUserInputNode
   * @param xsdType
   * @return
   */
  static DefaultNode createDefaultNodeByXsd(CompositeNode<?> parent, DefaultNodeType xsdType) {
    DefaultNode defaultNode = new DefaultNode();
    defaultNode.setGid(xsdType.getGid());
    defaultNode.setId(xsdType.getId());
    defaultNode.setDate(xsdType.getDate());
    defaultNode.setIsOffline(convertBoolean(xsdType.isIsOffline()));
    defaultNode.setPeerType(xsdType);

    defaultNode.setParent(parent);
    parent.getChildren().add(defaultNode);
    service.regsiterModel(defaultNode.getId(), defaultNode);
    return defaultNode;
  }

  /**
   *
   * @param folderNode
   * @param xsdType
   * @return
   */
  static IfNode createIfNodeByXsd(CompositeNode<?> parent, IfNodeType xsdType) {
    IfNode ifNode = new IfNode();
    ifNode.setGid(xsdType.getGid());
    ifNode.setId(xsdType.getId());
    ifNode.setComment(xsdType.getComment());
    ifNode.setDate(xsdType.getDate());
    ifNode.setIsOffline(convertBoolean(xsdType.isIsOffline()));
    ifNode.setMatchType(converMatchType(xsdType.getMatchType()));
    ifNode.setPeerType(xsdType);

    ifNode.setParent(parent);
    parent.getChildren().add(ifNode);
    service.regsiterModel(ifNode.getId(), ifNode);
    return ifNode;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static GoToNode createGoToNodeByXsd(CompositeNode<?> parent, GotoNodeType xsdType) {
    GoToNode goToNode = new GoToNode();
    goToNode.setGid(xsdType.getGid());
    goToNode.setId(xsdType.getId());
    goToNode.setComment(xsdType.getComment());
    goToNode.setDate(xsdType.getDate());
    goToNode.setIsOffline(convertBoolean(xsdType.isIsOffline()));
    goToNode.setRef(xsdType.getRef());
    goToNode.setPeerType(xsdType);

    goToNode.setParent(parent);
    parent.getChildren().add(goToNode);
    service.regsiterModel(goToNode.getId(), goToNode);
    return goToNode;
  }

  /**
   *
   * @param xsdType
   * @return
   */
  static ConceptNode createConceptNodeByXsd(CompositeNode<?> parent, ConceptNodeType xsdType) {
    ConceptNode conceptNode = new ConceptNode();
    conceptNode.setGid(xsdType.getGid());
    conceptNode.setId(xsdType.getId());
    conceptNode.setComment(xsdType.getComment());
    conceptNode.setDate(xsdType.getDate());
    conceptNode.setIsOffline(convertBoolean(xsdType.isIsOffline()));
    conceptNode.setDescription(xsdType.getDescription());
    conceptNode.setRef(xsdType.getRef());
    conceptNode.setPeerType(xsdType);

    conceptNode.setParent(parent);
    parent.getChildren().add(conceptNode);
    service.regsiterModel(conceptNode.getId(), conceptNode);
    return conceptNode;
  }

  /**
   *
   * @param xsdType
   * @return
   */
  static Cond createCondByXsd(IfNode parent, CondType xsdType) {
    Cond cond = new Cond();
    cond.setGid(xsdType.getGid());
    cond.setId(xsdType.getId());
    cond.setVarName(xsdType.getVarName());
    cond.setOperatorType(convertConditionOperatorType(xsdType.getOperator()));
    cond.setValue(xsdType.getValue());
    cond.setPeerType(xsdType);

    cond.setParent(parent);
    parent.getConds().add(cond);
    service.regsiterModel(cond.getId(), cond);
    return cond;
  }

  /**
   *
   * @param xsdType
   * @return
   */
  static Grammar createGrammarByXsd(GrammarType xsdType) {
    Grammar grammar = new Grammar();
    grammar.setType(converGrammarType(xsdType.getType()));
    grammar.setPeerType(xsdType);

    return grammar;
  }

  /**
   *
   * @param xsdType
   * @return
   */
  static Prompt createPromptByXsd(PromptType xsdType) {
    Prompt prompt = new Prompt();
    prompt.setSelectionType(convertSelectionType(xsdType.getSelectionType()));
    prompt.setPromptType(converPromptType(xsdType.getType()));
    prompt.setPeerType(xsdType);

    return prompt;
  }

  /**
   *
   * @param jaxbElement
   * @return
   */
  static Item createItemByXsd(JAXBElement<String> jaxbElement) {
    Item item = new Item();
    item.setValue(jaxbElement.getValue());
    item.setPeerType(jaxbElement);

    return item;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static Entities createEntitiesByXsd(Dialog parent, EntitiesType xsdType) {
    Entities entities = new Entities();
    entities.setPeerType(xsdType);

    entities.setParent(parent);
    parent.getEntitiesList().add(entities);
    return entities;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static Entity createEntityByXsd(Entities parent, EntityType xsdType) {
    Entity entity = new Entity();
    entity.setGid(xsdType.getGid());
    entity.setName(xsdType.getName());
    entity.setDate(xsdType.getDate());
    entity.setPeerType(xsdType);

    entity.setParent(parent);
    parent.getEntities().add(entity);
    return entity;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static EntityValue createEntityValueByXsd(Entity parent, EntityValueType xsdType) {
    EntityValue entityValue = new EntityValue();
    entityValue.setName(xsdType.getName());
    entityValue.setValue(xsdType.getValue());
    entityValue.setPeerType(xsdType);

    entityValue.setParent(parent);
    parent.getEntityValues().add(entityValue);
    return entityValue;
  }

  /**
  *
  * @param parent
  * @param xsdType
  * @return
  */
  static Constants createConstantsByXsd(Dialog parent, ConstantsType xsdType) {
    Constants constants = new Constants();
    constants.setPeerType(xsdType);

    constants.setParent(parent);
    parent.getConstantsList().add(constants);
    return constants;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static Variables createVariablesByXsd(Dialog parent, VariablesType xsdType) {
    Variables variables = new Variables();
    variables.setPeerType(xsdType);

    variables.setParent(parent);
    parent.getVariablesList().add(variables);
    return variables;
  }

  /**
  *
  * @param parent
  * @param varFolderType
  * @return
  */
  static VarFolder createVarFolderByXsd(Constants parent, VarFolderType xsdType) {
    VarFolder varFolder = doCreateVarFolder(xsdType);
    varFolder.setParent(parent);
    parent.getVarFolders().add(varFolder);
    return varFolder;
  }

  /**
   *
   * @param parent
   * @param varFolderType
   * @return
   */
  static VarFolder createVarFolderByXsd(Variables parent, VarFolderType xsdType) {
    VarFolder varFolder = doCreateVarFolder(xsdType);
    varFolder.setParent(parent);
    parent.getVarFolders().add(varFolder);
    return varFolder;
  }

  /**
   *
   * @param parent
   * @param varFolderType
   * @return
   */
  static VarFolder createVarFolderByXsd(VarFolder parent, VarFolderType xsdType) {
    VarFolder varFolder = doCreateVarFolder(xsdType);
    varFolder.setParent(parent);
    parent.getVarFolders().add(varFolder);
    return varFolder;
  }

  /**
   *
   * @param xsdType
   * @return
   */
  private static VarFolder doCreateVarFolder(VarFolderType xsdType) {
    VarFolder varFolder = new VarFolder();
    varFolder.setGid(xsdType.getGid());
    varFolder.setId(xsdType.getId());
    varFolder.setName(xsdType.getName());
    varFolder.setType(convertVarFolderType(xsdType.getType()));
    varFolder.setPeerType(xsdType);

    service.regsiterModel(varFolder.getId(), varFolder);
    return varFolder;
  }

  /**
   *
   * @param varFolder
   * @param varType
   * @return
   */
  static Var createVarByXsd(VarFolder parent, VarType xsdType) {
    Var var = new Var();
    var.setGid(xsdType.getGid());
    var.setId(xsdType.getId());
    var.setName(xsdType.getName());
    var.setDescription(xsdType.getDescription());
    var.setInitValue(xsdType.getInitValue());
    var.setType(convertVarType(xsdType.getType()));
    var.setPeerType(xsdType);

    var.setParent(parent);
    parent.getVars().add(var);
    service.regsiterModel(var.getId(), var);
    return var;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static Settings createSettingsByXsd(Dialog parent, SettingsType xsdType) {
    Settings settings = new Settings();
    settings.setPeerType(xsdType);

    settings.setParent(parent);
    parent.getSettingsList().add(settings);
    return settings;
  }

  /**
   *
   * @param parent
   * @param xsdType
   * @return
   */
  static Setting createSettingByXsd(Settings parent, SettingType xsdType) {
    Setting setting = new Setting();
    setting.setName(xsdType.getName());
    setting.setValue(xsdType.getValue());
    setting.setSettingType(convertSettingType(xsdType.getType()));
    setting.setPeerType(xsdType);

    setting.setParent(parent);
    parent.getSettings().add(setting);
    return setting;
  }

  /**
   *
   * @param dialog
   * @param specialSettingsType
   * @return
   */
  static SpecialSettings createSpecialSettingsByXsd(Dialog parent, SpecialSettingsType xsdType) {
    SpecialSettings specialSettings = new SpecialSettings();
    specialSettings.setPeerType(xsdType);

    specialSettings.setParent(parent);
    parent.getSpecialSettingsList().add(specialSettings);
    return specialSettings;
  }

  /**
   *
   * @param specialSettings
   * @param specialSettingType
   */
  static SpecialSetting createSpecialSettingByXsd(SpecialSettings parent, SpecialSettingType xsdType) {
    SpecialSetting specialSetting = new SpecialSetting();
    specialSetting.setGid(xsdType.getGid());
    specialSetting.setId(xsdType.getId());
    specialSetting.setDate(xsdType.getDate());
    specialSetting.setLabel(xsdType.getLabel());
    String type = xsdType.getSelectionType();
    if (type != null) {
      specialSetting.setSelectionType(SelectionType.valueOf(type));
    }
    specialSetting.setPeerType(xsdType);

    specialSetting.setParent(parent);
    parent.getSpecialSettings().add(specialSetting);
    service.regsiterModel(specialSetting.getId(), specialSetting);

    return specialSetting;
  }

  /**
   *
   * @param specialSetting
   * @param variationsType
   */
  static Variations createVariationByXsd(SpecialSetting parent, VariationsType xsdType) {
    Variations variations = new Variations();
    variations.setPeerType(xsdType);
    variations.setParent(parent);
    parent.getVariations().add(variations);

    return variations;
  }

  /**
   *
   * @param parent
   * @param actionType
   * @return
   */
  static Action createActionByXsd(CompositeNode<?> parent, ActionType xsdType) {
    Action action = new Action();
    action.setGid(xsdType.getGid());
    action.setId(xsdType.getId());
    action.setVarName(xsdType.getVarName());
    action.setOperatorType(convertActionOperatorType(xsdType.getOperator()));
    action.setValue(xsdType.getValue());
    action.setPeerType(xsdType);

    action.setParent(parent);
    parent.getActions().add(action);
    service.regsiterModel(action.getId(), action);
    return action;
  }

  /**
   *
   * @param b
   * @return
   */
  private static boolean convertBoolean(Boolean b) {
    if (b == null) {
      return false;
    }
    return b.booleanValue();
  }

  /**
   * @param type
   * @return
   */
  private static SelectionType convertSelectionType(SelectionTypeType type) {
    if (type == null) {
      return null;
    }
    return SelectionType.fromValue(type.value());
  }

  /**
   *
   * @param type
   * @return
   */
  private static watson.dialog.tools.model.domain.IType.GrammarType converGrammarType(GrammarTypeType type) {
    if (type == null) {
      return null;
    }
    return watson.dialog.tools.model.domain.IType.GrammarType.fromValue(type.value());
  }

  /**
   *
   * @param type
   * @return
   */
  private static watson.dialog.tools.model.domain.IType.PromptType converPromptType(PromptTypeType type) {
    if (type == null) {
      return null;
    }
    return watson.dialog.tools.model.domain.IType.PromptType.fromValue(type.value());
  }

  /**
   *
   * @param type
   * @return
   */
  private static watson.dialog.tools.model.domain.IType.MatchType converMatchType(MatchTypeType type) {
    if (type == null) {
      return null;
    }
    return watson.dialog.tools.model.domain.IType.MatchType.fromValue(type.value());
  }

  /**
   *
   * @param type
   * @return
   */
  private static watson.dialog.tools.model.domain.IType.ConditionOperatorType convertConditionOperatorType(ConditionOperatorType type) {
    if (type == null) {
      return null;
    }
    return watson.dialog.tools.model.domain.IType.ConditionOperatorType.fromValue(type.value());
  }

  /**
   *
   * @param type
   * @return
   */
  private static watson.dialog.tools.model.domain.IType.VarFolderType convertVarFolderType(VarFolderTypeType type) {
    if (type == null) {
      return null;
    }
    return watson.dialog.tools.model.domain.IType.VarFolderType.fromValue(type.value());
  }

  /**
   *
   * @param type
   * @return
   */
  private static watson.dialog.tools.model.domain.IType.VarType convertVarType(VarTypeType type) {
    if (type == null) {
      return null;
    }
    return watson.dialog.tools.model.domain.IType.VarType.fromValue(type.value());
  }

  /**
   *
   * @param type
   * @return
   */
  private static watson.dialog.tools.model.domain.IType.SettingType convertSettingType(SettingTypeType type) {
    if (type == null) {
      return null;
    }
    return watson.dialog.tools.model.domain.IType.SettingType.fromValue(type.value());
  }

  /**
   *
   * @param type
   * @return
   */
  private static ActionOperatorType convertActionOperatorType(watson.dialog.tools.model.dao.xsd.ActionOperatorType type) {
    if (type == null) {
      return null;
    }
    return watson.dialog.tools.model.domain.IType.ActionOperatorType.fromValue(type.value());
  }
}
