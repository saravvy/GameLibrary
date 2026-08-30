package app.daos;

import app.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameDAO implements IDAO<Game>{
    private EntityManagerFactory emf;


    public GameDAO(EntityManagerFactory _emf){
        this.emf = _emf;
    }
    @Override
    public Game create(Game game) {
        try(EntityManager em = emf.createEntityManager()){

            em.getTransaction().begin();
            em.persist(game);
            em.getTransaction().commit();
            return game;
        }
    }

    @Override
    public void remove(Game game) {
        try(EntityManager em = emf.createEntityManager()){
            Game found = em.find(Game.class, game.getId());
            if(found == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
            System.out.println("Removal successfull!:)");

        } }

    @Override
    public Game update(Game game) {
        try(EntityManager em = emf.createEntityManager()){
            Game found = em.find(Game.class, game.getId());
            if(found == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            Game game1 = em.merge(game);
            em.getTransaction().commit();
            return game1;
        }
    }

    @Override
    public Game getById(int id) {
        try(EntityManager em = emf.createEntityManager()){
            Game found = em.find(Game.class, id);
            if(found == null)
                throw new EntityNotFoundException("No entity found with id: "+id);
            return found;
        }
    }

    @Override
    public Set<Game> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            return new HashSet<>(em.createQuery("SELECT e FROM Game e", Game.class).getResultList());
        }
    }
}