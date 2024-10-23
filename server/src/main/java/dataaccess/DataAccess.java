package dataaccess;

import model.UserData;
import model.GameData;
import model.AuthData;

import java.util.Collection;

public interface DataAccess {

  void clear() throws DataAccessException;

  UserData createUser(UserData user) throws DataAccessException;

  UserData getUser(String username) throws DataAccessException;

  GameData createGame(GameData game) throws DataAccessException;

  GameData getGame(int gameID) throws DataAccessException;

  Collection<GameData> listGames() throws DataAccessException;

  void updateGame(int gameID, GameData game) throws DataAccessException;

  AuthData createAuth(AuthData auth) throws DataAccessException;

  AuthData getAuth(String authToken) throws DataAccessException;

  void deleteAuth(String authToken) throws DataAccessException;
}
