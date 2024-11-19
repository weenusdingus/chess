import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Scanner;
import server.ServerFacade;
import ui.ChessBoard;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.*;


public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    private static String authToken = null;
    private static final Gson gson = new Gson();
    private static final Map<Integer, Integer> displayNumberToGameId = new HashMap<>();
    private static final int currentDisplayNumber = 1;
    private static final Map<String, Integer> gameNameToId = new HashMap<>();



    public static void main(String[] args) {
        System.out.println("♕ Welcome to 240 Chess! Type 'help' to get started.♕");
        preLoginUI();
    }

    private static String extractAuthToken(String jsonString) {
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        return jsonObject.get("authToken").getAsString();
    }

    private static void preLoginUI() {
        while (authToken == null) {
            System.out.print("[LOGGED_OUT] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help" -> displayPreloginHelp();
                case "register" -> register();
                case "login" -> login();
                case "quit" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
        postLoginUI();
    }

    private static void postLoginUI() {
        while (authToken != null) {
            System.out.print("[LOGGED_IN] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help" -> displayPostloginHelp();
                case "logout" -> logout();
                case "create" -> createGame();
                case "list" -> listGames();
                case "join" -> joinGame();
                case "observe" -> displayChessBoard();
                case "quit" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
        preLoginUI();
    }

    private static void displayPreloginHelp() {
        System.out.println("Available commands:");
        System.out.println("  register <username> <password> <email> - Register a new account");
        System.out.println("  login <username> <password> - Login to your account");
        System.out.println("  quit - Exit the application");
    }

    private static void displayPostloginHelp() {
        System.out.println("Available commands:");
        System.out.println("  create <gameName> - Create a new game");
        System.out.println("  list - List all games");
        System.out.println("  join - Join or observe a game");
        System.out.println("  logout - Logout of your account");
        System.out.println("  quit - Exit the application");
    }

    private static void login() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Username and password cannot be empty.");
                return;
            }

            String response = serverFacade.login(username, password);
            authToken = extractAuthToken(response);
            System.out.println("Welcome back, " + username + "!");
        } catch (IOException e) {
            System.out.println("Invalid username or password.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }


    private static void register() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }

            String response = serverFacade.register(username, password, email);
            authToken = extractAuthToken(response);
            System.out.println("Welcome, " + username + "!");
        } catch (IOException e) {
            System.out.println("Unable to register. Username may already be taken.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void logout() {
        try {
            serverFacade.logout(authToken);
            System.out.println("Goodbye!");
            authToken = null;
        } catch (IOException e) {
            System.out.println("Unable to logout. Please try again.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void createGame() {
        try {
            System.out.print("Enter game name: ");
            String gameName = scanner.nextLine().trim();

            if (gameName.isEmpty()) {
                System.out.println("Game name cannot be empty.");
                return;
            }

            // Check existing games for duplicate names
            String gamesJson = serverFacade.listGames(authToken);
            JsonObject gamesObject = gson.fromJson(gamesJson, JsonObject.class);
            JsonArray games = gamesObject.getAsJsonArray("games");

            // Check for duplicate game names
            for (JsonElement gameElement : games) {
                JsonObject game = gameElement.getAsJsonObject();
                if (game.get("gameName").getAsString().equals(gameName)) {
                    System.out.println("A game with this name already exists. Please choose a different name.");
                    return;
                }
            }

            // If no duplicates found, create the game
            serverFacade.createGame(authToken, gameName);
            System.out.println("Game '" + gameName + "' created successfully.");
        } catch (IOException e) {
            System.out.println("Unable to create game. Please try again.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void listGames() {
        try {
            String gamesJson = serverFacade.listGames(authToken);
            JsonObject gamesObject = gson.fromJson(gamesJson, JsonObject.class);
            JsonArray games = gamesObject.getAsJsonArray("games");

            if (games.size() == 0) {
                System.out.println("No games available.");
                return;
            }

            // Clear previous mapping
            gameNameToId.clear();

            System.out.println("\nAvailable Games:");
            System.out.println("----------------");
            for (JsonElement gameElement : games) {
                JsonObject game = gameElement.getAsJsonObject();
                String gameName = game.get("gameName").getAsString();
                String whitePlayer = game.has("whiteUsername") ? game.get("whiteUsername").getAsString() : "<EMPTY>";
                String blackPlayer = game.has("blackUsername") ? game.get("blackUsername").getAsString() : "<EMPTY>";
                int gameId = game.get("gameID").getAsInt();

                // Store the mapping
                gameNameToId.put(gameName, gameId);

                System.out.printf("Game: %s\n", gameName);
                System.out.printf("  White Player: %s\n", whitePlayer);
                System.out.printf("  Black Player: %s\n", blackPlayer);
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Unable to retrieve games. Please try again.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void joinGame() {
        try {
            // First list the games so user can see available games
            listGames();

            System.out.print("Enter game name: ");
            String gameName = scanner.nextLine().trim();
            System.out.print("Enter color (WHITE/BLACK/empty to observe): ");
            String color = scanner.nextLine().trim().toUpperCase();

            // Input validation
            if (gameName.isEmpty()) {
                System.out.println("Game name cannot be empty.");
                return;
            }

            // Get the game ID from our mapping
            Integer gameId = gameNameToId.get(gameName);
            if (gameId == null) {
                System.out.println("Game not found. Please enter an existing game name from the list.");
                return;
            }

            if (!color.isEmpty() && !color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. Please enter WHITE, BLACK, or leave empty to observe.");
                return;
            }

            serverFacade.joinGame(authToken, gameId, color);

            if (color.isEmpty()) {
                System.out.printf("Observing game '%s'.\n", gameName);
                displayChessBoard(null); // Pass null to indicate observer
            } else {
                System.out.printf("Joined game '%s' as %s.\n", gameName, color);
                displayChessBoard(color); // Pass the color to show proper perspective first
            }

        } catch (IOException e) {
            System.out.println("Unable to join game. Please verify the game name and try again.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void displayChessBoard(String playerColor) {
        try {
            Scanner userInput = new Scanner(System.in);
            ChessBoard board = new ChessBoard();

            // If player is playing (not observing)
            if (playerColor != null) {
                System.out.println("\nPress Enter to see the board from your perspective (" + playerColor + ")...");
                userInput.nextLine();

                // Show player's perspective first
                if (playerColor.equals("BLACK")) {
                    board.displayBlackPerspective();
                } else {
                    board.displayWhitePerspective();
                }

                // Then show the opposite perspective
                System.out.println("\nPress Enter to see the opposite perspective...");
                userInput.nextLine();

                if (playerColor.equals("BLACK")) {
                    board.displayWhitePerspective();
                } else {
                    board.displayBlackPerspective();
                }
            }
            // If player is observing
            else {
                System.out.println("\nPress Enter to see Black's perspective...");
                userInput.nextLine();
                board.displayBlackPerspective();

                System.out.println("\nPress Enter to see White's perspective...");
                userInput.nextLine();
                board.displayWhitePerspective();
            }

        } catch (Exception e) {
            System.out.println("Error displaying chess board. Press Enter to continue.");
        }
    }

    private static void displayChessBoard() {
        try {
            Scanner userInput = new Scanner(System.in);
            ChessBoard board = new ChessBoard();

            System.out.println("\nPress Enter to see the board...");
            userInput.nextLine();

            // Show Black's perspective first
            board.displayBlackPerspective();

            System.out.println("\nPress Enter to see White's perspective...");
            userInput.nextLine();

            // Show White's perspective
            board.displayWhitePerspective();

        } catch (Exception e) {
            System.out.println("Error displaying chess board. Press Enter to continue.");
        }
    }
}