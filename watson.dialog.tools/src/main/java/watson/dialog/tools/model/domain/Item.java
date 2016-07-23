package watson.dialog.tools.model.domain;

import javax.xml.bind.JAXBElement;

public class Item extends BaseModel<JAXBElement<String>> {

  public static final String DEFAULT_ITEM_VALUE = "FIXME : Auto-generated item value";

  private String value;

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
