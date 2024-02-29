package serviceTests;
import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.UserService;
import service.serviceExceptions.UsernameException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
  static final UserService service = new UserService(new MemoryDataAccess());
  static final ClearService clearService = new ClearService(new MemoryDataAccess());
  ChessGame chessGame = new ChessGame();
  UserData user = new UserData("username", "password", "email@email.com");
  AuthData auth = new AuthData(UUID.randomUUID().toString(), "username");
  GameData game = new GameData(12345, "wusername", "busername", "chess", chessGame);

  MemoryDataAccess dao = new MemoryDataAccess();

  @BeforeEach
  void setup() throws DataAccessException{
    clearService.clear();
  }


  @Test
  void testClear() throws DataAccessException, UsernameException {
    dao.createUser(user);
    dao.createAuth(auth);
    dao.createGame(game);
    clearService.clear();

    assertEquals(0, dao.getUsers().size());
    assertEquals(0, dao.getAuths().size());
    assertEquals(0, dao.getGames().size());
  }
  @Test
  void testRegister() throws DataAccessException, UsernameException{
    auth = service.register(user);
    assertNotNull(auth);
    assertEquals(user.username(), auth.username());
    assertThrows(UsernameException.class, () -> {
      service.register(user);
    });

  }
  @Test
  void testLogin() throws DataAccessException, UsernameException{
    dao.createUser(user);
    UserData badUser = new UserData("badUsername", "password", "email@email.com");
    auth = service.login(user);
    assertNotNull(auth);
    assertEquals(user.username(), auth.username());
    assertThrows(UsernameException.class, () -> {
      service.login(badUser);
    });
  }
}

