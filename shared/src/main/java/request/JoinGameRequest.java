package request;

import chess.ChessGame;
public record JoinGameRequest(String authToken, ChessGame.TeamColor playerColor, Integer gameID) {
}
