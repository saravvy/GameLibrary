package app.service;

import app.config.HTTPClient;
import app.daos.GameDAO;
import app.dtos.GameDTO;
import app.dtos.GameDetailDTO;
import app.dtos.ResultDTO;
import app.entities.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GamePersist {
    private HTTPClient httpClient;
    private GameDAO gameDAO;

    public GamePersist(HTTPClient httpClient, GameDAO gameDAO) {
        this.httpClient = httpClient;
        this.gameDAO = gameDAO;
    }

    private List<GameDTO> fetchAllGameDTOs() throws Exception {


        ResultDTO result = httpClient.getGames();
        List<GameDTO> allGames = new ArrayList<>(result.games());
        int maxPages = 3;
        int page = 1;
        while (result.next() != null && page < maxPages) {
            result = httpClient.getGames(result.next());
            allGames.addAll(result.games());
            page++;
        }

        return allGames;
    }


    public List<Game> importGames() throws Exception {
        List<GameDTO> gameDTOs = fetchAllGameDTOs();


        List<Game> savedGames = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<Game>> futures = new ArrayList<>();


        for (GameDTO gameDTO : gameDTOs) {
            Callable<Game> task = () -> {
                GameDetailDTO detail = httpClient.getGameDetails(gameDTO.id());
                Game game = Game.fromDTO(detail);
                return game;
            };
            futures.add(executor.submit(task));

        }
        List<Game> games = new ArrayList<>();
        for (Future<Game> future : futures) {
            try {
                games.add(future.get());
            } catch (Exception e) {
                System.out.println( e.getMessage());
            }
        }

        executor.shutdown();


        for(Game game : games) {
        savedGames.add(gameDAO.create(game));
    }

        return savedGames;
}
}