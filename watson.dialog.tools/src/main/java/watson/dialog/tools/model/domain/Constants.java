package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.ConstantsType;

public class Constants extends BaseModel<ConstantsType>{

  private List<VarFolder> varFolders = new ArrayList<VarFolder>();

  public List<VarFolder> getVarFolders() {
    return varFolders;
  }

  public void setVarFolders(List<VarFolder> varFolders) {
    this.varFolders = varFolders;
  }
}
