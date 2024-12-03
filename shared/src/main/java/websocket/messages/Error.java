package websocket.messages;

public class Error extends ServerMessage {
  private final String errorMessage;

  public Error(String errorMessage) {
    this.errorMessage = errorMessage;
    this.serverMessageType = ServerMessageType.ERROR;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
