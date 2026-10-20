package app;

import app.config.AplicationConfig;
import app.config.Fetcher;
import app.config.HibernateConfig;
import app.daos.GameDAO;
import app.routes.Routes;
import app.service.GamePersist;
import jakarta.persistence.EntityManagerFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {


        Fetcher fetcher = new Fetcher();
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        GameDAO gameDAO = new GameDAO(emf);


        Routes routes = new Routes();
        AplicationConfig aplicationConfig = new AplicationConfig(routes);

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


