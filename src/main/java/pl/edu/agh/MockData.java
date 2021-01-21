package pl.edu.agh;

import pl.edu.agh.model.*;
import pl.edu.agh.service.AccountService;
import pl.edu.agh.service.BudgetService;
import pl.edu.agh.service.CategoryService;
import pl.edu.agh.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MockData {
    private final AccountService accountService;
    private final BudgetService budgetService;
    private List<Category> categoryList;
    private List<Subcategory> subcategories;
    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final Random random = new Random();

    public void init(){
        List<Account> accounts = createMockAccounts();
        createMockCategories();
        createMockTransactions(accounts);
        createMockBudget();
    }

    public MockData(AccountService accountService, BudgetService budgetService, CategoryService categoryService, TransactionService transactionService) {
        this.accountService = accountService;
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.transactionService = transactionService;
    }

    public List<Account> createMockAccounts(){
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("Moje konto 1", BigDecimal.valueOf(24.38)));
        accounts.add(new Account("Moje konto 2", BigDecimal.valueOf(1.36)));
        accounts.add(new Account("Moje konto 3", BigDecimal.valueOf(100.35)));
        accounts.add(new Account("Moje konto 4", BigDecimal.valueOf(43.21)));

        for (Account account : accounts){
            accountService.createAccount(account);
        }

        return accounts;
    }

    public void createMockCategories() {
        categoryList = Arrays.asList(
                new Category("Przychody", Type.INCOME),
                new Category("Wydatki", Type.EXPENSE)
        );
        categoryList.get(0).setCanBeDeleted(false);
        categoryList.get(1).setCanBeDeleted(false);

        for (Category category : categoryList) {
            categoryService.createCategory(category);
        }

        subcategories = new ArrayList<>();
        subcategories.add(new Subcategory("Inne", categoryList.get(0)));
        subcategories.add(new Subcategory("Inne", categoryList.get(1)));
        subcategories.get(0).setCanBeDeleted(false);
        subcategories.get(1).setCanBeDeleted(false);

        for (Subcategory subcategory : subcategories){
            categoryService.createSubcategory(subcategory);
        }
    }

    public void createMockTransactions(List<Account> accounts){

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(Transaction.builder()
                .name("Podatek CIT")
                .price(BigDecimal.valueOf(89.97))
                .date(LocalDate.now())
                .description("Zapłacone za mandat")
                .account(accounts.get(0))
                .subCategory(subcategories.get(1))
                .type(subcategories.get(1).getCategory().getType())
                .build());
        transactions.add(Transaction.builder()
                .name("Warzywa")
                .price(BigDecimal.valueOf(23.97))
                .date(LocalDate.now())
                .description("Dla babci")
                .account(accounts.get(0))
                .subCategory(subcategories.get(1))
                .type(subcategories.get(1).getCategory().getType())
                .build());
        transactions.add(Transaction.builder()
                .name("Urodziny")
                .price(BigDecimal.valueOf(100.0))
                .date(LocalDate.now())
                .description("U cioci na imieninach")
                .account(accounts.get(0))
                .subCategory(subcategories.get(0))
                .type(subcategories.get(0).getCategory().getType())
                .build());

        for (int y = 2019; y < 2022; y++) {
            for (int i = 1; i <= 12; i++) {
                transactions.add(Transaction.builder()
                        .name("Wydatek")
                        .price(BigDecimal.valueOf(random.nextInt(300)))
                        .date(LocalDate.of(y, i, 1))
                        .description("Description")
                        .account(accounts.get(0))
                        .subCategory(subcategories.get(1))
                        .type(subcategories.get(1).getCategory().getType())
                        .build());
                transactions.add(Transaction.builder()
                        .name("Przychód")
                        .price(BigDecimal.valueOf(random.nextInt(300)))
                        .date(LocalDate.of(y, i, 1))
                        .description("Description")
                        .account(accounts.get(0))
                        .subCategory(subcategories.get(0))
                        .type(subcategories.get(0).getCategory().getType())
                        .build());
            }
        }

        for (Transaction transaction : transactions){
            transactionService.saveTransaction(transaction);
            accountService.addTransaction(transaction.getAccount(), transaction);
        }
    }

    public void createMockBudget(){

        for (int y = 2019; y < 2022; y++) {
            for (Month m : Month.values()) {
                Budget budget = new Budget();
                budget.setMonth(m);
                budget.setYear(y);

                for (Subcategory subcategory : subcategories){
                    BigDecimal plannedBudget = new BigDecimal(200);
                    if (!plannedBudget.equals(BigDecimal.ZERO)){
                        budget.addSubcategoryBudget(new SubcategoryBudget(subcategory, plannedBudget));
                    }
                }

                budgetService.createBudget(budget);
            }
        }


    }
}
