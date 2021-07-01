package dao;

import model.User;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

public class UserDao extends BaseDao<User> {

    public UserDao() {
        super(User.class);
    }

    public User findByUsername(String username) {
        try {
            return entityManager.createQuery("from User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException();
        }
    }

    public boolean verify(String username, String password) {
        return !entityManager.createQuery("from User WHERE username = :username AND password = :password", User.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .getResultList().isEmpty();
    }
}
