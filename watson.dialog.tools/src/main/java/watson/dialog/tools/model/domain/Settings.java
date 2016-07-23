package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.SettingsType;

public class Settings extends BaseModel<SettingsType>{

  private List<Setting> settings = new ArrayList<Setting>();

  public List<Setting> getSettings() {
    return settings;
  }

  public void setSettings(List<Setting> settings) {
    this.settings = settings;
  }
}
