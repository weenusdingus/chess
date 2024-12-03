package websocket.messages;

public class Notification extends ServerMessage {
  private final String message;

  public Notification(String message) {
    this.message = message;
    this.serverMessageType = ServerMessageType.NOTIFICATION;
  }

  public String getMessage() {
    return message;
  }
}
