package app.service;

import app.config.HTTPClient;
import app.daos.GameDAO;
import app.dtos.GameDTO;
import app.dtos.GameDetailDTO;
import app.dtos.ResultDTO;
import app.entities.Game;

import java.util.ArrayList;
import java.util.List;

public class GamePersist {
    private HTTPClient httpClient;
    private GameDAO gameDAO;

    public GamePersist(HTTPClient httpClient, GameDAO gameDAO) {
        this.httpClient = httpClient;
        this.gameDAO = gameDAO;
    }

    public List<Game> importGames() throws Exception {
        ResultDTO result = httpClient.getGames();

        List<Game> savedGames = new ArrayList<>();
        for (GameDTO gameDTO : result.games()) {
            GameDetailDTO detail = httpClient.getGameDetails(gameDTO.id());
            Game game = Game.fromDTO(detail);
            Game saved = gameDAO.create(game);
            savedGames.add(saved);
        }
        return savedGames;
    }
}
