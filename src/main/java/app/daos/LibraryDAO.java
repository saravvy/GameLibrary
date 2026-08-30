package app.daos;

import app.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LibraryDAO implements IDAO<Library>, ILibraryDAO{

    private EntityManagerFactory emf;

    public LibraryDAO(EntityManagerFactory _emf){
        this.emf = _emf;
    }
    @Override
   public Library create(Library library) {
        try(EntityManager em = emf.createEntityManager()){

            em.getTransaction().begin();
            em.persist(library);
            em.getTransaction().commit();
            return library;
        }
    }

    @Override
    public void remove(Library library) {
        try(EntityManager em = emf.createEntityManager()){
            Library found = em.find(Library.class, library.getId());
            if(found == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
            System.out.println("Removal successfull!:)");

        } }

    @Override
    public Library update(Library library) {
        try(EntityManager em = emf.createEntityManager()){
            Library foundlibrary = em.find(Library.class, library.getId());
            if(foundlibrary == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            Library library1 = em.merge(library);
            em.getTransaction().commit();
            return library1;
        }
    }

    @Override
    public Library getById(int id) {
        try(EntityManager em = emf.createEntityManager()){
            Library foundLibrary = em.find(Library.class, id);
            if(foundLibrary == null)
                throw new EntityNotFoundException("No entity found with id: "+id);
            return foundLibrary;
        }
    }

    @Override
    public Set<Library> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            return new HashSet<>(em.createQuery("SELECT e FROM Library e", Library.class).getResultList());
        }
    }

    @Override
    public Library getLibraryByUser(User user) {
        try(EntityManager em = emf.createEntityManager()){
            TypedQuery<Library> query = em.createQuery("SELECT s FROM Library s WHERE s.user.id = :userId", Library.class);
            query.setParameter("userId", user.getId());
            return query.getSingleResult();

    } }

    @Override
    public GameInLibrary addGame(Library library, Game game) {
        return null;
    }

    @Override
    public void removeGame(Library library, Game game) {

    }

    @Override
    public List<GameInLibrary> getGames(Library library) {
        return List.of();
    }

    @Override
    public boolean containsGame(Library library, Game game) {
        return false;
    }
}