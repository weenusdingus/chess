package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import service.serviceexceptions.AlreadyTakenException;
import service.serviceexceptions.BadRequestException;
import service.serviceexceptions.UnauthorizedException;

import java.util.Collection;

public class GameService {

  private final DataAccess dataAccess;

  public GameService(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
  }

  public GameData createGame(GameData game, String authToken) throws DataAccessException, BadRequestException, UnauthorizedException {
    if(game.gameName() == null){
      throw new BadRequestException("Needs a game name");
    }
    else if(dataAccess.getAuth(authToken) != null) {
      return dataAccess.createGame(game);
    }
    else {
      throw new UnauthorizedException("Unauthorized");
    }
  }
  public Collection<GameData> listGames(String authToken) throws DataAccessException, UnauthorizedException {
    if(dataAccess.getAuth(authToken) == null){
      throw new UnauthorizedException("Unauthorized");
    }
    return dataAccess.listGames();
  }
  public void joinGame(String authToken, String color, int gameID) throws UnauthorizedException,
          DataAccessException, BadRequestException, AlreadyTakenException {
    if(dataAccess.getAuth(authToken) == null){
      throw new UnauthorizedException("Unauthorized");
    }
    if(dataAccess.getGame(gameID) == null){
      throw new BadRequestException("Game does not exist");
    }
    if(color == null){
      throw new BadRequestException("Need player color");
    }
    if(color.equals("WHITE")){
      if(dataAccess.getGame(gameID).whiteUsername() != null){
        throw new AlreadyTakenException("Already taken");
      }
      else{
        GameData game = new GameData(gameID, dataAccess.getAuth(authToken).username(),
                dataAccess.getGame(gameID).blackUsername(), dataAccess.getGame(gameID).gameName(), dataAccess.getGame(gameID).game());
        dataAccess.updateGame(gameID, game);
      }
    }
    if(color.equals("BLACK")){
      if(dataAccess.getGame(gameID).blackUsername() != null){
        throw new AlreadyTakenException("Already Taken");
      }
      else{
        GameData game = new GameData(gameID, dataAccess.getGame(gameID).whiteUsername(),
                dataAccess.getAuth(authToken).username(), dataAccess.getGame(gameID).gameName(), dataAccess.getGame(gameID).game());
        dataAccess.updateGame(gameID, game);
      }
    }

  }


}

