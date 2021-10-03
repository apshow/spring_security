package web.dao;

import org.springframework.stereotype.Repository;
import web.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Override // добавить
    public void addUser(User user) {
        entityManager.persist(user);
    }

    @Override //обновить
    public void updateUser(User user) {
        entityManager.merge(user);
    }

    @Override // удалить юзера
    public void removeUserById(long id) {
        entityManager.remove(entityManager.find(User.class, id));
    }

    @Override
    //получаем юзера по айди
    public User getUserById(long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    //получаем список всех юзеров
    public List<User> getAllUsers() {
        return entityManager.createQuery("from User").getResultList();
    }

    @Override
    public User getUserByName(String username) {
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.username=:username",
                User.class).setParameter("username", username);
        return query.getSingleResult();
    }
}
