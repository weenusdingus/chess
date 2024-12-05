package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.messages.*;
import websocket.commands.*;

import javax.xml.crypto.Data;
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
    GameData game = dataAccess.getGame(command.getGameID());

    if (game == null) {
      session.getRemote().sendString(createErrorMessage("Game not found."));
      return;
    }

    connectionManager.broadcast(username, createNotificationMessage(username + " joined the game."));
    session.getRemote().sendString(createServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gson.toJson(game)));
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
    ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
    return gson.toJson(errorMessage) + " " + error;
  }

  private String createNotificationMessage(String notification) {
    ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    return gson.toJson(notificationMessage) + " " + notification;
  }

  private String createServerMessage(ServerMessage.ServerMessageType type, String data) {
    ServerMessage message = new ServerMessage(type);
    return gson.toJson(message) + " " + data;
  }
}


