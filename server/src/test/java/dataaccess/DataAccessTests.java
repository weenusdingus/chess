package dataaccess;

import exception.ResponseException;
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

  @ParameterizedTest
  @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
  void createUser(Class<? extends DataAccess> dbClass) throws ResponseException, DataAccessException {
    DataAccess dataAccess = getDataAccess(dbClass);

    var user = new UserData("username", "joe", "email");
    assertDoesNotThrow(() -> dataAccess.createUser(user));
  }
}
