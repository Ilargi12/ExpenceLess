package pl.edu.agh.dao;

import org.hibernate.Session;
import pl.edu.agh.model.Account;
import pl.edu.agh.model.Category;
import pl.edu.agh.model.Subcategory;
import pl.edu.agh.model.Transaction;
import pl.edu.agh.util.SessionUtil;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionDao extends Dao {

    public List<Transaction> getAllTransactionsOfAccount(Account account) {
        org.hibernate.Transaction tr = null;

        try {
            Session session = SessionUtil.getSession();
            tr = session.beginTransaction();
            List<Transaction> transactionList = session.
                    createQuery("FROM Transactions WHERE account.id = :id", Transaction.class).
                    setParameter("id", account.getId()).
                    getResultList();
            tr.commit();

            return transactionList;
        } catch (Exception e) {

            if (tr != null) {
                tr.rollback();
            }

            throw e;
        }

    }

    public List<Transaction> findTransactionByYearMonthSubcategory(Subcategory subcategory, int year, Month month) {
        org.hibernate.Transaction tr = null;

        String pattern = "dd-MM-yyyy";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);

        LocalDate startDate = LocalDate.parse("01-" + (month.getValue() < 10 ? "0" + month.getValue() : month.getValue()) + "-" + year, dateTimeFormatter);
        if (month == Month.DECEMBER) {
            year++;
        }
        LocalDate endDate = LocalDate.parse("01-" + (month.plus(1).getValue() < 10 ? "0" + month.plus(1).getValue() : month.plus(1).getValue()) + "-" + year, dateTimeFormatter);

        try {
            Session session = SessionUtil.getSession();
            tr = session.beginTransaction();
            List<Transaction> res = session.createQuery("SELECT T FROM Transactions T where T.subCategory = :subcategory and T.date >= :start and T.date < :end ", Transaction.class)
                    .setParameter("subcategory", subcategory)
                    .setParameter("start", startDate)
                    .setParameter("end", endDate)
                    .getResultList();
            tr.commit();

            return res;
        } catch (Exception e) {
            if (tr != null) {
                tr.rollback();
            }

            throw e;
        }
    }

    public List<Transaction> getTransactionOfSubcategoryAndAccount(Subcategory subcategory, Account account) {
        org.hibernate.Transaction tr = null;

        try {
            Session session = SessionUtil.getSession();
            tr = session.beginTransaction();
            List<Transaction> transactionList = session.
                    createQuery("SELECT T FROM Transactions T " +
                            "WHERE T.subCategory = :subcategory and T.account = :account", Transaction.class).
                    setParameter("subcategory", subcategory).
                    setParameter("account", account).
                    getResultList();
            tr.commit();

            return transactionList;
        } catch (Exception e) {

            if (tr != null) {
                tr.rollback();
            }

            throw e;
        }

    }

    public List<Transaction> getTransactionsOfCategoryAndAccount(Category category, Account account) {
        org.hibernate.Transaction tr = null;

        try {
            Session session = SessionUtil.getSession();
            tr = session.beginTransaction();
            List<Transaction> transactionList = session.
                    createQuery("SELECT T FROM Transactions T " +
                            "JOIN Subcategories S ON S.id = T.subCategory.id " +
                            "WHERE T.account = :account and S.category = :category", Transaction.class).
                    setParameter("category", category).
                    setParameter("account", account).
                    getResultList();
            tr.commit();

            return transactionList;
        } catch (Exception e) {

            if (tr != null) {
                tr.rollback();
            }

            throw e;
        }

    }

    public void delete(Transaction transaction) {
        org.hibernate.Transaction tr = null;

        try {
            Session session = SessionUtil.getSession();
            tr = session.beginTransaction();
            session.delete(transaction);
            tr.commit();
        } catch (Exception e) {

            if (tr != null) {
                tr.rollback();
            }

            throw e;
        }
    }

    public void update(Transaction transaction) {
        org.hibernate.Transaction tr = null;

        try {
            Session session = SessionUtil.getSession();
            tr = session.beginTransaction();
            session.update(transaction);
            session.update(transaction.getAccount());
            tr.commit();
        } catch (Exception e) {

            if (tr != null) {
                tr.rollback();
            }

            throw e;
        }
    }
}
