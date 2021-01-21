package pl.edu.agh.service;

import com.google.inject.Inject;
import pl.edu.agh.dao.AccountDao;
import pl.edu.agh.model.Account;
import pl.edu.agh.model.Transaction;
import pl.edu.agh.util.SessionUtil;

import java.util.List;

public class AccountService {

    private final AccountDao accountDao;

    @Inject
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void createAccount(Account account){
        SessionUtil.openSession();
        accountDao.save(account);
        SessionUtil.closeSession();
    }

    public List<Account> getAllAccounts(){
        SessionUtil.openSession();
        var list = accountDao.getAllAccounts();
        SessionUtil.closeSession();

        return list;
    }

    public void addTransaction(Account account, Transaction transaction){
        SessionUtil.openSession();
        accountDao.addTransaction(account, transaction);
        SessionUtil.closeSession();
    }

    public void removeTransaction(Account account, Transaction transaction){
        SessionUtil.openSession();
        accountDao.removeTransaction(account, transaction);
        SessionUtil.closeSession();
    }
}
