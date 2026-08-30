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

public class RevivewDAO implements IDAO<Review> {


    private EntityManagerFactory emf;
    public RevivewDAO(EntityManagerFactory _emf){
        this.emf = _emf;
    }
    @Override
    public Review create(Review review) {
        try(EntityManager em = emf.createEntityManager()){

            em.getTransaction().begin();
            em.persist(review);
            em.getTransaction().commit();
            return review;
        }
    }

    @Override
    public void remove(Review review) {
        try(EntityManager em = emf.createEntityManager()){
            Review found = em.find(Review.class, review.getId());
            if(found == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            em.remove(found);
            em.getTransaction().commit();
            System.out.println("Removal successfull!:)");

        } }

    @Override
    public Review update(Review review) {
        try(EntityManager em = emf.createEntityManager()){
            Review found = em.find(Review.class, review.getId());
            if(found == null)
                throw new EntityNotFoundException("No entity found");
            em.getTransaction().begin();
            Review review1 = em.merge(review);
            em.getTransaction().commit();
            return review1;
        }
    }

    @Override
    public Review getById(int id) {
        try(EntityManager em = emf.createEntityManager()){
         Review foundReview = em.find(Review.class, id);
            if(foundReview == null)
                throw new EntityNotFoundException("No entity found with id: "+id);
            return foundReview;
        }
    }

    @Override
    public Set<Review> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            return new HashSet<>(em.createQuery("SELECT e FROM Review e", Review.class).getResultList());
        }
    }
}