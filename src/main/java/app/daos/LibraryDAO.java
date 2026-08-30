package app.daos;

import app.entities.Library;
import app.entities.Review;

import java.util.List;

public class LibraryDAO implements IDAO<Library>{
    @Override
    public Library create(Library library) {
        return null;
    }

    @Override
    public void remove(Library library) {

    }

    @Override
    public Library update(Library library) {
        return null;
    }

    @Override
    public Library getById(int id) {
        return null;
    }

    @Override
    public List<Library> getAll(int id) {
        return List.of();
    }
}
