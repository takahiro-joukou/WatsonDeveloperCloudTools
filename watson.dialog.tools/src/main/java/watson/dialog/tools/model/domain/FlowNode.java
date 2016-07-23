package watson.dialog.tools.model.domain;

import watson.dialog.tools.model.dao.xsd.ChatflowNode;



public abstract class FlowNode<T> extends BaseModel<T> {

  protected String gid;

  protected String id;

  protected String comment;

  protected String date;

  protected boolean isOffline;

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
      ((ChatflowNode)peerType).setId(id);
    }
    this.id = id;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    if (peerType != null) {
      ((ChatflowNode) peerType).setComment(comment);
    }
    this.comment = comment;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public boolean getIsOffline() {
    return isOffline;
  }

  public void setIsOffline(boolean isOffline) {
    if (peerType != null) {
      ((ChatflowNode) peerType).setIsOffline(isOffline);
    }
    this.isOffline = isOffline;
  }
}
