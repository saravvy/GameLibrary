package app.daos;

import app.entities.Game;
import app.entities.GameInLibrary;
import app.entities.Library;
import app.entities.User;

import java.util.List;

public interface ILibraryDAO {

    Library getLibraryByUser(User user);

    GameInLibrary addGame(Library library, Game game);

    void removeGame(Library library, Game game);

    List<GameInLibrary> getGames(Library library);

    boolean containsGame(Library library, Game game);
}