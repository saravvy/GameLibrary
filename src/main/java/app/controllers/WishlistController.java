package app.controllers;

import app.daos.GameDAO;
import app.daos.UserDAO;
import app.daos.WishlistDAO;
import app.entities.Game;
import app.entities.GameInWishlist;
import app.entities.User;
import app.entities.Wishlist;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

import java.util.Map;

public class WishlistController {

    private final WishlistDAO wishlistDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public WishlistController(
            WishlistDAO wishlistDAO,
            UserDAO userDAO,
            GameDAO gameDAO
    ) {
        this.wishlistDAO = wishlistDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public void getGames(Context ctx) {
        int userId = getUserId(ctx);

        try {
            User user = userDAO.getById(userId);
            Wishlist wishlist = wishlistDAO.getWishlistByUser(user);

            ctx.status(HttpStatus.OK)
                    .json(wishlistDAO.getGames(wishlist));

        } catch (EntityNotFoundException | NoResultException e) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                    "message", "No wishlist found for user",
                    "userId", userId
            ));
        }
    }

    public void addGame(Context ctx) {
        int userId = getUserId(ctx);
        int gameId = getGameId(ctx);

        try {
            User user = userDAO.getById(userId);
            Game game = gameDAO.getById(gameId);
            Wishlist wishlist = wishlistDAO.getWishlistByUser(user);

            GameInWishlist added = wishlistDAO.addGame(wishlist, game);

            ctx.status(HttpStatus.CREATED).json(added);

        } catch (EntityNotFoundException | NoResultException e) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                    "message", "User, game or wishlist not found"
            ));

        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT).json(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    public void removeGame(Context ctx) {
        int userId = getUserId(ctx);
        int gameId = getGameId(ctx);

        try {
            User user = userDAO.getById(userId);
            Game game = gameDAO.getById(gameId);
            Wishlist wishlist = wishlistDAO.getWishlistByUser(user);

            boolean removed = wishlistDAO.removeGame(wishlist, game);

            if (removed) {
                ctx.status(HttpStatus.OK).json(Map.of(
                        "message", "Game removed from wishlist"
                ));
            } else {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                        "message", "Game not found in wishlist"
                ));
            }

        } catch (EntityNotFoundException | NoResultException e) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                    "message", "User, game or wishlist not found"
            ));
        }
    }

    private int getUserId(Context ctx) {
        return ctx.pathParamAsClass("userId", Integer.class).get();
    }

    private int getGameId(Context ctx) {
        return ctx.pathParamAsClass("gameId", Integer.class).get();
    }
}