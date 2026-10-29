package app.controllers;

import app.daos.GameDAO;
import app.entities.Game;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;

import java.util.Map;
import java.util.List;
import java.util.Set;

public class GameController {

    private final GameDAO gameDAO;

    public GameController(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public void getAll(Context ctx) {
        try{
            Set<Game> games = gameDAO.getAll();
            ctx.status(HttpStatus.OK).json(games);
    } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of(
                    "message", "Could not fetch games"
            ));

    } }

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

        Game game = ctx.bodyValidator(Game.class)
                .check(g -> g.getName() != null && !g.getName().isBlank(), "Name is required")
                .check(g -> g.getDeveloper() != null && !g.getDeveloper().isBlank(), "Developer is required")
                .check(g -> g.getDateReleased() != null, "Release date is required")
                .check(g -> g.getDescription() != null && !g.getDescription().isBlank(), "Description is required")
                .get();

        try {
            Game created = gameDAO.create(game);

            ctx.status(HttpStatus.CREATED).json(created);

        } catch (Exception e){
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of(
                    "message", "Could not create game"
            ));

        }
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