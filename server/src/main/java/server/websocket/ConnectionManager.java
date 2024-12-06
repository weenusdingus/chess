package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
  final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> gameConnections = new ConcurrentHashMap<>();

  // Add a user to a specific game
  public void add(int gameID, String username, Session session) {
    var connection = new Connection(username, session);
    connections.put(username, connection);

    gameConnections.putIfAbsent(gameID, new ConcurrentHashMap<>());
    gameConnections.get(gameID).put(username, connection);
  }

  // Remove a user from all tracking
  public void remove(int gameID, String username) {
    if (gameConnections.containsKey(gameID)) {
      gameConnections.get(gameID).remove(username);
      if (gameConnections.get(gameID).isEmpty()) {
        gameConnections.remove(gameID);
      }
    }
    connections.remove(username);
  }

  // Broadcast to all users in the same game
  public void broadcast(int gameID, String excludeUsername, String message) throws IOException {
    if (gameConnections.containsKey(gameID)) {
      for (var c : gameConnections.get(gameID).values()) {
        if (c.session.isOpen() && !c.username.equals(excludeUsername)) {
          c.send(message);
        }
      }
    }
  }

  // Send a message to a specific user
  public void send(String username, String message) throws IOException {
    Connection connection = connections.get(username);
    if (connection != null && connection.session.isOpen()) {
      connection.send(message);
    }
  }

  // Cancel a session for a user
  public void cancelSession(Session session, String message) throws IOException {
    for (var c : connections.values()) {
      if (c.session.equals(session) && c.session.isOpen()) {
        c.send(message);
        return;
      }
    }
  }
}

