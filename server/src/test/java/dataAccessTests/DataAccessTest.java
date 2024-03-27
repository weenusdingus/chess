package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.serviceExceptions.AlreadyTakenException;
import service.serviceExceptions.BadRequestException;
import service.serviceExceptions.UnauthorizedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTest {
  UserService service = null;
  ClearService clearService = null;
  GameService gameService = null;
  ChessGame chessGame = new ChessGame();
  UserData user = new UserData("username", "password", "email@email.com");
  AuthData auth = new AuthData(UUID.randomUUID().toString(), "username");
  GameData game = new GameData(1, "wusername", "busername", "chess", chessGame);

  MySqlDataAccess dao = null;

  @BeforeEach
  void setup() throws DataAccessException{
    dao = new MySqlDataAccess();
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

    assertEquals(0, dao.getSize("Auth"));
    assertEquals(0, dao.getSize("User"));
    assertEquals(0, dao.getSize("Game"));
  }
  @Test
  void createUser() throws DataAccessException, AlreadyTakenException, BadRequestException {
    dao.createUser(user);
    UserData testUser = dao.getUser("username");
    assertEquals(testUser.username(), user.username());
  }
  @Test
  void createUserFail() throws DataAccessException, AlreadyTakenException, BadRequestException {
    UserData badUser = new UserData("bad", null, null);
    assertThrows(IllegalArgumentException.class, () ->{
      dao.createUser(badUser);
    });
  }
  @Test
  void getUser() throws DataAccessException, AlreadyTakenException, UnauthorizedException {
    dao.createUser(user);
    UserData testUser = dao.getUser("username");
    assertEquals(testUser.username(), user.username());
  }
  @Test
  void getUserFail() throws DataAccessException, AlreadyTakenException {
    assertNull(dao.getUser("nonesistantuser")) ;
  }
  @Test
  void getGame() throws DataAccessException, UnauthorizedException, AlreadyTakenException {
    dao.createGame(game);
    GameData testGame = dao.getGame(1);
    assertEquals(testGame.gameID(), game.gameID());

  }
  @Test
  void getGameFail() throws UnauthorizedException, DataAccessException {
    assertNull(dao.getGame(1));
  }
  @Test
  void listGames() throws DataAccessException, UnauthorizedException, BadRequestException {
    dao.createGame(game);
    assertEquals(1, dao.listGames().size());
  }
  @Test
  void listGamesFail() throws DataAccessException {
    assertEquals(0,dao.listGames().size());
  }
  @Test
  void createGame() throws UnauthorizedException, BadRequestException, DataAccessException {
    dao.createAuth(auth);
    gameService.createGame(game, auth.authToken());
    assertEquals(1, dao.getSize("game"));
  }
  @Test
  void createGameFail(){
    assertThrows(UnauthorizedException.class, () -> {
      gameService.createGame(game, auth.authToken());
    });
    GameData badGame = new GameData(1, null,null,null,null);
    assertThrows(BadRequestException.class, () -> {
      gameService.createGame(badGame, auth.authToken());
    });
  }
  @Test
  void testJoinGame() throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
    dao.createAuth(auth);
    GameData goodGame = new GameData(1, null,null, "chess", null);
    goodGame = dao.createGame(goodGame);
    gameService.joinGame(auth.authToken(), "WHITE", goodGame.gameID());
    assertEquals("username", dao.getGame(goodGame.gameID()).whiteUsername());
  }
  @Test
  void testJoinGameFail() throws DataAccessException {
    assertThrows(UnauthorizedException.class, () -> {
      gameService.joinGame(auth.authToken(), "WHITE", 1);
    });
  }
}


