
package app.controllers;

import app.daos.GameDAO;
import app.daos.LibraryDAO;
import app.daos.UserDAO;
import app.entities.Game;
import app.entities.GameInLibrary;
import app.entities.Library;
import app.entities.User;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Map;

public class LibraryController {

    private final LibraryDAO libraryDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public LibraryController(
            LibraryDAO libraryDAO,
            UserDAO userDAO,
            GameDAO gameDAO) {

        this.libraryDAO = libraryDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public void getGames(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("userId"));

            User user = userDAO.getById(userId);
            Library library = libraryDAO.getLibraryByUser(user);

            List<Map<String, Object>> games = libraryDAO.getGames(library)
                    .stream()
                    .map(entry -> Map.<String, Object>of(
                            "id", entry.getId(),
                            "gameId", entry.getGame().getId(),
                            "dateAdded", entry.getDateAdded().toString()
                    ))
                    .toList();

            ctx.json(games);

        } catch (EntityNotFoundException | NoResultException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("message", e.getMessage()));
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("message", "Invalid user ID"));
        }
    }

    public void addGame(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            int gameId = Integer.parseInt(ctx.pathParam("gameId"));

            User user = userDAO.getById(userId);
            Game game = gameDAO.getById(gameId);
            Library library = libraryDAO.getLibraryByUser(user);

            GameInLibrary added = libraryDAO.addGame(library, game);

            ctx.status(HttpStatus.CREATED).json(Map.of(
                    "id", added.getId(),
                    "gameId", added.getGame().getId(),
                    "dateAdded", added.getDateAdded().toString()
            ));

        } catch (EntityNotFoundException | NoResultException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT)
                    .json(Map.of("message", e.getMessage()));
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("message", "Invalid user ID or game ID"));
        }
    }

    public void removeGame(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            int gameId = Integer.parseInt(ctx.pathParam("gameId"));

            User user = userDAO.getById(userId);
            Game game = gameDAO.getById(gameId);
            Library library = libraryDAO.getLibraryByUser(user);

            boolean deleted = libraryDAO.removeGame(library, game);

            if (deleted) {
                ctx.status(HttpStatus.NO_CONTENT);
            } else {
                ctx.status(HttpStatus.NOT_FOUND)
                        .json(Map.of(
                                "message",
                                "Game not found in library"
                        ));
            }

        } catch (EntityNotFoundException | NoResultException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("message", e.getMessage()));
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("message", "Invalid user ID or game ID"));
        }
    }
}
