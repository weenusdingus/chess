package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
public class MySqlDataAccess implements DataAccess {
  public MySqlDataAccess() throws DataAccessException {
    configureDatabase();
  }
  private final String[] createStatements = {
          """
            CREATE TABLE IF NOT EXISTS Auth (
              `authToken` varchar(255) NOT NULL,
              `username` varchar(255) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,
          """
            CREATE TABLE IF NOT EXISTS Game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(255),
              `blackUsername` varchar(255),
              `gameName` varchar(255) NOT NULL,
              `gameJSON` varchar(2048) NOT NULL,
              PRIMARY KEY (gameID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,
          """
            CREATE TABLE IF NOT EXISTS User (
              `username` varchar(255) NOT NULL,
              `password` varchar(255) NOT NULL,
              `email` varchar(255) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
      throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
    }
  }

  private int myExecute(String statement, Object... params) throws DataAccessException {
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
      }
    } catch (SQLException e) {
      throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
    }
    return 0;
  }
  @Override
  public void clear() throws DataAccessException {
    String statement = "TRUNCATE Auth";
    myExecute(statement);
    statement = "TRUNCATE User";
    myExecute(statement);
    statement = "TRUNCATE Game";
    myExecute(statement);
  }

  @Override
  public UserData createUser(UserData user) throws DataAccessException {
    var statement = "INSERT INTO User (username, password, email) VALUES (?, ?, ?)";
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String hashedPassword =  encoder.encode(user.password());
    myExecute(statement, user.username(), hashedPassword, user.email());
    return null;
  }

  public UserData getUser(String username) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT * FROM User WHERE username = ?";
      try (var ps = conn.prepareStatement(statement)) {
        ps.setString(1, username);
        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            return readUser(rs);
          }
        }
      }
    } catch (Exception e) {
      throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
    }
    return null;
  }

  private UserData readUser(ResultSet rs) throws SQLException {
    var username = rs.getString("username");
    var password = rs.getString("password");
    var email = rs.getString("email");
    return new UserData(username, password, email);
  }

  @Override
  public GameData createGame(GameData game) throws DataAccessException {
    var gameJSON = new Gson().toJson(game.game());
    var statement = "INSERT INTO Game (gameID, whiteUsername, blackUsername, gameName, gameJSON) VALUES (?, ?, ?, ?, ?)";
    int gameId = myExecute(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), gameJSON);
    GameData newGame = new GameData(gameId, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    return newGame;
  }

  @Override
  public GameData getGame(int gameID) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT * FROM Game WHERE gameID = ?";
      try (var ps = conn.prepareStatement(statement)) {
        ps.setInt(1, gameID);
        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            return readGame(rs);
          }
        }
      }
    } catch (Exception e) {
      throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
    }
    return null;
  }

  private GameData readGame(ResultSet rs) throws SQLException {
    String gameJSON = rs.getString("gameJSON");
    ChessGame game = new Gson().fromJson(gameJSON, ChessGame.class);
    var gameID = rs.getInt("gameID");
    var whiteUsername = rs.getString("whiteUsername");
    var blackUsername = rs.getString("blackUsername");
    var gameName = rs.getString("gameName");
    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
  }

  @Override
  public Collection<GameData> listGames() throws DataAccessException {
    var result = new ArrayList<GameData>();
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT * FROM Game";
      try (var ps = conn.prepareStatement(statement)) {
        try (var rs = ps.executeQuery()) {
          while (rs.next()) {
            result.add(readGame(rs));
          }
        }
      } catch (SQLException e) {
        throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
      }
    } catch (SQLException e) {
      throw new RuntimeException(String.format("Unable to read data: %s", e.getMessage()));
    }
    return result;
  }

  @Override
  public void updateGame(int gameID, GameData game) throws DataAccessException {
    var newGameJSON = new Gson().toJson(game.game());
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "UPDATE Game SET gameJSON = ?, whiteUsername = ?, blackUsername = ?, gameName = ?  WHERE gameID = ?";
      try (var ps = conn.prepareStatement(statement)) {
        ps.setString(1, newGameJSON);
        ps.setString(2, game.whiteUsername());
        ps.setString(3, game.blackUsername());
        ps.setString(4, game.gameName());
        ps.setInt(5, gameID);
        ps.executeUpdate();
      }

    } catch (SQLException e) {
      throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
    }
  }

  @Override
  public AuthData createAuth(AuthData auth) throws DataAccessException {
    var statement = "INSERT INTO Auth (authToken, username) VALUES (?, ?)";
    myExecute(statement, auth.authToken(), auth.username());
    return new AuthData(auth.authToken(), auth.username());
  }

  @Override
  public AuthData getAuth(String authToken) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT * FROM Auth WHERE authToken = ?";
      try (var ps = conn.prepareStatement(statement)) {
        ps.setString(1, authToken);
        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            return readAuth(rs);
          }
        }
      }
    } catch (Exception e) {
      throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
    }
    return null;
  }

  private AuthData readAuth(ResultSet rs) throws SQLException {
    var authToken = rs.getString("authToken");
    var username = rs.getString("username");
    return new AuthData(authToken, username);
  }

  @Override
  public void deleteAuth(String authToken) throws DataAccessException {
    var statement = "DELETE FROM Auth WHERE authToken = ?";
    myExecute(statement, authToken);
  }
  public int getSize(String tableName) throws DataAccessException {
    try (var conn = DatabaseManager.getConnection()) {
      var statement = "SELECT COUNT(*) as total FROM " + tableName;
      try (var ps = conn.prepareStatement(statement)) {
        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            return rs.getInt("total");
          }
        }
      }
    } catch (Exception e) {
      throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
    }
    return 0;
  }
}
