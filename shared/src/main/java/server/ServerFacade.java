package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import model.GameData;
import model.AuthData;

import java.io.*;
import java.net.*;
import java.util.Collection;

public class ServerFacade {

  private final String serverUrl;

  public ServerFacade(String url) {
    serverUrl = url;
  }

  public UserData createUser(UserData user) throws ResponseException {
    var path = "/user";
    return this.makeRequest("POST", path, user, UserData.class);
  }

  public UserData getUser(String username) throws ResponseException {
    var path = String.format("/user/%s", username);
    return this.makeRequest("GET", path, null, UserData.class);
  }

  public GameData createGame(GameData game) throws ResponseException {
    var path = "/game";
    return this.makeRequest("POST", path, game, GameData.class);
  }

  public GameData getGame(int gameID) throws ResponseException {
    var path = String.format("/game/%d", gameID);
    return this.makeRequest("GET", path, null, GameData.class);
  }

  public Collection<GameData> listGames() throws ResponseException {
    var path = "/games";
    record ListGamesResponse(Collection<GameData> games) {}
    var response = this.makeRequest("GET", path, null, ListGamesResponse.class);
    return response.games();
  }

  public void updateGame(int gameID, GameData game) throws ResponseException {
    var path = String.format("/game/%d", gameID);
    this.makeRequest("PUT", path, game, null);
  }

  public AuthData createAuth(AuthData auth) throws ResponseException {
    var path = "/auth";
    return this.makeRequest("POST", path, auth, AuthData.class);
  }

  public AuthData getAuth(String authToken) throws ResponseException {
    var path = String.format("/auth/%s", authToken);
    return this.makeRequest("GET", path, null, AuthData.class);
  }

  public void deleteAuth(String authToken) throws ResponseException {
    var path = String.format("/auth/%s", authToken);
    this.makeRequest("DELETE", path, null, null);
  }

  public void clear() throws ResponseException {
    var path = "/clear";
    this.makeRequest("POST", path, null, null);
  }

  private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
    try {
      URL url = (new URI(serverUrl + path)).toURL();
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setRequestMethod(method);
      http.setDoOutput(true);

      writeBody(request, http);
      http.connect();
      throwIfNotSuccessful(http);
      return readBody(http, responseClass);
    } catch (Exception ex) {
      throw new ResponseException( ex.getMessage());
    }
  }

  private static void writeBody(Object request, HttpURLConnection http) throws IOException {
    if (request != null) {
      http.addRequestProperty("Content-Type", "application/json");
      String reqData = new Gson().toJson(request);
      try (OutputStream reqBody = http.getOutputStream()) {
        reqBody.write(reqData.getBytes());
      }
    }
  }

  private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
    var status = http.getResponseCode();
    if (!isSuccessful(status)) {
      throw new ResponseException("Request failed with status: " + status);
    }
  }

  private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
    T response = null;
    if (http.getContentLength() > 0) {
      try (InputStream respBody = http.getInputStream()) {
        InputStreamReader reader = new InputStreamReader(respBody);
        if (responseClass != null) {
          response = new Gson().fromJson(reader, responseClass);
        }
      }
    }
    return response;
  }

  private boolean isSuccessful(int status) {
    return status / 100 == 2;
  }
}
