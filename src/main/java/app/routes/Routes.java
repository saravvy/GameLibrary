package app.routes;

import app.controllers.*;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final GameController gameController;
    private final UserController userController;
    private final ReviewController reviewController;
    private final LibraryController libraryController;
    private final WishlistController wishlistController;
    private final SecurityController securityController;

    public Routes(GameController gameController, UserController userController, ReviewController reviewController, LibraryController libraryController, WishlistController wishlistController, SecurityController securityController) {
        this.gameController = gameController;
        this.userController = userController;
        this.reviewController = reviewController;
        this.libraryController = libraryController;
        this.wishlistController = wishlistController;
        this.securityController = securityController;
    }

    public EndpointGroup getRoutes(){
        return ()->{
            path("/game",()->{
                get("",gameController::getAll, AppRouteRole.ANYONE);
                get("/{id}",gameController::getById, AppRouteRole.ANYONE);
                post("/",gameController::create, AppRouteRole.ADMIN);
                delete("/{id}", gameController ::delete, AppRouteRole.ADMIN);

            });  path("/users", () -> {
                get("", userController::getAll);
                get("/{id}", userController::getById);
                post("", userController::create);
                delete("/{id}", userController::delete, AppRouteRole.ADMIN);
            });

            path("/reviews", () -> {
                get("", reviewController::getAll);
                get("/{id}", reviewController::getById);
                post("/{gameId}", reviewController::create);
                delete("/{id}", reviewController::delete, AppRouteRole.ADMIN);
            });

            path("/library", () -> {
                get("/{userId}", libraryController::getGames);
                post("/{userId}/games/{gameId}", libraryController::addGame);
                delete("/{userId}/games/{gameId}", libraryController::removeGame, AppRouteRole.ADMIN);
            });

            path("/wishlist", () -> {
                get("/{userId}", wishlistController::getGames);
                post("/{userId}/games/{gameId}", wishlistController::addGame);
                delete("/{userId}/games/{gameId}", wishlistController::removeGame, AppRouteRole.ADMIN);
            });

            path("/auth", ()->{

                post("/login", securityController::login, AppRouteRole.ANYONE );
                post("/register", securityController::register, AppRouteRole.ANYONE);
            });

        };

    }

}
