package app;

import app.config.AplicationConfig;
import app.config.Fetcher;
import app.config.HibernateConfig;
import app.controllers.*;
import app.daos.*;
import app.routes.Routes;
import app.service.GamePersist;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {


        Fetcher fetcher = new Fetcher();
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        GameDAO gameDAO = new GameDAO(emf);
        RevivewDAO revivewDAO = new RevivewDAO(emf);
        LibraryDAO libraryDAO = new LibraryDAO(emf);
        SecurityDAO securityDAO = new SecurityDAO(emf);
        UserDAO userDAO = new UserDAO(emf);
        WishlistDAO wishlistDAO = new WishlistDAO(emf);

        GameController gameController = new GameController(gameDAO);
        ReviewController reviewController =new ReviewController(revivewDAO);
        LibraryController libraryController = new LibraryController(libraryDAO,userDAO,gameDAO);
        SecurityController securityController = new SecurityController(securityDAO,userDAO);
        UserController userController = new UserController(userDAO);
        WishlistController wishlistController = new WishlistController(wishlistDAO,userDAO,gameDAO);


        Routes routes = new Routes(gameController,userController,reviewController,libraryController,wishlistController,securityController);
        AplicationConfig aplicationConfig = new AplicationConfig(routes);


        aplicationConfig.startServer(7070);

     //   GamePersist gamePersist = new GamePersist(fetcher, gameDAO);

      //  List<Game> importedGames = gamePersist.importGames();

       // System.out.println(importedGames);


        /*
       ResultDTO result = httpClient.getGames();
       for (GameDTO game : result.games()) {
           System.out.println(game);
       }

           ResultDTO resultPage2 = httpClient.getGames(result.next());
           for (GameDTO game : resultPage2.games()){
               System.out.println(game);


       } */
     /*   List<Game> games = httpClient.fetchGamesWithDetails();
        for (Game game : games){
            System.out.println(game); }
       */



        } }


