package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoard {
  private final String[][] board;

  public ChessBoard() {
    board = new String[8][8]; // Empty board for initialization
    resetBoard();
  }

  // Reset board to the default setup
  public void resetBoard() {
    board[0] = new String[]{EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP,
            EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP,
            EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK};
    board[1] = new String[]{EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN,
            EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN,
            EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN};
    for (int i = 2; i < 6; i++) {
      for (int j = 0; j < 8; j++) {
        board[i][j] = EscapeSequences.EMPTY;
      }
    }
    board[6] = new String[]{EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN,
            EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN,
            EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN};
    board[7] = new String[]{EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP,
            EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP,
            EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK};
  }

  // Update the board based on the current chess.ChessBoard state
  public void updateBoard(chess.ChessBoard chessBoard) {
    for (int row = 1; row <= 8; row++) {
      for (int col = 1; col <= 8; col++) {
        ChessPiece piece = chessBoard.getPiece(new ChessPosition(row, col));
        if (piece == null) {
          board[8 - row][col - 1] = EscapeSequences.EMPTY;
        } else {
          // Map ChessPiece to its corresponding escape sequence
          board[8 - row][col - 1] = getPieceSymbol(piece);
        }
      }
    }
  }

  // Helper method to map ChessPiece to escape sequences
  private String getPieceSymbol(ChessPiece piece) {
    switch (piece.getPieceType()) {
      case KING:
        return piece.getTeamColor() == ChessGame.TeamColor.BLACK
                ? EscapeSequences.BLACK_KING : EscapeSequences.WHITE_KING;
      case QUEEN:
        return piece.getTeamColor() == ChessGame.TeamColor.BLACK
                ? EscapeSequences.BLACK_QUEEN : EscapeSequences.WHITE_QUEEN;
      case BISHOP:
        return piece.getTeamColor() == ChessGame.TeamColor.BLACK
                ? EscapeSequences.BLACK_BISHOP : EscapeSequences.WHITE_BISHOP;
      case KNIGHT:
        return piece.getTeamColor() == ChessGame.TeamColor.BLACK
                ? EscapeSequences.BLACK_KNIGHT : EscapeSequences.WHITE_KNIGHT;
      case ROOK:
        return piece.getTeamColor() == ChessGame.TeamColor.BLACK
                ? EscapeSequences.BLACK_ROOK : EscapeSequences.WHITE_ROOK;
      case PAWN:
        return piece.getTeamColor() == ChessGame.TeamColor.BLACK
                ? EscapeSequences.BLACK_PAWN : EscapeSequences.WHITE_PAWN;
      default:
        return EscapeSequences.EMPTY;
    }
  }

  // Display board from black perspective
  public void displayBlackPerspective() {
    printBoard(true); // True will indicate Black's perspective
  }

  // Display board from white perspective
  public void displayWhitePerspective() {
    printBoard(false); // False will indicate White's perspective
  }

  private void printBoard(boolean blackPerspective) {
    String[][] displayBoard = blackPerspective ? reversePieces(copyBoard()) : copyBoard(); // Reverse rows for black's perspective
    String columnLabels = blackPerspective ? "  H   G   F   E   D   C   B   A" : "  A   B   C   D   E   F   G   H";

    System.out.print(columnLabels);
    System.out.println();

    for (int i = 0; i < displayBoard.length; i++) {
      int rowLabel = blackPerspective ? 8 - i : i + 1; // Reverse row label for black
      System.out.print(rowLabel + " ");

      for (int j = 0; j < displayBoard[i].length; j++) {
        if ((i + j) % 2 == 0) {
          System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREEN + displayBoard[i][j] + EscapeSequences.RESET_BG_COLOR);
        } else {
          System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + displayBoard[i][j] + EscapeSequences.RESET_BG_COLOR);
        }
      }

      System.out.print(" " + rowLabel);
      System.out.println();
    }

    System.out.print(columnLabels);
    System.out.println();
  }

  private String[][] copyBoard() {
    String[][] copy = new String[board.length][board[0].length];
    for (int i = 0; i < board.length; i++) {
      System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
    }
    return copy;
  }

  // Reverse the rows for black's perspective, keeping the columns intact
  private String[][] reversePieces(String[][] board) {
    String[][] reversed = new String[board.length][board[0].length];
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        // Flip both rows and columns
        reversed[i][j] = board[board.length - 1 - i][board[i].length - 1 - j];
      }
    }
    return reversed;
  }
}




