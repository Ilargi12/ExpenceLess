package pl.edu.agh.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.edu.agh.model.Category;
import pl.edu.agh.util.SessionUtil;

import java.util.List;


public class CategoryDao implements ICategoryDao {

    @Override
    public void saveCategory(Category category) {
        CommonDaoSave.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        Transaction transaction = null;

        try {
            Session session = SessionUtil.getSession();
            transaction = session.beginTransaction();

            return session.createQuery("FROM Categories", Category.class).getResultList();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public Category findCategoryByName(String name) {
        Transaction transaction = null;

        try {
            Session session = SessionUtil.getSession();
            transaction = session.beginTransaction();

            Category res = (Category) session.createQuery("FROM Categories where name = :name")
                    .setParameter("name", name)
                    .getSingleResult();
            transaction.commit();

            return res;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }
}
