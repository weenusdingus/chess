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
        System.out.println("♕ Welcome to 240 Chess! Type 'help' to get started ♕");
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
                case "help" -> displayPreLoginHelp();
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
                case "help" -> displayPostLoginHelp();
                case "logout" -> logout();
                case "create" -> createGame();
                case "list" -> listGames();
                case "join" -> joinGame();
                case "redraw" -> redrawChessBoard();
                case "quit" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
        preLoginUI();
    }

    private static void displayPreLoginHelp() {
        System.out.println("register <username> <password> <email> - to create an account");
        System.out.println("login <username> <password> - to play chess");
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }

    private static void displayPostLoginHelp() {
        System.out.println("  create <name> - a game");
        System.out.println("  list - games");
        System.out.println("  join - join a game");
        System.out.println("  logout - when you are done");
        System.out.println("  quit - playing chess");
        System.out.println("help - with possible commands");
    }

    private static void login() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Invalid entry");
                return;
            }

            String response = serverFacade.login(username, password);
            authToken = extractAuthToken(response);
            System.out.println("Welcome back, " + username + "!");
        } catch (IOException e) {
            System.out.println("Invalid username or password.");
        } catch (Exception e) {
            System.out.println("Something went wrong.");
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
                System.out.println("Invalid entry");
                return;
            }

            String response = serverFacade.register(username, password, email);
            authToken = extractAuthToken(response);
            System.out.println("Welcome back, " + username + "!");
        } catch (IOException e) {
            System.out.println("Try a different username");
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    private static void logout() {
        try {
            serverFacade.logout(authToken);
            authToken = null;
        } catch (IOException e) {
            System.out.println("Something went wrong");
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    private static void createGame() {
        try {
            System.out.print("Enter game name: ");
            String gameName = scanner.nextLine().trim();

            if (gameName.isEmpty()) {
                System.out.println("Invalid Entry");
                return;
            }

            String gamesJson = serverFacade.listGames(authToken);
            JsonObject gamesObject = gson.fromJson(gamesJson, JsonObject.class);
            JsonArray games = gamesObject.getAsJsonArray("games");

            for (JsonElement gameElement : games) {
                JsonObject game = gameElement.getAsJsonObject();
                if (game.get("gameName").getAsString().equals(gameName)) {
                    System.out.println("A game with this name already exists. Please choose a different name.");
                    return;
                }
            }

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

            if (games.isEmpty()) {
                System.out.println("No games available.");
                return;
            }

            gameNameToId.clear();

            System.out.println("\nGames:");
            System.out.println("----------------");
            for (JsonElement gameElement : games) {
                JsonObject game = gameElement.getAsJsonObject();
                String gameName = game.get("gameName").getAsString();
                String whitePlayer = game.has("whiteUsername") ? game.get("whiteUsername").getAsString() : "<EMPTY>";
                String blackPlayer = game.has("blackUsername") ? game.get("blackUsername").getAsString() : "<EMPTY>";
                int gameId = game.get("gameID").getAsInt();

                gameNameToId.put(gameName, gameId);

                System.out.printf("Game: %s\n", gameName);
                System.out.printf("  White Player: %s\n", whitePlayer);
                System.out.printf("  Black Player: %s\n", blackPlayer);
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong");
        } catch (Exception e) {
            System.out.println("An error occurred");
        }
    }

    private static void joinGame() {
        try {
            listGames();

            System.out.print("Enter game name: ");
            String gameName = scanner.nextLine().trim();
            System.out.print("Valid entries: 'white', 'black', enter (to observe)");
            System.out.print("Enter color: ");
            String color = scanner.nextLine().trim().toUpperCase();

            if (gameName.isEmpty()) {
                System.out.println("Invalid entry");
                return;
            }

            Integer gameId = gameNameToId.get(gameName);
            if (gameId == null) {
                System.out.println("Game not found");
                return;
            }

            if (!color.isEmpty() && !color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid entry");
                return;
            }

            serverFacade.joinGame(authToken, gameId, color);

            if (color.isEmpty()) {
                System.out.printf("Observing game '%s'.\n", gameName);
                displayChessBoard(null);
            } else {
                System.out.printf("Joined game '%s' as %s.\n", gameName, color);
                displayChessBoard(color);
            }

        } catch (IOException e) {
            System.out.println("Unable to join game.");
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    private static void displayChessBoard(String playerColor) {
        try {
            Scanner userInput = new Scanner(System.in);
            ChessBoard board = new ChessBoard();

            if (playerColor != null) {
                System.out.println("\nPress Enter to see the board (" + playerColor + ")...");
                userInput.nextLine();

                if (playerColor.equals("BLACK")) {
                    board.displayBlackPerspective();
                } else {
                    board.displayWhitePerspective();
                }

            }
            else {
                System.out.println("\nPress Enter to see the board");
                userInput.nextLine();
                board.displayBlackPerspective();

                System.out.println("\nPress Enter to change perspectives");
                userInput.nextLine();
                board.displayWhitePerspective();
            }

        } catch (Exception e) {
            System.out.println("Error displaying chess board");
        }
    }

    private static void redrawChessBoard() {
        try {
            Scanner userInput = new Scanner(System.in);
            ChessBoard board = new ChessBoard();

            board.displayBlackPerspective();

            System.out.println("\nPress Enter to see White's perspective...");
            userInput.nextLine();

            board.displayWhitePerspective();

        } catch (Exception e) {
            System.out.println("Error displaying chess board. Press Enter to continue.");
        }
    }
}