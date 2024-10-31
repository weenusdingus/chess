package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
  private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws ResponseException, DataAccessException {
    DataAccess db;
    if (databaseClass.equals(MySqlDataAccess.class)) {
      db = new MySqlDataAccess();
    } else {
      db = new MemoryDataAccess();
    }
    db.clear();
    return db;
  }
  ChessGame chessGame = new ChessGame();

  @ParameterizedTest
  @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
  void createUser(Class<? extends DataAccess> dbClass) throws ResponseException, DataAccessException {
    DataAccess dataAccess = getDataAccess(dbClass);

    var user = new UserData("username", "joe", "email");
    assertDoesNotThrow(() -> dataAccess.createUser(user));
  }
  @ParameterizedTest
  @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
  void getUser(Class<? extends DataAccess> dbClass) throws ResponseException, DataAccessException {
    DataAccess dataAccess = getDataAccess(dbClass);
    var originalUser = new UserData("username", "password", "email@example.com");
    dataAccess.createUser(originalUser);
    UserData retrievedUser = dataAccess.getUser("username");

    assertNotNull(retrievedUser, "User should be found in the database");
    assertEquals(originalUser.username(), retrievedUser.username(), "Username should match");
    assertEquals(originalUser.password(), retrievedUser.password(), "Password should match");
    assertEquals(originalUser.email(), retrievedUser.email(), "Email should match");
  }
  @ParameterizedTest
  @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
  void createAuth(Class<? extends DataAccess> dbClass) throws ResponseException, DataAccessException {
    DataAccess dataAccess = getDataAccess(dbClass);

    var auth = new AuthData(UUID.randomUUID().toString(), "username");
    assertDoesNotThrow(() -> dataAccess.createAuth(auth));
  }
  @ParameterizedTest
  @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
  void getAuth(Class<? extends DataAccess> dbClass) throws ResponseException, DataAccessException {
    DataAccess dataAccess = getDataAccess(dbClass);
    String random = UUID.randomUUID().toString();
    var originalAuth = new AuthData(random, "username");
    dataAccess.createAuth(originalAuth);
    AuthData retrievedAuth = dataAccess.getAuth(random);

    assertNotNull(retrievedAuth, "Auth should be found in the database");
    assertEquals(originalAuth.authToken(), retrievedAuth.authToken(), "Auth should match");
    assertEquals(originalAuth.username(), retrievedAuth.username(), "Username should match");
  }
  @ParameterizedTest
  @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
  void deleteAuth(Class<? extends DataAccess> dbClass) throws ResponseException, DataAccessException {
    DataAccess dataAccess = getDataAccess(dbClass);

    String authToken = UUID.randomUUID().toString();
    var auth = new AuthData(authToken, "username");
    dataAccess.createAuth(auth);

    AuthData retrievedAuth = dataAccess.getAuth(authToken);
    assertNotNull(retrievedAuth, "Auth entry should be found in the database");

    dataAccess.deleteAuth(authToken);

    retrievedAuth = dataAccess.getAuth(authToken);
    assertNull(retrievedAuth, "Auth entry should no longer exist in the database after deletion");
  }
  @ParameterizedTest
  @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
  void createGame(Class<? extends DataAccess> dbClass) throws ResponseException, DataAccessException {
    DataAccess dataAccess = getDataAccess(dbClass);

    var game = new GameData(1,"wusername" , "busername", "chessName", chessGame);
    assertDoesNotThrow(() -> dataAccess.createGame(game));
  }
  @ParameterizedTest
  @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
  void getGame(Class<? extends DataAccess> dbClass) throws ResponseException, DataAccessException {
    DataAccess dataAccess = getDataAccess(dbClass);
    var originalGame = new GameData(1,"wusername" , "busername", "chessName", chessGame);
    dataAccess.createGame(originalGame);
    GameData retrievedGame = dataAccess.getGame(originalGame.gameID());

    assertNotNull(retrievedGame, "Game should be found in the database");
    assertEquals(originalGame.gameID(), retrievedGame.gameID(), "Game ID should match");
    assertEquals(originalGame.whiteUsername(), retrievedGame.whiteUsername(), "White username should match");
    assertEquals(originalGame.blackUsername(), retrievedGame.blackUsername(), "Black username should match");
    assertEquals(originalGame.gameName(), retrievedGame.gameName(), "Game name should match");
    assertEquals(originalGame.game(), retrievedGame.game(), "Game should match");
  }
}
