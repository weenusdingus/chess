package serviceTests;
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

public class ServiceTests {
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
  void testRegister() throws DataAccessException, AlreadyTakenException, BadRequestException {
    auth = service.register(user);
    assertNotNull(auth);
    assertEquals(user.username(), auth.username());
  }
  @Test
  void testRegisterFail() throws DataAccessException, AlreadyTakenException, BadRequestException {
    auth = service.register(user);
    assertThrows(AlreadyTakenException.class, () -> {
      service.register(user);
    });
  }
  @Test
  void testLogin() throws DataAccessException, AlreadyTakenException, UnauthorizedException {
    dao.createUser(user);
    UserData newUser = dao.getUser(user.username());
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    assertTrue(encoder.matches(user.password(), newUser.password()));
    auth = service.login(user);
    assertNotNull(auth);
    assertEquals(user.username(), auth.username());
  }
  @Test
  void testLoginFail() throws DataAccessException, AlreadyTakenException {
    dao.createUser(user);
    UserData badUser = new UserData("badUsername", "password", "email@email.com");
    assertThrows(UnauthorizedException.class, () -> {
      service.login(badUser);
    });
  }
  @Test
  void testLogout() throws DataAccessException, UnauthorizedException, AlreadyTakenException {
    dao.createUser(user);
    auth = service.login(user);
    service.logout(auth.authToken());
    assertNull(dao.getAuth(auth.authToken()));
  }
  @Test
  void testLogoutFail() throws UnauthorizedException{
    assertThrows(UnauthorizedException.class, () -> {
      service.logout(auth.authToken());
    });
  }
  @Test
  void testListGames() throws DataAccessException, UnauthorizedException {
    dao.createAuth(auth);
    dao.createGame(game);
    assertEquals(1, gameService.listGames(auth.authToken()).size());
  }
  @Test
  void testListGamesFail(){
    assertThrows(UnauthorizedException.class, () -> {
      gameService.listGames(auth.authToken());
    });
  }
  @Test
  void testCreateGame() throws UnauthorizedException, BadRequestException, DataAccessException {
    dao.createAuth(auth);
    gameService.createGame(game, auth.authToken());
    assertEquals(1, dao.getSize("game"));
  }
  @Test
  void testCreateGameFail(){
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

