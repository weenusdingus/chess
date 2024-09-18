package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessGame.TeamColor pieceColor;
    public ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private boolean addMoveOrCapture(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition, ChessPosition checkPosition) {
        if (board.getPiece(checkPosition) != null) {
            if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                ChessMove captureMove = new ChessMove(myPosition, checkPosition, null);
                validMoves.add(captureMove);
            }
            return false;
        }

        ChessMove validMove = new ChessMove(myPosition, checkPosition, null);
        validMoves.add(validMove);
        return true;
    }
    private Collection<ChessMove> getBishopRookQueen(ChessBoard board, ChessPosition startPosition, int rowIncrement, int colIncrement) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int myrow = startPosition.getRow();
        int mycol = startPosition.getColumn();

        while (true) {
            myrow += rowIncrement;
            mycol += colIncrement;

            if (myrow < 1 || myrow > 8 || mycol < 1 || mycol > 8) {
                break;
            }

            ChessPosition checkPosition = new ChessPosition(myrow, mycol);

            if (!addMoveOrCapture(validMoves, board, startPosition, checkPosition)) {
                break;
            }
        }

        return validMoves;
    }
    private Collection<ChessMove> getKnightKing(ChessBoard board, ChessPosition startPosition, int rowIncrement, int colIncrement) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int myrow = startPosition.getRow();
        int mycol = startPosition.getColumn();


        myrow += rowIncrement;
        mycol += colIncrement;

        ChessPosition checkPosition = new ChessPosition(myrow, mycol);

        if (!(myrow < 1 || myrow > 8 || mycol < 1 || mycol > 8)) {
            addMoveOrCapture(validMoves, board, startPosition, checkPosition);
        }
        return validMoves;
    }
    private void addPawnMove(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition, int newRow, int newCol) {
        ChessPosition checkPosition = new ChessPosition(newRow, newCol);
        if (board.getPiece(checkPosition) == null) {
            ChessMove validMove = new ChessMove(myPosition, checkPosition, null);
            validMoves.add(validMove);
        }
    }

    private void addPawnCapture(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition, int newRow, int newCol, boolean isPromotion) {
        ChessPosition checkPosition = new ChessPosition(newRow, newCol);
        if (board.getPiece(checkPosition) != null && board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
            if (isPromotion) {
                addPawnPromotionMoves(validMoves, myPosition, checkPosition);
            } else {
                ChessMove captureMove = new ChessMove(myPosition, checkPosition, null);
                validMoves.add(captureMove);
            }
        }
    }
    private void addPawnDoubleMove(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();

        if (this.pieceColor == ChessGame.TeamColor.WHITE){
            ChessPosition firstStep = new ChessPosition(myRow + 1, myCol);
            ChessPosition secondStep = new ChessPosition(myRow + 2, myCol);

            if (board.getPiece(firstStep) == null && board.getPiece(secondStep) == null) {
                ChessMove doubleMove = new ChessMove(myPosition, secondStep, null);
                validMoves.add(doubleMove);
            }
        }
        if (this.pieceColor == ChessGame.TeamColor.BLACK){
            ChessPosition firstStep = new ChessPosition(myRow - 1, myCol);
            ChessPosition secondStep = new ChessPosition(myRow - 2, myCol);

            if (board.getPiece(firstStep) == null && board.getPiece(secondStep) == null) {
                ChessMove doubleMove = new ChessMove(myPosition, secondStep, null);
                validMoves.add(doubleMove);
            }
        }
    }

    private void addPawnPromotionMoves(Collection<ChessMove> validMoves, ChessPosition myPosition, ChessPosition promotionPosition) {
        validMoves.add(new ChessMove(myPosition, promotionPosition, PieceType.QUEEN));
        validMoves.add(new ChessMove(myPosition, promotionPosition, PieceType.ROOK));
        validMoves.add(new ChessMove(myPosition, promotionPosition, PieceType.BISHOP));
        validMoves.add(new ChessMove(myPosition, promotionPosition, PieceType.KNIGHT));
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();

        if (this.getPieceType() == PieceType.BISHOP||this.getPieceType() == PieceType.QUEEN) {
            validMoves.addAll(getBishopRookQueen(board, myPosition, 1, 1));   // Top-right
            validMoves.addAll(getBishopRookQueen(board, myPosition, 1, -1));  // Top-left
            validMoves.addAll(getBishopRookQueen(board, myPosition, -1, 1));  // Bottom-right
            validMoves.addAll(getBishopRookQueen(board, myPosition, -1, -1)); // Bottom-left
        }
        if (this.getPieceType() == PieceType.ROOK||this.getPieceType() == PieceType.QUEEN){
            validMoves.addAll(getBishopRookQueen(board, myPosition, 1, 0));   //Up
            validMoves.addAll(getBishopRookQueen(board, myPosition, 0, 1));   //Right
            validMoves.addAll(getBishopRookQueen(board, myPosition, -1, 0));  //Down
            validMoves.addAll(getBishopRookQueen(board, myPosition, 0, -1));  //Left
        }
        if (this.getPieceType() == PieceType.KNIGHT){
            validMoves.addAll(getKnightKing(board, myPosition, 2, 1));
            validMoves.addAll(getKnightKing(board, myPosition, 2, -1));
            validMoves.addAll(getKnightKing(board, myPosition, -2, 1));
            validMoves.addAll(getKnightKing(board, myPosition, -2, -1));
            validMoves.addAll(getKnightKing(board, myPosition, 1, 2));
            validMoves.addAll(getKnightKing(board, myPosition, 1, -2));
            validMoves.addAll(getKnightKing(board, myPosition, -1, 2));
            validMoves.addAll(getKnightKing(board, myPosition, -1, -2));
        }
        if (this.getPieceType() == PieceType.KING){
            validMoves.addAll(getKnightKing(board, myPosition, 1, 1)); //TopRight
            validMoves.addAll(getKnightKing(board, myPosition, 1, 0)); //Up
            validMoves.addAll(getKnightKing(board, myPosition, 1, -1));//TopLeft
            validMoves.addAll(getKnightKing(board, myPosition, 0, -1));//Left
            validMoves.addAll(getKnightKing(board, myPosition, 0, 1)); //Right
            validMoves.addAll(getKnightKing(board, myPosition, -1, -1));//BottomLeft
            validMoves.addAll(getKnightKing(board, myPosition, -1, 0)); //Down
            validMoves.addAll(getKnightKing(board, myPosition, -1, 1));//BottomRight
        }


        return validMoves;
    }
}
