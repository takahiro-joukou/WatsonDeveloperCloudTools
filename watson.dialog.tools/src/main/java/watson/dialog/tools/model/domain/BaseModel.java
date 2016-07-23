package watson.dialog.tools.model.domain;


public abstract class BaseModel<T> {

  protected T peerType;

  protected BaseModel<?> parent;

  public T getPeerType() {
    return peerType;
  }

  public void setPeerType(T peerType) {
    this.peerType = peerType;
  }

  public BaseModel<?> getParent() {
    return parent;
  }

  public void setParent(BaseModel<?> parent) {
    this.parent = parent;
  }
}
