package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.VariablesType;

public class Variables extends BaseModel<VariablesType>{

  private List<VarFolder> varFolders = new ArrayList<VarFolder>();

  public List<VarFolder> getVarFolders() {
    return varFolders;
  }

  public void setVarFolders(List<VarFolder> varFolders) {
    this.varFolders = varFolders;
  }
}
