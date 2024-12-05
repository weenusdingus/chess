package websocket.commands;

public class ResignCommand extends UserGameCommand {

  public ResignCommand(String auth, Integer gameID) {
    super(CommandType.RESIGN, auth, gameID);
  }
}
