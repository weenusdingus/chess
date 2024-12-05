package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
  public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

  public void add(String username, Session session) {
    var connection = new Connection(username, session);
    connections.put(username, connection);
  }

  public void remove(String username) {
    connections.remove(username);
  }

  public void broadcast(String excludeUsername, String message) throws IOException {
    for (var c : connections.values()) {
      if (c.session.isOpen()) {
        if (!c.username.equals(excludeUsername)) {
          c.send(message);
        }
      }
    }
  }

  public void send(String username, String message) throws IOException {
    for (var c : connections.values()) {
      if (c.session.isOpen()) {
        if (c.username.equals(username)) {
          c.send(message);
        }
      }
    }
  }

  public void cancelSession(Session session, String message) throws IOException {
    var connection = new Connection(null, session);
    if (connection.session.isOpen()) {
      connection.send(message);
    }
  }
}
