package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.UserData;

public class ClearService {

  private final DataAccess dataAccess;

  public ClearService(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
  }

  public void clear() throws DataAccessException {
    dataAccess.clear();
  }
}
