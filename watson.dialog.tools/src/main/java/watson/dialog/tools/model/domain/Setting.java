package watson.dialog.tools.model.domain;

import watson.dialog.tools.model.dao.xsd.SettingTypeType;
import watson.dialog.tools.model.domain.IType.ConditionOperatorType;
import watson.dialog.tools.model.domain.IType.SettingType;

public class Setting extends BaseModel<watson.dialog.tools.model.dao.xsd.SettingType> {

  public static final String DEFAULT_NAME = "FIXME : name";

  public static final String DEFAULT_VALUE = "FIXME : value";

  public static final ConditionOperatorType DEFAULT_OPERATOR_TYPE = ConditionOperatorType.EQUALS;

  private String name;

  private SettingType settingType;

  private String value;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (peerType != null) {
      peerType.setName(name);
    }
    this.name = name;
  }

  public SettingType getSettingType() {
    return settingType;
  }

  public void setSettingType(SettingType type) {
    if (peerType != null) {
      if (type == null) {
        peerType.setType(null);
      } else {
        peerType.setType(SettingTypeType.fromValue(type.value()));
      }
    }
    this.settingType = type;
  }


  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    if (peerType != null) {
      peerType.setValue(value);
    }
    this.value = value;
  }
}
