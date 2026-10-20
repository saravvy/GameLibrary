package app.daos;

import app.entities.User;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class SecurityDAO {

    private EntityManagerFactory emf;
    public SecurityDAO(EntityManagerFactory emf){
        this.emf = emf;
    }

    public User getVerifiedUser(String username, String password) throws ValidationException {
        User foundUser = null;
        try(EntityManager em = emf.createEntityManager()){
            foundUser = em.find(User.class,username);
            if(!foundUser.verifyPassword(password)){
                throw new ValidationException("not correct");
            }

        }  return foundUser;
    }
}
