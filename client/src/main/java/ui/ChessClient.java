package ui;

import exception.ResponseException;
import model.UserData;
import server.ServerFacade;

import java.util.Scanner;

public class ChessClient {
  private final ServerFacade serverFacade;
  private boolean loggedIn = false;
  private Scanner scanner;

  public ChessClient(String serverUrl) {
    this.serverFacade = new ServerFacade(serverUrl);
    this.scanner = new Scanner(System.in);
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
    System.out.println("\nRemi's Chess app");
    System.out.println("\nEnter a command to continue:");
    System.out.println("help, register, login, quit");

    String command = scanner.nextLine().trim().toLowerCase();
    switch (command) {
      case "help":
        displayHelp(false);
        break;
      case "register":
        register();
        break;
      case "login":
        login();
        break;
      case "quit":
        quit();
        break;
      default:
        System.out.println("Invalid command. Type 'help' for options.");
    }
  }

  private void postloginMenu() {
    System.out.println("\nEnter a command to continue:");
    System.out.println("help, logout, create game, list games, play game, observe game, quit");

    String command = scanner.nextLine().trim().toLowerCase();
    switch (command) {
      case "help":
        displayHelp(true);
        break;
      case "logout":
        logout();
        break;
      case "create game":
        createGame();
        break;
      case "list games":
        listGames();
        break;
      case "play game":
        playGame();
        break;
      case "observe game":
        observeGame();
        break;
      case "quit":
        quit();
        break;
      default:
        System.out.println("Invalid command. Type 'help' for options.");
    }
  }
  private void displayHelp(boolean loggedIn) {
    if (loggedIn) {
      System.out.println("Available commands: help, logout, create game, list games, play game, observe game, quit");
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

    UserData newUser = new UserData(username, password, email);

    try {
      serverFacade.createUser(newUser);
      loggedIn = true;
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
      serverFacade.login(username, password);
      loggedIn = true;
      System.out.println("Login successful!");
    } catch (DataAccessException e) {
      System.out.println("Login failed: " + e.getMessage());
    }
  }

  private void quit() {
    System.out.println("Goodbye!");
    System.exit(0);
  }
  private void logout() {
    try {
      serverFacade.logout();
      loggedIn = false;
      System.out.println("Logged out successfully.");
    } catch (DataAccessException e) {
      System.out.println("Logout failed: " + e.getMessage());
    }
  }

  private void createGame() {
    System.out.print("Enter a name for the new game: ");
    String gameName = scanner.nextLine();

    try {
      serverFacade.createGame(gameName);
      System.out.println("Game created successfully.");
    } catch (DataAccessException e) {
      System.out.println("Failed to create game: " + e.getMessage());
    }
  }

  private void listGames() {
    try {
      var games = serverFacade.listGames();
      System.out.println("Available games:");
      int index = 1;
      for (var game : games) {
        System.out.printf("%d. %s - Players: %s%n", index++, game.getName(), game.getPlayers());
      }
    } catch (DataAccessException e) {
      System.out.println("Failed to list games: " + e.getMessage());
    }
  }

  private void playGame() {
    System.out.print("Enter the number of the game you want to join: ");
    int gameNumber = Integer.parseInt(scanner.nextLine());
    System.out.print("Choose color (white/black): ");
    String color = scanner.nextLine();

    try {
      serverFacade.joinGame(gameNumber, color);
      System.out.println("Joined game successfully.");
      drawBoard(color.equals("white"));
    } catch (DataAccessException e) {
      System.out.println("Failed to join game: " + e.getMessage());
    }
  }

  private void observeGame() {
    System.out.print("Enter the number of the game you want to observe: ");
    int gameNumber = Integer.parseInt(scanner.nextLine());

    try {
      serverFacade.observeGame(gameNumber);
      System.out.println("Observing game:");
      drawBoard(true);  // Draw initial board from white's perspective.
      drawBoard(false); // Draw initial board from black's perspective.
    } catch (DataAccessException e) {
      System.out.println("Failed to observe game: " + e.getMessage());
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
        System.out.print((col % 2 == row % 2) ? piece : ' '); // Simple pattern for light/dark squares.
        System.out.print(' ');
      }
      System.out.println();
    }
  }


}
