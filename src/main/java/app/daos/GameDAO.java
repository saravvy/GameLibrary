package app.daos;

import app.entities.Game;
import app.entities.Review;

import java.util.List;

public class GameDAO implements IDAO<Game>{
    @Override
    public Game create(Game game) {
        return null;
    }

    @Override
    public void remove(Game game) {

    }

    @Override
    public Game update(Game game) {
        return null;
    }

    @Override
    public Game getById(int id) {
        return null;
    }

    @Override
    public List<Game> getAll(int id) {
        return List.of();
    }
}
