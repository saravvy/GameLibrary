package app.daos;

import app.entities.Review;
import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDAO implements IDAO<User>{
    EntityManagerFactory emf;


    public UserDAO(EntityManagerFactory _emf){
        this.emf = _emf;
    }
    @Override
    public User create(User user) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
    }

    @Override
    public void remove(User user) {
        try(EntityManager em = emf.createEntityManager()){
            User found = em.find(User.class, user.getId());
            if(found == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
            System.out.println("Removal successfull!:)");

    } }

    @Override
    public User update(User user) {
        try(EntityManager em = emf.createEntityManager()){
            User founduser = em.find(User.class, user.getId());
            if(founduser == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            User user1 = em.merge(user);
            em.getTransaction().commit();
            return user1;
        }
    }

    @Override
    public User getById(int id) {
        try(EntityManager em = emf.createEntityManager()){
            User foundUser = em.find(User.class, id);
            if(foundUser == null)
                throw new EntityNotFoundException("No entity found with id: "+id);
            return foundUser;
        }
    }

    @Override
    public Set<User> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            return new HashSet<>(em.createQuery("SELECT e FROM User e", User.class).getResultList());
        }
    }
}
