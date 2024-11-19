import chess.*;
import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        ChessClient chessClient = new ChessClient(serverUrl);
        chessClient.start();
    }
}