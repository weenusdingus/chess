
package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.UserData;
import model.AuthData;
import service.serviceExceptions.UsernameException;

import java.util.UUID;

public class UserService {
  private final DataAccess dataAccess;

  public UserService(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
  }

  public AuthData register(UserData user) throws UsernameException, DataAccessException {
    if (dataAccess.getUser(user.username()) == null){
      dataAccess.createUser(user);
      return dataAccess.createAuth(new AuthData(UUID.randomUUID().toString(),user.username()));
    }
    else {
      throw new UsernameException("Username is Taken");
    }
  }

  public AuthData login(UserData user) throws DataAccessException, UsernameException {
    if(dataAccess.getUser(user.username()) != null){
      return dataAccess.createAuth(new AuthData(UUID.randomUUID().toString(),user.username()));
    }
    else {
      throw new UsernameException("Username does not exist");
    }
  }

  public void logout(UserData user) throws DataAccessException {}
}