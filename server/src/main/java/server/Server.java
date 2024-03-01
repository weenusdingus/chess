package server;

import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.serviceExceptions.AlreadyTakenException;
import service.serviceExceptions.BadRequestException;
import service.serviceExceptions.UnauthorizedException;
import spark.*;

import java.util.Collection;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private final DataAccess dataAccess;

    public Server() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        clearService = new ClearService(dataAccess);
    }

    public int run(int desiredPort) throws DataAccessException {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearApplication);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);

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
            return new Gson().toJson(new DataAccessException("Error: " + e.getMessage()));
        }
    }
    public Object register(Request req, Response res) throws AlreadyTakenException, DataAccessException, BadRequestException {
        try {
            UserData user=new Gson().fromJson(req.body(), UserData.class);
            userService.register(user);
            res.status(200);
            return new Gson().toJson(user);
        } catch(BadRequestException e) {
            res.status(400);
            return new Gson().toJson(new BadRequestException("Error: bad request"));
        } catch (AlreadyTakenException e){
            res.status(403);
            return new Gson().toJson(new AlreadyTakenException("Error: already taken"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new Exception("Error: " + e.getMessage()));
        }

    }
    public Object login(Request req, Response res) throws UnauthorizedException{
        try {
            UserData user = new Gson().fromJson(req.body(), UserData.class);
            AuthData auth = new Gson().fromJson(req.body(), AuthData.class);
            userService.login(user);
            res.status(200);
            return new Gson().toJson(auth);
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new UnauthorizedException("Error: unauthorized"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new Exception("Error: " + e.getMessage()));
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
            return new Gson().toJson(new UnauthorizedException("Error: unauthorized"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new Exception("Error: " + e.getMessage()));
        }
    }
    public Object listGames(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            Collection<GameData> games = gameService.listGames(authToken);
            res.status(200);
            return new Gson().toJson(games);
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new UnauthorizedException("Error: unauthorized"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new Exception("Error: " + e.getMessage()));
        }
    }
    public Object createGame(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            GameData game = new Gson().fromJson(req.body(), GameData.class);
            gameService.createGame(game, authToken);
            res.status(200);
            return new Gson().toJson(game.gameId());
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new UnauthorizedException("Error: unauthorized"));
        } catch (BadRequestException e) {
            res.status(400);
            return new Gson().toJson(new BadRequestException("Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new Exception("Error: " + e.getMessage()));
        }
    }
    public Object joinGame(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            GameData game = new Gson().fromJson(req.body(), GameData.class);
            gameService.joinGame(authToken, null, game.gameId());
            res.status(200);
            return "";
        } catch (UnauthorizedException e) {
            res.status(401);
            return new Gson().toJson(new UnauthorizedException("Error: unauthorized"));
        } catch (AlreadyTakenException e) {
            res.status(403);
            return new Gson().toJson(new AlreadyTakenException("Error: " + e.getMessage()));
        } catch (BadRequestException e) {
            res.status(400);
            return new Gson().toJson(new BadRequestException("Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(new Exception("Error: " + e.getMessage()));
        }
    }

}
