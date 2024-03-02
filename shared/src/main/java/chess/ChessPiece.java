package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that=(ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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

    /**

     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int myrow=myPosition.getRow();
        int mycol=myPosition.getColumn();

        Collection<ChessMove> validMoves=new ArrayList<ChessMove>();

        //BISHOP

        if (this.getPieceType() == PieceType.BISHOP||this.getPieceType() == PieceType.QUEEN) {
            //TopRight
            addDiagonalMoves(validMoves, myPosition, board, 1, 1);
            //TopLeft
            addDiagonalMoves(validMoves, myPosition, board, 1, -1);
            //BottomRight
            addDiagonalMoves(validMoves, myPosition, board, -1, 1);
            //BottomLeft
            addDiagonalMoves(validMoves, myPosition, board, -1, -1);
        }

        //KING

        if (this.getPieceType() == PieceType.KING){
            //Up
            if (myrow < 8){
                ChessPosition checkPosition=new ChessPosition(myrow + 1, mycol);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //Down
            if (myrow > 1){
                ChessPosition checkPosition=new ChessPosition(myrow - 1, mycol);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //Right
            if (mycol < 8){
                ChessPosition checkPosition=new ChessPosition(myrow, mycol + 1);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //Left
            if (mycol > 1){
                ChessPosition checkPosition=new ChessPosition(myrow, mycol - 1);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //TopRight
            if (myrow < 8 && mycol < 8){
                ChessPosition checkPosition=new ChessPosition(myrow + 1, mycol + 1);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //TopLeft
            if (myrow < 8 && mycol > 1){
                ChessPosition checkPosition=new ChessPosition(myrow + 1, mycol - 1);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //BottomLeft
            if (myrow > 1 && mycol > 1){
                ChessPosition checkPosition=new ChessPosition(myrow - 1, mycol - 1);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //BottomRight
            if (myrow > 1 && mycol < 8){
                ChessPosition checkPosition=new ChessPosition(myrow - 1, mycol + 1);
                helper(board, myPosition, validMoves, checkPosition);
            }
        }

        //KNIGHT
        if (this.getPieceType() == PieceType.KNIGHT){
            //UpRight
            if(myrow < 7 && mycol < 8){
                ChessPosition checkPosition=new ChessPosition(myrow + 2, mycol + 1);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //UpLeft
            if(myrow < 7 && mycol > 1){
                ChessPosition checkPosition=new ChessPosition(myrow + 2, mycol - 1);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //DownRight
            if(myrow > 2 && mycol < 8){
                ChessPosition checkPosition=new ChessPosition(myrow - 2, mycol + 1);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //DownLeft
            if(myrow > 2 && mycol > 1){
                ChessPosition checkPosition=new ChessPosition(myrow - 2, mycol - 1);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //RightUp
            if(myrow < 8 && mycol < 7){
                ChessPosition checkPosition=new ChessPosition(myrow + 1, mycol + 2);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //LeftUp
            if(myrow < 8 && mycol > 2){
                ChessPosition checkPosition=new ChessPosition(myrow + 1, mycol - 2);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //RightDown
            if(myrow > 1 && mycol < 7){
                ChessPosition checkPosition=new ChessPosition(myrow - 1, mycol + 2);
                helper(board, myPosition, validMoves, checkPosition);
            }
            //LeftDown
            if(myrow > 1 && mycol > 2){
                ChessPosition checkPosition=new ChessPosition(myrow - 1, mycol - 2);
                helper(board, myPosition, validMoves, checkPosition);
            }

        }

        //ROOK
        myrow = myPosition.getRow();
        mycol = myPosition.getColumn();

        if (this.getPieceType() == PieceType.ROOK||this.getPieceType() == PieceType.QUEEN){
            //Up
            addStraightMoves(validMoves, myPosition, board, 1, 0);
            //Down
            addStraightMoves(validMoves, myPosition, board, -1, 0);
            //Left
            addStraightMoves(validMoves, myPosition, board, 0, -1);
            //Right
            addStraightMoves(validMoves, myPosition, board, 0, 1);
        }
        //WhitePawn
        if (this.getPieceType() == PieceType.PAWN && this.getTeamColor() == ChessGame.TeamColor.WHITE){
            //Starting Position
            if(myrow == 2){
                ChessPosition checkPosition1=new ChessPosition(myrow+1, mycol);
                if (board.getPiece(checkPosition1) == null){
                    ChessMove validMove=new ChessMove(myPosition, checkPosition1,  null);
                    validMoves.add(validMove);
                }
                ChessPosition checkPosition2=new ChessPosition(myrow+2, mycol);
                if (board.getPiece(checkPosition2) == null && board.getPiece(checkPosition1) == null){
                    ChessMove validMove=new ChessMove(myPosition, checkPosition2,  null);
                    validMoves.add(validMove);
                }
            }
            //Other Position
            if(myrow > 2 && myrow < 7){
                ChessPosition checkPosition=new ChessPosition(myrow+1, mycol);
                if (board.getPiece(checkPosition) == null){
                    ChessMove validMove=new ChessMove(myPosition, checkPosition,  null);
                    validMoves.add(validMove);
                }
            }
            //CaptureTopRight
            addPawnCaptureMoveWhite(validMoves, myPosition, board, 1, 1);
            //CaptureTopLeft
            addPawnCaptureMoveWhite(validMoves, myPosition, board, 1, -1);
            //Promotion
            if(myrow == 7){
                ChessPosition checkPosition=new ChessPosition(myrow+1, mycol);
                if (board.getPiece(checkPosition) == null) {
                    ChessMove promoMove1=new ChessMove(myPosition, checkPosition, PieceType.QUEEN);
                    ChessMove promoMove2=new ChessMove(myPosition, checkPosition, PieceType.ROOK);
                    ChessMove promoMove3=new ChessMove(myPosition, checkPosition, PieceType.BISHOP);
                    ChessMove promoMove4=new ChessMove(myPosition, checkPosition, PieceType.KNIGHT);
                    validMoves.add(promoMove1);
                    validMoves.add(promoMove2);
                    validMoves.add(promoMove3);
                    validMoves.add(promoMove4);
                }

                //PromotionCaptureRight
                ChessPosition checkPosition1=new ChessPosition(myrow+1, mycol+1);
                if (board.getPiece(checkPosition1) != null){
                    if (board.getPiece(checkPosition1).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove promoMove1=new ChessMove(myPosition, checkPosition1, PieceType.QUEEN);
                        ChessMove promoMove2=new ChessMove(myPosition, checkPosition1, PieceType.ROOK);
                        ChessMove promoMove3=new ChessMove(myPosition, checkPosition1, PieceType.BISHOP);
                        ChessMove promoMove4=new ChessMove(myPosition, checkPosition1, PieceType.KNIGHT);
                        validMoves.add(promoMove1);
                        validMoves.add(promoMove2);
                        validMoves.add(promoMove3);
                        validMoves.add(promoMove4);
                    }
                }
                //PromotionCaptureLeft
                ChessPosition checkPosition2=new ChessPosition(myrow+1, mycol-1);
                if (board.getPiece(checkPosition2) != null){
                    if (board.getPiece(checkPosition2).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove promoMove1=new ChessMove(myPosition, checkPosition2, PieceType.QUEEN);
                        ChessMove promoMove2=new ChessMove(myPosition, checkPosition2, PieceType.ROOK);
                        ChessMove promoMove3=new ChessMove(myPosition, checkPosition2, PieceType.BISHOP);
                        ChessMove promoMove4=new ChessMove(myPosition, checkPosition2, PieceType.KNIGHT);
                        validMoves.add(promoMove1);
                        validMoves.add(promoMove2);
                        validMoves.add(promoMove3);
                        validMoves.add(promoMove4);
                    }
                }


            }

        }
        //BlackPawn
        if (this.getPieceType() == PieceType.PAWN && this.getTeamColor() == ChessGame.TeamColor.BLACK){
            //Starting Position
            if(myrow == 7){
                ChessPosition checkPosition1=new ChessPosition(myrow-1, mycol);
                if (board.getPiece(checkPosition1) == null){
                    ChessMove validMove=new ChessMove(myPosition, checkPosition1,  null);
                    validMoves.add(validMove);
                }
                ChessPosition checkPosition2=new ChessPosition(myrow-2, mycol);
                if (board.getPiece(checkPosition2) == null && board.getPiece(checkPosition1) == null){
                    ChessMove validMove=new ChessMove(myPosition, checkPosition2,  null);
                    validMoves.add(validMove);
                }
            }
            //Other Position
            if(myrow > 2 && myrow < 7){
                ChessPosition checkPosition=new ChessPosition(myrow-1, mycol);
                if (board.getPiece(checkPosition) == null){
                    ChessMove validMove=new ChessMove(myPosition, checkPosition,  null);
                    validMoves.add(validMove);
                }
            }
            //CaptureBottomRight
            if(myrow > 2 && mycol < 8){
                ChessPosition checkPosition=new ChessPosition(myrow-1, mycol+1);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove captureMove=new ChessMove(myPosition, checkPosition, null);
                        validMoves.add(captureMove);
                    }
                }
            }
            //CaptureBottomLeft
            if(myrow > 2 && mycol > 1){
                ChessPosition checkPosition=new ChessPosition(myrow-1, mycol-1);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove captureMove=new ChessMove(myPosition, checkPosition, null);
                        validMoves.add(captureMove);
                    }
                }
            }
            //Promotion
            if(myrow == 2){
                ChessPosition checkPosition=new ChessPosition(myrow-1, mycol);
                if (board.getPiece(checkPosition) == null) {
                    ChessMove promoMove1=new ChessMove(myPosition, checkPosition, PieceType.QUEEN);
                    ChessMove promoMove2=new ChessMove(myPosition, checkPosition, PieceType.ROOK);
                    ChessMove promoMove3=new ChessMove(myPosition, checkPosition, PieceType.BISHOP);
                    ChessMove promoMove4=new ChessMove(myPosition, checkPosition, PieceType.KNIGHT);
                    validMoves.add(promoMove1);
                    validMoves.add(promoMove2);
                    validMoves.add(promoMove3);
                    validMoves.add(promoMove4);
                }
                //PromotionCaptureRight
                ChessPosition checkPosition1=new ChessPosition(myrow-1, mycol+1);
                if (board.getPiece(checkPosition1) != null){
                    if (board.getPiece(checkPosition1).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove promoMove1=new ChessMove(myPosition, checkPosition1, PieceType.QUEEN);
                        ChessMove promoMove2=new ChessMove(myPosition, checkPosition1, PieceType.ROOK);
                        ChessMove promoMove3=new ChessMove(myPosition, checkPosition1, PieceType.BISHOP);
                        ChessMove promoMove4=new ChessMove(myPosition, checkPosition1, PieceType.KNIGHT);
                        validMoves.add(promoMove1);
                        validMoves.add(promoMove2);
                        validMoves.add(promoMove3);
                        validMoves.add(promoMove4);
                    }
                }
                //PromotionCaptureLeft
                ChessPosition checkPosition2=new ChessPosition(myrow-1, mycol-1);
                if (board.getPiece(checkPosition2) != null){
                    if (board.getPiece(checkPosition2).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove promoMove1=new ChessMove(myPosition, checkPosition2, PieceType.QUEEN);
                        ChessMove promoMove2=new ChessMove(myPosition, checkPosition2, PieceType.ROOK);
                        ChessMove promoMove3=new ChessMove(myPosition, checkPosition2, PieceType.BISHOP);
                        ChessMove promoMove4=new ChessMove(myPosition, checkPosition2, PieceType.KNIGHT);
                        validMoves.add(promoMove1);
                        validMoves.add(promoMove2);
                        validMoves.add(promoMove3);
                        validMoves.add(promoMove4);
                    }
                }

            }

        }

        return validMoves;}
    private void addDiagonalMoves(Collection<ChessMove> validMoves, ChessPosition myPosition, ChessBoard board, int rowIncrement, int colIncrement) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();

        while (myRow >= 0 && myRow < 8 && myCol >= 0 && myCol < 8) {
            myRow += rowIncrement;
            myCol += colIncrement;
            ChessPosition checkPosition = new ChessPosition(myRow, myCol);

            if (board.getPiece(checkPosition) != null) {
                if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove captureMove = new ChessMove(myPosition, checkPosition, null);
                    validMoves.add(captureMove);
                }
                break;
            }

            ChessMove validMove = new ChessMove(myPosition, checkPosition, null);
            validMoves.add(validMove);
        }
    }
    private void addStraightMoves(Collection<ChessMove> validMoves, ChessPosition myPosition, ChessBoard board, int rowIncrement, int colIncrement) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();

        while (myRow >= 0 && myRow < 8 && myCol >= 0 && myCol < 8) {
            myRow += rowIncrement;
            myCol += colIncrement;
            ChessPosition checkPosition = new ChessPosition(myRow, myCol);

            if (board.getPiece(checkPosition) != null) {
                if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove captureMove = new ChessMove(myPosition, checkPosition, null);
                    validMoves.add(captureMove);
                }
                break;
            }

            ChessMove validMove = new ChessMove(myPosition, checkPosition, null);
            validMoves.add(validMove);
        }
    }
    private void addPawnCaptureMoveWhite(Collection<ChessMove> validMoves, ChessPosition myPosition, ChessBoard board, int rowIncrement, int colIncrement) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();

        if (myRow < 7 && myCol >= 0 && myCol < 8) {
            ChessPosition checkPosition = new ChessPosition(myRow + rowIncrement, myCol + colIncrement);
            if (board.getPiece(checkPosition) != null) {
                if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove captureMove = new ChessMove(myPosition, checkPosition, null);
                    validMoves.add(captureMove);
                }
            }
        }
    }

    private void helper(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, ChessPosition checkPosition) {
        if (board.getPiece(checkPosition) != null) {
            if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                ChessMove captureMove=new ChessMove(myPosition, checkPosition, null);
                validMoves.add(captureMove);
            }
        }
        else {
            ChessMove validMove=new ChessMove(myPosition, checkPosition,  null);
            validMoves.add(validMove);
        }
    }

}

