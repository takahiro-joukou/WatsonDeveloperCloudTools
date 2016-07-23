package watson.dialog.tools.model.domain.builder;

import java.io.Serializable;

import javax.xml.bind.JAXBElement;

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
import watson.dialog.tools.model.dao.xsd.FlowType;
import watson.dialog.tools.model.dao.xsd.FolderNodeType;
import watson.dialog.tools.model.dao.xsd.GetUserInputNodeType;
import watson.dialog.tools.model.dao.xsd.GotoNodeType;
import watson.dialog.tools.model.dao.xsd.GrammarType;
import watson.dialog.tools.model.dao.xsd.IfNodeType;
import watson.dialog.tools.model.dao.xsd.InputNodeType;
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
import watson.dialog.tools.model.domain.ConceptNode;
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
import watson.dialog.tools.model.domain.IfNode;
import watson.dialog.tools.model.domain.InputNode;
import watson.dialog.tools.model.domain.Item;
import watson.dialog.tools.model.domain.OutputNode;
import watson.dialog.tools.model.domain.Prompt;
import watson.dialog.tools.model.domain.SearchNode;
import watson.dialog.tools.model.domain.Settings;
import watson.dialog.tools.model.domain.SpecialSetting;
import watson.dialog.tools.model.domain.SpecialSettings;
import watson.dialog.tools.model.domain.VarFolder;
import watson.dialog.tools.model.domain.Variables;
import watson.dialog.tools.model.domain.Variations;

public class DomainBuilder {

  private DomainBuilder() {
    // singleton
  }

  public static Dialog build(DialogType dialogType) {
    return buildDialog(dialogType);
  }

  private static Dialog buildDialog(DialogType dialogType) {
    Dialog dialog = new Dialog();

    dialog.setPeerType(dialogType);

    FlowType flowType = dialogType.getFlow();
    Flow flow = BuilderHelper.createFlowNodeByXsd(dialog, flowType);
    buildFlow(flow, flowType);

    for (EntitiesType entitiesType : dialogType.getEntities()) {
      Entities entities = BuilderHelper.createEntitiesByXsd(dialog, entitiesType);
      buildEntities(entities, entitiesType);
    }
    for (ConstantsType constantsType : dialogType.getConstants()) {
      Constants constants = BuilderHelper.createConstantsByXsd(dialog, constantsType);
      buildConstants(constants, constantsType);
    }
    for (VariablesType variablesType : dialogType.getVariables()) {
      Variables variables = BuilderHelper.createVariablesByXsd(dialog, variablesType);
      buildVariables(variables, variablesType);
    }
    for (SettingsType settingsType : dialogType.getSettings()) {
      Settings settings = BuilderHelper.createSettingsByXsd(dialog, settingsType);
      buildSettings(settings, settingsType);
    }
    for (SpecialSettingsType specialSettingsType : dialogType.getSpecialSettings()) {
      SpecialSettings specialSettings = BuilderHelper.createSpecialSettingsByXsd(dialog, specialSettingsType);
      buildSpecialSettings(specialSettings, specialSettingsType);
    }
    return dialog;
  }

  private static void buildFlow(Flow flow, FlowType flowType) {
    for (FolderNodeType folderNodeType : flowType.getFolder()) {
      FolderNode folderNode = BuilderHelper.createFolderNodeByXsd(flow, folderNodeType);
      buidFolderNode(folderNode, folderNodeType);
    }
  }

  private static void buidFolderNode(FolderNode folderNode, FolderNodeType folderNodeType) {
    for (Object obj : folderNodeType.getActionOrScript()) {
      if (obj instanceof ActionType) {
        ActionType actionType = (ActionType) obj;
        BuilderHelper.createActionByXsd(folderNode, actionType);
      }
    }

    for (ChatflowNode child : folderNodeType.getInputOrOutputOrDefault()) {
      if (child instanceof FolderNodeType) {
        FolderNodeType childFolderNodeType = (FolderNodeType) child;
        FolderNode childFolderNode = BuilderHelper.createFolderNodeByXsd(folderNode, childFolderNodeType);
        buidFolderNode(childFolderNode, childFolderNodeType);
      }

      if (child instanceof InputNodeType) {
        InputNodeType inputNodeType = (InputNodeType) child;
        InputNode inputNode = BuilderHelper.createInputNodeByXsd(folderNode, inputNodeType);
        buildInputNode(inputNode, inputNodeType);
      }

      if (child instanceof OutputNodeType) {
        OutputNodeType outputNodeType = (OutputNodeType) child;
        OutputNode outputNode = BuilderHelper.createOutputNodeByXsd(folderNode, outputNodeType);
        buildOutputNode(outputNode, outputNodeType);
      }

      if (child instanceof GetUserInputNodeType) {
        GetUserInputNodeType getUserInputNodeType = (GetUserInputNodeType) child;
        GetUserInputNode getUserInputNodeNode = BuilderHelper.createGetUserInputNodeByXsd(folderNode, getUserInputNodeType);
        buildGetUserInputNode(getUserInputNodeNode, getUserInputNodeType);
      }

      if (child instanceof ConceptNodeType) {
        ConceptNodeType conceptNodeType = (ConceptNodeType) child;
        ConceptNode conceptNode = BuilderHelper.createConceptNodeByXsd(folderNode, conceptNodeType);
        buildConceptNode(conceptNode, conceptNodeType);
      }

      if (child instanceof IfNodeType) {
        IfNodeType ifNodeType = (IfNodeType) child;
        IfNode ifNode = BuilderHelper.createIfNodeByXsd(folderNode, ifNodeType);
        buildIfNode(ifNode, ifNodeType);
      }

      if (child instanceof GotoNodeType) {
        GotoNodeType gotoNodeType = (GotoNodeType) child;
        GoToNode goToNode = BuilderHelper.createGoToNodeByXsd(folderNode, gotoNodeType);
        buildGoToNode(goToNode, gotoNodeType);
      }
    }
  }

  private static void buildInputNode(InputNode inputNode, InputNodeType inputNodeType) {
    for (Object obj : inputNodeType.getActionOrScriptOrGrammar()) {
      if (obj instanceof ActionType) {
        ActionType actionType = (ActionType) obj;
        BuilderHelper.createActionByXsd(inputNode, actionType);
      }

      if (obj instanceof GrammarType) {
        GrammarType grammarType = (GrammarType) obj;
        Grammar grammar = BuilderHelper.createGrammarByXsd(grammarType);
        grammar.setParent(inputNode);
        inputNode.getGrammars().add(grammar);

        for (JAXBElement<?> jaxbElement : grammarType.getItemOrSourceOrParam()) {
          @SuppressWarnings("unchecked")
          Item item = BuilderHelper.createItemByXsd((JAXBElement<String>)jaxbElement);
          item.setParent(grammar);
          grammar.getItems().add(item);
        }
      }
    }

    for (ChatflowNode child : inputNodeType.getInputOrOutputOrDefault()) {
      if (child instanceof InputNodeType) {
        InputNodeType childInputNodeType = (InputNodeType) child;
        InputNode childInputNode = BuilderHelper.createInputNodeByXsd(inputNode, childInputNodeType);
        buildInputNode(childInputNode, childInputNodeType);
      }

      if (child instanceof OutputNodeType) {
        OutputNodeType outputNodeType = (OutputNodeType) child;
        OutputNode outputNode = BuilderHelper.createOutputNodeByXsd(inputNode, outputNodeType);
        buildOutputNode(outputNode, outputNodeType);
      }

      if (child instanceof IfNodeType) {
        IfNodeType ifNodeType = (IfNodeType) child;
        IfNode ifNode = BuilderHelper.createIfNodeByXsd(inputNode, ifNodeType);
        buildIfNode(ifNode, ifNodeType);
      }

      if (child instanceof GotoNodeType) {
        GotoNodeType gotoNodeType = (GotoNodeType) child;
        GoToNode goToNode = BuilderHelper.createGoToNodeByXsd(inputNode, gotoNodeType);
        buildGoToNode(goToNode, gotoNodeType);
      }
    }
  }

  private static void buildOutputNode(OutputNode outputNode, OutputNodeType outputNodeType) {
    for (Object obj : outputNodeType.getActionOrScriptOrPrompt()) {
      if (obj instanceof ActionType) {
        ActionType actionType = (ActionType) obj;
        BuilderHelper.createActionByXsd(outputNode, actionType);
      }

      if (obj instanceof PromptType) {
        PromptType promptType = (PromptType) obj;
        Prompt prompt = BuilderHelper.createPromptByXsd(promptType);
        prompt.setParent(outputNode);
        outputNode.getPrompts().add(prompt);

        for (Serializable serializable : promptType.getContent()) {
          if (serializable instanceof JAXBElement<?>) {
            @SuppressWarnings("unchecked")
            JAXBElement<String> jaxbElement = (JAXBElement<String>) serializable;
            Item item = BuilderHelper.createItemByXsd(jaxbElement);
            item.setParent(prompt);
            prompt.getItems().add(item);
          }
        }
      }
    }

    for (ChatflowNode child : outputNodeType.getInputOrOutputOrDefault()) {
      if (child instanceof OutputNodeType) {
        OutputNodeType childOutputNodeType = (OutputNodeType) child;
        OutputNode childOutputNode = BuilderHelper.createOutputNodeByXsd(outputNode, childOutputNodeType);
        buildOutputNode(childOutputNode, childOutputNodeType);
      }

      if (child instanceof GetUserInputNodeType) {
        GetUserInputNodeType getUserInputNodeType = (GetUserInputNodeType) child;
        GetUserInputNode getUserInputNode = BuilderHelper.createGetUserInputNodeByXsd(outputNode, getUserInputNodeType);
        buildGetUserInputNode(getUserInputNode, getUserInputNodeType);
      }

      if (child instanceof IfNodeType) {
        IfNodeType ifNodeType = (IfNodeType) child;
        IfNode ifNode = BuilderHelper.createIfNodeByXsd(outputNode, ifNodeType);
        buildIfNode(ifNode, ifNodeType);
      }

      if (child instanceof GotoNodeType) {
        GotoNodeType gotoNodeType = (GotoNodeType) child;
        GoToNode goToNode = BuilderHelper.createGoToNodeByXsd(outputNode, gotoNodeType);
        buildGoToNode(goToNode, gotoNodeType);
      }
    }
  }

  private static void buildGetUserInputNode(GetUserInputNode getUserInputNode, GetUserInputNodeType getUserInputNodeType) {
    for (Object obj : getUserInputNodeType.getActionOrScript()) {
      if (obj instanceof ActionType) {
        ActionType actionType = (ActionType) obj;
        BuilderHelper.createActionByXsd(getUserInputNode, actionType);
      }
    }

    for (ChatflowNode child : getUserInputNodeType.getInputOrOutputOrDefault()) {
      if (child instanceof InputNodeType) {
        InputNodeType inputNodeType = (InputNodeType) child;
        InputNode inputNode = BuilderHelper.createInputNodeByXsd(getUserInputNode, inputNodeType);
        buildInputNode(inputNode, inputNodeType);
      }

      if (child instanceof OutputNodeType) {
        OutputNodeType outputNodeType = (OutputNodeType) child;
        OutputNode outputNode = BuilderHelper.createOutputNodeByXsd(getUserInputNode, outputNodeType);
        buildOutputNode(outputNode, outputNodeType);
      }

      if (child instanceof SearchNodeType) {
        SearchNodeType searchNodeType = (SearchNodeType) child;
        SearchNode searchNode = BuilderHelper.createSearchNodeByXsd(getUserInputNode, searchNodeType);
        buildSearchNode(searchNode, searchNodeType);
      }

      if (child instanceof IfNodeType) {
        IfNodeType ifNodeType = (IfNodeType) child;
        IfNode ifNode = BuilderHelper.createIfNodeByXsd(getUserInputNode, ifNodeType);
        buildIfNode(ifNode, ifNodeType);
      }

      if (child instanceof GotoNodeType) {
        GotoNodeType gotoNodeType = (GotoNodeType) child;
        GoToNode gotoNode = BuilderHelper.createGoToNodeByXsd(getUserInputNode, gotoNodeType);
        buildGoToNode(gotoNode, gotoNodeType);
      }

      if (child instanceof DefaultNodeType) {
        DefaultNodeType defaultNodeType = (DefaultNodeType) child;
        DefaultNode defaultNode = BuilderHelper.createDefaultNodeByXsd(getUserInputNode, defaultNodeType);
        buildDefaultNode(defaultNode, defaultNodeType);
      }
    }
  }

  private static void buildSearchNode(SearchNode searchNode, SearchNodeType searchNodeType) {
    for (Object obj : searchNodeType.getActionOrScript()) {
      if (obj instanceof ActionType) {
        ActionType actionType = (ActionType) obj;
        BuilderHelper.createActionByXsd(searchNode, actionType);
      }
    }
  }

  private static void buildDefaultNode(DefaultNode defaultNode, DefaultNodeType defaultNodeType) {
    for (Object obj : defaultNodeType.getActionOrScript()) {
      if (obj instanceof ActionType) {
        ActionType actionType = (ActionType) obj;
        BuilderHelper.createActionByXsd(defaultNode, actionType);
      }
    }

    for (ChatflowNode child : defaultNodeType.getInputOrOutputOrDefault()) {
      if (child instanceof OutputNodeType) {
        OutputNodeType outputNodeType = (OutputNodeType) child;
        OutputNode outputNode = BuilderHelper.createOutputNodeByXsd(defaultNode, outputNodeType);
        buildOutputNode(outputNode, outputNodeType);
      }

      if (child instanceof GetUserInputNodeType) {
        GetUserInputNodeType getUserInputNodeType = (GetUserInputNodeType) child;
        GetUserInputNode getUserInputNode = BuilderHelper.createGetUserInputNodeByXsd(defaultNode, getUserInputNodeType);
        buildGetUserInputNode(getUserInputNode, getUserInputNodeType);
      }

      if (child instanceof IfNodeType) {
        IfNodeType ifNodeType = (IfNodeType) child;
        IfNode ifNode = BuilderHelper.createIfNodeByXsd(defaultNode, ifNodeType);
        buildIfNode(ifNode, ifNodeType);
      }

      if (child instanceof GotoNodeType) {
        GotoNodeType gotoNodeType = (GotoNodeType) child;
        GoToNode gotoNode = BuilderHelper.createGoToNodeByXsd(defaultNode, gotoNodeType);
        buildGoToNode(gotoNode, gotoNodeType);
      }
    }
  }

  private static void buildIfNode(IfNode ifNode, IfNodeType ifNodeType) {
    for (Object obj : ifNodeType.getActionOrScriptOrCond()) {
      if (obj instanceof ActionType) {
        ActionType actionType = (ActionType) obj;
        BuilderHelper.createActionByXsd(ifNode, actionType);
      }

      if (obj instanceof CondType) {
        CondType condType = (CondType) obj;
        BuilderHelper.createCondByXsd(ifNode, condType);
      }
    }

    for (ChatflowNode child : ifNodeType.getInputOrOutputOrDefault()) {
      if (child instanceof InputNodeType) {
        InputNodeType inputNodeType = (InputNodeType) child;
        InputNode inputNode = BuilderHelper.createInputNodeByXsd(ifNode, inputNodeType);
        buildInputNode(inputNode, inputNodeType);
      }

      if (child instanceof OutputNodeType) {
        OutputNodeType outputNodeType = (OutputNodeType) child;
        OutputNode outputNode = BuilderHelper.createOutputNodeByXsd(ifNode, outputNodeType);
        buildOutputNode(outputNode, outputNodeType);
      }

      if (child instanceof GetUserInputNodeType) {
        GetUserInputNodeType getUserInputNodeType = (GetUserInputNodeType) child;
        GetUserInputNode getUserInputNode = BuilderHelper.createGetUserInputNodeByXsd(ifNode, getUserInputNodeType);
        buildGetUserInputNode(getUserInputNode, getUserInputNodeType);
      }

      if (child instanceof IfNodeType) {
        IfNodeType childIfNodeType = (IfNodeType) child;
        IfNode childIfNode = BuilderHelper.createIfNodeByXsd(ifNode, childIfNodeType);
        buildIfNode(childIfNode, childIfNodeType);
      }

      if (child instanceof GotoNodeType) {
        GotoNodeType gotoNodeType = (GotoNodeType) child;
        GoToNode gotoNode = BuilderHelper.createGoToNodeByXsd(ifNode, gotoNodeType);
        buildGoToNode(gotoNode, gotoNodeType);
      }
    }
  }

  private static void buildGoToNode(GoToNode goToNode, GotoNodeType gotoNodeType) {
    for (Object obj : gotoNodeType.getActionOrScript()) {
      if (obj instanceof ActionType) {
        ActionType actionType = (ActionType) obj;
        BuilderHelper.createActionByXsd(goToNode, actionType);
      }
    }
  }

  private static void buildConceptNode(ConceptNode conceptNode, ConceptNodeType conceptNodeType) {
    for (Object obj : conceptNodeType.getGrammarOrConceptOrFolder()) {
      if (obj instanceof GrammarType) {
        GrammarType grammarType = (GrammarType) obj;
        Grammar grammar = BuilderHelper.createGrammarByXsd(grammarType);
        grammar.setParent(conceptNode);
        conceptNode.getGrammars().add(grammar);

        for (JAXBElement<?> jaxbElement : grammarType.getItemOrSourceOrParam()) {
          @SuppressWarnings("unchecked")
          Item item = BuilderHelper.createItemByXsd((JAXBElement<String>)jaxbElement);
          item.setParent(grammar);
          grammar.getItems().add(item);
        }
      }
    }
  }

  private static void buildEntities(Entities entities, EntitiesType entitiesType) {
    for (EntityType entityType : entitiesType.getEntity()) {
      Entity entity = BuilderHelper.createEntityByXsd(entities, entityType);
      buildEntity(entity, entityType);
    }
  }

  private static void buildEntity(Entity entity, EntityType entityType) {
    for (EntityValueType entityValueType : entityType.getValue()) {
      EntityValue entityValue = BuilderHelper.createEntityValueByXsd(entity, entityValueType);
      buildEntityValue(entityValue, entityValueType);
    }
  }

  private static void buildEntityValue(EntityValue entityValue, EntityValueType entityValueType) {
    for (GrammarType grammarType : entityValueType.getGrammar()) {
      Grammar grammar = BuilderHelper.createGrammarByXsd(grammarType);
      grammar.setParent(entityValue);
      entityValue.getGrammars().add(grammar);

      for (JAXBElement<?> jaxbElement : grammarType.getItemOrSourceOrParam()) {
        @SuppressWarnings("unchecked")
        Item item = BuilderHelper.createItemByXsd((JAXBElement<String>)jaxbElement);
        item.setParent(grammar);
        grammar.getItems().add(item);
      }
    }
  }

  private static void buildVariables(Variables variables, VariablesType variablesType) {
    for (VarFolderType varFolderType : variablesType.getVarFolder()) {
      VarFolder varFolder = BuilderHelper.createVarFolderByXsd(variables, varFolderType);
      buildVarFolder(varFolder, varFolderType);
    }
  }

  private static void buildConstants(Constants constants, ConstantsType constantsType) {
    for (VarFolderType varFolderType : constantsType.getVarFolder()) {
      VarFolder varFolder = BuilderHelper.createVarFolderByXsd(constants, varFolderType);
      buildVarFolder(varFolder, varFolderType);
    }
  }

  private static void buildVarFolder(VarFolder varFolder, VarFolderType varFolderType) {
    for (VarFolderType parentFolderType : varFolderType.getVarFolder()) {
      BuilderHelper.createVarFolderByXsd(varFolder, parentFolderType);
    }

    for (VarType varType : varFolderType.getVar()) {
      BuilderHelper.createVarByXsd(varFolder, varType);
    }
  }

  private static void buildSettings(Settings settings, SettingsType settingsType) {
    for (SettingType settingType : settingsType.getSetting()) {
      BuilderHelper.createSettingByXsd(settings, settingType);
    }
  }

  private static void buildSpecialSettings(SpecialSettings specialSettings, SpecialSettingsType specialSettingsType) {
    for (SpecialSettingType specialSettingType : specialSettingsType.getSpecialSetting()) {
      SpecialSetting specialSetting = BuilderHelper.createSpecialSettingByXsd(specialSettings, specialSettingType);

      for (VariationsType variationsType : specialSettingType.getVariations()) {
        Variations variations = BuilderHelper.createVariationByXsd(specialSetting, variationsType);
        buildVariations(variations, variationsType);
      }
    }
  }

  private static void buildVariations(Variations variations, VariationsType variationsType) {
    for (String str : variationsType.getItem()) {
      Item item = new Item();
      item.setValue(str);
      item.setParent(variations);
      variations.getItems().add(item);
    }
  }
}
