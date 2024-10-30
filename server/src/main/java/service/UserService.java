package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.UserData;
import model.AuthData;
import service.serviceExceptions.AlreadyTakenException;
import service.serviceExceptions.BadRequestException;
import service.serviceExceptions.UnauthorizedException;

import java.util.UUID;

public class UserService {
  private final DataAccess dataAccess;

  public UserService(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
  }

  public AuthData register(UserData user) throws AlreadyTakenException, DataAccessException, BadRequestException {
    if (user.username() == null || user.password() == null || user.email() == null){
      throw new BadRequestException("Bad request");
    }
    else if (dataAccess.getUser(user.username()) == null){
      dataAccess.createUser(user);
      return dataAccess.createAuth(new AuthData(UUID.randomUUID().toString(),user.username()));
    }
    else {
      throw new AlreadyTakenException("Username is Taken");
    }
  }

  public AuthData login(UserData user) throws DataAccessException, UnauthorizedException {
    UserData correctUser = dataAccess.getUser(user.username());
    if ((correctUser != null) && (user.password().equals(correctUser.password()))) {
      String auth = UUID.randomUUID().toString();
      return dataAccess.createAuth(new AuthData(auth, user.username()));
    }
    else {
      throw new UnauthorizedException("Unauthorized");
    }
  }

  public void logout(String auth) throws DataAccessException, UnauthorizedException {
    if(dataAccess.getAuth(auth) != null) {
      dataAccess.deleteAuth(auth);
    }
    else {
      throw new UnauthorizedException("Unauthorized");
    }
  }
}
