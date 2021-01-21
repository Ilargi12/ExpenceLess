package pl.edu.agh.service;

import com.google.inject.Inject;
import pl.edu.agh.dao.TransactionDao;
import pl.edu.agh.model.Account;
import pl.edu.agh.model.Category;
import pl.edu.agh.model.Subcategory;
import pl.edu.agh.model.Transaction;
import pl.edu.agh.util.SessionUtil;

import java.util.List;

public class TransactionService {
    private final TransactionDao transactionDao;

    @Inject
    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public void saveTransaction(Transaction transaction) {
        SessionUtil.openSession();
        transactionDao.save(transaction);
        SessionUtil.closeSession();
    }

    public List<Transaction> getAllTransactionsOfAccount(Account account) {
        SessionUtil.openSession();
        List<Transaction> transactionList = transactionDao.getAllTransactionsOfAccount(account);
        SessionUtil.closeSession();

        return transactionList;
    }

    public List<Transaction> getTransactionsOfCategoryAndAccount(Category category, Account account) {
        SessionUtil.openSession();
        List<Transaction> transactionList = transactionDao.getTransactionsOfCategoryAndAccount(category, account);
        SessionUtil.closeSession();

        return transactionList;
    }

    public List<Transaction> getTransactionOfSubcategoryAndAccount(Subcategory subcategory, Account account) {
        SessionUtil.openSession();
        List<Transaction> transactionList = transactionDao.getTransactionOfSubcategoryAndAccount(subcategory, account);
        SessionUtil.closeSession();

        return transactionList;
    }

    public void deleteTransaction(Transaction transaction) {
        SessionUtil.openSession();
        transactionDao.delete(transaction);
        SessionUtil.closeSession();
    }

    public void updateTransaction(Transaction transaction) {
        SessionUtil.openSession();
        transactionDao.update(transaction);
        SessionUtil.closeSession();
    }
}
