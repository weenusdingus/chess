package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.HttpRetryException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
  Session session;
  NotificationHandler notificationHandler;

  public WebSocketFacade(String url, NotificationHandler notificationHandler) throws HttpRetryException{
    try {
      url = url.replace("http", "ws");
      URI socketURI = new URI(url + "/ws");
      this.notificationHandler = notificationHandler;

      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      this.session = container.connectToServer(this, socketURI);
      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
          notificationHandler.notify(message);
        }
      });
    } catch (DeploymentException | IOException | URISyntaxException ex) {
      throw new HttpRetryException(ex.getMessage(),500);
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
  }

  public void connectToGame(String authToken, int gameID) throws HttpRetryException {
    try {
      UserGameCommand action = new UserGameCommand(UserGameCommand.CommandType.CONNECT,authToken, gameID);
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
    } catch (IOException ex) {
      throw new HttpRetryException(ex.getMessage(),500);
    }
  }

  public void makeMove(String authToken, int gameID, ChessMove move) throws HttpRetryException {
    try {
      // Ensure move is not null before sending
      if (move == null) {
        throw new HttpRetryException("Move cannot be null.", 400);  // Throw if move is null
      }

      // Construct MakeMoveCommand with the move
      MakeMoveCommand action = new MakeMoveCommand(authToken, gameID, move);
      String message = new Gson().toJson(action);  // Serialize the entire command

      // Send the message through WebSocket
      this.session.getBasicRemote().sendText(message);
    } catch (IOException ex) {
      throw new HttpRetryException(ex.getMessage(), 500);
    }
  }



  public void leaveGame(String authToken, int gameID) throws HttpRetryException {
    try {
      var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
      this.session.close();
    } catch (IOException ex) {
      throw new HttpRetryException(ex.getMessage(),500);
    }
  }

  public void resignGame(String authToken, int gameID) throws HttpRetryException {
    try {
      var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
      this.session.getBasicRemote().sendText(new Gson().toJson(action));
    } catch (IOException ex) {
      throw new HttpRetryException(ex.getMessage(),500);
    }
  }

}
