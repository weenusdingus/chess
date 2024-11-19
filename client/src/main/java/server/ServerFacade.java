package server;

import com.google.gson.Gson;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ServerFacade {
  private final String serverUrl;
  private final Gson gson = new Gson();

  public ServerFacade(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  public String register(String username, String password, String email) throws IOException {
    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
      throw new IOException("Invalid registration data");
    }

    URL url = new URL(serverUrl + "/user");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);

    String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"}",
            username, password, email);
    try (OutputStream os = conn.getOutputStream()) {
      os.write(requestBody.getBytes());
    }

    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException("Registration failed with response code: " + conn.getResponseCode());
    }

    try (Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
      return scanner.hasNext() ? scanner.next() : "";
    }
  }

  public String login(String username, String password) throws IOException {
    URL url = new URL(serverUrl + "/session");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);

    String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
    try (OutputStream os = conn.getOutputStream()) {
      os.write(requestBody.getBytes());
    }

    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException("Login failed with response code: " + conn.getResponseCode());
    }

    try (Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
      return scanner.hasNext() ? scanner.next() : "";
    }
  }

  public void logout(String authToken) throws IOException {
    URL url = new URL(serverUrl + "/session");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("DELETE");
    conn.setRequestProperty("Authorization", authToken);

    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException("Logout failed with response code: " + conn.getResponseCode());
    }
  }

  public String createGame(String authToken, String gameName) throws IOException {
    URL url = new URL(serverUrl + "/game");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Authorization", authToken);
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);

    String jsonInputString = "{\"gameName\":\"" + gameName + "\"}";
    try (OutputStream os = conn.getOutputStream()) {
      os.write(jsonInputString.getBytes());
    }

    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException("Game creation failed with response code: " + conn.getResponseCode());
    }

    try (Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
      return scanner.hasNext() ? scanner.next() : "";
    }
  }

  public String listGames(String authToken) throws IOException {
    URL url = new URL(serverUrl + "/game");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Authorization", authToken);

    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException("Listing games failed with response code: " + conn.getResponseCode());
    }

    try (Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
      return scanner.hasNext() ? scanner.next() : "";
    }
  }

  public String joinGame(String authToken, int gameId, String color) throws IOException {
    URL url = new URL(serverUrl + "/game");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("PUT");
    conn.setRequestProperty("Authorization", authToken);
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);

    String jsonInputString = String.format("{\"gameID\":%d,\"playerColor\":\"%s\"}", gameId, color);
    try (OutputStream os = conn.getOutputStream()) {
      os.write(jsonInputString.getBytes());
    }

    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException("Join game failed with response code: " + conn.getResponseCode());
    }

    try (Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
      return scanner.hasNext() ? scanner.next() : "";
    }
  }
}
