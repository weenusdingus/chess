package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.MakeMoveCommand;
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
        case MAKE_MOVE -> handleMakeMove(session, username, message);
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

  private void handleMakeMove(Session session, String username, String message) throws IOException, DataAccessException {
    MakeMoveCommand command = gson.fromJson(message, MakeMoveCommand.class);
    System.out.println("Received command: " + command);
    GameData gameData = dataAccess.getGame(command.getGameID());
    if (gameData == null) {
      session.getRemote().sendString(createErrorMessage("Game not found."));
      return;
    }

    ChessGame game = gameData.game();

    try {
      // Deserialize the MakeMoveCommand
      MakeMoveCommand makeMoveCommand = gson.fromJson(gson.toJson(command), MakeMoveCommand.class);
      ChessMove move = makeMoveCommand.getMove();

      // Log the received move
      System.out.println("Received Move: " + move);  // Log move here

      // Check if move is null
      if (move == null) {
        session.getRemote().sendString(createErrorMessage("Move cannot be null."));
        return;
      }

      // Check if the user is authorized to make this move
      boolean isWhitePlayer = username.equals(gameData.whiteUsername());
      boolean isBlackPlayer = username.equals(gameData.blackUsername());

      // Ensure the user is one of the players
      if (!isWhitePlayer && !isBlackPlayer) {
        session.getRemote().sendString(createErrorMessage("You are not a player in this game."));
        return;
      }

      // Ensure it's the user's turn
      if ((game.getTeamTurn() == ChessGame.TeamColor.WHITE && !isWhitePlayer) ||
              (game.getTeamTurn() == ChessGame.TeamColor.BLACK && !isBlackPlayer)) {
        session.getRemote().sendString(createErrorMessage("It's not your turn."));
        return;
      }

      // Attempt to make the move
      game.makeMove(move);
      dataAccess.updateGame(command.getGameID(), gameData);

      // Notify other players with an updated game state
      LoadGameMessage loadGameMessage = new LoadGameMessage(
              gameData.gameID(),
              gameData.whiteUsername(),
              gameData.blackUsername(),
              gameData.gameName(),
              game
      );

      connectionManager.broadcast(username, gson.toJson(loadGameMessage));
      connectionManager.send(username, gson.toJson(loadGameMessage));
      connectionManager.broadcast(username, createNotificationMessage("Move made"));


    } catch (InvalidMoveException e) {
      session.getRemote().sendString(createErrorMessage("Invalid move: " + e.getMessage()));
    } catch (Exception e) {
      session.getRemote().sendString(createErrorMessage("Error processing message: " + e.getMessage()));
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



