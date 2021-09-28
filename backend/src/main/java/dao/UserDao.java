package dao;

import auth.PasswordHasher;
import model.User;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

public class UserDao extends BaseDao<User> {

    public UserDao() {
        super(User.class);
    }

    /**
     * UserDao overrides BaseEntity's save method to automatically
     * hash user password before saving it to the database
     * @param entity the user object to save
     */
    @Transactional
    @Override
    public void save(User entity) {
        entity.setPassword(PasswordHasher.hashPassword(entity.getPassword()));
        super.save(entity);
    }

    /**
     * Find the user with specified email
     * @param email the user's email
     * @return the User object if a user with such email exists, otherwise throws EntityNotFoundException
     */
    public User findByEmail(String email) {
        try {
            return entityManager.createQuery("from User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Check user credentials against the database
     * @param email the user's email
     * @param password the user's password
     * @return true if the credentials match, false otherwise
     */
    public boolean verify(String email, String password) {
        return !entityManager.createQuery("from User WHERE email = :email AND password = :password", User.class)
                .setParameter("email", email)
                .setParameter("password", password)
                .getResultList().isEmpty();
    }

    /**
     * Login the user with the specified credentials
     * @param email the user's email
     * @param password the user's password
     * @return the User object if the credentials match, otherwise throws EntityNotFoundException
     */
    public User login(String email, String password) {
        if (verify(email, PasswordHasher.hashPassword(password)))
            return findByEmail(email);
        else
            throw new EntityNotFoundException();
    }
}
