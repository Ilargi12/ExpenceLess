package pl.edu.agh.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.edu.agh.model.Budget;
import pl.edu.agh.model.SubcategoryBudget;
import pl.edu.agh.util.SessionUtil;

import java.time.Month;
import java.util.List;

public class BudgetDao extends Dao {

    public List<Budget> getBudgetsByYear(int year) {
        Transaction transaction = null;

        try {
            Session session = SessionUtil.getSession();
            transaction = session.beginTransaction();
             List <Budget> res = session.createQuery("FROM Budget B where B.year = :year ", Budget.class)
                    .setParameter("year", year)
                    .getResultList();
            transaction.commit();

             return res;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public Budget getBudgetByMonth(int year, Month month) {
        return null; // FIXME bruh
    }

    public void addSubcategoryBudget(Budget budget, SubcategoryBudget subcategoryBudget){
        Transaction transaction = null;

        try {
            Session session = SessionUtil.getSession();
            transaction = session.beginTransaction();
            budget.addSubcategoryBudget(subcategoryBudget);
            session.update(budget);
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }

    }
    public void removeSubcategoryBudget(Budget budget, SubcategoryBudget subcategoryBudget){
        Transaction transaction = null;

        try {
            Session session = SessionUtil.getSession();
            transaction = session.beginTransaction();
            budget.removeSubcategoryBudget(subcategoryBudget);
            session.update(budget);
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }

    }
}
