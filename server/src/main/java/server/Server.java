package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.serviceExceptions.AlreadyTakenException;
import service.serviceExceptions.BadRequestException;
import service.serviceExceptions.UnauthorizedException;
import spark.*;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private final DataAccess dataaccess;

    public Server() {
        dataaccess = new MemoryDataAccess();
        userService = new UserService(dataaccess);
        gameService = new GameService(dataaccess);
        clearService = new ClearService(dataaccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearApplication);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
//        Spark.delete("/session", this::logout);
//        Spark.get("/game", this::listGames);
//        Spark.post("/game", this::createGame);
//        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    record ErrorMessage (String message){}
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
}
