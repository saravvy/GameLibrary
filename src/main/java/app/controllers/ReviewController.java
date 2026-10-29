package app.controllers;

import app.daos.GameDAO;
import app.daos.RevivewDAO;
import app.daos.UserDAO;
import app.entities.Game;
import app.entities.Review;
import app.entities.User;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;

import java.util.Map;

public class ReviewController {

    private final RevivewDAO reviewDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public ReviewController(RevivewDAO reviewDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.reviewDAO = reviewDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void getAll(Context ctx) {
        ctx.json(reviewDAO.getAll());
    }

    public void getById(Context ctx) {
        int id = getId(ctx);

        try {
            Review review = reviewDAO.getById(id);
            ctx.status(HttpStatus.OK).json(review);
        } catch (EntityNotFoundException e) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                    "message", "No review found",
                    "id", id
            ));
        }
    }

    public void create(Context ctx) {

        int gameId = Integer.parseInt(ctx.pathParam("gameId"));
        Review review = ctx.bodyValidator(Review.class)
                .check(r -> r.getStars() >= 1 && r.getStars() <= 5, "Stars must be between 1 and 5")
                .check(r -> r.getDescription() != null && !r.getDescription().isBlank(), "Description is required")
                .check(r -> r.getAdded() != null, "Added date is required")
            .get();
        try {
            int userId = review.getUser().getId();

            User user = userDAO.getById(userId);
            Game game = gameDAO.getById(gameId);

            if (reviewDAO.existsByUserAndGame(userId, gameId)) {
                ctx.status(HttpStatus.CONFLICT).json(Map.of(
                        "message", "You have already reviewed this game"
                ));
                return;
            }

            review.setUser(user);
            review.setGame(game);
        Review created = reviewDAO.create(review);



        ctx.status(HttpStatus.CREATED).json(created);
    } catch (Exception e) {
            e.printStackTrace();
        ctx.status(HttpStatus.BAD_REQUEST).json(Map.of(
                "message", "Could not create review"
        ));
    } }

    public void delete(Context ctx) {
        int id = getId(ctx);

        try {
            Review review = reviewDAO.getById(id);
            boolean deleted = reviewDAO.remove(review);

            if (deleted) {
                ctx.status(HttpStatus.OK).json(Map.of(
                        "message", "Review deleted"
                ));
            } else {
                ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                        "message", "No review found",
                        "id", id
                ));
            }
        } catch (EntityNotFoundException e) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of(
                    "message", "No review found",
                    "id", id
            ));
        }
    }

    private int getId(Context ctx) {
        return ctx.pathParamAsClass("id", Integer.class).get();
    }
}