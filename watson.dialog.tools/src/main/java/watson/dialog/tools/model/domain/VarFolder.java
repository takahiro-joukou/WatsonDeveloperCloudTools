package watson.dialog.tools.model.domain;

import java.util.ArrayList;
import java.util.List;

import watson.dialog.tools.model.dao.xsd.VarFolderTypeType;
import watson.dialog.tools.model.domain.IType.VarFolderType;

public class VarFolder extends BaseModel<watson.dialog.tools.model.dao.xsd.VarFolderType> {

  public static final String NODE_NAME = "var_folder";

  private String gid;

  private String id;

  private String name;

  private VarFolderType type;

  private List<VarFolder> varFolders = new ArrayList<VarFolder>();

  private List<Var> vars = new ArrayList<Var>();

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (peerType != null) {
      peerType.setName(name);
    }
    this.name = name;
  }

  public VarFolderType getType() {
    return type;
  }

  public void setType(VarFolderType type) {
    if (peerType != null) {
      peerType.setType(VarFolderTypeType.fromValue(type.value()));
    }
    this.type = type;
  }

  public List<VarFolder> getVarFolders() {
    return varFolders;
  }

  public void setVarFolders(List<VarFolder> varFolders) {
    this.varFolders = varFolders;
  }

  public List<Var> getVars() {
    return vars;
  }

  public void setVars(List<Var> vars) {
    this.vars = vars;
  }
}
