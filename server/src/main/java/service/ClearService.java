package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ResponseException;

public class ClearService {

  private final DataAccess dataAccess;

  public ClearService(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
  }

  public void clear() throws DataAccessException{
    dataAccess.clear();
  }
}
