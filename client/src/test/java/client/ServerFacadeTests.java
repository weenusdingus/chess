package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() throws InterruptedException {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        Thread.sleep(1000);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws IOException {
        URL url = new URL("http://localhost:" + server.getPort() + "/db");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Database cleared successfully.");
        } else {
            System.out.println("Failed to clear the database. Response code: " + responseCode);
        }
    }

    @Test
    public void testRegisterPass() throws IOException {
        String response = facade.register("newUser", "password123", "newuser@example.com");
        assertNotNull(response);
        assertTrue(response.contains("authToken"));
    }

    @Test
    public void testRegisterFail() {
        assertThrows(IOException.class, () -> {
            facade.register("", "", "");
        });
    }

    @Test
    public void testLoginPass() throws IOException {
        facade.register("user", "password", "user@gmail.com");

        String response = facade.login("user", "password");
        assertNotNull(response, "Response should not be null");
        assertTrue(response.contains("authToken"), "Response should contain 'authToken'");
    }

    @Test
    public void testLoginFail() {
        assertThrows(IOException.class, () -> {
            facade.login("bad", "bad");
        });
    }

    @Test
    public void testLogoutPass() throws IOException {
        facade.register("logoutUser", "password123", "logoutuser@example.com");
        String loginResponse = facade.login("logoutUser", "password123");

        JsonObject jsonObject = JsonParser.parseString(loginResponse).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();

        assertDoesNotThrow(() -> facade.logout(authToken));
    }

    @Test
    public void testLogoutFail() {
        assertThrows(IOException.class, () -> {
            facade.logout("invalidToken");
        });
    }

    @Test
    public void testCreateGamePass() throws IOException {
        facade.register("gameUser", "password123", "gameuser@example.com");
        String loginResponse = facade.login("gameUser", "password123");

        JsonObject jsonObject = JsonParser.parseString(loginResponse).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();

        String response = facade.createGame(authToken, "TestGame");
        assertNotNull(response);
        assertTrue(response.contains("gameID"));
    }

    @Test
    public void testCreateGameFail() {
        assertThrows(IOException.class, () -> {
            facade.createGame("invalidToken", "TestGame");
        });
    }

    @Test
    public void testListGamePass() throws IOException {
        facade.register("listUser", "password123", "listuser@example.com");
        String loginResponse = facade.login("listUser", "password123");

        JsonObject jsonObject = JsonParser.parseString(loginResponse).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();

        facade.createGame(authToken, "TestGame");

        String listResponse = facade.listGames(authToken);
        assertNotNull(listResponse);
        assertTrue(listResponse.contains("TestGame"));
    }

    @Test
    public void testListGamesFail() {
        assertThrows(IOException.class, () -> {
            facade.listGames("invalidToken");
        });
    }

    @Test
    public void testJoinGamePass() throws IOException {
        facade.register("joinUser", "password123", "joinuser@example.com");
        String loginResponse = facade.login("joinUser", "password123");

        JsonObject jsonObject = JsonParser.parseString(loginResponse).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();

        String gameResponse = facade.createGame(authToken, "TestGame");
        JsonObject gameJson = JsonParser.parseString(gameResponse).getAsJsonObject();
        int gameId = gameJson.get("gameID").getAsInt();

        String joinResponse = facade.joinGame(authToken, gameId, "WHITE");
        assertNotNull(joinResponse);
    }

    @Test
    public void testJoinGameFail() {
        assertThrows(IOException.class, () -> {
            facade.joinGame("invalidToken", 99999, "WHITE");
        });
    }
}


