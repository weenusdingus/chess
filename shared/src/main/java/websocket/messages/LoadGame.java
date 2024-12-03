package websocket.messages;

import chess.ChessGame;

public class LoadGame extends ServerMessage {
  private final ChessGame game;

  public LoadGame(ChessGame game) {
    this.serverMessageType = ServerMessageType.LOAD_GAME;
    this.game = game;
  }

  public ChessGame getGame() {
    return game;
  }
}