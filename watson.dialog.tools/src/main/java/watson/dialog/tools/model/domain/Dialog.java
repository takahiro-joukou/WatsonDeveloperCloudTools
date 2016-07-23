package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.DialogType;

public class Dialog extends BaseModel<DialogType>{

  private Flow flow;

  private List<Entities> entitiesList = new ArrayList<Entities>();

  private List<Constants> constantsList = new ArrayList<Constants>();

  private List<Variables> variablesList = new ArrayList<Variables>();

  private List<Settings> settingsList = new ArrayList<Settings>();

  private List<SpecialSettings> specialSettingsList = new ArrayList<SpecialSettings>();

  public Flow getFlow() {
    return flow;
  }

  public void setFlow(Flow flow) {
    this.flow = flow;
  }

  public List<Entities> getEntitiesList() {
    return entitiesList;
  }

  public void setEntitiesList(List<Entities> entitiesList) {
    this.entitiesList = entitiesList;
  }

  public List<Constants> getConstantsList() {
    return constantsList;
  }

  public void setConstantsList(List<Constants> constantsList) {
    this.constantsList = constantsList;
  }

  public List<Variables> getVariablesList() {
    return variablesList;
  }

  public void setVariablesList(List<Variables> variablesList) {
    this.variablesList = variablesList;
  }

  public List<Settings> getSettingsList() {
    return settingsList;
  }

  public void setSettingsList(List<Settings> settingsList) {
    this.settingsList = settingsList;
  }

  public List<SpecialSettings> getSpecialSettingsList() {
    return specialSettingsList;
  }

  public void setSpecialSettingsList(List<SpecialSettings> specialSettingsList) {
    this.specialSettingsList = specialSettingsList;
  }
}
