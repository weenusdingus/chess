package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
  private final int gameID;
  private final String whiteUsername;
  private final String blackUsername;
  private final String gameName;
  private final ChessGame game;

  public LoadGameMessage(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    this.serverMessageType = ServerMessageType.LOAD_GAME;
    this.gameID = gameID;
    this.whiteUsername = whiteUsername;
    this.blackUsername = blackUsername;
    this.gameName = gameName;
    this.game = game;
  }

  public int getGameID() {
    return gameID;
  }

  public String getWhiteUsername() {
    return whiteUsername;
  }

  public String getBlackUsername() {
    return blackUsername;
  }

  public String getGameName() {
    return gameName;
  }

  public ChessGame getGame() {
    return game;
  }
}