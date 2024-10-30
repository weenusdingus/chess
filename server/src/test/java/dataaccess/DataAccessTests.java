package dataaccess;

import exception.ResponseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DataAccessTests {
  MySqlDataAccess temporary;
  @Test
  void initializingDatabase() throws ResponseException, DataAccessException {
    temporary = new MySqlDataAccess();
    assert true;
  }
}
