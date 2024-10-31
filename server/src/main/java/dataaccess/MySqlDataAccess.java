package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {

  public MySqlDataAccess() throws DataAccessException {
    configureDatabase();
  }
  @Override
  public void clear() throws DataAccessException {
    var statement = "TRUNCATE user";
    executeUpdate(statement);
    var statement2 = "TRUNCATE game";
    executeUpdate(statement2);
    var statement3 = "TRUNCATE auth";
    executeUpdate(statement3);
  }

  @Override
  public UserData createUser(UserData user) throws DataAccessException{
    var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
    executeUpdate(statement, user.username(), user.password(), user.email());
    return new UserData(user.username(), user.password(), user.email());
  }

  @Override
  public UserData getUser(String username) throws DataAccessException {
    var statement = "SELECT username, password, email FROM user WHERE username = ?";
    try (var conn = DatabaseManager.getConnection();
         var ps = conn.prepareStatement(statement)) {
      ps.setString(1, username);
      try (var rs = ps.executeQuery()) {
        if (rs.next()) {
          String retrievedUsername = rs.getString("username");
          String password = rs.getString("password");
          String email = rs.getString("email");
          return new UserData(retrievedUsername, password, email);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("Unable to retrieve user from database: " + e.getMessage());
    }
  }

  @Override
  public GameData createGame(GameData game) throws DataAccessException {
    var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, gameJson) VALUES (?,?,?,?,?)";
    var gameJson = new Gson().toJson(game);
    int generatedGameID = executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), gameJson);
    return new GameData(generatedGameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
  }

  @Override
  public GameData getGame(int gameID) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson FROM game WHERE gameID=?";
      try (var ps = conn.prepareStatement(statement)) {
        ps.setInt(1, gameID);
        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            return readGame(rs);
          }
        }
      }
    } catch (Exception e) {
      throw new DataAccessException("Unable to read game data: %s"+ e.getMessage());
    }
    return null;
  }

  @Override
  public Collection<GameData> listGames() throws DataAccessException {
    var result = new ArrayList<GameData>();
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson FROM game";
      try (var ps = conn.prepareStatement(statement)) {
        try (var rs = ps.executeQuery()) {
          while (rs.next()) {
            result.add(readGame(rs));
          }
        }
      }
    } catch (Exception e) {
      throw new DataAccessException("Unable to read game data: %s"+ e.getMessage());
    }
    return result;
  }

  @Override
  public void updateGame(int gameID, GameData game) throws DataAccessException {
    String sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameJson = ? WHERE gameID = ?";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, game.whiteUsername());
      ps.setString(2, game.blackUsername());
      ps.setString(3, game.gameName());
      ps.setString(4, new Gson().toJson(game.game()));
      ps.setInt(5, gameID);

      int rowsAffected = ps.executeUpdate();
      if (rowsAffected == 0) {
        throw new DataAccessException("No game found with gameID: " + gameID);
      }
    } catch (SQLException e) {
      throw new DataAccessException("Unable to update game data: " + e.getMessage());
    }

  }

  @Override
  public AuthData createAuth(AuthData auth) throws DataAccessException {
    var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
    executeUpdate(statement, auth.authToken(), auth.username());
    return new AuthData(auth.authToken(), auth.username());
  }

  @Override
  public AuthData getAuth(String authToken) throws DataAccessException {
    var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
    try (var conn = DatabaseManager.getConnection();
         var ps = conn.prepareStatement(statement)) {
      ps.setString(1, authToken);
      try (var rs = ps.executeQuery()) {
        if (rs.next()) {
          String retrievedAuthToken = rs.getString("authToken");
          String username = rs.getString("username");
          return new AuthData(retrievedAuthToken, username);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("Unable to retrieve user from database: " + e.getMessage());
    }
  }

  @Override
  public void deleteAuth(String authToken) throws DataAccessException {
    var statement = "DELETE FROM auth WHERE authToken=?";
    executeUpdate(statement, authToken);
  }
  private GameData readGame(ResultSet rs) throws SQLException {
    var gameID = rs.getInt("gameID");
    var whiteUsername = rs.getString("whiteUsername");
    var blackUsername = rs.getString("blackUsername");
    var gameName = rs.getString("gameName");
    var gameJson = rs.getString("gameJson");
    var game = new Gson().fromJson(gameJson, ChessGame.class);
    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
  }
  private int executeUpdate(String statement, Object... params) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {
      try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
        for (var i = 0; i < params.length; i++) {
          var param = params[i];
          if (param instanceof String p) ps.setString(i + 1, p);
          else if (param instanceof Integer p) ps.setInt(i + 1, p);
          else if (param == null) ps.setNull(i + 1, NULL);
        }
        ps.executeUpdate();

        var rs = ps.getGeneratedKeys();
        if (rs.next()) {
          return rs.getInt(1);
        }

        return 0;
      }
    } catch (SQLException e) {
      throw new DataAccessException("Unable to update database: %s, %s");
    }
  }
  private final String[] createStatements = {
          """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
              `whiteUsername` varchar (256),
              `blackUsername` varchar (256),
              `gameName` varchar (256) NOT NULL,
              `gameJson` longtext NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar (256) NOT NULL,
              `password` varchar (256) NOT NULL,
              `email` varchar (256) NOT NULL,
               PRIMARY KEY (`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
              `authToken` varchar (256) NOT NULL PRIMARY KEY,
              `username` varchar (256) NOT NULL
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
  };


  private void configureDatabase() throws DataAccessException {
    DatabaseManager.createDatabase();
    try (var conn = DatabaseManager.getConnection()) {
      for (var statement : createStatements) {
        try (var preparedStatement = conn.prepareStatement(statement)) {
          preparedStatement.executeUpdate();
        }
      }
    } catch (SQLException ex) {
      throw new DataAccessException("Unable to configure database: %s");
    }
  }
}
