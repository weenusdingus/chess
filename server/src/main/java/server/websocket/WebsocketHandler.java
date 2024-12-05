package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
  private final ConnectionManager connectionManager = new ConnectionManager();
  private final DataAccess dataAccess;
  private final Gson gson = new Gson();

  public WebsocketHandler(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
  }

  @OnWebSocketConnect
  public void onConnect(Session session) {
    System.out.println("A client connected: " + session.getRemoteAddress().getAddress());
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) {
    try {
      UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

      // Authenticate the user
      AuthData authData = dataAccess.getAuth(command.getAuthToken());
      if (authData == null) {
        session.getRemote().sendString(createErrorMessage("Authentication failed."));
        return;
      }

      String username = authData.username();

      switch (command.getCommandType()) {
        case CONNECT -> handleConnect(session, username, command);
        case MAKE_MOVE -> handleMakeMove(session, username, command);
        case LEAVE -> handleLeave(session, username, command);
        case RESIGN -> handleResign(session, username, command);
      }
    } catch (Exception e) {
      try {
        session.getRemote().sendString(createErrorMessage("Error processing message: " + e.getMessage()));
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  }

  @OnWebSocketClose
  public void onClose(Session session, int statusCode, String reason) {
    System.out.println("A client disconnected: " + reason);
    connectionManager.connections.values().removeIf(conn -> conn.session.equals(session));
  }

  @OnWebSocketError
  public void onError(Session session, Throwable error) {
    System.err.println("WebSocket error: " + error.getMessage());
  }

  private void handleConnect(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
    connectionManager.add(username, session);

    GameData gameData = dataAccess.getGame(command.getGameID());
    if (gameData == null) {
      session.getRemote().sendString(createErrorMessage("Game not found."));
      return;
    }

    // Broadcast notification to other players/observers
    connectionManager.broadcast(username, createNotificationMessage(username + " joined the game."));

    // Send LOAD_GAME message with all game details
    LoadGameMessage loadGameMessage = new LoadGameMessage(
            gameData.gameID(),
            gameData.whiteUsername(),
            gameData.blackUsername(),
            gameData.gameName(),
            gameData.game()
    );
    session.getRemote().sendString(gson.toJson(loadGameMessage));
  }

  private void handleMakeMove(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
    GameData gameData = dataAccess.getGame(command.getGameID());
    if (gameData == null) {
      session.getRemote().sendString(createErrorMessage("Game not found."));
      return;
    }

    ChessGame game = gameData.game();
    ChessMove move = gson.fromJson(command.getAuthToken(), ChessMove.class);
    try {
      game.makeMove(move);
      dataAccess.updateGame(command.getGameID(), gameData);

      connectionManager.broadcast(username, createNotificationMessage(username + " made a move: " + move));
    } catch (Exception e) {
      session.getRemote().sendString(createErrorMessage("Invalid move: " + e.getMessage()));
    }
  }

  private void handleLeave(Session session, String username, UserGameCommand command) throws IOException {
    connectionManager.remove(username);
    connectionManager.broadcast(username, createNotificationMessage(username + " left the game."));
  }

  private void handleResign(Session session, String username, UserGameCommand command) throws IOException {
    connectionManager.broadcast(username, createNotificationMessage(username + " resigned."));
  }

  private String createErrorMessage(String error) {
    ErrorMessage errorMessage = new ErrorMessage(error);
    return gson.toJson(errorMessage);
  }

  private String createNotificationMessage(String notification) {
    JsonObject message = new JsonObject();
    message.addProperty("serverMessageType", ServerMessage.ServerMessageType.NOTIFICATION.toString());
    message.addProperty("message", notification);
    return gson.toJson(message);
  }

  private String createServerMessage(ServerMessage.ServerMessageType type, String data) {
    JsonObject message = new JsonObject();
    message.addProperty("serverMessageType", type.toString());
    message.add("data", gson.toJsonTree(data));
    return gson.toJson(message);
  }
}



