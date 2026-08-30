package app.daos;

import app.entities.Library;
import app.entities.Review;
import app.entities.User;
import app.entities.Wishlist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

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
    public void remove(Wishlist wishlist) {
        try(EntityManager em = emf.createEntityManager()){
            Wishlist found = em.find(Wishlist.class, wishlist.getId());
            if(found == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
            System.out.println("Removal successfull!:)");

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
}
