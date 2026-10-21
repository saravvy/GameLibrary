package app.daos;

import app.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WishlistDAO implements IDAO<Wishlist>{
    private EntityManagerFactory emf;


    public WishlistDAO(EntityManagerFactory _emf){
        this.emf = _emf;
    }
    @Override
    public Wishlist create(Wishlist wishlist) {
        try(EntityManager em = emf.createEntityManager()){

            em.getTransaction().begin();
            em.persist(wishlist);
            em.getTransaction().commit();
            return wishlist;
        }
    }

    @Override
    public boolean remove(Wishlist wishlist) {
        try(EntityManager em = emf.createEntityManager()){
            Wishlist found = em.find(Wishlist.class, wishlist.getId());
            if(found == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
            System.out.println("Removal successfull!:)");
            return true;

        } }

    @Override
    public Wishlist update(Wishlist wishlist) {
        try(EntityManager em = emf.createEntityManager()){
            Wishlist found = em.find(Wishlist.class, wishlist.getId());
            if(found == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            Wishlist wishlist1 = em.merge(wishlist);
            em.getTransaction().commit();
            return wishlist1;
        }
    }

    @Override
    public Wishlist getById(int id) {
        try(EntityManager em = emf.createEntityManager()){
            Wishlist found = em.find(Wishlist.class, id);
            if(found == null)
                throw new EntityNotFoundException("No entity found with id: "+id);
            return found;
        }
    }

    @Override
    public Set<Wishlist> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            return new HashSet<>(em.createQuery("SELECT e FROM Wishlist e", Wishlist.class).getResultList());
        }
    }

    public Wishlist getWishlistByUser(User user) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT w FROM Wishlist w WHERE w.user.id = :userId",
                            Wishlist.class)
                    .setParameter("userId", user.getId())
                    .getSingleResult();
        }
    }

    public List<GameInWishlist> getGames(Wishlist wishlist) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT g FROM GameInWishlist g WHERE g.wishlist.id = :wishlistId",
                            GameInWishlist.class)
                    .setParameter("wishlistId", wishlist.getId())
                    .getResultList();
        }
    }

    public GameInWishlist addGame(Wishlist wishlist, Game game) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Wishlist managedWishlist = em.find(Wishlist.class, wishlist.getId());
            Game managedGame = em.find(Game.class, game.getId());

            if (managedWishlist == null || managedGame == null) {
                em.getTransaction().rollback();
                throw new EntityNotFoundException("Wishlist or game not found");
            }

            boolean exists = !em.createQuery(
                            "SELECT g FROM GameInWishlist g " +
                                    "WHERE g.wishlist.id = :wishlistId AND g.game.id = :gameId",
                            GameInWishlist.class)
                    .setParameter("wishlistId", managedWishlist.getId())
                    .setParameter("gameId", managedGame.getId())
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();

            if (exists) {
                em.getTransaction().rollback();
                throw new IllegalStateException("Game already in wishlist");
            }

            GameInWishlist entry = new GameInWishlist();
            entry.setWishlist(managedWishlist);
            entry.setGame(managedGame);
            entry.setDateAdded(LocalDate.now());

            em.persist(entry);
            em.getTransaction().commit();

            return entry;
        }
    }

    public boolean removeGame(Wishlist wishlist, Game game) {
        try (EntityManager em = emf.createEntityManager()) {
            GameInWishlist entry = em.createQuery(
                            "SELECT g FROM GameInWishlist g " +
                                    "WHERE g.wishlist.id = :wishlistId AND g.game.id = :gameId",
                            GameInWishlist.class)
                    .setParameter("wishlistId", wishlist.getId())
                    .setParameter("gameId", game.getId())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (entry == null) {
                return false;
            }

            em.getTransaction().begin();
            em.remove(entry);
            em.getTransaction().commit();

            return true;
        }
    }
}
