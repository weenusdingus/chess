package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess {

  public MySqlDataAccess() throws ResponseException, DataAccessException {
    configureDatabase();
  }
  private final String[] createStatements = {
          """
            CREATE TABLE IF NOT EXISTS  game (
              ‘gameID’ int NOT NULL AUTO_INCREMENT PRIMARY KEY,
              ‘whiteUsername’ varchar (256),
              ‘blackUsername’ varchar (256),
              ‘gameName’ varchar (256) NOT NULL,
              ‘Game’ longtext NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS User (
              ‘username’ varchar (256) NOT NULL,
              ‘password’ varchar (256) NOT NULL,
              ‘email’ varchar (256) NOT NULL,
               PRIMARY KEY (‘username’)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS Auth (
              ‘authToken’ varchar (256) NOT NULL PRIMARY KEY,
              ‘username’ varchar (256) NOT NULL
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
  };


  private void configureDatabase() throws ResponseException, DataAccessException {
    DatabaseManager.createDatabase();
    try (var conn = DatabaseManager.getConnection()) {
      for (var statement : createStatements) {
        try (var preparedStatement = conn.prepareStatement(statement)) {
          preparedStatement.executeUpdate();
        }
      }
    } catch (SQLException ex) {
      throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
    }
  }
}
