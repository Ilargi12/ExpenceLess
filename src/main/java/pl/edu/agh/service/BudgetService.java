package pl.edu.agh.service;

import com.google.inject.Inject;
import pl.edu.agh.dao.*;
import pl.edu.agh.model.*;
import pl.edu.agh.util.SessionUtil;

import java.math.BigDecimal;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BudgetService {

    private final SubcategoryBudgetDao subcategoryBudgetDao;

    private final BudgetDao budgetDao;

    private final TransactionDao transactionDao;


    @Inject
    public BudgetService(SubcategoryBudgetDao subcategoryBudgetDao, BudgetDao budgetDao, TransactionDao transactionDao) {
        this.subcategoryBudgetDao = subcategoryBudgetDao;
        this.budgetDao = budgetDao;
        this.transactionDao = transactionDao;
    }

    public void saveSubcategoryBudget(SubcategoryBudget subcategoryBudget) {
        SessionUtil.openSession();
        subcategoryBudgetDao.save(subcategoryBudget);
        SessionUtil.closeSession();
    }

    public void deleteSubcategoryBudget(SubcategoryBudget subcategoryBudget, Budget budget) {
        SessionUtil.openSession();
        budgetDao.removeSubcategoryBudget(budget, subcategoryBudget);
        subcategoryBudgetDao.delete(subcategoryBudget);
        SessionUtil.closeSession();
    }

    public void updateSubcategoryBudget(SubcategoryBudget subcategoryBudget) {
        SessionUtil.openSession();
        subcategoryBudgetDao.updateBudget(subcategoryBudget);
        SessionUtil.closeSession();
    }

    public void createBudget(Budget budget) {
        SessionUtil.openSession();
        budgetDao.save(budget);
        SessionUtil.closeSession();
    }

    public void addSubcategoryBudget(SubcategoryBudget subcategoryBudget, Budget budget) {
        SessionUtil.openSession();
        budgetDao.addSubcategoryBudget(budget, subcategoryBudget);
        SessionUtil.closeSession();
    }

    public BigDecimal calculateBudgetBalance(Budget budget, Subcategory subcategory) {
        BigDecimal balance = BigDecimal.ZERO;
        SessionUtil.openSession();
        List<Transaction> transactions = transactionDao.findTransactionByYearMonthSubcategory(subcategory, budget.getYear(), budget.getMonth());
        SessionUtil.closeSession();
        for (Transaction t : transactions) {
            balance = balance.add(t.getPrice());
        }
        return balance;
    }

    public List<Budget> getBudgetsByYear(int year) {
        SessionUtil.openSession();
        List<Budget> budgetList = budgetDao.getBudgetsByYear(year);
        SessionUtil.closeSession();

        return budgetList;
    }

    public List<Month> getFreeMonths(int year) {
        List<Integer> a = new ArrayList<>();
        List<Month> months = new ArrayList<>();
        for (Budget budget : getBudgetsByYear(year)) {
            a.add(budget.getMonth().getValue());
        }
        for (int i = 1; i <= 12; i++) {
            if (!a.contains(i)) {
                months.add(Month.of(i));
            }
        }
        return months;
    }

    public Optional<SubcategoryBudget> findSubcategoryBudget(Subcategory subcategory, Budget budget) {
        List<SubcategoryBudget> subcategoryBudgetList = budget.getSubcategoryBudgetList();
        return subcategoryBudgetList.stream()
                .filter(subcategoryBudget -> subcategoryBudget.getSubcategory().getId() == subcategory.getId())
                .findFirst();
    }

}
