
package dataaccess;
import java.util.*;

import chess.ChessGame;
import model.UserData;
import model.GameData;
import model.AuthData;
import java.util.UUID;
import java.util.stream.Collectors;

public class MemoryDataAccess implements DataAccess {

  private int nextGameId = 0;

  private Collection<UserData> users = new ArrayList<>();
  private HashMap<Integer, GameData> games = new HashMap<>();
  private Collection<AuthData> auths = new ArrayList<>();

  @Override
  public void clear() {
    users.clear();
    games.clear();
    auths.clear();
  }

  public Collection<UserData> getUsers() {
    return users;
  }

  public HashMap<Integer, GameData> getGames() {
    return games;
  }

  public Collection<AuthData> getAuths() {
    return auths;
  }

  @Override
  public UserData createUser(UserData user) throws DataAccessException {
    users.add(user);
    return user;
  }

  @Override
  public UserData getUser(String username) throws DataAccessException {
    return users.stream().filter(u -> u.username().equals(username)).findFirst().orElse(null);
  }

  @Override
  public GameData createGame(GameData game) throws DataAccessException {
    GameData newGame = new GameData(++nextGameId, game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame());
    games.put(nextGameId, newGame);
    return newGame;
  }

  @Override
  public GameData getGame(int gameID) throws DataAccessException {
    return games.get(gameID);
  }

  @Override
  public Collection<GameData> listGames() throws DataAccessException {
    return games.values();
  }

  @Override
  public void updateGame(int gameID, GameData game) throws DataAccessException {
    games.replace(gameID, game);
  }

  @Override
  public AuthData createAuth(AuthData auth) throws DataAccessException {
    auths.add(auth);
    return auth;
  }

  @Override
  public AuthData getAuth(String authToken) throws DataAccessException {
    return auths.stream().filter(a -> a.authToken().equals(authToken)).findFirst().orElse(null);
  }

  @Override
  public void deleteAuth(String authToken) throws DataAccessException {
    auths.removeIf(a -> a.authToken().equals(authToken));
  }
}
