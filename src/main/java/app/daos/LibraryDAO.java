package app.daos;

import app.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LibraryDAO implements IDAO<Library>, ILibraryDAO {

    private EntityManagerFactory emf;

    public LibraryDAO(EntityManagerFactory _emf) {
        this.emf = _emf;
    }

    @Override
    public Library create(Library library) {
        try (EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();
            em.persist(library);
            em.getTransaction().commit();
            return library;
        }
    }

    @Override
    public void remove(Library library) {
        try (EntityManager em = emf.createEntityManager()) {
            Library found = em.find(Library.class, library.getId());
            if (found == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
            System.out.println("Removal successfull!:)");

        }
    }

    @Override
    public Library update(Library library) {
        try (EntityManager em = emf.createEntityManager()) {
            Library foundlibrary = em.find(Library.class, library.getId());
            if (foundlibrary == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            Library library1 = em.merge(library);
            em.getTransaction().commit();
            return library1;
        }
    }

    @Override
    public Library getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Library foundLibrary = em.find(Library.class, id);
            if (foundLibrary == null)
                throw new EntityNotFoundException("No entity found with id: " + id);
            return foundLibrary;
        }
    }

    @Override
    public Set<Library> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return new HashSet<>(em.createQuery("SELECT e FROM Library e", Library.class).getResultList());
        }
    }

    @Override
    public Library getLibraryByUser(User user) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Library> query = em.createQuery("SELECT s FROM Library s WHERE s.user.id = :userId", Library.class);
            query.setParameter("userId", user.getId());
            return query.getSingleResult();

        }
    }

    @Override
    public GameInLibrary addGame(Library library, Game game) {

        if (containsGame(library, game)) {
            throw new IllegalStateException("Game already in library");
        }

        GameInLibrary game1 = new GameInLibrary();
        game1.setLibrary(library);
        game1.setGame(game);
        game1.setDateAdded(LocalDate.now());

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(game1);
            em.getTransaction().commit();
            return game1;
        }


    }

    @Override
    public void removeGame(Library library, Game game) {
        if (!containsGame(library, game)) {
            throw new IllegalStateException("Game isnt in library");
        }

        try (EntityManager em = emf.createEntityManager()) {
            GameInLibrary game1 = em.createQuery(
                            "SELECT g FROM GameInLibrary g WHERE g.library.id = :libraryId AND g.game.id = :gameId",
                            GameInLibrary.class)
                    .setParameter("libraryId", library.getId())
                    .setParameter("gameId", game.getId())
                    .getSingleResult();

            em.getTransaction().begin();
            em.remove(em.merge(game1));
            em.getTransaction().commit();

        }

    }

    @Override
    public List<GameInLibrary> getGames(Library library) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT e FROM GameInLibrary e WHERE e.library.id = :libraryId", GameInLibrary.class)
                    .setParameter("libraryId", library.getId())
                    .getResultList();
        }
    }

    @Override
    public boolean containsGame(Library library, Game game) {
        try (EntityManager em = emf.createEntityManager()) {
            List<GameInLibrary> games = library.getGames();
            for (GameInLibrary g : games) {
                if (g.getGame().equals(game)) {
                    return true;
                }

            }
        } return false;
    }
}