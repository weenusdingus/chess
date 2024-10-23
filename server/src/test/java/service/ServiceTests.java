package service;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.serviceExceptions.AlreadyTakenException;
import service.serviceExceptions.BadRequestException;
import service.serviceExceptions.UnauthorizedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
  UserService service = null;
  ClearService clearService = null;
  GameService gameService = null;
  ChessGame chessGame = new ChessGame();
  UserData user = new UserData("username", "password", "email@email.com");
  AuthData auth = new AuthData(UUID.randomUUID().toString(), "username");
  GameData game = new GameData(1, "wusername", "busername", "chess", chessGame);

  MemoryDataAccess dao = null;

  @BeforeEach
  void setup() throws DataAccessException{
    dao = new MemoryDataAccess();
    service = new UserService(dao);
    clearService = new ClearService(dao);
    gameService = new GameService(dao);
    clearService.clear();
  }


  @Test
  void testClear() throws DataAccessException, AlreadyTakenException {
    dao.createUser(user);
    dao.createAuth(auth);
    dao.createGame(game);
    clearService.clear();

    assertEquals(0, dao.getUsers().size());
    assertEquals(0, dao.getAuths().size());
    assertEquals(0, dao.getGames().size());
  }
