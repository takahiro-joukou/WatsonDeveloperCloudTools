package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.SpecialSettingsType;

public class SpecialSettings extends BaseModel<SpecialSettingsType>{

  private List<SpecialSetting> specialSettings = new ArrayList<SpecialSetting>();

  public List<SpecialSetting> getSpecialSettings() {
    return specialSettings;
  }

  public void setSpecialSettings(List<SpecialSetting> specialSettings) {
    this.specialSettings = specialSettings;
  }
}
