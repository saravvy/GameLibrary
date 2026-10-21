package app.controllers;

import app.daos.RevivewDAO;
import app.entities.Review;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;

import java.util.Map;

public class ReviewController {

    private final RevivewDAO reviewDAO;

    public ReviewController(RevivewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
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
        Review review = ctx.bodyAsClass(Review.class);
        Review created = reviewDAO.create(review);

        ctx.status(HttpStatus.CREATED).json(created);
    }

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