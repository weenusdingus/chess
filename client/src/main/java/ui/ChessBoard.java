package ui;

public class ChessBoard {
  private final String[][] board;

  public ChessBoard() {
    board = new String[][]{
            {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP,
                    EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP,
                    EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK},
            {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN,
                    EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN,
                    EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN},
            {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY},
            {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY},
            {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY},
            {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY},
            {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN,
                    EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN,
                    EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN},
            {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP,
                    EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP,
                    EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK}
    };
  }

  public void displayBlackPerspective() {
    System.out.println("\nBlack's perspective:");
    printBoard(false);
  }

  public void displayWhitePerspective() {
    System.out.println("\nWhite's perspective:");
    printBoard(true);
  }

  private void printBoard(boolean whitePerspective) {
    String[][] displayBoard = copyBoard();
    if (!whitePerspective) {
      reverseBoard(displayBoard);
    }

    for (int i = 0; i < displayBoard.length; i++) {
      for (int j = 0; j < displayBoard[i].length; j++) {
        if ((i + j) % 2 == 0) {
          System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + displayBoard[i][j] + EscapeSequences.RESET_BG_COLOR);
        } else {
          System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + displayBoard[i][j] + EscapeSequences.RESET_BG_COLOR);
        }
      }
      System.out.println();
    }
  }

  private String[][] copyBoard() {
    String[][] copy = new String[board.length][board[0].length];
    for (int i = 0; i < board.length; i++) {
      System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
    }
    return copy;
  }

  private void reverseBoard(String[][] board) {
    // Reverse rows
    for (int i = 0; i < board.length / 2; i++) {
      String[] temp = board[i];
      board[i] = board[board.length - 1 - i];
      board[board.length - 1 - i] = temp;
    }
    // Reverse columns
    for (String[] row : board) {
      for (int j = 0; j < row.length / 2; j++) {
        String temp = row[j];
        row[j] = row[row.length - 1 - j];
        row[row.length - 1 - j] = temp;
      }
    }
  }
}

