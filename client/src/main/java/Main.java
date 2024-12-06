import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.HttpRetryException;
import java.util.Scanner;
import server.ServerFacade;
import ui.ChessBoard;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.*;

import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;


public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final ServerFacade SERVER_FACADE= new ServerFacade("http://localhost:8080");
    private static String authToken = null;
    private static final Gson GSON = new Gson();
    private static final Map<String, Integer> NAME_TO_ID= new HashMap<>();
    private static WebSocketFacade websocket;
    private ChessBoard chessboard = new ChessBoard();
    private ChessGame.TeamColor perspective;
    private static ChessGame game;

    public static void main(String[] args) {
        System.out.println("♕ Welcome to 240 Chess! Type 'help' to get started ♕");
        preLoginUI();
    }

    private static String extractAuthToken(String jsonString) {
        JsonObject jsonObject = GSON.fromJson(jsonString, JsonObject.class);
        return jsonObject.get("authToken").getAsString();
    }

    private static void preLoginUI() {
        while (authToken == null) {
            System.out.print("[LOGGED_OUT] >>> ");
            String command = SCANNER.nextLine().trim().toLowerCase();

            switch (command) {
                case "help" -> displayPreLoginHelp();
                case "register" -> register();
                case "login" -> login();
                case "quit" -> {
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
            String command = SCANNER.nextLine().trim().toLowerCase();

            switch (command) {
                case "help" -> displayPostLoginHelp();
                case "logout" -> logout();
                case "create" -> createGame();
                case "list" -> listGames();
                case "join" -> joinGame();
                case "observe" -> observeGame();
                case "quit" -> {
                    return;
                }
                default -> System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
        preLoginUI();
    }

    private static void gameplayLoop(int gameID, String playerColor) throws HttpRetryException {
        System.out.println("Entering gameplay... Type 'help' for gameplay commands.");
        boolean isPlaying = true;

        while (isPlaying) {
            System.out.print("[GAMEPLAY] >>> ");
            String command = SCANNER.nextLine().trim().toLowerCase();

            switch (command) {
                case "help" -> displayGameplayHelp();
                case "move" -> makeMove(gameID);
                case "resign" -> resignGame(gameID);
                case "board" -> displayChessBoard(playerColor);
                case "highlight" -> highlightLegalMoves();
                case "redraw" -> redrawChessBoard();
                case "quit" -> {
                    System.out.println("Leaving the game...");
                    leaveGame(gameID);
                    isPlaying = false;
                }
                default -> System.out.println("Unknown command. Type 'help' for gameplay commands.");
            }
        }
    }

    private static void highlightLegalMoves() {

    }

    private static void makeMove(int gameID) throws HttpRetryException {
        System.out.print("Enter start position: ");
        String start = SCANNER.next().toLowerCase();
        SCANNER.nextLine();
        System.out.print("Enter end position: ");
        String end = SCANNER.next().toLowerCase();
        SCANNER.nextLine();

        // Convert input to row and column
        int startRow = Character.getNumericValue(start.charAt(1));
        int startCol = 1 + (start.charAt(0) - 'a');
        int endRow = Character.getNumericValue(end.charAt(1));
        int endCol = 1 + (end.charAt(0) - 'a');

        // Create ChessPosition for start and end
        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ChessPosition endPosition = new ChessPosition(endRow, endCol);

        ChessPiece.PieceType type = null;
        if (startRow >= 1 && startRow <= 8 && startCol >= 1 && startCol <= 8
                && game.getBoard().getPiece(startPosition).getPieceType() == ChessPiece.PieceType.PAWN
                && (endRow == 1 || endRow == 8)) {
            System.out.print("Enter promotion piece type: ");
            String typeString = SCANNER.next().toLowerCase();
            SCANNER.nextLine();
            switch (typeString) {
                case "queen" -> type = ChessPiece.PieceType.QUEEN;
                case "rook" -> type = ChessPiece.PieceType.ROOK;
                case "bishop" -> type = ChessPiece.PieceType.BISHOP;
                case "knight" -> type = ChessPiece.PieceType.KNIGHT;
            }
        }

        // Create ChessMove object
        ChessMove move = new ChessMove(startPosition, endPosition, type);
        System.out.println("Sending Move: " + move);  // Log the move


        // Ensure move is valid before sending
        if (move != null) {
            websocket.makeMove(authToken, gameID, move);
        } else {
            System.out.println("Invalid move: move is null");
        }
    }

    private static void resignGame(int gameID) {
        try {
            websocket.resignGame(authToken, gameID);
            System.out.println("You have resigned the game.");
        } catch (HttpRetryException e) {
            System.out.println("Failed to resign: " + e.getMessage());
        }
    }

    private static void leaveGame(int gameID) {
        try {
            websocket.leaveGame(authToken, gameID);
            System.out.println("You left the game.");
        } catch (HttpRetryException e) {
            System.out.println("Failed to leave game: " + e.getMessage());
        }
    }

    private static void displayGameplayHelp() {
        System.out.println("  move - make a move on the board");
        System.out.println("  resign - resign from the game");
        System.out.println("  board - view the current chessboard");
        System.out.println("  quit - leave the game");
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
        System.out.println("  observe - a game");
        System.out.println("  logout - when you are done");
        System.out.println("  quit - playing chess");
        System.out.println("help - with possible commands");
    }

    private static void login() {
        try {
            System.out.print("Enter username: ");
            String username = SCANNER.nextLine().trim();
            System.out.print("Enter password: ");
            String password = SCANNER.nextLine().trim();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Invalid entry");
                return;
            }

            String response = SERVER_FACADE.login(username, password);
            authToken = extractAuthToken(response);
            System.out.println("Welcome " + username + "!");
        } catch (IOException e) {
            System.out.println("Invalid username or password.");
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }
    }


    private static void register() {
        try {
            System.out.print("Enter username: ");
            String username = SCANNER.nextLine().trim();
            System.out.print("Enter password: ");
            String password = SCANNER.nextLine().trim();
            System.out.print("Enter email: ");
            String email = SCANNER.nextLine().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                System.out.println("Invalid entry");
                return;
            }

            String response = SERVER_FACADE.register(username, password, email);
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
            SERVER_FACADE.logout(authToken);
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
            String gameName = SCANNER.nextLine().trim();

            if (gameName.isEmpty()) {
                System.out.println("Invalid Entry");
                return;
            }

            String gamesJson = SERVER_FACADE.listGames(authToken);
            JsonObject gamesObject = GSON.fromJson(gamesJson, JsonObject.class);
            JsonArray games = gamesObject.getAsJsonArray("games");

            for (JsonElement gameElement : games) {
                JsonObject game = gameElement.getAsJsonObject();
                if (game.get("gameName").getAsString().equals(gameName)) {
                    System.out.println("A game with this name already exists. Please choose a different name.");
                    return;
                }
            }

            SERVER_FACADE.createGame(authToken, gameName);
            System.out.println("Game '" + gameName + "' created successfully.");
        } catch (IOException e) {
            System.out.println("Unable to create game. Please try again.");
        } catch (Exception e) {
            System.out.println("An error occurred. Please try again.");
        }
    }

    private static void listGames() {
        try {
            String gamesJson = SERVER_FACADE.listGames(authToken);
            JsonObject gamesObject = GSON.fromJson(gamesJson, JsonObject.class);
            JsonArray games = gamesObject.getAsJsonArray("games");

            if (games.isEmpty()) {
                System.out.println("No games available.");
                return;
            }

            NAME_TO_ID.clear();

            System.out.println("\nGames:");
            System.out.println("----------------");

            int index = 1;
            for (JsonElement gameElement : games) {
                JsonObject game = gameElement.getAsJsonObject();
                String gameName = game.get("gameName").getAsString();
                String whitePlayer = game.has("whiteUsername") ? game.get("whiteUsername").getAsString() : "<EMPTY>";
                String blackPlayer = game.has("blackUsername") ? game.get("blackUsername").getAsString() : "<EMPTY>";
                int gameId = game.get("gameID").getAsInt();

                NAME_TO_ID.put(String.valueOf(index), gameId);

                System.out.printf("%d. %s\n", index, gameName);
                System.out.printf("  White Player: %s\n", whitePlayer);
                System.out.printf("  Black Player: %s\n", blackPlayer);
                System.out.println();
                index++;
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

            System.out.print("Enter game number: ");
            String gameNumber = SCANNER.nextLine().trim();
            System.out.print("Valid entries: 'white', 'black'\n");
            System.out.print("Enter color: ");
            String color = SCANNER.nextLine().trim().toUpperCase();

            if (gameNumber.isEmpty()) {
                System.out.println("Invalid entry");
                return;
            }

            Integer gameId = NAME_TO_ID.get(gameNumber);
            if (gameId == null) {
                System.out.println("Game not found");
                return;
            }

            if (color.isEmpty() || (!color.equals("WHITE") && !color.equals("BLACK"))) {
                System.out.println("Invalid entry");
                return;
            }

            SERVER_FACADE.joinGame(authToken, gameId, color);
            System.out.printf("Joined game #%s as %s.\n", gameNumber, color);
            displayChessBoard(color);

        } catch (IOException e) {
            System.out.println("Unable to join game.");
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }
    private static void observeGame() {
        try {
            listGames();

            System.out.print("Enter game number: ");
            String gameNumber = SCANNER.nextLine().trim();

            if (gameNumber.isEmpty()) {
                System.out.println("Invalid entry");
                return;
            }

            Integer gameId = NAME_TO_ID.get(gameNumber);
            if (gameId == null) {
                System.out.println("Game not found");
                return;
            }

            SERVER_FACADE.joinGame(authToken, gameId, null);
            System.out.printf("Observing game #%s.\n", gameNumber);
            displayChessBoard(null);

        } catch (IOException e) {
            System.out.println("Unable to observe game.");
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    private static void displayChessBoard(String playerColor) {
        try {
            ChessBoard board = new ChessBoard();

            if (playerColor != null) {
                if (playerColor.equals("BLACK")) {
                    board.displayBlackPerspective();
                } else {
                    board.displayWhitePerspective();
                }

            }
            //default white for now if observing
            else {
                board.displayWhitePerspective();
            }

        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    private static void redrawChessBoard() {
        try {
            Scanner userInput = new Scanner(System.in);
            ChessBoard board = new ChessBoard();

            board.displayBlackPerspective();

            //IMPLEMENT REDRAWING CORRECT PERSPECTIVE

        } catch (Exception e) {
            System.out.println("Error");
        }
    }
}