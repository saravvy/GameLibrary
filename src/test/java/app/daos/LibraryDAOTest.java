package app.daos;

import app.config.HibernateConfig;
import app.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LibraryDAOTest {

    private EntityManagerFactory emf;
    private LibraryDAO libraryDAO;
    private User user;
    private Library library;
    private Game game;
    private Game game2;

    @BeforeEach
    void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        libraryDAO = new LibraryDAO(emf);

        user = new User("testuser", "password");
        user.setRole(Role.USER);

        library = new Library(user);

        game = new Game(
                0,
                "Minecraft",
                "Mojang",
                LocalDate.of(2011, 11, 18),
                "Sandbox game"
        );

        game2 = new Game(
                0,
                "Terraria",
                "Re-Logic",
                LocalDate.of(2011, 5, 16),
                "Adventure game"
        );

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.persist(user);
            em.persist(library);
            em.persist(game);
            em.persist(game2);

            em.getTransaction().commit();
        }
    }


    @AfterEach
    void tearDown() {
        HibernateConfig.shutdownTestEmf();
    }

    @Test
    void createLibrary() {
        User newUser = new User("newuser", "password");
        newUser.setRole(Role.USER);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(newUser);
            em.getTransaction().commit();
        }

        Library newLibrary = new Library(newUser);

        Library result = libraryDAO.create(newLibrary);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(libraryDAO.getById(result.getId()));
    }


    @Test
    void getByIdReturnsLibrary() {
        Library result = libraryDAO.getById(library.getId());

        assertNotNull(result);
        assertEquals(library.getId(), result.getId());
    }

    @Test
    void getAllReturnsLibraries() {
        Set<Library> result = libraryDAO.getAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(
                result.stream().anyMatch(
                        l -> l.getId() == library.getId()
                )
        );
    }

    @Test
    void getLibraryByUserReturnsLibrary() {
        Library result = libraryDAO.getLibraryByUser(user);

        assertNotNull(result);
        assertEquals(library.getId(), result.getId());
    }


    @Test
    void updateLibrary() {
        User newUser = new User("updateduser", "password");
        newUser.setRole(Role.USER);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(newUser);
            em.getTransaction().commit();
        }

        library.setUser(newUser);

        Library result = libraryDAO.update(library);

        assertNotNull(result);
        assertEquals(library.getId(), result.getId());

        Library updatedLibrary =
                libraryDAO.getLibraryByUser(newUser);

        assertNotNull(updatedLibrary);
        assertEquals(library.getId(), updatedLibrary.getId());
    }


    @Test
    void removeLibrary() {
        boolean result = libraryDAO.remove(library);

        assertTrue(result);

        assertThrows(
                EntityNotFoundException.class,
                () -> libraryDAO.getById(library.getId())
        );
    }


    @Test
    void addGameToLibrary() {
        GameInLibrary result = libraryDAO.addGame(library, game);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(game.getId(), result.getGame().getId());
        assertEquals(library.getId(), result.getLibrary().getId());
        assertEquals(LocalDate.now(), result.getDateAdded());

        List<GameInLibrary> games = libraryDAO.getGames(library);

        assertEquals(1, games.size());
    }


    @Test
    void removeGameFromLibrary() {
        libraryDAO.addGame(library, game);

        Library updatedLibrary = libraryDAO.getById(library.getId());
        Game gameToRemove = game;

        boolean result = libraryDAO.removeGame(updatedLibrary, gameToRemove);

        assertTrue(result);
        assertTrue(libraryDAO.getGames(library).isEmpty());
    }


    @Test
    void getGamesReturnsGamesInLibrary() {
        libraryDAO.addGame(library, game);
        libraryDAO.addGame(library, game2);

        List<GameInLibrary> result = libraryDAO.getGames(library);

        assertNotNull(result);
        assertEquals(2, result.size());
    }


}
