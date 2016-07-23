package watson.dialog.tools.exception;

public class WatsonDialogToolsException extends RuntimeException {
  /**
   *
   * @param message
   * @param cause
   */
  public WatsonDialogToolsException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   *
   * @param cause
   */
  public WatsonDialogToolsException(Throwable cause) {
    super(cause);
  }
}
