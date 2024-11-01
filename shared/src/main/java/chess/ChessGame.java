package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame chessGame=(ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }

    private ChessBoard board = new ChessBoard();
    private TeamColor currentTurn;

    public ChessGame() {
        board.resetBoard();
        currentTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) != null) {
            ChessPiece startPiece = board.getPiece(startPosition);
            Collection<ChessMove> allMoves = startPiece.pieceMoves(board, startPosition);
            Collection<ChessMove> moveList = new ArrayList<>();
            for (ChessMove move: allMoves) {
                ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
                board.addPiece(move.getEndPosition(), startPiece);
                board.addPiece(move.getStartPosition(), null);
                if (!isInCheck(startPiece.getTeamColor())) {
                    moveList.add(move);
                }
                board.addPiece(move.getStartPosition(), startPiece);
                board.addPiece(move.getEndPosition(), capturedPiece);
            }
            return moveList;
        }
        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No Piece here");
        }
        if (piece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("Wrong Color");
        }
        if (piece.pieceMoves(board, move.getStartPosition()).contains(move)){
            if (move.getPromotionPiece() == null){
                board.addPiece(move.getEndPosition(), piece);
            }
            else {
                board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            }
            board.addPiece(move.getStartPosition(), null);
            if (isInCheck(piece.getTeamColor()) || isInCheckmate(piece.getTeamColor()) || isInStalemate(piece.getTeamColor())){
                board.addPiece(move.getStartPosition(), piece);
                board.addPiece(move.getEndPosition(), capturedPiece);
                throw new InvalidMoveException("Dangerous Move");
            }
            setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        }
        else {
            throw new InvalidMoveException("No moves possible");
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor advantageTeam;
        if (teamColor == TeamColor.WHITE) {
            advantageTeam = TeamColor.BLACK;
        }
        else {
            advantageTeam = TeamColor.WHITE;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() == advantageTeam) {
                    Collection<ChessMove> currentValidMoves = currentPiece.pieceMoves(board, currentPosition);
                    for (ChessMove currentMove : currentValidMoves) {
                        if (board.getPiece(currentMove.getEndPosition()) != null) {
                            if (board.getPiece(currentMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (anyValidMoves(teamColor) && isInCheck(teamColor));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (anyValidMoves(teamColor) && !isInCheck(teamColor));
    }
    public boolean anyValidMoves(TeamColor teamColor) {
        boolean hasPieces = false;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    hasPieces = true;
                    break;
                }
            }
            if (hasPieces) {
                break;
            }
        }

        if (!hasPieces) {
            return false;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    if (!validMoves(currentPosition).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
