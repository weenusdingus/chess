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
    printBoard(true);
  }

  public void displayWhitePerspective() {
    printBoard(false);
  }

  private void printBoard(boolean blackPerspective) {
    String[][] displayBoard = blackPerspective ? reversePieces(copyBoard()) : copyBoard();
    String columnLabels = blackPerspective ? "HGFEDCBA" : "ABCDEFGH";

    System.out.print("    ");
    for (char col : columnLabels.toCharArray()) {
      System.out.print(" " + col + " ");
    }
    System.out.println();

    for (int i = 0; i < displayBoard.length; i++) {
      int rowLabel = blackPerspective ? i + 1 : 8 - i;
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

    System.out.print("   ");
    for (char col : columnLabels.toCharArray()) {
      System.out.print(" " + col + " ");
    }
    System.out.println();
  }

  private String[][] copyBoard() {
    String[][] copy = new String[board.length][board[0].length];
    for (int i = 0; i < board.length; i++) {
      System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
    }
    return copy;
  }

  private String[][] reversePieces(String[][] board) {
    String[][] reversed = new String[board.length][];
    for (int i = 0; i < board.length; i++) {
      reversed[i] = board[board.length - 1 - i];
    }
    return reversed;
  }
}


