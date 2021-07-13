package dao;

import model.User;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

public class UserDao extends BaseDao<User> {

    public UserDao() {
        super(User.class);
    }

    public User findByEmail(String email) {
        try {
            return entityManager.createQuery("from User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException();
        }
    }

    public boolean verify(String email, String password) {
        return !entityManager.createQuery("from User WHERE email = :email AND password = :password", User.class)
                .setParameter("email", email)
                .setParameter("password", password)
                .getResultList().isEmpty();
    }

    public User login(String email, String password) {
        if (verify(email, password))
            return findByEmail(email);
        else
            throw new EntityNotFoundException();
    }
}
