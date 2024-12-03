package websocket.commands;

public class Resign extends UserGameCommand {

  public Resign(String auth, Integer gameID) {
    super(CommandType.RESIGN, auth, gameID);
  }
}
