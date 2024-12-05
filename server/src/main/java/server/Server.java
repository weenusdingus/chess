package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import model.*;
import server.websocket.WebsocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.serviceexceptions.AlreadyTakenException;
import service.serviceexceptions.BadRequestException;
import service.serviceexceptions.UnauthorizedException;
import spark.*;

import java.util.Collection;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private DataAccess dataaccess;


    public Server() {
        try {
            dataaccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            System.err.println("Falling back to memory");
            dataaccess = new MemoryDataAccess();
        }
        userService = new UserService(dataaccess);
        gameService = new GameService(dataaccess);
        clearService = new ClearService(dataaccess);
    }
    public int getPort() {
        return Spark.port();
    }

    public int run(int desiredPort){
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        WebsocketHandler webSocketHandler = new WebsocketHandler(dataaccess);

        Spark.webSocket("/ws", webSocketHandler);
        Spark.delete("/db", this::clearApplication);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object clearApplication(Request req, Response res) throws DataAccessException {
        try {
            clearService.clear();
            res.status(200);
            return "";
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }
    public Object register(Request req, Response res) throws AlreadyTakenException, DataAccessException, BadRequestException {
        try {
            UserData user=new Gson().fromJson(req.body(), UserData.class);
            AuthData auth = userService.register(user);
            res.status(200);
            return new Gson().toJson(auth);
        } catch(BadRequestException e) {
            res.status(400);
            return new Gson().toJson(new ErrorMessage("Error: bad request"));
        } catch (AlreadyTakenException e){
            res.status(403);
            return new Gson().toJson(new ErrorMessage("Error: already taken"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    public Object login(Request req, Response res) {
        try {
            UserData user = new Gson().fromJson(req.body(), UserData.class);
            AuthData auth = userService.login(user);
            res.status(200);
            return new Gson().toJson(auth);
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    public Object logout(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            userService.logout(authToken);
            res.status(200);
            return "";
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }
    public Object listGames(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            Collection<GameData> games = gameService.listGames(authToken);
            ListGameData listGames = new ListGameData(games);
            res.status(200);
            return new Gson().toJson(listGames);
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }
    public Object createGame(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            GameData game = new Gson().fromJson(req.body(), GameData.class);
            GameData newGame = gameService.createGame(game, authToken);
            res.status(200);
            return new Gson().toJson(newGame);
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        } catch (BadRequestException e) {
            res.status(400);
            return new Gson().toJson(new ErrorMessage("Error: bad request"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }
    public Object joinGame(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            JoinGameData joinGame = new Gson().fromJson(req.body(), JoinGameData.class);
            gameService.joinGame(authToken, joinGame.playerColor(), joinGame.gameID());
            res.status(200);
            return "{}";
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
        } catch (AlreadyTakenException e) {
            res.status(403);
            return new Gson().toJson(new ErrorMessage("Error: already taken"));
        } catch (BadRequestException e) {
            res.status(400);
            return new Gson().toJson(new ErrorMessage("Error: bad request"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }
    record ErrorMessage (String message){}

}
