package app;

import app.config.HTTPClient;
import app.dtos.GameDTO;
import app.dtos.ResultDTO;
import app.entities.Game;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {


        HTTPClient httpClient = new HTTPClient();

/*
       ResultDTO result = httpClient.getGames();
       for (GameDTO game : result.games()) {
           System.out.println(game);
       }

           ResultDTO resultPage2 = httpClient.getGames(result.next());
           for (GameDTO game : resultPage2.games()){
               System.out.println(game);


       } */
        List<Game> games = httpClient.fetchGamesWithDetails();
        for (Game game : games){
            System.out.println(game);
        }

        } }


