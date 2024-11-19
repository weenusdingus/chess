package ui;

import model.GameData;
import server.ServerFacade;
import request.*;
import response.*;
import exception.ResponseException;
import chess.ChessGame.TeamColor;

import java.util.ArrayList;
import java.util.Scanner;

public class ChessClient {
  private final ServerFacade serverFacade;
  private final Scanner scanner;
  private boolean loggedIn;
  private String authToken;

  public ChessClient(String serverUrl) {
    this.serverFacade = new ServerFacade(serverUrl);
    this.scanner = new Scanner(System.in);
    this.loggedIn = false;
    this.authToken = null;
  }

  public void start() {
    while (true) {
      if (!loggedIn) {
        preloginMenu();
      } else {
        postloginMenu();
      }
    }
  }

  private void preloginMenu() {
    System.out.println("\nPrelogin Menu - Enter a command:");
    System.out.println("help, register, login, quit");

    String command = scanner.nextLine().trim().toLowerCase();
    switch (command) {
      case "help" -> displayHelp(false);
      case "register" -> register();
      case "login" -> login();
      case "quit" -> quit();
      default -> System.out.println("Invalid command. Type 'help' for options.");
    }
  }

  private void postloginMenu() {
    System.out.println("\nPostlogin Menu - Enter a command:");
    System.out.println("help, logout, create game, list games, play game, quit");

    String command = scanner.nextLine().trim().toLowerCase();
    switch (command) {
      case "help" -> displayHelp(true);
      case "logout" -> logout();
      case "create game" -> createGame();
      case "list games" -> listGames();
      case "play game" -> playGame();
      case "quit" -> quit();
      default -> System.out.println("Invalid command. Type 'help' for options.");
    }
  }

  private void displayHelp(boolean loggedIn) {
    if (loggedIn) {
      System.out.println("Available commands: help, logout, create game, list games, play game, quit");
    } else {
      System.out.println("Available commands: help, register, login, quit");
    }
  }

  private void register() {
    System.out.print("Enter username: ");
    String username = scanner.nextLine();
    System.out.print("Enter password: ");
    String password = scanner.nextLine();
    System.out.print("Enter email: ");
    String email = scanner.nextLine();

    try {
      var request = new RegisterRequest(username, password, email);
      var response = serverFacade.register(request);
      this.authToken = response.authToken();
      this.loggedIn = true;
      System.out.println("Registration successful! You are now logged in.");
    } catch (ResponseException e) {
      System.out.println("Registration failed: " + e.getMessage());
    }
  }

  private void login() {
    System.out.print("Enter username: ");
    String username = scanner.nextLine();
    System.out.print("Enter password: ");
    String password = scanner.nextLine();

    try {
      var request = new LoginRequest(username, password);
      var response = serverFacade.login(request);
      this.authToken = response.authToken();
      this.loggedIn = true;
      System.out.println("Login successful!");
    } catch (ResponseException e) {
      System.out.println("Login failed: " + e.getMessage());
    }
  }

  private void logout() {
    try {
      var request = new LogoutRequest(authToken);
      serverFacade.logout(request);
      this.authToken = null;
      this.loggedIn = false;
      System.out.println("Logged out successfully.");
    } catch (ResponseException e) {
      System.out.println("Logout failed: " + e.getMessage());
    }
  }

  private void createGame() {
    System.out.print("Enter a name for the new game: ");
    String gameName = scanner.nextLine();

    try {
      var request = new CreateGameRequest(authToken, gameName);
      serverFacade.create(request);
      System.out.println("Game created successfully.");
    } catch (ResponseException e) {
      System.out.println("Failed to create game: " + e.getMessage());
    }
  }

  private void listGames() {
    try {
      var request = new GetGamesRequest(authToken);
      var response = serverFacade.list(request);
      ArrayList<GameData> games = (ArrayList<GameData>) response.games();

      System.out.println("Available games:");
      for (int i = 0; i < games.size(); i++) {
        GameData game = games.get(i);
        System.out.printf("%d. Game name: %-8s White: %-8s Black: %-8s\n",
        i + 1, game.gameName(), game.whiteUsername(), game.blackUsername());
      }
    } catch (ResponseException e) {
      System.out.println("Failed to list games: " + e.getMessage());
    }
  }

  private void playGame() {
    System.out.print("Enter the number of the game you want to join: ");
    int gameNumber = Integer.parseInt(scanner.nextLine());

    System.out.print("Choose color (white/black): ");
    String colorInput = scanner.nextLine().trim().toLowerCase();

    TeamColor color;
    if (colorInput.equals("white")) {
      color = TeamColor.WHITE;
    } else if (colorInput.equals("black")) {
      color = TeamColor.BLACK;
    } else {
      System.out.println("Invalid color choice. Please choose 'white' or 'black'.");
      return; // Exit the method if the input is invalid.
    }

    try {
      var request = new JoinGameRequest(authToken, color, gameNumber);
      serverFacade.join(request);
      System.out.println("Joined game successfully.");
      drawBoard(color == TeamColor.WHITE);
    } catch (ResponseException e) {
      System.out.println("Failed to join game: " + e.getMessage());
    }
  }

  private void drawBoard(boolean whitePerspective) {
    char[][] board = {
            {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
            {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
    };

    System.out.println(whitePerspective ? "White's perspective:" : "Black's perspective:");
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        char piece = whitePerspective ? board[row][col] : board[7 - row][7 - col];
        System.out.print(piece + " ");
      }
      System.out.println();
    }
  }

  private void quit() {
    System.out.println("Goodbye!");
    System.exit(0);
  }
}

