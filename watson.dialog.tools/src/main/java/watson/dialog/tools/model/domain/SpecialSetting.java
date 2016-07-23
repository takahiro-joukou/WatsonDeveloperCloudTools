package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.SpecialSettingType;
import watson.dialog.tools.model.domain.IType.SelectionType;

public class SpecialSetting extends BaseModel<SpecialSettingType> {

  public static final String DEFAULT_SPECIAL_SETTING_TYPE_LABEL = "DNR join statement";

  public static final String NODE_NAME = "specialSetting";

  private String gid;

  private String id;

  private String label;

  private SelectionType selectionType;

  private String date;

  private List<Variations> variations = new ArrayList<Variations>();

  public String getGid() {
    return gid;
  }

  public void setGid(String gid) {
    this.gid = gid;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    if (peerType != null) {
      peerType.setId(id);
    }
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    if (peerType != null) {
      peerType.setLabel(label);
    }
    this.label = label;
  }

  public SelectionType getSelectionType() {
    return selectionType;
  }

  public void setSelectionType(SelectionType selectionType) {
    if (peerType != null) {
      peerType.setSelectionType(selectionType.value());
    }
    this.selectionType = selectionType;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<Variations> getVariations() {
    return variations;
  }

  public void setVariations(List<Variations> variations) {
    this.variations = variations;
  }
}
