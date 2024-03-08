package chess;

import java.lang.Math;
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
        int myrow = myPosition.getRow();
        int mycol = myPosition.getColumn();

        Collection<ChessMove> validMoves=new ArrayList<ChessMove>();

        //BISHOP&QUEEN
        bishopMove(myrow, mycol,8,8,validMoves,board, myPosition);//TopRight
        bishopMove(myrow, -mycol,8,-1,validMoves,board, myPosition);//TopLeft
        bishopMove(-myrow, mycol,-1,8,validMoves,board, myPosition);//BottomRight
        bishopMove(-myrow, -mycol,-1,-1,validMoves,board, myPosition);//BottomLeft
        //KING
        kingMove(myrow, mycol, 8,9,1,0,validMoves,board,myPosition);//Up
        kingMove(-myrow, mycol, -1,9,-1,0,validMoves,board,myPosition);//Down
        kingMove(myrow, mycol, 9,8,0,1,validMoves,board,myPosition);//Right
        kingMove(myrow, -mycol, 9,-1,0,-1,validMoves,board,myPosition);//Left
        kingMove(myrow, mycol, 8,8,1,1,validMoves,board,myPosition);//UpRight
        kingMove(myrow, -mycol, 8,-1,1,-1,validMoves,board,myPosition);//UpLeft
        kingMove(-myrow, mycol, -1,8,-1,1,validMoves,board,myPosition);//DownRight
        kingMove(-myrow, -mycol, -1,-1,-1,-1,validMoves,board,myPosition);//DownLeft
        //KNIGHT
        if (this.getPieceType() == PieceType.KNIGHT){
            //UpRight
            if(myrow < 7 && mycol < 8){
                ChessPosition checkPosition=new ChessPosition(myrow + 2, mycol + 1);
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
            //UpLeft
            if(myrow < 7 && mycol > 1){
                ChessPosition checkPosition=new ChessPosition(myrow + 2, mycol - 1);
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
            //DownRight
            if(myrow > 2 && mycol < 8){
                ChessPosition checkPosition=new ChessPosition(myrow - 2, mycol + 1);
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
            //DownLeft
            if(myrow > 2 && mycol > 1){
                ChessPosition checkPosition=new ChessPosition(myrow - 2, mycol - 1);
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
            //RightUp
            if(myrow < 8 && mycol < 7){
                ChessPosition checkPosition=new ChessPosition(myrow + 1, mycol + 2);
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
            //LeftUp
            if(myrow < 8 && mycol > 2){
                ChessPosition checkPosition=new ChessPosition(myrow + 1, mycol - 2);
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
            //RightDown
            if(myrow > 1 && mycol < 7){
                ChessPosition checkPosition=new ChessPosition(myrow - 1, mycol + 2);
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
            //LeftDown
            if(myrow > 1 && mycol > 2){
                ChessPosition checkPosition=new ChessPosition(myrow - 1, mycol - 2);
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

        //ROOK
        myrow = myPosition.getRow();
        mycol = myPosition.getColumn();

        if (this.getPieceType() == PieceType.ROOK||this.getPieceType() == PieceType.QUEEN){
            //Up
            while (myrow < 8){
                myrow += 1;
                ChessPosition checkPosition=new ChessPosition(myrow, mycol);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove captureMove=new ChessMove(myPosition, checkPosition, null);
                        validMoves.add(captureMove);
                    }
                    break;
                }
                ChessMove validMove=new ChessMove(myPosition, checkPosition,  null);
                validMoves.add(validMove);
            }

            myrow = myPosition.getRow();

            //Down
            while (myrow > 1){
                myrow -= 1;
                ChessPosition checkPosition=new ChessPosition(myrow, mycol);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove captureMove=new ChessMove(myPosition, checkPosition, null);
                        validMoves.add(captureMove);
                    }
                    break;
                }
                ChessMove validMove=new ChessMove(myPosition, checkPosition,  null);
                validMoves.add(validMove);
            }
            myrow = myPosition.getRow();
            //Left
            while (mycol > 1){
                mycol -= 1;
                ChessPosition checkPosition=new ChessPosition(myrow, mycol);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove captureMove=new ChessMove(myPosition, checkPosition, null);
                        validMoves.add(captureMove);
                    }
                    break;
                }
                ChessMove validMove=new ChessMove(myPosition, checkPosition,  null);
                validMoves.add(validMove);
            }
            mycol = myPosition.getColumn();
            //Right
            while (mycol < 8){
                mycol += 1;
                ChessPosition checkPosition=new ChessPosition(myrow, mycol);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove captureMove=new ChessMove(myPosition, checkPosition, null);
                        validMoves.add(captureMove);
                    }
                    break;
                }
                ChessMove validMove=new ChessMove(myPosition, checkPosition,  null);
                validMoves.add(validMove);
            }
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
            if(myrow < 7 && mycol < 8){
                ChessPosition checkPosition=new ChessPosition(myrow+1, mycol+1);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove captureMove=new ChessMove(myPosition, checkPosition, null);
                        validMoves.add(captureMove);
                    }
                }
            }
            //CaptureTopLeft
            if(myrow < 7 && mycol > 1){
                ChessPosition checkPosition=new ChessPosition(myrow+1, mycol-1);
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove captureMove=new ChessMove(myPosition, checkPosition, null);
                        validMoves.add(captureMove);
                    }
                }
            }
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
    public void bishopMove(int startRow, int startCol, int maxRow, int maxCol, Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition){
        int myrow = startRow;
        int mycol = startCol;
        if (this.getPieceType() == PieceType.BISHOP||this.getPieceType() == PieceType.QUEEN) {
            while (myrow < maxRow && mycol < maxCol) {
                myrow+=1;
                mycol+=1;
                ChessPosition checkPosition=new ChessPosition(Math.abs(myrow), Math.abs(mycol));
                if (board.getPiece(checkPosition) != null) {
                    if (board.getPiece(checkPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        ChessMove captureMove=new ChessMove(myPosition, checkPosition,  null);
                        validMoves.add(captureMove);
                    }
                    break;
                }
                ChessMove validMove=new ChessMove(myPosition, checkPosition, null);
                validMoves.add(validMove);
            }
        }
    }
    public void kingMove(int startRow, int startCol, int maxRow, int maxCol, int rowAdd, int colAdd, Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition){
        int myrow = startRow;
        int mycol = startCol;
        if (this.getPieceType() == PieceType.KING){
            if (myrow < maxRow && mycol < maxCol){
                ChessPosition checkPosition=new ChessPosition(Math.abs(myrow) + rowAdd, Math.abs(mycol) + colAdd);
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
    }
}

