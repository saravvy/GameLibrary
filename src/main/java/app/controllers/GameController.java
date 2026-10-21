package app.controllers;

import app.daos.GameDAO;
import app.entities.Game;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;

import java.util.Map;
import java.util.List;

public class GameController {

    private final GameDAO gameDAO;

    public GameController(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public void getAll(Context ctx) {
        ctx.json(gameDAO.getAll());
    }

    public void getById(Context ctx) {
        int id = getId(ctx);

        try {
            Game game = gameDAO.getById(id);
            ctx.status(HttpStatus.OK).json(game);
        } catch (EntityNotFoundException e) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                    "message", "No game found",
                    "id", id
            ));
        }
    }

    public void create(Context ctx) {
        Game game = ctx.bodyAsClass(Game.class);
        Game created = gameDAO.create(game);

        ctx.status(HttpStatus.CREATED).json(created);
    }

    public void delete(Context ctx) {
        int id = getId(ctx);

        try {
            Game game = gameDAO.getById(id);
            boolean deleted = gameDAO.remove(game);

            if (deleted) {
                ctx.status(HttpStatus.OK).json(Map.of(
                        "message", "Game deleted"
                ));
            } else {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                        "message", "No game found",
                        "id", id
                ));
            }
        } catch (EntityNotFoundException e) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                    "message", "No game found",
                    "id", id
            ));
        }
    }

    private int getId(Context ctx) {
        return ctx.pathParamAsClass("id", Integer.class).get();
    }
}