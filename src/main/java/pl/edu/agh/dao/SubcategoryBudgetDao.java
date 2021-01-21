package pl.edu.agh.dao;

import org.hibernate.Session;
import pl.edu.agh.model.SubcategoryBudget;
import pl.edu.agh.util.SessionUtil;

import java.math.BigDecimal;

public class SubcategoryBudgetDao extends Dao {

    public void delete(SubcategoryBudget subcategoryBudget){
        org.hibernate.Transaction transaction = null;

        try {
            Session session = SessionUtil.getSession();
            transaction = session.beginTransaction();

            session.remove(subcategoryBudget);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public void updateBudget(SubcategoryBudget entity) {
        org.hibernate.Transaction tr = null;

        try {
            Session session = SessionUtil.getSession();
            tr = session.beginTransaction();
            session.merge(entity);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) {
                tr.rollback();
            }

            throw e;
        }
    }
}
